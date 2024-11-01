package org.example.MainContainer;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;

public class AgentSender extends Agent {


//execute apres just instancier l'agent
    protected void setup(){
        System.out.println("Agent Sender Start "+this.getAID().getName());
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("AgentReceiver",AID.ISLOCALNAME));
        message.setContent("bonjour depuis Agent Sender");
        send(message);


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
        System.out.println("l'agent "+this.getAID().getName()+"va mourir");
    }
}
