package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Authentication extends JFrame implements ActionListener {
    private Socket cSocket = null;
    DataOutputStream passchk = null;
    DataInputStream verification = null;
    String verify;
    JButton submit;
    JPanel panel;
    JLabel label, label1;
    String width = "";
    String height = "";
    JTextField text1;

    public Authentication(Socket socket) throws HeadlessException {
        label1 = new JLabel();
        label1.setText("Password");
        label = new JLabel();
        label.setText("");
        this.setLayout(new BorderLayout());
        submit = new JButton("Submit");

        text1 = new JTextField(15);
        this.cSocket = socket;

        panel = new JPanel(new GridLayout(2, 1));
        panel.add(label1);
        panel.add(text1);
        panel.add(label);
        panel.add(submit);
        add(panel, BorderLayout.CENTER);
        submit.addActionListener(this);
        setTitle("Login form");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String value1 = text1.getText();
        try {
            passchk = new DataOutputStream(cSocket.getOutputStream());
            verification = new DataInputStream(cSocket.getInputStream());
            passchk.writeUTF(value1);
            verify = verification.readUTF();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        if (verify.equals("valid")) {
            try {
                width = verification.readUTF();
                height = verification.readUTF();
            } catch (Exception e) {
                e.printStackTrace();
            }
            CreateFrame abc = new CreateFrame(cSocket, width, height);
            dispose();
        } else {
            System.out.println("Please enter the valid password");
            JOptionPane.showMessageDialog(this, "Password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
}
