package app.process.screen;

import app.bean.ConnectionContext;
import app.bean.ScreenPacket;
import app.service.ScreenToolsService;
import app.service.bean.Screen;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PrintScreenRunnable implements Runnable {

    private final ScreenToolsService screenService = new ScreenToolsService();
    private final ConnectionContext ctx;
    private Map<Integer, Integer> samples = new HashMap<>();
    private final Map<String, Integer> processedPackets = new HashMap<>();


    public PrintScreenRunnable(ConnectionContext ctx) {
        this.ctx = ctx;
    }

    // standard constructors
    public void run() {

        try {
            Screen bgScreen = screenService.get();
            int width = bgScreen.getWidth();
            int height = bgScreen.getHeight();
            int side = screenService.getMaxEnabledSquare(bgScreen);

            saveBg(bgScreen);
//            samples = bgScreen.cropToSet(side, side);

            while (true) {
                boolean breaked = false;
                int sampleIndex = 1;
                int changes = 0;
                boolean bgUpdated = false;
                BufferedImage currentScreen = screenService.get().getBufferedImage();
                for (int y = 0; y < height; y = y + side) {
                    for (int x = 0; x < width; x = x + side) {
                        changes += processArea(bgScreen, currentScreen, x, y, sampleIndex, side);

                        if (changes > samples.size() / 2) {
                            bgScreen = screenService.get();
                            saveBg(bgScreen);
                            samples.clear();
                            ctx.clearSave();
//                            samples.addAll(bgScreen.cropToSet(side, side));
                            //TODO start check from the begin, because of bg updated
                            bgUpdated = true;
                            breaked = true;
                        }
                        sampleIndex++;

                        if (breaked) {
                            break;
                        }
                    }
                    if (breaked) {
                        break;
                    }
                }
                if (bgUpdated) {
                    toPipe(ScreenPacket.builder()
                            .id(defineId(ctx.getCode(), 0, 0, 0, 0, 0))
                            .bytes(new byte[0])
                            .command("ONLY_BG")
                            .build()
                    );
                }
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            log.info("ScreenProcessor interrupted");
        }
    }

    private int processArea(Screen bgScreen, BufferedImage img, int x, int y, int sampleIndex, int sideSize) {
        byte[] bytes = new byte[0];
        try {
            if (x >= img.getWidth() || (x + sideSize) > img.getWidth()) {
                return -1;
            }
            if (y >= img.getHeight() || (y + sideSize) > img.getHeight()) {
                return -1;
            }
            if (!samples.containsKey(sampleIndex)){
                samples.put(sampleIndex, bgScreen.getAreaSum(x, y, sideSize, sideSize));
            }
            BufferedImage newCrop = img.getSubimage(x, y, sideSize, sideSize);
            Screen row = Screen.builder()
                    .bufferedImage(newCrop)
                    .width(sideSize)
                    .height(sideSize)
                    .build();
            int newBlockSize = row.getAreaSum(0, 0, sideSize, sideSize);
            if (newBlockSize >= 0) {
                int sampleBlockSize = samples.get(sampleIndex);
                if (newBlockSize != sampleBlockSize) {
                    bytes = getBytes(newCrop);
                }
            }

            toPipe(ScreenPacket.builder()
                    .id(defineId(ctx.getCode(), sampleIndex, x, y, row.getWidth(), row.getHeight()))
                    .bytes(bytes)
                    .build()
            );
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return bytes.length > 0 ? 1 : 0;
    }

    private String defineId(String code, int position, int x, int y, int w, int h) {
        return code +
                "_" +
                position +
                "_" +
                x +
                "_" +
                y +
                "_" +
                w +
                "_" +
                h;
    }

    private void saveBg(Screen screen) {
        try {
            String fileName = "bg.jpg";
            toPipe(ScreenPacket.builder()
                    .id(defineId(ctx.getCode(), 0, 0, 0, screen.getWidth(), screen.getHeight()))
                    .bytes(getBytes(screen.getBufferedImage()))
                    .build()
            );
            ImageIO.write(screen.getBufferedImage(), "jpg", new File("screen/" + fileName));
        } catch (IOException e) {
            log.error("SaveBg Exception " + e.getMessage(), e);
        }
    }

    private void toPipe(ScreenPacket packet) {
        if (packet.getPosition() == 0) {
            log.debug("Updated BG");
        }
        if (processedPackets.containsKey(packet.getId()) && processedPackets.get(packet.getId()) == packet.getBytes().length) {
            return;
        }
        ctx.toSave().add(packet);
        processedPackets.put(packet.getId(), packet.getBytes().length);
    }

    public byte[] getBytes(BufferedImage bufferedImage) {
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpeg", os);
            os.flush();
            return os.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return new byte[0];
    }
}
