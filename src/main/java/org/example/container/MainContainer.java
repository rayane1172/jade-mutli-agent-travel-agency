package org.example.container;

// Keep all original imports
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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.agents.AgentCentral;
import org.example.agents.Entity.Flight;
import org.example.agents.Entity.VolRequest;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class MainContainer extends Application {

    // Keep all original field declarations
    private AgentCentral centralAgent;
    private TextArea messageArea;
    private TableView<Flight> flightTable;

    public static void main(String[] args) {
        launch(args);
    }

    // Keep original JADE startup method unchanged
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
        startJADE();

        stage.setTitle("FIND YOUR FLIGHT");

        BorderPane borderPane = new BorderPane();
        VBox mainContainer = new VBox(20); // Increased spacing
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #f5f5f5;"); // Light gray background

        // Create styled form labels and fields (keeping original variables)
        Label labelFrom = createStyledLabel("De :");
        TextField textFieldFrom = createStyledTextField("Alger");

        Label labelTo = createStyledLabel("A :");
        TextField textFieldTo = createStyledTextField("Paris");

        Label labelDeparture = createStyledLabel("Depart :");
        DatePicker datePickerDeparture = new DatePicker();
        styleNode(datePickerDeparture);

        Label labelNumTickets = createStyledLabel("Nombre de billets :");
        Spinner<Integer> spinnerTickets = new Spinner<>(1, 10, 1);
        styleNode(spinnerTickets);

        Label labelPassengerAges = createStyledLabel("Âges des passagers :");
        TextField textFieldAges = createStyledTextField("Ex: 25, 30, 15");

        Label labelMinPrice = createStyledLabel("Prix minimum :");
        TextField textFieldMinPrice = createStyledTextField("Ex: 200.0");

        // Style the grid
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        gridPane.setStyle("-fx-background-color: white; -fx-background-radius: 5px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Add components to grid (keeping original layout)
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

        // Style the send button
        Button sendButton = new Button("Envoyer");
        sendButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 5px;");
        sendButton.setOnMouseEntered(e -> sendButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 5px;"));
        sendButton.setOnMouseExited(e -> sendButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 5px;"));

        // Keep original button logic
        sendButton.setOnAction(actionEvent -> {
            try {
                if (datePickerDeparture.getValue() == null || textFieldFrom.getText().isEmpty() || textFieldTo.getText().isEmpty() || textFieldAges.getText().isEmpty() || textFieldMinPrice.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs requis !");
                    return;
                }
                flightTable.getItems().clear();
                LocalDate departureLocalDate = datePickerDeparture.getValue();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDepartureDate = dateFormat.format(Date.from(departureLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                int[] passengerAges = Arrays.stream(textFieldAges.getText().split(","))
                        .map(String::trim)
                        .mapToInt(Integer::parseInt)
                        .toArray();

                double minimumPrice = Double.parseDouble(textFieldMinPrice.getText());

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

        // Initialize and style the flight table (keeping original columns)
        flightTable = new TableView<>();
        flightTable.setStyle("-fx-font-size: 14px;");

        TableColumn<Flight, String> flightNumberColumn = new TableColumn<>("Numéro de vol");
        flightNumberColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFlightNumber()));

        TableColumn<Flight, String> fromColumn = new TableColumn<>("De");
        fromColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFrom()));

        TableColumn<Flight, String> toColumn = new TableColumn<>("A");
        toColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTo()));

        TableColumn<Flight, String> departureColumn = new TableColumn<>("Départ");
        departureColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDate()));

        TableColumn<Flight, String> TotalSeats = new TableColumn<>("Total Seats");
        TotalSeats.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%d", cellData.getValue().getTotalSeats())));

        TableColumn<Flight, String> AirLine = new TableColumn<>("AirLine");
        AirLine.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAirlineName()));

        TableColumn<Flight, String> priceColumn = new TableColumn<>("Prix");
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f", cellData.getValue().getPrice())));

        flightTable.getColumns().addAll(flightNumberColumn, fromColumn, toColumn, departureColumn,
                TotalSeats, AirLine, priceColumn);

        mainContainer.getChildren().add(flightTable);

        // Style message area
        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setPromptText("Les messages des agents aériens apparaîtront ici...");
        messageArea.setWrapText(true);
        messageArea.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
        messageArea.setPrefWidth(400);
        messageArea.setPrefHeight(450);
        mainContainer.getChildren().add(messageArea);

        borderPane.setCenter(mainContainer);

        // Create scene with styles
        Scene scene = new Scene(borderPane, 800, 700);
        try {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load CSS file: " + e.getMessage());
        }
        stage.setScene(scene);
        stage.show();
    }

    // Helper methods for styling (new)
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        return label;
    }

    private TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        styleNode(textField);
        return textField;
    }

    private void styleNode(Control node) {
        node.setStyle("-fx-font-size: 14px; -fx-background-radius: 5px;");
    }

    // Keep all original methods unchanged
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void updateFlightTable(Flight flight) {
        Platform.runLater(() -> {
            flightTable.getItems().add(flight);
        });
    }

    public void airLineMessage(GuiEvent guiEvent) {
        String message = guiEvent.getParameter(0).toString();
        messageArea.appendText(message + "\n----------------------------\n");
    }

    public void setCentralAgent(AgentCentral agent) {
        this.centralAgent = agent;
    }

    public Flight deserializeFlightRequest(String flightJson) {
        Gson gson = new Gson();
        return gson.fromJson(flightJson, Flight.class);
    }

    private void handleNegotiation(ACLMessage message, String senderName) {
        if (message != null) {
            if (message.getPerformative() == ACLMessage.PROPOSE) {
                Flight flight = deserializeFlightRequest(message.getContent());
                updateFlightTable(flight);
            }
        }
    }
}