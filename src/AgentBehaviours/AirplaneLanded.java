package AgentBehaviours;

import Agents.Airplane;
import AuxiliarClasses.AirplaneInfo;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class AirplaneLanded extends OneShotBehaviour {

    private AirplaneInfo airplane;
    private int valuePerPassenger;

    public AirplaneLanded(AirplaneInfo airplane,int valuePerPassenger) {
        this.airplane = airplane;
        this.valuePerPassenger = valuePerPassenger;
    }
    public void action() {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID(airplane.getLocalName(), AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Weather-forecast-ontology");
        msg.setContent("Landed");
        myAgent.send(msg);

        jade.lang.acl.ACLMessage msgCompany = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
        msgCompany.addReceiver(new AID(airplane.getLocalName().replaceAll("\\d",""), AID.ISLOCALNAME));
        msgCompany.setLanguage("English");
        msgCompany.setOntology("Weather-forecast-ontology");
        msgCompany.setContent(airplane.toString() + "Value:" + valuePerPassenger);
        myAgent.send(msgCompany);
    }
}