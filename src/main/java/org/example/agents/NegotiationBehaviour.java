package org.example.agents;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import org.example.agents.Entity.Flight;

public class NegotiationBehaviour extends Behaviour {
    private final AID client; // Central agent
    private final Flight flight; // The flight being negotiated
    private int negotiationCounter = 0; // Number of negotiation rounds
    private boolean isNegotiationOver = false; // Tracks if negotiation is complete

    public NegotiationBehaviour(AID client, Flight flight) {
        this.client = client;
        this.flight = flight;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive(); // Receive a message
        if (message != null) {
            String content = message.getContent();

            switch (message.getPerformative()) {
                case ACLMessage.PROPOSE: // Client makes an offer
                    double clientOffer = Double.parseDouble(content.split(":")[1].trim());
                    if (clientOffer >= flight.getMinimumPrice()) {
                        // Accept the client's offer
                        ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        accept.addReceiver(client);
                        accept.setContent("Offer accepted: " + clientOffer);
                        myAgent.send(accept);
                        isNegotiationOver = true;
                    } else if (negotiationCounter < 3) {
                        // Make a counteroffer
                        double counterOffer = Math.max(flight.getMinimumPrice(), flight.getInitialPrice() * 0.9);
                        ACLMessage counter = new ACLMessage(ACLMessage.PROPOSE);
                        counter.addReceiver(client);
                        counter.setContent("Counter offer: " + counterOffer);
                        myAgent.send(counter);
                        negotiationCounter++;
                    } else {
                        // Reject the proposal after three rounds
                        ACLMessage reject = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                        reject.addReceiver(client);
                        reject.setContent("Negotiation failed.");
                        myAgent.send(reject);
                        isNegotiationOver = true;
                    }
                    break;

                default:
                    // Handle unexpected message types (optional)
                    break;
            }
        } else {
            block(); // Wait for incoming messages
        }
    }

    @Override
    public boolean done() {
        return isNegotiationOver;
    }
}
