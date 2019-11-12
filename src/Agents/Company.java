package Agents;

import AgentBehaviours.CompanyAirplane;
import AuxiliarClasses.AirplaneInfo;
import jade.core.Agent;
import jade.util.leap.ArrayList;

import java.util.HashSet;
import java.util.Set;

public class Company extends Agent {
    public Set<AirplaneInfo> getAirplanes() {
        return airplanes;
    }

    Set<AirplaneInfo> airplanes = new HashSet<>();

    public void setup(){
        addBehaviour(new CompanyAirplane(this, 5000));
    }

    public void addAirplane(Airplane a1) {
        AirplaneInfo airplane = new AirplaneInfo(a1.toString());
        airplanes.add(airplane);
    }


}
