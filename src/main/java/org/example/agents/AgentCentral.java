package org.example.agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.tools.sniffer.Message;
import jade.wrapper.ControllerException;
import org.example.agents.Entity.VolRequest;
import org.example.container.MainContainer;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class AgentCentral extends GuiAgent {

//    todo-> relation of agent with interface
    private MainContainer gui;



    protected void setup() {
        System.out.println("Agent Central started: " + this.getAID().getName());

//todo -> connect the agent with the interface
        gui = (MainContainer) getArguments()[0]; //todo-> to get the interface as object parameter
        gui.setCentralAgent(this);


        // todo -> add a behavior
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM));

                ACLMessage message = receive(messageTemplate);
                if (message != null) {
                    System.out.println("Message received from Air algerie : " + message.getContent());
                    GuiEvent guiEvent = new GuiEvent(this, 1);
                    guiEvent.addParameter(message.getContent());
                    gui.airLineMessage(guiEvent); //todo -> send to gui interface in mainContainer

                }
            }
        });
    }


    //    avant la migration vers d'autre machine
    @Override
    protected void beforeMove() {
        try {
            System.out.println("Avant migration de l'agent " + this.getAID().getName());
            System.out.println("de " + this.getContainerController().getContainerName());

        } catch (ControllerException e) {
            throw new RuntimeException(e);
        }
    }

    //    apres migration (deplacer pour faire un traitement)
    @Override
    protected void afterMove() {
        try {
            System.out.println("Apres migration du l'agent " + this.getAID().getName());
            System.out.println("vers " + getContainerController().getContainerName());

        } catch (ControllerException e) {
            throw new RuntimeException(e);
        }
    }


    //    avant l'agent arreter
    @Override
    protected void takeDown() {
        System.out.println("Agent " + this.getAID().getName() + " is terminating.");

    }

    /**
     * @param guiEvent
     */
// todo ->    bcz agent has an interface
    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
        //todo-> type 1 is for "send volRequest"
        if (guiEvent.getType() == 1) {
            List<String> receiverNames = Arrays.asList("AirFranceAgent", "AirAlgerieAgent", "EmiratesAgent", "QatarAirwaysAgent");

            //todo-> get the vol request from gui interface
            VolRequest volRequest = (VolRequest) guiEvent.getParameter(0);
//            System.out.println(volRequest);
            String jsonVolRequest = serializeVolRequest(volRequest);

            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(jsonVolRequest);

            for (String name : receiverNames) {
                aclMessage.addReceiver(new AID(name, AID.ISLOCALNAME));
            }
            send(aclMessage);
        }
    }


    private String serializeVolRequest(VolRequest volRequest) {
        Gson gson = new Gson(); // Create a new Gson object
        return gson.toJson(volRequest); // Convert the VolRequest object to a JSON string
    }












}