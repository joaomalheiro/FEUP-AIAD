package AgentBehaviours;

import Agents.Airplane;
import AuxiliarClasses.AirplaneInfo;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class AirplaneLanded extends OneShotBehaviour {

    private AirplaneInfo airplane;

    public AirplaneLanded(AirplaneInfo airplane) {
        this.airplane = airplane;
    }
    public void action() {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID(airplane.getLocalName(), AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Weather-forecast-ontology");
        msg.setContent("Landed");
        myAgent.send(msg);
    }
}