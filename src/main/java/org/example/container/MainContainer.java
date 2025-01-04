package org.example.container;

import com.google.gson.Gson;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.agents.AgentCentral;
import org.example.agents.Entity.Flight;
import org.example.agents.Entity.VolRequest;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

public class MainContainer extends Application {

    private AgentCentral centralAgent;
    private TextArea messageArea;

    // TableView for flight information
    private TableView<Flight> flightTable;

    public static void main(String[] args) {
        launch(args); // Start JavaFX
    }

    private void startJADE() {
        try {
            Runtime runtime = Runtime.instance();
            Properties mainProperties = new ExtendedProperties();
            mainProperties.setProperty(Profile.GUI, "true");
            Profile profileMain = new ProfileImpl(mainProperties);
            AgentContainer mainContainer = runtime.createMainContainer(profileMain);

            AgentController centralAgentController = mainContainer
                    .createNewAgent("AgentCentral", "org.example.agents.AgentCentral", new Object[]{this});
            centralAgentController.start();

            Profile profileSecondary = new ProfileImpl();
            profileSecondary.setParameter(Profile.CONTAINER_NAME, "SecondaryContainer");
            ContainerController secondaryContainer = runtime.createAgentContainer(profileSecondary);

            String[] agentNames = {"AirFranceAgent", "AirAlgerieAgent", "QatarAirwaysAgent", "EmiratesAgent"};
            for (String agentName : agentNames) {
                AgentController agent = secondaryContainer.createNewAgent(agentName, "org.example.agents." + agentName, new Object[]{});
                agent.start();
            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        startJADE(); // Start JADE runtime

        stage.setTitle("Interface Client");

        BorderPane borderPane = new BorderPane();
        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(15));

        // Input form for user to make requests
        Label labelFrom = new Label("De :");
        TextField textFieldFrom = new TextField();
        textFieldFrom.setPromptText("Alger");

        Label labelTo = new Label("A :");
        TextField textFieldTo = new TextField();
        textFieldTo.setPromptText("Paris");

        Label labelDeparture = new Label("Depart :");
        DatePicker datePickerDeparture = new DatePicker();
        datePickerDeparture.setPromptText("Date de départ");

        Label labelNumTickets = new Label("Nombre de billets :");
        Spinner<Integer> spinnerTickets = new Spinner<>(1, 10, 1);

        Label labelPassengerAges = new Label("Âges des passagers :");
        TextField textFieldAges = new TextField();
        textFieldAges.setPromptText("Ex: 25, 30, 15");

        Label labelMinPrice = new Label("Prix minimum :");
        TextField textFieldMinPrice = new TextField();
        textFieldMinPrice.setPromptText("Ex: 200.0");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(labelFrom, 0, 0);
        gridPane.add(textFieldFrom, 1, 0);
        gridPane.add(labelTo, 0, 1);
        gridPane.add(textFieldTo, 1, 1);
        gridPane.add(labelDeparture, 0, 2);
        gridPane.add(datePickerDeparture, 1, 2);
        gridPane.add(labelNumTickets, 0, 3);
        gridPane.add(spinnerTickets, 1, 3);
        gridPane.add(labelPassengerAges, 0, 4);
        gridPane.add(textFieldAges, 1, 4);
        gridPane.add(labelMinPrice, 0, 6);
        gridPane.add(textFieldMinPrice, 1, 6);

        mainContainer.getChildren().add(gridPane);

        Button sendButton = new Button("Envoyer");
        sendButton.setOnAction(actionEvent -> {
            try {
                if (datePickerDeparture.getValue() == null || textFieldFrom.getText().isEmpty() || textFieldTo.getText().isEmpty() || textFieldAges.getText().isEmpty() || textFieldMinPrice.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs requis !");
                    return;
                }

                LocalDate departureLocalDate = datePickerDeparture.getValue();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDepartureDate = dateFormat.format(Date.from(departureLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                int[] passengerAges = Arrays.stream(textFieldAges.getText().split(","))
                        .map(String::trim)
                        .mapToInt(Integer::parseInt)
                        .toArray();

                double minimumPrice = Double.parseDouble(textFieldMinPrice.getText());

                // Send request
                VolRequest volRequest = new VolRequest(
                        "FL" + System.currentTimeMillis(),
                        textFieldFrom.getText(),
                        textFieldTo.getText(),
                        minimumPrice,
                        formattedDepartureDate,
                        spinnerTickets.getValue(),
                        passengerAges
                );

                GuiEvent guiEvent = new GuiEvent(this, 1);
                guiEvent.addParameter(volRequest);
                centralAgent.onGuiEvent(guiEvent);

                showAlert(Alert.AlertType.INFORMATION, "Demande envoyée avec succès !");
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Veuillez entrer des données valides !");
            }
        });

        mainContainer.getChildren().add(sendButton);

        // Create the TableView to display flight information
        flightTable = new TableView<>();

        // Create columns for flight attributes
        TableColumn<Flight, String> flightNumberColumn = new TableColumn<>("Numéro de vol");
        flightNumberColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFlightNumber())); // Wrap the String in SimpleStringProperty

        TableColumn<Flight, String> fromColumn = new TableColumn<>("De");
        fromColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFrom())); // Wrap the String in SimpleStringProperty

