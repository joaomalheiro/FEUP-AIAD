package Agents;

import AgentBehaviours.AirplaneToControlTower;
import jade.core.*;



public class Airplane extends Agent {
    private int id;
    private float fuel;
    private int capacity;
    private int passengers;

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

    public void setup() {
        System.out.println("New Agents.Airplane!");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            id = Integer.parseInt(args[0].toString());
            fuel = Float.parseFloat(args[1].toString());
            capacity = Integer.parseInt(args[2].toString());
            passengers = Integer.parseInt(args[3].toString());
        }
        addBehaviour(new AirplaneToControlTower(this));
    }

    @Override
    public String toString() {
        return this.getLocalName() + " " + id + " " + fuel + " " + capacity + " " + passengers;
    }
}
