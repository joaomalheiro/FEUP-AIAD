package AgentBehaviours;

import Agents.Airplane;
import Agents.ControlTower;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ListeningTowerBehaviour extends CyclicBehaviour {

    private ControlTower controlTower;

    public ListeningTowerBehaviour(ControlTower controlTower){
        this.controlTower = controlTower;
    }

    public void action() {
        ACLMessage msg = controlTower.receive();
        if(msg != null) {
            System.out.println(msg);
            System.out.println("Received msg");
            Airplane airplane = new Airplane(msg.getContent());
            controlTower.pushAirplane(airplane);
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Got your message!");
            controlTower.send(reply);
        } else {
            block();
        }
    }
}

