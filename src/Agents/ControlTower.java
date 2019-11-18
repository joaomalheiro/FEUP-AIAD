package Agents;

import AgentBehaviours.AirplaneLanded;
import AgentBehaviours.ListeningTowerBehaviour;
import AuxiliarClasses.AgentType;
import AuxiliarClasses.AirplaneInfo;
import AuxiliarClasses.Pair;
import gui.AirportGUI;
import jade.core.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionInitiator;

import javax.naming.ldap.Control;
import java.util.*;

public class ControlTower extends Agent {

    Comparator<AirplaneInfo> airplaneComparator = new Comparator<AirplaneInfo>() {
        @Override
        public int compare(AirplaneInfo a1, AirplaneInfo a2) {
            if (!a1.getLocalName().equals(a2.getLocalName())) {
                if (a1.getTimeToTower() > a2.getTimeToTower())
                    return 1;
                else if (a1.getFuel() < 5 && a1.getFuel() < a2.getFuel())
                    return 1;
                else if (a1.getTimeWaiting() > 10 && a1.getTimeWaiting() > a2.getTimeWaiting())
                    return 1;
                else if (companyPriorities.get(a1.getLocalName().replaceAll("\\d","")) > companyPriorities.get(a2.getLocalName().replaceAll("\\d","")))
                    return 1;
                else if (a1.getPassengers() > a2.getPassengers())
                    return 1;
                else
                    return -1;
            } else
                return 0;
        }
    };

    public TreeSet<AirplaneInfo> getAirplanes() {
        return airplanes;
    }

    private TreeSet<AirplaneInfo> airplanes = new TreeSet<>(airplaneComparator);
    private Map<String, Integer> companyPriorities = new HashMap<>();

    private Vector<AID> passenger_vehicles;

    private Vector<Pair<String, Boolean>> passenger_vehicles_availability;

    private Character[][] map;
    private Character[][] vehicleMap;
    private int currLine;
    private AirportGUI gui;


    public ControlTower() {
        this.passenger_vehicles = new Vector<>();
        this.passenger_vehicles_availability = new Vector<>();

        map = new Character[11][20];
        for (Character[] row: map)
            Arrays.fill(row, '*');

        vehicleMap = new Character[3][10];
        for (Character[] row: vehicleMap)
            Arrays.fill(row, '*');

        map[9][0] = 'C';
        currLine = 0;
    }

    // **** GETTERS AND SETTERS ****
    public Vector<AID> getPassenger_vehicles() {
        return passenger_vehicles;
    }

    public Vector<Pair<String, Boolean>> getPassenger_vehicles_availability() {
        return passenger_vehicles_availability;
    }

    public Character[][] getMap() {
        return map;
    }

    public Character[][] getVehicleMap() {
        return vehicleMap;
    }

    public void setGui(AirportGUI gui) {
        this.gui = gui;
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
        sd.setType(AgentType.PASSENGER_VEHICLE.toString());
        dfd.addServices(sd);
        return dfd;
    }

