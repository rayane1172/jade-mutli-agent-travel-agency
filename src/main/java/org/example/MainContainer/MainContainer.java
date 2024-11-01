package org.example.MainContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class MainContainer {
    public static void main(String[] args){
        try {
            // Initialize the JADE runtime
            Runtime runtime = Runtime.instance();

            // Create the main container with GUI

            Properties properties = new ExtendedProperties();
            properties.setProperty(Profile.GUI,"true");
            Profile profileMain = new ProfileImpl(properties);
            AgentContainer mainContainer = runtime.createMainContainer(profileMain);
            AgentController agentController = mainContainer
                    .createNewAgent("etudiant","org.example.MainContainer.AgentSender",new Object[]{});
            //            l'agent demarer -> container demarer automatique
            agentController.start();



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}



