module multi.agent.travel.agency {
    requires transitive jade;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires com.google.gson;


    opens org.example.container;
    opens org.example.agents;
    opens org.example.agents.Entity;
}