package org.example.SecondContainer;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;

public class AgentReceiver extends Agent {



    protected void setup(){
        System.out.println("Agent Receiver started.");
        // Add a cyclic behavior to continuously check for incoming messages
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String contenu = msg.getContent();
                    AID adressEmeteur = msg.getSender();
                    String nomEmetteur = adressEmeteur.getLocalName();
                    System.out.println("Agent 1 message -> " + contenu + ", from " + nomEmetteur);
                } else {
                    block();
                }
            }
        });

    }




}