    private void initialPassengerVehicleSearch() {
        DFAgentDescription dfd = passengerVehicleDFAgentDescriptorCreator();

        try {
            DFAgentDescription[] search_result = DFService.search(this, dfd);

            for (DFAgentDescription vehicle : search_result) {
                System.out.println(this.passenger_vehicles.add(vehicle.getName()));
                this.passenger_vehicles_availability.add(new Pair<>(vehicle.getName().getLocalName(), false));
            }

        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public void initializePassengerGUI() {
        Iterator iterator = this.passenger_vehicles.iterator();
        int i = 0, j = 0;
        System.out.println(this.passenger_vehicles.size());
        while (iterator.hasNext()) {
            System.out.println("" + i + " j- " + j);
            if(i > 8) {
                i = 0;
                j++;
            }

            this.vehicleMap[j][i] = 'A';
            i = i + 2;
            iterator.next();
        }

        gui.getVehiclePanel().repaint();
        gui.getVehiclePanel().setFocusable(true);
        gui.getVehiclePanel().requestFocusInWindow();
    }

    private void subscribePassengerVehicleAgent() {
        DFAgentDescription template = passengerVehicleDFAgentDescriptorCreator();
        SearchConstraints sc = new SearchConstraints();
        sc.setMaxResults(new Long(1));

        send(DFService.createSubscriptionMessage(this, getDefaultDF(),
                template, sc));

        addBehaviour(new RegistrationNotification(this));
    }

    class RegistrationNotification extends CyclicBehaviour {

        private ControlTower ct;

        public RegistrationNotification(ControlTower ct) {
            super();
            this.ct = ct;
        }

        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchSender(getDefaultDF()));

            if (msg != null) {

                try {
                    DFAgentDescription[] dfds = DFService.decodeNotification(msg.getContent());

                    if (dfds.length > 0) {
                        for (DFAgentDescription dfd : dfds) {
                            AID new_agent = dfd.getName();
                            if (!ct.getPassenger_vehicles().contains(new_agent)) {
                                ct.getPassenger_vehicles().add(new_agent);
                                ct.getPassenger_vehicles_availability().add(new Pair<>(new_agent.getLocalName(), false));
                                System.out.println("New passenger vehicle on duty: " + new_agent.getLocalName());
                            }
                        }
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }

            }

            block();

        }
    }

    public void pushAirplane(AirplaneInfo airplane) {
        int y= -1;
        initializePassengerGUI();
        for (AirplaneInfo value : airplanes) {
            if (value.getLocalName().equals(airplane.getLocalName())) {
                y = value.getY();
                map[value.getX()][value.getY()] = '*';
                break;
            }
        }

        airplanes.removeIf(a1 -> a1.getLocalName().equals(airplane.getLocalName()));
        airplanes.add(airplane);

        Iterator<AirplaneInfo> iterator = airplanes.iterator();

        System.out.println();

        Character wait;
        switch (airplane.getTimeWaiting()) {
            case 0:
                wait = '0';
                break;
            case 1:
                wait = '1';
                break;
            case 2:
                wait = '2';
                break;
            case 3:
                wait = '3';
                break;
            case 4:
                wait = '4';
                break;
            case 5:
                wait = '5';
                break;
            case 6:
                wait = '6';
                break;
            default:
                wait = '7';
                break;
        }

        if (y != -1) {
            map[10 - airplane.getTimeToTower()][y] = wait;
            airplane.setX(10 - airplane.getTimeToTower());
            airplane.setY(y);
        } else {
            map[10 - airplane.getTimeToTower()][currLine] = wait;
            airplane.setX(10 - airplane.getTimeToTower());
            airplane.setY(currLine);

            if (currLine < 19)
                currLine++;
            else currLine = 0;
        }

        gui.getPanel().repaint();
        gui.getPanel().setFocusable(true);
        gui.getPanel().requestFocusInWindow();

        gui.getVehiclePanel().repaint();
        gui.getVehiclePanel().setFocusable(true);
        gui.getVehiclePanel().requestFocusInWindow();


        // Loop over the TreeSet values
        // and print the values
        System.out.print("TreeSet: ");
        while (iterator.hasNext())
            System.out.print(iterator.next()
                    + ", ");
        System.out.println();
    }

    public void landAirplane(AirplaneInfo airplane){
        addBehaviour(new AirplaneLanded(airplane,10 - companyPriorities.get(airplane.getLocalName().replaceAll("\\d",""))));

        for (AirplaneInfo value : airplanes) {
            if (value.getLocalName().equals(airplane.getLocalName())) {
                map[value.getX()][value.getY()] = '*';
                break;
            }
        }

        airplanes.removeIf(a1 -> a1.getLocalName().equals(airplane.getLocalName()) );
    }

    public void setPriority(String companyName, int priority) {
        companyPriorities.put(companyName, priority);
    }
}



