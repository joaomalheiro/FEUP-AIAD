package Agents;

import AgentBehaviours.CompanyAirplane;
import AgentBehaviours.CompanyPriorityStrategy;
import AgentBehaviours.ListeningCompanyBehaviour;
import AuxiliarClasses.AgentType;
import AuxiliarClasses.AirplaneInfo;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.HashSet;
import java.util.Set;

public class Company extends Agent {
    private int funds;
    private CompanyPriorityStrategy.Strategy strategy;

    public Company(int funds, CompanyPriorityStrategy.Strategy strategy) {
        this.funds = funds;
        this.strategy = strategy;
    }

    public Set<AirplaneInfo> getAirplanes() {
        return airplanes;
    }

    Set<AirplaneInfo> airplanes = new HashSet<>();

    public void setup(){
        addBehaviour(new CompanyAirplane(this, 8000));
        addBehaviour(new ListeningCompanyBehaviour(this));
        addBehaviour(new CompanyPriorityStrategy(this, strategy ,5000));
    }

    public void addAirplane(Airplane a1) {
        AirplaneInfo airplane = new AirplaneInfo(a1.toString());
        airplanes.add(airplane);
        changeFunds(airplane.getCapacity() * -5);
    }

    public void changeFunds(int amount){
        funds += amount;
        System.out.println("Company: " + this.getLocalName() + " Funds: " + funds);
    }

    public void landAirplane(AirplaneInfo airplane,int valuePerPassenger,int timeWaited) {
       int airplaneProfit = (airplane.getPassengers() * valuePerPassenger) - (airplane.getTimeWaiting() * airplane.getPassengers());
       changeFunds(airplaneProfit);
       airplanes.removeIf(ap -> ap.getId() == airplane.getId());

       if(strategy.equals(CompanyPriorityStrategy.Strategy.SMART)){

           int currentPriority = (valuePerPassenger - 10) * -1;
           if(timeWaited == 0 && currentPriority > 0){
               sendMessagePriority(currentPriority--);
           } else if(timeWaited > 0 && currentPriority < 4) {
               sendMessagePriority(currentPriority + 2);
           }
       }
    }

    public void sendMessagePriority(int priority) {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
        msg.addUserDefinedParameter("AGENT_TYPE", AgentType.COMPANY.toString());
        msg.addReceiver(new AID("ControlTower", AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Weather-forecast-ontology");
        msg.setContent(this.getLocalName() + "Priority:" + priority);
        this.send(msg);
    }


}
