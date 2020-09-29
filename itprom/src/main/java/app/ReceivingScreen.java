package app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ReceivingScreen extends Thread {
    private ObjectInputStream objectInputStream = null;
    private JPanel cPanel = null;
    private boolean continueLoop = false;
    InputStream oin = null;
    Image image1 = null;

    public ReceivingScreen(InputStream inputStream, JPanel panel) {
        oin = inputStream;
        cPanel = panel;
        start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] bytes = new byte[1024 * 1024];
                int count = 0;
                do {
                    count += oin.read(bytes, count, bytes.length - count);
                } while (!(count > 4 && bytes[count - 2] == (byte) - 1 && bytes[count - 1] == (byte) -39));
                //byte -1=00 00 00 00
                //byte -1=11 11 11 11
                image1 = ImageIO.read(new ByteArrayInputStream(bytes));
                image1 = image1.getScaledInstance(cPanel.getWidth(), cPanel.getHeight(), Image.SCALE_FAST);

                Graphics graphics = cPanel.getGraphics();
                graphics.drawImage(image1, 0, 0, cPanel.getWidth(), cPanel.getHeight(), cPanel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
