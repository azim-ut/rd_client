package app.process.screen;

import app.bean.ConnectionContext;
import app.bean.ScreenPacket;
import app.service.ScreenService;
import app.service.bean.Screen;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class ScreenProcessor implements Runnable {

    private final ScreenService screenService = new ScreenService();
    private final ConnectionContext ctx;
    private List<Integer> samples = null;

    public ScreenProcessor(ConnectionContext ctx) {
        this.ctx = ctx;
    }

    // standard constructors
    public void run() {

        long epoch = 0;
        try {
            Screen bgScreen = screenService.get();
            int width = bgScreen.getWidth();
            int height = bgScreen.getHeight();
            int side = screenService.getMaxEnabledSquare(bgScreen);

            saveBg(bgScreen);
            samples = bgScreen.croppedToSet(side, side);

            while (true) {
                int sampleIndex = 0;
                int changes = 0;

                for (int y = 0; y < height; y = y + side) {
                    for (int x = 0; x < width; x = x + side) {
                        if (ctx.screens().isFull()) {
                            continue;
                        }
                        changes += processArea(x, y, sampleIndex, side);

                        if (changes > samples.size() / 2) {
                            bgScreen = screenService.get();
                            saveBg(bgScreen);
                            samples = bgScreen.croppedToSet(side, side);
                            //TODO start check from the begin, because of bg updated
                            continue;
                        }
                        sampleIndex++;
                    }
                }
                Thread.sleep(100);
                epoch++;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("Screen Processor is OFF on epoch {}", epoch);
        }
    }

    private int processArea(int x, int y, int sampleIndex, int sideSize) {
        BufferedImage newCrop = screenService.get().getBufferedImage().getSubimage(x, y, sideSize, sideSize);
        byte[] bytes = new byte[0];
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

    private void toPipe(ScreenPacket action) {
        ctx.screens().add(action);
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
