package org.example.agents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.agents.Entity.Flight;
import org.example.agents.Entity.VolRequest;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AirFranceAgent extends Agent {

    private List<Flight> availableFlights = Arrays.asList(
            new Flight("3", "Jijel", "Paris", "18/12/2024", 450.0, 250.0, "Air France"),
            new Flight("4", "Algiers", "London", "22/12/2024", 550.0, 300.0, "Air France")
    );

    protected void setup() {
        System.out.println("Air France Agent started: " + this.getAID().getName());

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive();
                if (message != null) {
                    String jsonStringMsg = message.getContent();
                    Gson gson = new GsonBuilder()
                            .setDateFormat(new SimpleDateFormat("dd/MM/yyyy").toPattern())
                            .create();

                    VolRequest volRequest = gson.fromJson(jsonStringMsg, VolRequest.class);

                    Optional<Flight> matchingFlight = availableFlights.stream()
                            .filter(f -> f.getFrom().equals(volRequest.getFrom())
                                    && f.getTo().equals(volRequest.getTo())
                                    && f.getDepartureDate().equals(volRequest.getDepartureDate()))
                            .findFirst();

                    if (matchingFlight.isPresent()) {
                        Flight flight = matchingFlight.get();
                        ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
                        response.addReceiver(message.getSender());
                        response.setContent("Proposed price: " + flight.getInitialPrice());
                        send(response);

                        addBehaviour(new NegotiationBehaviour(message.getSender(), flight));
                    } else {
                        ACLMessage failure = new ACLMessage(ACLMessage.REFUSE);
                        failure.addReceiver(message.getSender());
                        failure.setContent("No matching flight found.");
                        send(failure);
                    }
                } else {
                    block();
                }
            }
        });
    }
}
