package AgentBehaviours;

import Agents.Airplane;
import Agents.ControlTower;
import AuxiliarClasses.AgentType;
import AuxiliarClasses.AirplaneInfo;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Properties;

public class ListeningTowerBehaviour extends CyclicBehaviour {

    private ControlTower controlTower;

    public ListeningTowerBehaviour(ControlTower controlTower){
        this.controlTower = controlTower;
    }

    public void action() {
        ACLMessage msg = controlTower.receive();

        if (msg != null && !msg.getContent().equals("Got your message!")) {
            Object tmp = msg.getAllUserDefinedParameters().get("AGENT_TYPE");
            if(tmp != null) {
                switch (tmp.toString()) {
                    case "PASSENGERVEHICLE":
                        passengerVehicleMessage(msg);
                        break;
                    case "AIRPLANE":
                        airplaneMessage(msg);
                        break;
                    default:
                        System.out.println("ListeningTowerBehaviour - ERROR: agent type unknown");
                }
            }
        } else {
            block();
        }
    }


    private void airplaneMessage(ACLMessage msg) {
        if (msg != null && !msg.getContent().equals("Got your message!")) {
            AirplaneInfo airplane = new AirplaneInfo(msg.getContent());
            controlTower.pushAirplane(airplane);
            if (airplane.getTimeToTower() == 0)
                controlTower.landAirplane(airplane);
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Got your message!");
            controlTower.send(reply);
        }
    }

    private void passengerVehicleMessage(ACLMessage msg) {
        System.out.println("VEHICLE MESSAGE: " + msg.getContent());
    }
}

