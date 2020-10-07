package app.runnable;

import app.bean.ActionPacket;
import app.service.ScreenService;
import app.service.bean.Screen;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Queue;

@Slf4j
public class ScreenProcessor implements Runnable {
    ScreenService screenService = new ScreenService();
    private Queue<ActionPacket> pipe;
    private List<Integer> samples = null;

    public ScreenProcessor(Queue<ActionPacket> pipe) {
        this.pipe = pipe;
    }

    // standard constructors
    public void run() {
        Screen bgScreen = screenService.get();
        int side = screenService.getMaxEnabledSquare(bgScreen);

        saveBg(bgScreen);
        samples = bgScreen.croppedToSet(side, side);

        while (true) {
            int sampleIndex = 0;
            int changes = 0;

            for (int j = 0; j < bgScreen.getHeight(); j = j + side) {
                for (int i = 0; i < bgScreen.getWidth(); i = i + side) {
                    try {
                        if (pipe.size() >= 10) {
                            Thread.sleep(500);
                            continue;
                        }
                        changes += processArea(i, j, sampleIndex, side);

                        if (changes > samples.size() / 2) {
                            bgScreen = screenService.get();
                            saveBg(bgScreen);
                            samples = bgScreen.croppedToSet(side, side);
                            return;
                        }
                    } catch (InterruptedException | IOException e) {
                        log.error(e.getMessage(), e);
                    }
                    sampleIndex++;
                }
            }
            log.debug("Screen processed");
        }
    }

    private int processArea(int i, int j, int sampleIndex, int sideSize) throws IOException {
        BufferedImage newCrop = screenService.get().getBufferedImage().getSubimage(i, j, sideSize, sideSize);
        Screen row = Screen.builder()
                .bufferedImage(newCrop)
                .width(sideSize)
                .height(sideSize)
                .build();
        String fileName = sampleIndex + ".jpg";
        String filePath = "screen/temp" + fileName;
        int newBlockSize = row.getAreaSum(0, 0, sideSize, sideSize);
        if (newBlockSize < 0) {
            Files.deleteIfExists(Paths.get(filePath));
            return 0;
        }
        int sampleBlockSize = samples.get(sampleIndex);

        if (newBlockSize != sampleBlockSize) {
            File file = new File(filePath);
            ImageIO.write(newCrop, "jpg", file);
            toPipe(ActionPacket.builder()
                    .createFile(fileName)
                    .code("TEST")
                    .bytes(getBytes(newCrop))
                    .build()
            );
            return 1;
        } else {
            Files.deleteIfExists(Paths.get(filePath));
            toPipe(ActionPacket.builder()
                    .removeFile(filePath)
                    .code("TEST")
                    .build()
            );
        }
        return 0;
    }

    private void saveBg(Screen screen) {
        try {
            String fileName = "bg.jpg";
            toPipe(ActionPacket.builder()
                    .createFile(fileName)
                    .code("TEST")
                    .bytes(getBytes(screen.getBufferedImage()))
                    .build()
            );
            ImageIO.write(screen.getBufferedImage(), "jpg", new File("screen/" + fileName));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void toPipe(ActionPacket action) {
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
