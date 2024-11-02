package org.example.SecondContainer;

import javax.swing.*;
import java.awt.*;

public class ReceiverInterface extends JFrame {
    private JTextArea messageDisplayArea;
    private JPanel mainPanel;

    public ReceiverInterface() {
        setTitle("Receiver Agent Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        mainPanel = new JPanel(new BorderLayout());
        messageDisplayArea = new JTextArea(10, 30);
        messageDisplayArea.setEditable(false);

        mainPanel.add(new JScrollPane(messageDisplayArea), BorderLayout.CENTER);
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }

    public void displayMessage(String message) {
        messageDisplayArea.append("Received: " + message + "\n");
    }
}
