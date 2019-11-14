package Agents;

import AgentBehaviours.AirplaneLanded;
import AgentBehaviours.ListeningTowerBehaviour;
import AuxiliarClasses.AirplaneInfo;
import jade.core.*;

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
    private Map<String, Integer> companyPriorities = new HashMap<>();

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

    public void setup() {
        System.out.println("New control tower");
        addBehaviour(new ListeningTowerBehaviour(this));
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

    public void insertCompany(Company company, int priority){
        companyPriorities.put(company.getLocalName(),priority);
    }

    public void landAirplane(AirplaneInfo airplane){
        addBehaviour(new AirplaneLanded(airplane));
    }
}



