package org.example.agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;
import org.example.agents.Entity.Flight;
import org.example.agents.Entity.VolRequest;
import org.example.container.MainContainer;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class AgentCentral extends GuiAgent {

    private MainContainer gui;
    private int negotiationRounds = 0; // Nombre de tours de négociation
    private double clientBudget = 0; // Budget du client

    @Override
    protected void setup() {
        System.out.println("Agent Central started: " + this.getAID().getName());

        // Lier l'agent avec l'interface graphique
        gui = (MainContainer) getArguments()[0];
        gui.setCentralAgent(this);

        // Ajouter un comportement pour gérer les messages de négociation
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate template = MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                        MessageTemplate.or(
                                MessageTemplate.MatchPerformative(ACLMessage.REFUSE),
                                MessageTemplate.MatchPerformative(ACLMessage.INFORM)
                        )
                );

                ACLMessage message = receive(template);
                if (message != null) {
                    String senderName = message.getSender().getLocalName();

                    if (message.getPerformative() == ACLMessage.REFUSE) {
                        // Notification d'un refus
//                        System.out.println("Refusal received from " + senderName + ": " + message.getContent());

                        // Envoyer un événement à l'interface graphique
                        GuiEvent guiEvent = new GuiEvent(this, 1);
                        guiEvent.addParameter("Refusal from " + senderName + ": " + message.getContent());
                        gui.airLineMessage(guiEvent);

                    } else if (message.getPerformative() == ACLMessage.PROPOSE) {
                        double proposedPrice = Double.parseDouble(message.getContent());
//                        System.out.println("Negotiation started with " + senderName + ": Proposed price = " + proposedPrice);

                        // Notifier le début de la négociation à l'interface graphique
                        GuiEvent guiEvent = new GuiEvent(this, 1);
                        guiEvent.addParameter("Negotiation started with " + senderName + ": Proposed price = " + proposedPrice);
                        gui.airLineMessage(guiEvent);

                        // Gestion de la négociation
                        handleNegotiation(message, senderName, proposedPrice);
                    }else if (message.getPerformative() == ACLMessage.INFORM) {
                        handleNegotiation(message, senderName);
                    }
                } else {
                    block();
                }
            }
        });
    }


    private void handleNegotiation(ACLMessage message, String senderName) {
        if (message.getPerformative() == ACLMessage.INFORM) {
            String flightJsonReceived = message.getContent();
            Flight flight_received = deserializeFlightRequest(flightJsonReceived);

            // Notify the GUI to update the table
            GuiEvent guiEvent = new GuiEvent(this, 1);
            guiEvent.addParameter(flight_received);  // Add the flight object to the event
            gui.updateFlightTable(flight_received);  // Call method to update the GUI with the flight details
        }
    }

    private void handleNegotiation(ACLMessage message, String senderName, double proposedPrice) {
        if (proposedPrice <= clientBudget) {
            if (message.getPerformative() == ACLMessage.PROPOSE) {
                // Accepter l'offre
                ACLMessage acceptMessage = message.createReply();
                acceptMessage.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                acceptMessage.setContent("Offer accepted at price: " + proposedPrice);
                send(acceptMessage);

                // Notifier l'interface graphique
                GuiEvent guiEvent = new GuiEvent(this, 1);
                guiEvent.addParameter("Offer accepted at price: " + proposedPrice + " by " + senderName);
                gui.airLineMessage(guiEvent);
            }
        } else if (negotiationRounds < 3) {
            // Envoyer une contre-offre
            negotiationRounds++;
            double counterOffer = Math.min(clientBudget * 0.9, proposedPrice * 0.95);

            ACLMessage counterMessage = message.createReply();
            counterMessage.setPerformative(ACLMessage.PROPOSE);
            counterMessage.setContent(String.valueOf(counterOffer));
            send(counterMessage);

            // Notifier l'interface graphique
            GuiEvent guiEvent = new GuiEvent(this, 1);
            guiEvent.addParameter("Agent Central offre sent : " + counterOffer + " to " + senderName);
            gui.airLineMessage(guiEvent);
        }
        else {
            // Terminer la négociation
            ACLMessage refuseMessage = message.createReply();
            refuseMessage.setPerformative(ACLMessage.REJECT_PROPOSAL);
            refuseMessage.setContent("Negotiation failed after 3 rounds with " + senderName);
            send(refuseMessage);

            // Notifier l'interface graphique
            GuiEvent guiEvent = new GuiEvent(this, 1);
            guiEvent.addParameter("Negotiation failed after 3 rounds with " + senderName);
            gui.airLineMessage(guiEvent);
        }
    }

    @Override
    protected void beforeMove() {
        try {
            System.out.println("Before migration of agent " + this.getAID().getName());
            System.out.println("From " + this.getContainerController().getContainerName());
        } catch (ControllerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void afterMove() {
        try {
            System.out.println("After migration of agent " + this.getAID().getName());
            System.out.println("To " + getContainerController().getContainerName());
        } catch (ControllerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent " + this.getAID().getName() + " is terminating.");
    }

    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
        if (guiEvent.getType() == 1) {
            // Get the VolRequest from the GUI event
            VolRequest volRequest = (VolRequest) guiEvent.getParameter(0);

            // Set clientBudget from volRequest's minimum price
            clientBudget = volRequest.getMinimumPrice();
            // Notify the GUI
            GuiEvent notifyGuiEvent = new GuiEvent(this, 1);
            notifyGuiEvent.addParameter("Client budget set to: " + clientBudget);
            gui.airLineMessage(notifyGuiEvent);

            // Serialize the VolRequest to JSON
            String jsonVolRequest = serializeVolRequest(volRequest);

            // Send the request to the airline agents
            List<String> receiverNames = Arrays.asList("AirFranceAgent", "AirAlgerieAgent", "EmiratesAgent", "QatarAirwaysAgent");
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(jsonVolRequest);

            for (String name : receiverNames) {
                aclMessage.addReceiver(new AID(name, AID.ISLOCALNAME));
            }
            send(aclMessage);

            // Notify the GUI that the request has been sent
            notifyGuiEvent = new GuiEvent(this, 1);
            notifyGuiEvent.addParameter("Request sent to airline agents.");
            gui.airLineMessage(notifyGuiEvent);
        }
    }

    private String serializeVolRequest(VolRequest volRequest) {
        Gson gson = new Gson();
        return gson.toJson(volRequest);
    }

    public Flight deserializeFlightRequest(String flightJson) {
        Gson gson = new Gson();
        return gson.fromJson(flightJson, Flight.class);  // Convert JSON string back to Flight object
    }
}


