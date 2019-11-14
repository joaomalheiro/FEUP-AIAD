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
            //System.out.println(msg);
            AirplaneInfo airplane = new AirplaneInfo(msg.getContent());
            company.updateAirplane(airplane);
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Got your message!");
            company.send(reply);
        } else {
            block();
        }
    }
}