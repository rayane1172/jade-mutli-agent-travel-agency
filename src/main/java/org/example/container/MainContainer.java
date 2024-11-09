package org.example.container;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.agents.AgentCentral;
import org.example.agents.Entity.VolRequest;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainContainer extends Application {

//todo ->    relation with both of them
    private AgentCentral centralAgent;

    private TextArea messageArea;

    public static void main(String[] args) {
        // Start JADE runtime in a separate thread to prevent blocking JavaFX
//        new Thread(MainContainer::startJADE).start();
// todo ->       launch javafx interface then call the start jade function
        launch(args);  // Required to start JavaFX
    }



    private void startJADE() {
        try {
            Runtime runtime = Runtime.instance();
            //todo -> Create the Main Container with the Central Agent
            Properties mainProperties = new ExtendedProperties();
            mainProperties.setProperty(Profile.GUI, "true");
            Profile profileMain = new ProfileImpl(mainProperties);
            AgentContainer mainContainer = runtime.createMainContainer(profileMain);

            AgentController centralAgent = mainContainer
                    .createNewAgent("AgentCentral", "org.example.agents.AgentCentral", new Object[]{this});
            centralAgent.start();

            // todo -> Create the Secondary Container for Airline Agents
            Profile profileSecondary = new ProfileImpl();
            profileSecondary.setParameter(Profile.CONTAINER_NAME, "SecondaryContainer");
            ContainerController secondaryContainer = runtime.createAgentContainer(profileSecondary);

            //todo -> Create and start Airline Agents in the secondary container
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
        // todo ->  demarer le container
        startJADE();

        // Set up JavaFX stage
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

        // GridPane for form layout
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

        // Create "Send" button
        Button sendButton = new Button("Send");
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
//                Map<String, String> formData = new HashMap<>();
//                formData.put("From", textFieldFrom.getText());
//                formData.put("To", textFieldTo.getText());
//                formData.put("DepartureDate", datePickerDeparture.getValue() != null ? datePickerDeparture.getValue().toString() : "Not selected");
//                formData.put("ReturnDate", datePickerReturn.getValue() != null ? datePickerReturn.getValue().toString() : "Not selected");
//                System.out.println("Form data before sending: " + formData);

                if (datePickerDeparture.getValue() != null && datePickerReturn.getValue() != null) {
                    LocalDate departureLocalDate = datePickerDeparture.getValue();
                    LocalDate returnLocalDate = datePickerReturn.getValue();

                    // Convert LocalDate to Date and then format as "dd/MM/yyyy"
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDepartureDate = dateFormat.format(Date.from(departureLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    String formattedReturnDate = dateFormat.format(Date.from(returnLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                    // Pass formatted date strings to VolRequest
                    VolRequest volRequest = new VolRequest(
                            textFieldFrom.getText(),
                            textFieldTo.getText(),
                            formattedDepartureDate,
                            formattedReturnDate
                    );

                    System.out.println("Main container hahaha : " + volRequest.toString());

                    GuiEvent guiEvent = new GuiEvent(this, 1); // type is 1 for example
                    guiEvent.addParameter(volRequest);
                    centralAgent.onGuiEvent(guiEvent);
                } else {
                    System.out.println("Please enter all information.");
                }
            }
        });

        // Add the "Send" button to the main container
        mainContainer.getChildren().add(sendButton);

        // TextArea for displaying messages received from airline agents
//        TextArea messageArea = new TextArea();
        messageArea = new TextArea();
        messageArea.setEditable(false); // Make it read-only
        messageArea.setPromptText("Messages from airline agents will appear here...");

        // Add the TextArea to the main container below the form
        mainContainer.getChildren().add(messageArea);

        // Set up the layout
        borderPane.setCenter(mainContainer);

        Scene scene = new Scene(borderPane, 700, 700); // Adjust height to accommodate the TextArea
        stage.setScene(scene);
        stage.show();
    }

//todo -> method to show the message in interface
    public void airLineMessage(GuiEvent guiEvent){
        String message = guiEvent.getParameter(0).toString();
        messageArea.appendText(message);

    }

    public AgentCentral getCentralAgent() {
        return centralAgent;
    }

    public void setCentralAgent(AgentCentral centralAgent) {
        this.centralAgent = centralAgent;
    }



}
