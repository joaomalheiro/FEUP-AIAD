package AgentBehaviours;

import Agents.Airplane;
import Agents.ControlTower;
import AuxiliarClasses.AirplaneInfo;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ListeningAirplaneBehaviour extends CyclicBehaviour {

    private Airplane airplane;

    public ListeningAirplaneBehaviour(Airplane airplane){
        this.airplane = airplane;
    }

    public void action() {
        ACLMessage msg = airplane.receive();
        if(msg != null && !msg.getContent().equals("Got your message!")) {
            //System.out.println(msg);
            if(msg.getContent().equals("Landed")){
                airplane.landPlane();
                System.out.println("LANDED Airplane");
            }

            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Got your message!");
            airplane.send(reply);
        } else {
            block();
        }
    }
}