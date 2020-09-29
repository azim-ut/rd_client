package app;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SendEvents implements KeyListener, MouseMotionListener, MouseListener {
    private Socket socket;
    private JPanel cPanel;
    private PrintWriter writer;
    String width = "";
    String height = "";
    double w;
    double h;

    public SendEvents(Socket socket, JPanel panel, String width, String height) {
        this.socket = socket;
        cPanel = panel;
        this.width = width;
        this.height = height;
        w = Double.valueOf(width.trim()).doubleValue();
        h = Double.valueOf(height.trim()).doubleValue();

        cPanel.addKeyListener(this);
        cPanel.addMouseListener(this);
        cPanel.addMouseMotionListener(this);

        try {
            writer = new PrintWriter(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        writer.println();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        double xScale = w / cPanel.getWidth();
        double yScale = h / cPanel.getHeight();
        writer.println(Commands.MOVE_MOUSE.getAbbrev());
        Double x = e.getX() * xScale;
        Double y = e.getY() * yScale;
        writer.println(x.intValue());
        writer.println(y.intValue());
        writer.flush();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        writer.println(Commands.MOUSE_DOWN.getAbbrev());
        int button = e.getButton();
        int xButton = 16;
        if (button == 3){
            xButton = 4;
        }
        writer.println(xButton);
        writer.flush();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        writer.println(Commands.KEY_DOWN.getAbbrev());
        writer.println(e.getKeyCode());
        writer.flush();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        writer.println(Commands.KEY_UP.getAbbrev());
        writer.println(e.getKeyCode());
        writer.flush();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        writer.println(Commands.MOUSE_UP.getAbbrev());
        int button = e.getButton();
        int xButton = 16;
        if (button == 3){
            xButton = 4;
        }
        writer.println(xButton);
        writer.flush();
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
