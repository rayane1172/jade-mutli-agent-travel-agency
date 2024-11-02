package org.example.SecondContainer;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;

public class AgentReceiver extends Agent {
    private ReceiverInterface receiverInterface;


    protected void setup() {
        System.out.println("Agent Receiver started: " + this.getAID().getName());

        // Initialize and display the receiver interface
        receiverInterface = new ReceiverInterface();
        receiverInterface.setVisible(true);

        // Add behaviour to handle received messages
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive();
                if (message != null) {
                    String receivedContent = message.getContent();
                    receiverInterface.displayMessage(receivedContent);
                    System.out.println("Receiver received: " + receivedContent);
                } else {
                    block();
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent "+ this.getAID().getName() +" is terminating..");
        if (receiverInterface != null) {
            receiverInterface.dispose();
        }
    }
}
