package Agents;

import AgentBehaviours.ListeningTowerBehaviour;
import AuxiliarClasses.AirplaneInfo;
import jade.core.*;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;

import java.util.*;

public class ControlTower extends Agent{

    Comparator<AirplaneInfo> airplaneComparator = new Comparator<AirplaneInfo>() {
        @Override public int compare(AirplaneInfo a1, AirplaneInfo a2) {
            return Float.compare(a1.getFuel(),a2.getFuel());
        }
    };

    private TreeSet<AirplaneInfo> airplanes = new TreeSet<>(airplaneComparator);
    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

    public void setup() {
        System.out.println("New control tower");
        addBehaviour(new ListeningTowerBehaviour(this));
    }
    public void pushAirplane(AirplaneInfo airplane) {
        airplanes.remove(airplane);
        airplanes.add(airplane);
        System.out.println(airplanes);
    }
}


