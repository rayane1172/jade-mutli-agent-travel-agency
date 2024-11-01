package org.example.SecondContainer;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;

public class SecondContainer {

    public static void main(String[] args){

        try {
            Runtime runtime = Runtime.instance();
            Profile profile = new ProfileImpl(false);
            profile.setParameter(Profile.MAIN_HOST,"localhost");
            AgentContainer agentContainer = runtime.createAgentContainer(profile);
            AgentController agentController = agentContainer.createNewAgent("AgentReceiver","org.example.SecondContainer.AgentReceiver",null);
            agentController.start();
        } catch (ControllerException e) {
            throw new RuntimeException(e);
        }

    }
}
