package app.service;

import app.service.bean.Screen;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

@Service
public class ScreenService {

    public Screen get() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//        Screen screen = screenService.get(250, 100);
        int width = dim.width;
        int height = dim.height;
        return get(width, height);
    }

    public Screen get(int width, int height) {
        try {
            Rectangle rectangle = new Rectangle(width, height);

            return Screen.builder()
                    .bufferedImage(screenCapture(rectangle))
                    .width(rectangle.width)
                    .height(rectangle.height)
                    .build();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return Screen.builder().build();
    }

    public int getMaxEnabledSquare(Screen screen){
        return getMaxEnabledSquare(screen.getWidth(), screen.getHeight());
    }

    public int getMaxEnabledSquare(int a, int b){
        if (a == 0) {
            return b;
        }
        if (b == 0) {
            return a;
        }
        if (a > b) {
            int div = Math.floorDiv(a, b);
            int delta = a - b*div;
            return getMaxEnabledSquare(b, delta);
        }
        int div = Math.floorDiv(b, a);
        int delta = b - a*div;
        return getMaxEnabledSquare(a, delta);
    }

    public BufferedImage screenCapture(Rectangle rectangle) throws AWTException {
        GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gDev = gEnv.getDefaultScreenDevice();
        Robot robot = new Robot(gDev);
        return robot.createScreenCapture(rectangle);
    }
}
