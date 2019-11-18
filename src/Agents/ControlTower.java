package Agents;

import AgentBehaviours.AirplaneLanded;
import AgentBehaviours.AssignTransportationTask;
import AgentBehaviours.LandingTicker;
import AgentBehaviours.ListeningTowerBehaviour;
import AuxiliarClasses.*;
import gui.AirportGUI;
import jade.core.*;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.MessageTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ControlTower extends Agent {

    Comparator<AirplaneInfo> airplaneComparator = new Comparator<AirplaneInfo>() {
        @Override
        public int compare(AirplaneInfo a1, AirplaneInfo a2) {
            if (!a1.getLocalName().equals(a2.getLocalName())) {
                if (a1.getTimeToTower() < a2.getTimeToTower())
                    return -1;
                else if (a1.getTimeToTower() > a2.getTimeToTower())
                    return 1;
                else if (a1.getFuel() < 5 && a1.getFuel() < a2.getFuel())
                    return -1;
                else if (a1.getTimeWaiting() > 10 && a1.getTimeWaiting() > a2.getTimeWaiting())
                    return -1;
                else if (companyPriorities.get(a1.getLocalName().replaceAll("\\d", "")) > companyPriorities.get(a2.getLocalName().replaceAll("\\d", "")))
                    return -1;
                else if (a1.getPassengers() > a2.getPassengers())
                    return -1;
                else
                    return 1;
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

    private ConcurrentHashMap<String, TransportVehicleAvailability> passenger_vehicles_availability;

    public int getTransports_available_counter() {
        return transports_available_counter;
    }

    public void increment_transport_counter() {
        transports_available_counter++;

    }

    public void decrement_transport_counter() {
        transports_available_counter--;
    }

    private int transports_available_counter;

    private Character[][] map;
    private Character[][] vehicleMap;
    private int currLine;
    private AirportGUI gui;


    public ControlTower() {
        this.passenger_vehicles = new Vector<>();
        this.passenger_vehicles_availability = new ConcurrentHashMap<>();
        this.transports_available_counter = 0;

        map = new Character[11][20];
        for (Character[] row : map)
            Arrays.fill(row, '*');

        vehicleMap = new Character[3][10];
        for (Character[] row : vehicleMap)
            Arrays.fill(row, '*');

        map[9][0] = 'C';
        currLine = 0;
    }

    // **** GETTERS AND SETTERS ****
    public Vector<AID> getPassenger_vehicles() {
        return passenger_vehicles;
    }

    public ConcurrentHashMap<String, TransportVehicleAvailability> getPassenger_vehicles_availability() {
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
        DFregistring();
        initialPassengerVehicleSearch();
        subscribePassengerVehicleAgent();

        //addBehaviour(new PrintDF(this, 5000));

        addBehaviour(new ListeningTowerBehaviour(this));
        addBehaviour(new LandingTicker(this, 500));
    }

    private void DFregistring() {
        // Register in DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AgentType.CONTROLTOWER.toString());
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
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
                this.passenger_vehicles_availability.put(vehicle.getName().getLocalName(), TransportVehicleAvailability.FREE);
                this.passenger_vehicles.add(vehicle.getName());
                increment_transport_counter();
            }

        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public void setAvailability() {
        int i = 0, j = 0;

        for (TransportVehicleAvailability value : passenger_vehicles_availability.values()) {
            if (i > 8) {
                i = 0;
                j++;
            }


            if (value == TransportVehicleAvailability.BUSY) {
                this.vehicleMap[j][i] = '7';
            } else if (value == TransportVehicleAvailability.WAITING_REPLY) {
                this.vehicleMap[j][i] = '3';
            } else if (value == TransportVehicleAvailability.FREE) {
                this.vehicleMap[j][i] = '0';
            }
            i = i + 2;
        }

        gui.getVehiclePanel().repaint();
        gui.getVehiclePanel().setFocusable(true);
        gui.getVehiclePanel().requestFocusInWindow();
    }

    private void subscribePassengerVehicleAgent() {
        DFAgentDescription template = passengerVehicleDFAgentDescriptorCreator();
        SearchConstraints sc = new SearchConstraints();
        sc.setMaxResults(1L);

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
                                ct.getPassenger_vehicles_availability().put(new_agent.getLocalName(), TransportVehicleAvailability.FREE);
                                increment_transport_counter();
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
        int y = -1;
        setAvailability();
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

        //System.out.println();

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
        // System.out.print("TreeSet: ");
        // while (iterator.hasNext())
        //   System.out.print(iterator.next()
        //           + ", ");
        // System.out.println();
        //  System.out.println(companyPriorities);


    }

    public void landAirplane(AirplaneInfo airplane) {
        addBehaviour(new AirplaneLanded(airplane, 10 - companyPriorities.get(airplane.getLocalName().replaceAll("\\d", ""))));

        for (AirplaneInfo value : airplanes) {
            if (value.getLocalName().equals(airplane.getLocalName())) {
                map[value.getX()][value.getY()] = '*';
                break;
            }
        }

        // Creating a transport task
       addBehaviour(new AssignTransportationTask(this, airplane, -1));

        airplanes.removeIf(a1 -> a1.getLocalName().equals(airplane.getLocalName()));
    }

    public void setPriority(String companyName, int priority) {
        companyPriorities.put(companyName, priority);
    }

    private AID[] getAllElementsDf() {
        DFAgentDescription dfd = new DFAgentDescription();


        SearchConstraints ALL = new SearchConstraints();
        ALL.setMaxResults(new Long(-1));

        try {
            DFAgentDescription[] result = DFService.search(this, dfd, ALL);
            AID[] agents = new AID[result.length];
            for (int i = 0; i < result.length; i++)
                agents[i] = result[i].getName();
            return agents;

        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        return null;
    }


    /* *** TESTING METHODS **** */
    private void printAllDfElements() {
        AID[] tmp = getAllElementsDf();
        for (int i = 0; i < tmp.length; i++)
            //System.out.println("DF [" + i + "]: " + tmp[i].getLocalName());

            System.out.println("--- ---");

    }

    private void printAllElementsAvailability() {
        for (Map.Entry<String, TransportVehicleAvailability> vehicle : passenger_vehicles_availability.entrySet()) {
            String k = vehicle.getKey();
            TransportVehicleAvailability v = vehicle.getValue();
            //System.out.println("Availability: " + k + "  |  " + v);
        }
    }

    private class PrintDF extends TickerBehaviour {

        public PrintDF(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            printAllDfElements();
            printAllElementsAvailability();
        }
    }


}




