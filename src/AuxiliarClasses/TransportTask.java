package AuxiliarClasses;

public class TransportTask {

    private String airplane_name;
    private int passenger_number;
    private int drive_distance;
    private double start_time = -1;
    private double end_time = -1;

    public TransportTask(String airplane_name, int passenger_number, int drive_distance) {
        this.airplane_name = airplane_name;
        this.passenger_number = passenger_number;
        this.drive_distance = drive_distance;
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
}
