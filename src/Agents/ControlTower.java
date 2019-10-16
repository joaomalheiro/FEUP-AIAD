package Agents;

import AgentBehaviours.ListeningTowerBehaviour;
import jade.core.*;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;

import java.util.HashSet;
import java.util.Set;

public class ControlTower extends Agent{
    private Set<Airplane> airplanes = new HashSet<>();

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

    public void setup() {
        System.out.println("New control tower");
        addBehaviour(new ListeningTowerBehaviour(this));
    }
    public void pushAirplane(Airplane airplane) {
        airplanes.add(airplane);
        System.out.println(airplanes);
    }
}
