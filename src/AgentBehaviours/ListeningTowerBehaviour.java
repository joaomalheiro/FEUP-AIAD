package AgentBehaviours;

import Agents.Airplane;
import Agents.ControlTower;
import AuxiliarClasses.AirplaneInfo;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ListeningTowerBehaviour extends CyclicBehaviour {

    private ControlTower controlTower;

    public ListeningTowerBehaviour(ControlTower controlTower){
        this.controlTower = controlTower;
    }

    public void action() {
        ACLMessage msg = controlTower.receive();
        if(msg != null && !msg.getContent().equals("Got your message!")) {
            //System.out.println(msg);
            AirplaneInfo airplane = new AirplaneInfo(msg.getContent());
            controlTower.pushAirplane(airplane);
            if(airplane.getTimeToTower() == 0)
                controlTower.landAirplane(airplane);
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Got your message!");
            controlTower.send(reply);
        } else {
            block();
        }
    }
}

