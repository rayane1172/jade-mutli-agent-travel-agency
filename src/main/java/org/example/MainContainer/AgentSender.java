package org.example.MainContainer;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;

public class AgentSender extends Agent {
    private SenderInterface senderInterface;

//execute apres just instancier l'agent
protected void setup() {
    System.out.println("Agent Sender started: " + this.getAID().getName());

    // Initialize the interface and set it visible
    senderInterface = new SenderInterface(this);
    senderInterface.setVisible(true);

    // Add a cyclic behaviour to listen for any replies or other messages
    addBehaviour(new CyclicBehaviour() {
        @Override
        public void action() {
            ACLMessage receivedMessage = receive();
            if (receivedMessage != null) {
                System.out.println("Sender received: " + receivedMessage.getContent());
            } else {
                block();
            }
        }
    });
}

    // Method to send a message to AgentReceiver
    public void sendMessageToReceiver(String messageContent) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("AgentReceiver", AID.ISLOCALNAME));
        message.setContent(messageContent);
        send(message);
        System.out.println("Message sent to AgentReceiver: " + messageContent);
    }


//    avant la migration vers d'autre machine
    @Override
    protected void beforeMove() {
        try {
            System.out.println("Avant migration de l'agent "+this.getAID().getName());
            System.out.println("de "+this.getContainerController().getContainerName());

        } catch (ControllerException e) {
            throw new RuntimeException(e);
        }
    }

//    apres migration (deplacer pour faire un traitement)
    @Override
    protected void afterMove() {
        try {
            System.out.println("Apres migration du l'agent "+this.getAID().getName());
            System.out.println("vers "+getContainerController().getContainerName());

        } catch (ControllerException e) {
            throw new RuntimeException(e);
        }
    }


//    avant l'agent arreter
    @Override
    protected void takeDown() {
        System.out.println("Agent " + this.getAID().getName() + " is terminating.");
        if (senderInterface != null) {
            senderInterface.dispose();
        }
    }
}
