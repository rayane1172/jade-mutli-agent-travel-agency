package org.example.agents;

import jade.core.Agent;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

public class AirLineAgent extends GuiAgent {
    private String airLineName;

    public AirLineAgent(String airLineName){
        this.airLineName = airLineName;
    }
    public AirLineAgent(){}

    /**
     * @param guiEvent
     */
    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }


    public String getAirLineName() {
        return airLineName;
    }

    public void setAirLineName(String airLineName) {
        this.airLineName = airLineName;
    }

    @Override
    protected void setup() {
        System.out.println("Agent : "+this.getAirLineName()+" just Start now .... ");
    }
}
