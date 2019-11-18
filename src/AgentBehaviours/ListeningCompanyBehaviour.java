package AgentBehaviours;

import Agents.Company;
import Agents.ControlTower;
import AuxiliarClasses.AgentType;
import AuxiliarClasses.AirplaneInfo;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ListeningCompanyBehaviour extends CyclicBehaviour {

    private Company company;

    public ListeningCompanyBehaviour(Company company){
        this.company = company;
    }

    public void action() {
        ACLMessage msg = company.receive();

        if (msg != null && !msg.getContent().equals("Got your message!")) {
            Object tmp = msg.getAllUserDefinedParameters().get("AGENT_TYPE");
            if(tmp != null) {
                switch (tmp.toString()) {
                    case "AIRPLANE":
                        airplaneMessage(msg);
                        break;
                    case "CONTROLTOWER":
                        controlTowerMessage(msg);
                        break;
                    default:
                        System.out.println("ListeningTowerBehaviour - ERROR: agent type unknown");
                }
            }
        } else {
            block();
        }
    }

    private void controlTowerMessage(ACLMessage msg) {
        int nPlanes = Integer.parseInt(msg.getContent());

        if(nPlanes < 15){
            company.sendMessagePriority(0);
        } if (nPlanes >= 15 && nPlanes < 30){
            company.sendMessagePriority(1);
        } if (nPlanes >= 30 && nPlanes < 50){
            company.sendMessagePriority(2);
        } if (nPlanes >= 50 && nPlanes < 70){
            company.sendMessagePriority(3);
        } if (nPlanes >= 70 && nPlanes < 100){
            company.sendMessagePriority(4);
        } else {
            company.sendMessagePriority(5);
        }
    }

    private void airplaneMessage(ACLMessage msg) {
        String[] args = msg.getContent().split("Value:");
        AirplaneInfo airplane = new AirplaneInfo(args[0]);
        company.landAirplane(airplane,Integer.parseInt(args[1]),airplane.getTimeWaiting());
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent("Got your message!");
        company.send(reply);
    }
}