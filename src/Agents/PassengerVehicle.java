package Agents;

import AgentBehaviours.PassengerVehicleStartWork;
import jade.core.Agent;

public class PassengerVehicle extends Agent {

    private int id;
    private int capacity;
    // private int speed;
    // private int fuel;

    public void setup() {
        System.out.println("New Agents.PassengerVehicle!");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            id = Integer.parseInt(args[0].toString());
            capacity = Integer.parseInt(args[1].toString());
            // speed = Integer.parseInt(args[2].toString());
            // fuel = Integer.parseInt(args[3].toString());
        }

        addBehaviour(new PassengerVehicleStartWork(this));
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

    public int getID() {
        return this.id;
    }

    public int getCapacity() {
        return this.capacity;
    }
}
