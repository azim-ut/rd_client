package app;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class CreateFrame extends Thread {
    String width;
    String height;
    JFrame frame = new JFrame();
    JDesktopPane desktop = new JDesktopPane();
    Socket socket;
    JInternalFrame interFrame = new JInternalFrame("Server Screen", true, true, true);
    JPanel cpanel = new JPanel();

    public CreateFrame(Socket socket, String width, String height) throws HeadlessException {
        this.width = width;
        this.height = height;
        this.socket = socket;
        start();
    }

    public void drawGUI() {
        frame.add(desktop, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        interFrame.setLayout(new BorderLayout());
        interFrame.getContentPane().add(cpanel, BorderLayout.CENTER);
        interFrame.setSize(100, 100);
        desktop.add(interFrame);

        try {
            interFrame.setMaximum(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpanel.setFocusable(true);
        interFrame.setVisible(true);
    }

    @Override
    public void run() {
        InputStream in = null;
        drawGUI();
        try {
            in = socket.getInputStream();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        new ReceivingScreen(in, cpanel);
        new SendEvents(socket, cpanel, width, height);
    }
}
