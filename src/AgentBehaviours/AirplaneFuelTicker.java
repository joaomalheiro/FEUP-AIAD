package AgentBehaviours;

import Agents.Airplane;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class AirplaneFuelTicker extends TickerBehaviour {

    private Airplane airplane;

    public AirplaneFuelTicker(Airplane airplane, long period) {
        super(airplane, period);
        this.airplane = airplane;
    }

    public void informTower() {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
        msg.addUserDefinedParameter("AGENT_TYPE", "AIRPLANE");
        msg.addReceiver(new AID("ControlTower", AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Weather-forecast-ontology");
        msg.setContent(airplane.toString());
        airplane.send(msg);
    }

    @Override
    protected void onTick() {
        this.airplane.timeTick();
        if(airplane.getTimeToTower() <= 10){
            informTower();
        }

    }
}
