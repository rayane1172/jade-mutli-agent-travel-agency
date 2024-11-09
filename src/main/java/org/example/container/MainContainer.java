package org.example.container;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainContainer extends Application {  // Ensure this extends Application

    public static void main(String[] args) {
        // Start JADE runtime in a separate thread to prevent blocking JavaFX
        new Thread(MainContainer::startJADE).start();

//        launch javafx interface
        launch(args);  // Required to start JavaFX
    }



    private static void startJADE() {
        try {
            Runtime runtime = Runtime.instance();
            // Create the Main Container with the Central Agent
            Properties mainProperties = new ExtendedProperties();
            mainProperties.setProperty(Profile.GUI, "true");
            Profile profileMain = new ProfileImpl(mainProperties);
            AgentContainer mainContainer = runtime.createMainContainer(profileMain);

            AgentController centralAgent = mainContainer
                    .createNewAgent("AgentCentral", "org.example.agents.AgentCentral", new Object[]{});
            centralAgent.start();

            // Create the Secondary Container for Airline Agents
            Profile profileSecondary = new ProfileImpl();
            profileSecondary.setParameter(Profile.CONTAINER_NAME, "SecondaryContainer");
            ContainerController secondaryContainer = runtime.createAgentContainer(profileSecondary);

            // Create and start Airline Agents in the secondary container
            AgentController airFranceAgent = secondaryContainer
                    .createNewAgent("AirFranceAgent", "org.example.agents.AirFranceAgent", new Object[]{});
            airFranceAgent.start();

            AgentController airAlgerieAgent = secondaryContainer
                    .createNewAgent("AirAlgerieAgent", "org.example.agents.AirAlgerieAgent", new Object[]{});
            airAlgerieAgent.start();

            AgentController qatarAirwaysAgent = secondaryContainer
                    .createNewAgent("QatarAirwaysAgent", "org.example.agents.QatarAirwaysAgent", new Object[]{});
            qatarAirwaysAgent.start();

            AgentController emiratesAgent = secondaryContainer
                    .createNewAgent("EmiratesAgent", "org.example.agents.EmiratesAgent", new Object[]{});
            emiratesAgent.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        // Correct override of start method from Application
        stage.setTitle("Interface Client");

        BorderPane borderPane = new BorderPane();
        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(15));

        Label labelFrom = new Label("De :");
        TextField textFieldFrom = new TextField();
        textFieldFrom.setPromptText("Alger");

        Label labelTo = new Label("A :");
        TextField textFieldTo = new TextField();
        textFieldTo.setPromptText("Paris");

        Label labelDeparture = new Label("Depart :");
        DatePicker datePickerDeparture = new DatePicker();
        datePickerDeparture.setPromptText("Date de départ");

        Label labelReturn = new Label("Retour :");
        DatePicker datePickerReturn = new DatePicker();
        datePickerReturn.setPromptText("Date de retour");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(labelFrom, 0, 0);
        gridPane.add(textFieldFrom, 1, 0);
        gridPane.add(labelTo, 0, 1);
        gridPane.add(textFieldTo, 1, 1);
        gridPane.add(labelDeparture, 0, 2);
        gridPane.add(datePickerDeparture, 1, 2);
        gridPane.add(labelReturn, 0, 3);
        gridPane.add(datePickerReturn, 1, 3);

        mainContainer.getChildren().add(gridPane);
        borderPane.setCenter(mainContainer);

        Scene scene = new Scene(borderPane, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}
