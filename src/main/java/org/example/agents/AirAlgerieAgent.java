package org.example.agents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.example.agents.Entity.Flight;
import org.example.agents.Entity.VolRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AirAlgerieAgent extends Agent {

    private double initialPrice = 600.0; // Default initial price for the flight
    private double minimumPrice = 400.0; // Minimum price the airline can offer
    private int negotiationRounds = 0; // Track the number of negotiation rounds
    private List<Flight> availableFlights = new ArrayList<>();
    private ACLMessage flightReceived; // Stocke le premier message REQUEST
    private Flight flight_found;
    VolRequest volRequestReceived;

    @Override
    protected void setup() {
        System.out.println("Air Algerie Agent started: " + this.getAID().getName());

        // Add some sample flights to the availableFlights list
        initializeAvailableFlights();

        // Add behavior to handle incoming messages
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Filtrer les messages pour REQUEST, CFP (Call for Proposal), PROPOSE, et ACCEPT_PROPOSAL
                MessageTemplate template = MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.or(
                                MessageTemplate.MatchPerformative(ACLMessage.CFP),
                                MessageTemplate.or(
                                        MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                                        MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL)
                                )
                        )
                );

                ACLMessage message = receive(template);
                if (message != null) {
                    // Utiliser un switch sur la performative pour traiter différents types de messages
                    switch (message.getPerformative()) {
                        case ACLMessage.REQUEST:
                            // Gérer la demande initiale de vol
                            flightReceived = message;
                            handleFlightRequest(message);
                            break;

                        case ACLMessage.CFP:
                            // Gérer la contre-offre de AgentCentral
                            handleOffer(message);
                            break;

                        case ACLMessage.PROPOSE:
                            // Gérer la proposition (contre-offre de AgentCentral)
                            handleOffer(message);
                            break;

                        case ACLMessage.ACCEPT_PROPOSAL:
                            // Gérer l'acceptation de la proposition
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.INFORM);
//                            System.out.println("vol request recevied si => "+volRequestReceived);
                            flight_found.decreaseSeats(volRequestReceived.getNumTickets());
                            System.out.println(".....send flight_found to central agent --> "+flight_found);
                            String flightJson = serializeVolRequest(flight_found);
                            System.out.println("-----> "+flightJson);
                            reply.setContent(flightJson);
                            send(reply);
                            break;

                        default:
//                            System.out.println("Performative inconnue : " + message.getPerformative());
                            break;
                    }
                } else {
                    block();  // Si aucun message n'est reçu, bloquer l'agent
                }
            }
        });
    }

    private void initializeAvailableFlights() {
        availableFlights.add(new Flight("AH101", "Algiers", "Paris", "15/01/2025",4,"AirAlgerie",0));
        availableFlights.add(new Flight("AH102", "Algiers", "London", "16/01/2025",50,"AirAlgerie",0));
        availableFlights.add(new Flight("AH103", "Oran", "Madrid", "17/01/2025",10,"AirAlgerie",0));
        availableFlights.add(new Flight("AH104", "Algiers", "New York", "20/01/2025",30,"AirAlgerie",0));
    }

    private void handleFlightRequest(ACLMessage message) {
        // Deserialize the flight request from JSON
        String jsonStringMsg = message.getContent();
        GsonBuilder gsonBuilder = new GsonBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        gsonBuilder.setDateFormat(sdf.toPattern()); // Set the date format as dd/MM/yyyy
        Gson gson = gsonBuilder.create();

        volRequestReceived = gson.fromJson(jsonStringMsg, VolRequest.class);
        // Check if the requested flight is available
        flight_found = checkFlightAvailability(volRequestReceived);
        ACLMessage reply = message.createReply();
        if (flight_found != null) {
            // Respond with the initial price proposal if the flight is available
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContent(String.valueOf(initialPrice)); // Send the initial price
            send(reply);
            System.out.println("Air Algerie proposed initial price: " + initialPrice);
            flight_found.setPrice(initialPrice); //todo
        } else {
            // Respond with a REFUSE message if the flight is not available
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent("Requested flight not available or no seats left !!");
            send(reply);
        }
    }

    private Flight checkFlightAvailability(VolRequest volRequest) {
        for (Flight flight : availableFlights) {
            if (flight.matches(volRequest.getFrom(), volRequest.getTo(), volRequest.getDepartureDate())) {
                if (flight.hasAvailableSeats()) {
                    return flight; // Flight is available and has seats
                }
            }
        }
        return null;
    }

    private void handleOffer(ACLMessage message) {

        //todo ->  Parse the counteroffer price from the message
        double offre = Double.parseDouble(message.getContent());

        System.out.println("Air Algerie received offer: " + offre);

        if (offre >= minimumPrice) {
            // Accept the counteroffer if it's above or equal to the minimum price
            ACLMessage reply = message.createReply();
            volRequestReceived = new Gson().fromJson(flightReceived.getContent(), VolRequest.class);
            // decrease the number of available seats for the flight
            boolean isSuccess = flight_found.decreaseSeats(volRequestReceived.getNumTickets());
            if (isSuccess) {
                // Serialize the flight to JSON
                flight_found.setPrice(offre); //todo

                String flightJson = serializeVolRequest(flight_found);
                reply.setPerformative(ACLMessage.INFORM);
//                System.out.println("flight_choosed Json sended with accept propsal");
                reply.setContent(flightJson);
            } else {
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("Flight unavailable: Not enough seats or flight not found.");
            }

            send(reply);
        } else if (negotiationRounds < 3) {
            negotiationRounds++;
            double newPrice = Math.max(minimumPrice, offre * 0.9); //todo ->  Reduce by 10%

            ACLMessage reply = message.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContent(String.valueOf(newPrice)); // Send the new price
            send(reply);
            System.out.println("Air Algerie proposed new price: " + newPrice);
            flight_found.setPrice(newPrice); //todo
        } else {
            // Terminate negotiation if maximum rounds are reached
            ACLMessage reply = message.createReply();
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent("Negotiation failed: Maximum rounds reached.");
            send(reply);
//            takeDown();
        }
    }
    private String serializeVolRequest(Flight flightReceived) {
        Gson gson = new Gson();
        return gson.toJson(flightReceived);
    }

    @Override
    protected void takeDown() {
        System.out.println("Air Algerie Agent shutting down.");
    }

}
