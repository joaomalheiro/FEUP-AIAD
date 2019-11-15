package Agents;

import AgentBehaviours.ListeningTowerBehaviour;
import AuxiliarClasses.AgentType;
import AuxiliarClasses.AirplaneInfo;
import jade.core.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;
import jade.proto.SubscriptionInitiator;

import java.util.*;

public class ControlTower extends Agent{

    Comparator<AirplaneInfo> airplaneComparator = new Comparator<AirplaneInfo>() {
        @Override public int compare(AirplaneInfo a1, AirplaneInfo a2) {
            if(!a1.getLocalName().equals(a2.getLocalName())){
                if(a1.getFuel() > a2.getFuel())
                    return 1;
                else
                    return -1;
            }
            else
                return 0;
        }
    };

    private TreeSet<AirplaneInfo> airplanes = new TreeSet<>(airplaneComparator);

    private Vector<AID> passenger_vehicles;


    public ControlTower() {
        this.passenger_vehicles = new Vector<>();
    }

    // **** GETTERS AND SETTERS ****
    public Vector<AID> getPassenger_vehicles() {
        return passenger_vehicles;
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

    public void setup() {
        System.out.println("New control tower");
        initialPassengerVehicleSearch();
        subscribePassengerVehicleAgent();
        addBehaviour(new ListeningTowerBehaviour(this));
    }

    private DFAgentDescription passengerVehicleDFAgentDescriptorCreator() {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("passenger_vehicle");
        dfd.addServices(sd);
        return dfd;
    }

    private void initialPassengerVehicleSearch(){
        DFAgentDescription dfd = passengerVehicleDFAgentDescriptorCreator();

        try {
            DFAgentDescription[] search_result = DFService.search(this, dfd);

            for(DFAgentDescription vehicle : search_result)
                System.out.println(this.passenger_vehicles.add(vehicle.getName()));
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void subscribePassengerVehicleAgent() {
        DFAgentDescription template = passengerVehicleDFAgentDescriptorCreator();
        
        addBehaviour( new SubscriptionInitiator( this,
                DFService.createSubscriptionMessage( this, getDefaultDF(),
                        template, null))
        {
            protected void handleInform(ACLMessage inform) {
                try {
                    DFAgentDescription[] dfds =
                            DFService.decodeNotification(inform.getContent());

                    ControlTower ct = (ControlTower) myAgent;
                    for(DFAgentDescription dfd : dfds) {
                        AID new_agent = dfd.getName();
                        if(!ct.getPassenger_vehicles().contains(new_agent)) {
                            ct.getPassenger_vehicles().add(new_agent);
                            System.out.println("New passenger vehicle on duty: " + new_agent.getLocalName());
                        }
                    }

                }
                catch (FIPAException fe) {fe.printStackTrace(); }
            }
        });

    }


    public void pushAirplane(AirplaneInfo airplane) {
        airplanes.removeIf(a1 -> a1.getLocalName().equals(airplane.getLocalName()) );
        airplanes.add(airplane);
        Iterator<AirplaneInfo> iterator = airplanes.iterator();

        // Loop over the TreeSet values
        // and print the values
        System.out.print("TreeSet: ");
        while (iterator.hasNext())
            System.out.print(iterator.next()
                    + ", ");
        System.out.println();
    }

}



