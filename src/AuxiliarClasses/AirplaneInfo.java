package AuxiliarClasses;

import Agents.Airplane;

public class AirplaneInfo {

    public int getId() {
        return id;
    }
    private int id;
    private float fuel;
    private int capacity;
    private int passengers;
    private int timeToTower;
    private int timeWaiting = 0;
    private String localName;

    public float getFuel() {
        return fuel;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getPassengers() {
        return passengers;
    }

    public int getTimeToTower() {
        return timeToTower;
    }

    public int getTimeWaiting() {
        return timeWaiting;
    }

    public String getLocalName() {
        return localName;
    }

    public AirplaneInfo(String message) {
        String[] args = message.split(" ");
        this.id = Integer.parseInt(args[1]);
        this.fuel = Float.parseFloat(args[2]);
        this.capacity = Integer.parseInt(args[3]);
        this.passengers = Integer.parseInt(args[4]);
        this.timeToTower = Integer.parseInt(args[5]);
        this.timeWaiting = Integer.parseInt(args[6]);
    }

    @Override
    public boolean equals(Object obj) {
        AirplaneInfo airplane = (AirplaneInfo) obj;
        return this.id == airplane.getId();
    }
}