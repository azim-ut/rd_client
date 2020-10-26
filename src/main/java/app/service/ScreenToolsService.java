package app.service;

import app.service.bean.Screen;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
@Service
public class ScreenToolsService {
    private Robot robot;

    public Screen get() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//        Screen screen = screenService.get(250, 100);
        int width = dim.width;
        int height = dim.height;
        return get(width, height);
    }

    public Screen get(int width, int height) {
        Rectangle rectangle = new Rectangle(width, height);
        BufferedImage screen = screenCapture(rectangle);

        int newWidth = (int) Math.round(width / 1.2);
        int newHeight = (int) Math.round(height / 1.2);
        BufferedImage res =
                Scalr.resize(screen, Scalr.Method.BALANCED, newWidth, newHeight);

            return Screen.builder()
                .bufferedImage(res)
                .width(rectangle.width)
                .height(rectangle.height)
                .build();
    }

    public int getMaxEnabledSquare(Screen screen) {
        return getMaxEnabledSquare(screen.getWidth(), screen.getHeight());
    }

    public int getMaxEnabledSquare(int a, int b) {
        if (a == 0) {
            return b;
        }
        if (b == 0) {
            return a;
        }
        if (a > b) {
            int div = Math.floorDiv(a, b);
            int delta = a - b * div;
            return getMaxEnabledSquare(b, delta);
        }
        int div = Math.floorDiv(b, a);
        int delta = b - a * div;
        return getMaxEnabledSquare(a, delta);
    }

    private void defineRobot() {
        if (this.robot == null) try {
            {
                GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice gDev = gEnv.getDefaultScreenDevice();
                this.robot = new Robot(gDev);
            }
        } catch (AWTException e) {
            log.error("AWTException. Can't create Robot instance. ");
        }
    }

    public BufferedImage screenCapture(Rectangle rectangle) {
        defineRobot();
        if (robot != null) {
            return robot.createScreenCapture(rectangle);
        }
        return null;
    }
}
