package app;

import javax.swing.*;
import java.net.Socket;

public class Start {

    public static void main(String[] args) {
        String ip = JOptionPane.showInputDialog("Please enter server IP");
        new Start().initilize(ip, Integer.parseInt(Constants.port));

    }

    private void initilize(String ip, int port) {
        try {
            Socket sc = new Socket(ip, port);
            System.out.println("Connection to the server: " + ip + ":" + port);
            Authentication frame1 = new Authentication(sc);
            frame1.setSize(300, 80);
            frame1.setLocation(500, 300);
            frame1.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
