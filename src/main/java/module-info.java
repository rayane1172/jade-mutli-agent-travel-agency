module multi.agent.travel.agency {
    requires transitive jade;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;


    opens org.example.container;
    opens org.example.agents;
}