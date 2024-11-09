package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;

public class AgentCentral extends Agent {

//execute apres just instancier l'agent
protected void setup() {
    System.out.println("Agent Sender started: " + this.getAID().getName());

}

    // Method to send a message to AirAlgerieAgent
    public void sendMessageToReceiver(String messageContent) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("AirAlgerieAgent", AID.ISLOCALNAME));
        message.setContent(messageContent);
        send(message);
        System.out.println("Message sent to AirAlgerieAgent: " + messageContent);
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

    }
}
