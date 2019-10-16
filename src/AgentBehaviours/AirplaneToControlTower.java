package AgentBehaviours;

import Agents.Airplane;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class AirplaneToControlTower extends OneShotBehaviour {
    private Airplane airplane;

    public AirplaneToControlTower(Airplane airplane) {
        this.airplane = airplane;
    }
    public void action() {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("ControlTower", AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Weather-forecast-ontology");
        msg.setContent(airplane.toString());
        airplane.send(msg);
    }
}

