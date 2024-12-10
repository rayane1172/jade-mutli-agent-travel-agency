package org.example.agents;

import jade.core.AID;
import jade.core.MainContainer;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import org.example.agents.Entity.VolRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class AgentCentral extends GuiAgent {

    private MainContainer gui;
    private int negotiationRounds = 3; // Maximum negotiation rounds
    private Map<String, NegotiationState> negotiations = new HashMap<>();

    @Override
    protected void setup() {
        System.out.println("Agent Central started: " + this.getAID().getName());
        gui = (MainContainer) getArguments()[0];
        gui.setCentralAgent(this);

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive();
                if (message != null) {
                    processResponse(message);
                }
            }
        });
    }

    private void processResponse(ACLMessage message) {
        String sender = message.getSender().getLocalName();
        String content = message.getContent();

        switch (message.getPerformative()) {
            case ACLMessage.PROPOSE:
                System.out.println("Received offer from " + sender + ": " + content);
                gui.airLineMessage(new GuiEvent(this, 1));
                handleProposal(sender, Double.parseDouble(content.split(":")[1].trim()));
                break;
            case ACLMessage.ACCEPT_PROPOSAL:
                System.out.println("Deal accepted by " + sender + ": " + content);
                gui.airLineMessage(new GuiEvent(this, 1, "Deal finalized with " + sender + "."));
                break;
            case ACLMessage.REFUSE:
                System.out.println("No matching flight from " + sender + ".");
                gui.airLineMessage(new GuiEvent(this, 1, "No flights found by " + sender + "."));
                break;
            default:
                System.out.println("Unknown message type received.");
        }
    }

    private void handleProposal(String sender, double proposedPrice) {
        NegotiationState state = negotiations.getOrDefault(sender, new NegotiationState());
        state.setProposedPrice(proposedPrice);

        if (state.getCounter() < negotiationRounds) {
            VolRequest volRequest = getVolRequestFromGuiEvent();
            double budgetMin = volRequest.getBudgetMin();
            double budgetMax = volRequest.getBudgetMax();

            if (proposedPrice <= budgetMax && proposedPrice >= budgetMin) {
                ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                accept.addReceiver(new AID(sender, AID.ISLOCALNAME));
                accept.setContent("Price accepted: " + proposedPrice);
                send(accept);

                double clientSatisfaction = calculateClientSatisfaction(proposedPrice, budgetMin, budgetMax);
                double airlineSatisfaction = calculateAirlineSatisfaction(proposedPrice, budgetMin, proposedPrice);
                gui.airLineMessage(new GuiEvent(this, 1, "Client satisfaction: " + clientSatisfaction + "%"));
                gui.airLineMessage(new GuiEvent(this, 1, "Airline satisfaction: " + airlineSatisfaction + "%"));
            } else {
                double counterOffer = Math.max(budgetMin, proposedPrice * 0.9);
                ACLMessage counter = new ACLMessage(ACLMessage.PROPOSE);
                counter.addReceiver(new AID(sender, AID.ISLOCALNAME));
                counter.setContent("Counter offer: " + counterOffer);
                send(counter);
                state.incrementCounter();
            }
        } else {
            System.out.println("Negotiation failed with " + sender);
            gui.airLineMessage(new GuiEvent(this, 1, "Negotiation failed with " + sender + "."));
        }
        negotiations.put(sender, state);
    }

    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
        if (guiEvent.getType() == 1) {
            VolRequest volRequest = (VolRequest) guiEvent.getParameter(0);
            negotiations.clear();
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            String jsonRequest = serializeVolRequest(volRequest);
            request.setContent(jsonRequest);
            String[] airlineAgents = {"AirFranceAgent", "AirAlgerieAgent", "EmiratesAgent", "QatarAirwaysAgent"};
            for (String agent : airlineAgents) {
                request.addReceiver(new AID(agent, AID.ISLOCALNAME));
                negotiations.put(agent, new NegotiationState(volRequest.getBudgetMin(), volRequest.getBudgetMax()));
            }
            send(request);
        }
    }

    private String serializeVolRequest(VolRequest volRequest) {
        Gson gson = new Gson();
        return gson.toJson(volRequest);
    }

    private double calculateClientSatisfaction(double finalPrice, double budgetMin, double budgetMax) {
        return (budgetMax - finalPrice) / (budgetMax - budgetMin) * 100;
    }

    private double calculateAirlineSatisfaction(double finalPrice, double minPrice, double initialPrice) {
        return (finalPrice - minPrice) / (initialPrice - minPrice) * 100;
    }

    private VolRequest getVolRequestFromGuiEvent() {
        return (VolRequest) gui.getLastVolRequest();
    }

    static class NegotiationState {
        private int counter = 0;
        private double clientBudgetMin;
        private double clientBudgetMax;
        private double proposedPrice;

        public NegotiationState() {}

        public NegotiationState(double clientBudgetMin, double clientBudgetMax) {
            this.clientBudgetMin = clientBudgetMin;
            this.clientBudgetMax = clientBudgetMax;
        }

        public int getCounter() {
            return counter;
        }

        public void incrementCounter() {
            counter++;
        }

        public double getClientBudgetMin() {
            return clientBudgetMin;
        }

        public double getClientBudgetMax() {
            return clientBudgetMax;
        }

        public double getProposedPrice() {
            return proposedPrice;
        }

        public void setProposedPrice(double proposedPrice) {
            this.proposedPrice = proposedPrice;
        }
    }
}
