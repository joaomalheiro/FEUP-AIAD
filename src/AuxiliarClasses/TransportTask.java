package AuxiliarClasses;

import java.io.Serializable;
import java.util.Vector;

public class TransportTask implements Serializable {

    private String airplane_name;
    private int passenger_number;
    private int drive_distance;
    private Vector<Pair<String, Integer>> assigned_passenger_vehicles;
    private double start_time = -1;
    private double end_time = -1;

    public TransportTask(String airplane_name, int passenger_number, int drive_distance) {
        this.airplane_name = airplane_name;
        this.passenger_number = passenger_number;
        this.drive_distance = drive_distance;
        assigned_passenger_vehicles = new Vector<>();
    }

    public String getAirplane_name() {
        return airplane_name;
    }

    public int getPassenger_number() {
        return passenger_number;
    }

    public double getStart_time() {
        return start_time;
    }

    public int getDrive_distance() {
        return this.drive_distance;
    }

    public void setDrive_distance(int distance) {
        this.drive_distance = distance;
    }

    public void setStart_time(double start_time) {
        this.start_time = start_time;
    }

    public double getEnd_time() {
        return end_time;
    }

    public void setEnd_time(double end_time) {
        this.end_time = end_time;
    }

    public Vector<Pair<String, Integer>> getAssigned_passenger_vehicles() {
        return assigned_passenger_vehicles;
    }

    public void addVehicleToTask(String vehicle_id, int capacity) {
        assigned_passenger_vehicles.add(new Pair<>(vehicle_id, capacity));
    }

    public boolean isTaskSatisfied() {
        int transports_capacity = 0;

        for (Pair<String, Integer> trans : this.assigned_passenger_vehicles) {
            transports_capacity += trans.getR();
        }

        System.out.println(this.passenger_number + " ===== " + transports_capacity);
        return this.passenger_number <= transports_capacity;
    }

    @Override
    public String toString() {
        return "TASK FOR: " + airplane_name + " with " + passenger_number + " passengers, at " + drive_distance + "m drive distance.";
    }
}
