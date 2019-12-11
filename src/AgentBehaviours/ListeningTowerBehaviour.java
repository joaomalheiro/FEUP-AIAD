package AgentBehaviours;

import Agents.ControlTower;
import AuxiliarClasses.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class ListeningTowerBehaviour extends CyclicBehaviour {

    private ControlTower controlTower;

    public ListeningTowerBehaviour(ControlTower controlTower) {
        this.controlTower = controlTower;
    }

    public void action() {
        ACLMessage msg = controlTower.receive();

        if (msg != null && !msg.getContent().equals("Got your message!")) {
            Object tmp = msg.getAllUserDefinedParameters().get("AGENT_TYPE");
            if (tmp != null) {
                switch (tmp.toString()) {
                    case "PASSENGER_VEHICLE":
                        passengerVehicleMessage(msg);
                        break;
                    case "AIRPLANE":
                        airplaneMessage(msg);
                        break;
                    case "COMPANY":
                        companyMessage(msg);
                        break;
                    default:
                        System.out.println("ListeningTowerBehaviour - ERROR: agent type " + tmp.toString() + " unknown");
                }
            }
        } else {
            block();
        }
    }

    private void companyMessage(ACLMessage msg) {
        if (msg.getContent().contains("Priority:")) {
            String[] args = msg.getContent().split("Priority:");
            controlTower.setPriority(args[0], Integer.parseInt(args[1]));
        } else {
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.addUserDefinedParameter("AGENT_TYPE", AgentType.CONTROLTOWER.toString());
            reply.setContent(Integer.toString(controlTower.getAirplanes().size()));
            controlTower.send(reply);
        }
    }


    private void airplaneMessage(ACLMessage msg) {
        if (msg != null && !msg.getContent().equals("Got your message!")) {
            AirplaneInfo airplane = new AirplaneInfo(msg.getContent());
            controlTower.pushAirplane(airplane);
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Got your message!");
            controlTower.send(reply);
        }
    }

    private void passengerVehicleMessage(ACLMessage msg) {

        if (msg != null && !msg.getContent().equals("Got your message!")) {

            if (msg.getPerformative() == ACLMessage.INFORM) {
                System.out.println("CONTROLTOWER: Received INFORM from " + msg.getSender().getLocalName());
                controlTower.getPassenger_vehicles_availability().put(msg.getSender().getLocalName(), TransportVehicleAvailability.FREE);
                controlTower.increment_transport_counter();
            } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                System.out.println("CONTROLTOWER: Received ACCEPT_PROPOSAL from " + msg.getSender().getLocalName());

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.CONFIRM);
                reply.clearUserDefinedParameter("AGENT_TYPE");
                reply.addUserDefinedParameter("AGENT_TYPE", "CONTROLTOWER");
                reply.setContent("Confirmed");
                myAgent.send(reply);
                System.out.println("CONTROLTOWER: Sent CONFIRM to " + msg.getSender().getLocalName());

                try {
                    TransportTask task = (TransportTask) msg.getContentObject();

                    Pair<String, Integer> vehicle = task.getAssigned_passenger_vehicles().lastElement();
                    controlTower.getPassenger_vehicles_availability().put(vehicle.getL(), TransportVehicleAvailability.BUSY);
                    controlTower.decrement_transport_counter();
                    
                    controlTower.checkTaskFulfillment(task);

                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
            }
        }

        controlTower.setAvailability();
    }
}

