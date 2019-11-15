package Agents;

import AgentBehaviours.AirplaneFuelTicker;
import AgentBehaviours.ListeningAirplaneBehaviour;
import jade.core.*;



public class Airplane extends Agent {
    private int id;
    private float fuel;
    private int capacity;
    private int passengers;
    private int timeToTower;
    private boolean landed = false;
    private int timeWaiting = 0;
    private int priority;
    private String companyName;

    public Airplane(){};

    public Airplane(String message){
        String[] args = message.split(" ");
        this.id = Integer.parseInt(args[1]);
        this.fuel = Float.parseFloat(args[2]);
        this.capacity = Integer.parseInt(args[3]);
        this.passengers = Integer.parseInt(args[4]);
        this.timeToTower = Integer.parseInt(args[5]);
    }

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
            timeToTower = Integer.parseInt(args[4].toString());
        }
        addBehaviour(new AirplaneFuelTicker(this, 1000));
        addBehaviour(new ListeningAirplaneBehaviour(this));

    }

    @Override
    public String toString() {
        return getLocalName() + " " + id + " " + fuel + " " + capacity + " " + passengers + " " + timeToTower + " " + timeWaiting;
    }

    public void timeTick() {
        this.fuel--;
        if(this.timeToTower >= 1) {
            this.timeToTower--;
        } else {
            this.timeToTower = 0;
            this.timeWaiting++;
        }
    }

    public int getTimeToTower() {
        return timeToTower;
    }

    public void setTimeToTower(int timeToTower) {
        this.timeToTower = timeToTower;
    }

    public void landPlane() {
        this.landed = true;
    }
}
