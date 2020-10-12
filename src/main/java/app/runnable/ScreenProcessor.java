package app.runnable;

import app.bean.ScreenPacket;
import app.bean.ConnectionContext;
import app.service.ScreenService;
import app.service.bean.Screen;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Queue;

@Slf4j
public class ScreenProcessor implements Runnable {

    private ConnectionContext ctx;
    private final ScreenService screenService = new ScreenService();
    private final Queue<ScreenPacket> pipe;
    private List<Integer> samples = null;
    private Screen bgScreen;

    public ScreenProcessor(ConnectionContext ctx, Queue<ScreenPacket> pipe) {
        this.ctx = ctx;
        this.pipe = pipe;
    }

    // standard constructors
    public void run() {

        long epoch = 0;
        try {
            bgScreen = screenService.get();
            int side = screenService.getMaxEnabledSquare(bgScreen);

            saveBg(bgScreen);
            samples = bgScreen.croppedToSet(side, side);

            while (true) {
                if (!ctx.getConnected()) {
                    continue;
                }
                int sampleIndex = 0;
                int changes = 0;

                for (int y = 0; y < bgScreen.getHeight(); y = y + side) {
                    for (int x = 0; x < bgScreen.getWidth(); x = x + side) {
                        if (pipe.size() >= 50) {
                            continue;
                        }
                        changes += processArea(epoch, x, y, sampleIndex, side);

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
                epoch++;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("Screen Processor is OFF on epoch {}", epoch);
        }
    }

    private int processArea(long epoch, int x, int y, int sampleIndex, int sideSize) {
        BufferedImage newCrop = screenService.get().getBufferedImage().getSubimage(x, y, sideSize, sideSize);
        Screen row = Screen.builder()
                .bufferedImage(newCrop)
                .width(sideSize)
                .height(sideSize)
                .build();
        String fileName = "temp" + sampleIndex + ".jpg";
        int newBlockSize = row.getAreaSum(0, 0, sideSize, sideSize);
        if (newBlockSize < 0) {
            return 0;
        }
        int sampleBlockSize = samples.get(sampleIndex);

        if (newBlockSize != sampleBlockSize) {
            toPipe(ScreenPacket.builder()
                    .epoch(epoch)
                    .createFile(fileName)
                    .position(sampleIndex)
                    .tw(bgScreen.getWidth())
                    .th(bgScreen.getHeight())
                    .w(row.getWidth())
                    .h(row.getHeight())
                    .x(x)
                    .y(y)
                    .code("TEST")
                    .bytes(getBytes(newCrop))
                    .build()
            );
            return 1;
        }
        return 0;
    }

    private void saveBg(Screen screen) {
        try {
            String fileName = "bg.jpg";
            toPipe(ScreenPacket.builder()
                    .createFile(fileName)
                    .position(0)
                    .tw(bgScreen.getWidth())
                    .th(bgScreen.getHeight())
                    .w(screen.getWidth())
                    .h(screen.getHeight())
                    .x(0)
                    .y(0)
                    .code("TEST")
                    .bytes(getBytes(screen.getBufferedImage()))
                    .build()
            );
            ImageIO.write(screen.getBufferedImage(), "jpg", new File("screen/" + fileName));
        } catch (IOException e) {
            log.error("SaveBg Exception " + e.getMessage(), e);
        }
    }

    private void toPipe(ScreenPacket action) {
        pipe.add(action);
        System.out.println("Pipe updated: " + pipe.size());
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
