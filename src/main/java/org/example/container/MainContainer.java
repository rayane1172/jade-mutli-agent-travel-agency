package org.example.container;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.util.ExtendedProperties;
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
import java.util.Properties;

public class MainContainer extends Application {

    private AgentCentral centralAgent;
    private TextArea messageArea;
    private VolRequest lastVolRequest;

    public static void main(String[] args) {
        launch(args);
    }

    private void startJADE() {
        try {
            Runtime runtime = Runtime.instance();
            Properties mainProperties = new ExtendedProperties();
            mainProperties.setProperty(Profile.GUI, "true");
            Profile profileMain = new ProfileImpl(jade.util.leap.Properties.toLeapProperties(mainProperties));
            AgentContainer mainContainer = runtime.createMainContainer(profileMain);

            AgentController centralAgent = mainContainer.createNewAgent("AgentCentral", "org.example.agents.AgentCentral", new Object[]{this});
            centralAgent.start();

            Profile profileSecondary = new ProfileImpl();
            profileSecondary.setParameter(Profile.CONTAINER_NAME, "SecondaryContainer");
            ContainerController secondaryContainer = runtime.createAgentContainer(profileSecondary);

            AgentController airFranceAgent = secondaryContainer.createNewAgent("AirFranceAgent", "org.example.agents.AirFranceAgent", new Object[]{});
            airFranceAgent.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        startJADE();

        stage.setTitle("Interface Client");

        BorderPane borderPane = new BorderPane();
        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(15));

        Label labelFrom = new Label("From:");
        TextField textFieldFrom = new TextField();
        textFieldFrom.setPromptText("Alger");

        Label labelTo = new Label("To:");
        TextField textFieldTo = new TextField();
        textFieldTo.setPromptText("Paris");

        Label labelDeparture = new Label("Departure:");
        DatePicker datePickerDeparture = new DatePicker();
        datePickerDeparture.setPromptText("Departure Date");

        Label labelReturn = new Label("Return:");
        DatePicker datePickerReturn = new DatePicker();
        datePickerReturn.setPromptText("Return Date");

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

        Button sendButton = new Button("Send");
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (datePickerDeparture.getValue() != null && datePickerReturn.getValue() != null) {
                    LocalDate departureLocalDate = datePickerDeparture.getValue();
                    LocalDate returnLocalDate = datePickerReturn.getValue();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDepartureDate = dateFormat.format(Date.from(departureLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    String formattedReturnDate = dateFormat.format(Date.from(returnLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                    VolRequest volRequest = new VolRequest(
                            textFieldFrom.getText(),
                            textFieldTo.getText(),
                            formattedDepartureDate,
                            formattedReturnDate,
                            500,
                            1000
                    );

                    lastVolRequest = volRequest;

                    GuiEvent guiEvent = new GuiEvent(this, 1);
                    guiEvent.addParameter(volRequest);
                    centralAgent.onGuiEvent(guiEvent);
                } else {
                    System.out.println("Please enter all information.");
                }
            }
        });

        mainContainer.getChildren().add(sendButton);

        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setPromptText("Messages from airline agents will appear here...");

        mainContainer.getChildren().add(messageArea);

        borderPane.setCenter(mainContainer);

        Scene scene = new Scene(borderPane, 700, 700);
        stage.setScene(scene);
        stage.show();
    }

    public void airLineMessage(GuiEvent guiEvent) {
        String message = (String) guiEvent.getParameter(0);
        messageArea.appendText(message + "\n");
    }

    // Add the missing setCentralAgent method to properly link the MainContainer to AgentCentral
    public void setCentralAgent(AgentCentral centralAgent) {
        this.centralAgent = centralAgent;
    }

    public AgentCentral getCentralAgent() {
        return centralAgent;
    }

    public VolRequest getLastVolRequest() {
        return lastVolRequest;
    }
}
