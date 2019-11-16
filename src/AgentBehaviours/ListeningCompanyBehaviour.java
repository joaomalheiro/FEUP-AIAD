package AgentBehaviours;

import Agents.Company;
import Agents.ControlTower;
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
        if(msg != null && !msg.getContent().equals("Got your message!")) {
            String[] args = msg.getContent().split("Value:");
            AirplaneInfo airplane = new AirplaneInfo(args[0]);
            company.landAirplane(airplane,Integer.parseInt(args[1]));
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Got your message!");
            company.send(reply);
        } else {
            block();
        }
    }
}