        TableColumn<Flight, String> toColumn = new TableColumn<>("A");
        toColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTo())); // Wrap the String in SimpleStringProperty

        TableColumn<Flight, String> departureColumn = new TableColumn<>("Départ");
        departureColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDate())); // Wrap the String in SimpleStringProperty

        TableColumn<Flight, String> priceColumn = new TableColumn<>("Prix");
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f", cellData.getValue().getPrice()))); // Format to 2 decimal places

        flightTable.setPrefWidth(120);
        flightTable.setPrefHeight(100);

        // Add columns to TableView
        flightTable.getColumns().add(flightNumberColumn);
        flightTable.getColumns().add(fromColumn);
        flightTable.getColumns().add(toColumn);
        flightTable.getColumns().add(departureColumn);
        flightTable.getColumns().add(priceColumn);

        // Add TableView to layout
        mainContainer.getChildren().add(flightTable);

        // Add message area for agent responses
        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setPromptText("Les messages des agents aériens apparaîtront ici...");
        messageArea.setWrapText(true); // Wraps text to improve readability
        messageArea.setPrefWidth(300);
        messageArea.setPrefHeight(250);
        mainContainer.getChildren().add(messageArea);

        borderPane.setCenter(mainContainer);

        Scene scene = new Scene(borderPane, 800, 650);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to update the flight table
    public void updateFlightTable(Flight flight) {
        // Add the flight to the table or update it
        Platform.runLater(() -> {
            flightTable.getItems().add(flight); // Example of adding a new flight
        });
    }

    public void airLineMessage(GuiEvent guiEvent) {
        String message = guiEvent.getParameter(0).toString();
        messageArea.appendText(message + "\n------\n");
    }

//    // Update the TableView with the flight data received
//    public void updateFlightTable(Flight flight) {
//        flightTable.getItems().add(flight);
//    }

    public void setCentralAgent(AgentCentral agent) {
        this.centralAgent = agent;
    }

    public Flight deserializeFlightRequest(String flightJson) {
        Gson gson = new Gson();
        return gson.fromJson(flightJson, Flight.class);  // Convert JSON string back to Flight object
    }
    // Modify handleNegotiation to call updateFlightTable
    private void handleNegotiation(ACLMessage message, String senderName) {
        if (message != null) {
            if (message.getPerformative() == ACLMessage.PROPOSE) {
                // Use your deserializeFlightRequest method to convert JSON string to Flight object
                Flight flight = deserializeFlightRequest(message.getContent());
                updateFlightTable(flight); // Update the TableView with the new flight information
            }
        }
    }
}
