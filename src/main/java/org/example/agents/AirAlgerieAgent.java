package org.example.agents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.MessageTemplate;
import jade.tools.sniffer.Message;
import org.example.agents.Entity.VolRequest;

import java.text.SimpleDateFormat;

public class AirAlgerieAgent extends Agent {


    private VolRequest volRequestReceived;
    protected void setup() {
        System.out.println("Air algerie Agent start : " + this.getAID().getName());

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // todo -> filter the type of message received
//                MessageTemplate messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
//                        MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
                ACLMessage message = receive();
                if (message != null){
                    String jsonStringmsg = message.getContent();
//                    System.out.println(message.getContent());

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    gsonBuilder.setDateFormat(sdf.toPattern()); // Set the date format as dd/MM/yyyy
                    Gson gson = gsonBuilder.create();

                    VolRequest volRequestReceived = gson.fromJson(jsonStringmsg, VolRequest.class);
//                    System.out.println(volRequestReceived.toString());


                    ACLMessage respone = new ACLMessage(ACLMessage.AGREE);
                    respone.addReceiver(message.getSender());
                    respone.setContent("this flight from jijel is available.....");
                    send(respone);
                }

            }

//            @Override
//            public boolean done() {
//                if (volRequestReceived.getFrom().equals("jijel")){
//                    return true;
//                }else return false;
//            }
        });
    }

}
