package Agents;

import AgentBehaviours.CompanyAirplane;
import AgentBehaviours.CompanyPriorityStrategy;
import AgentBehaviours.ListeningCompanyBehaviour;
import AuxiliarClasses.AirplaneInfo;
import jade.core.Agent;
import jade.util.leap.ArrayList;

import java.util.HashSet;
import java.util.Set;

public class Company extends Agent {
    private int funds;

    public Company(int funds) {
        this.funds = funds;
    }

    public Set<AirplaneInfo> getAirplanes() {
        return airplanes;
    }

    Set<AirplaneInfo> airplanes = new HashSet<>();

    public void setup(){
        addBehaviour(new CompanyAirplane(this, 20000));
        addBehaviour(new ListeningCompanyBehaviour(this));
        addBehaviour(new CompanyPriorityStrategy(this, CompanyPriorityStrategy.Strategy.RANDOM ,5000));
    }

    public void addAirplane(Airplane a1) {
        AirplaneInfo airplane = new AirplaneInfo(a1.toString());
        airplanes.add(airplane);
        changeFunds(airplane.getCapacity() * -5);
    }

    public void changeFunds(int amount){
        funds += amount;
        System.out.println("Funds: " + funds);
    }

    public void landAirplane(AirplaneInfo airplane,int valuePerPassenger) {
       int airplaneProfit = airplane.getPassengers() * valuePerPassenger - airplane.getTimeWaiting() * airplane.getPassengers();
       changeFunds(airplaneProfit);
       System.out.println("Profit " + (airplaneProfit - (airplane.getCapacity() * 5)));
       airplanes.removeIf(ap -> ap.getId() == airplane.getId());
    }


}
