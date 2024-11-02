package org.example.MainContainer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SenderInterface extends JFrame {
    private JPanel mainPanel;
    private JTextField messageInput;
    private JButton sendBtn;
    private AgentSender agentSender;

    public SenderInterface(AgentSender agentSender) {
        this.agentSender = agentSender;

        // Initialize mainPanel (for testing if using IntelliJ GUI Designer)
        mainPanel = new JPanel();
        messageInput = new JTextField(20);
        sendBtn = new JButton("Send");
        mainPanel.add(new JLabel("Enter Message:"));
        mainPanel.add(messageInput);
        mainPanel.add(sendBtn);

        setContentPane(mainPanel);
        setTitle("Sender Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageInput.getText();
                if (!message.isEmpty()) {
                    agentSender.sendMessageToReceiver(message);
                    JOptionPane.showMessageDialog(SenderInterface.this, "Message sent: " + message);
                    messageInput.setText("");
                } else {
                    JOptionPane.showMessageDialog(SenderInterface.this, "Please enter a message.");
                }
            }
        });
    }
}
