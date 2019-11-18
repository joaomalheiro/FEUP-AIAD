package AgentBehaviours;

import Agents.Airplane;
import Agents.Company;
import AuxiliarClasses.AgentType;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class CompanyPriorityStrategy extends TickerBehaviour {
    private Company company;

    public enum Strategy {
        RANDOM,
        MEDIUM,
        SMART,
    }

    Strategy type;

    public CompanyPriorityStrategy( Company company, Strategy type, long period) {
        super(company,period);
        this.company = company;
        this.type = type;
    }

    @Override
    protected void onTick() {
        switch (type){
            case MEDIUM:
                handleSmart();
                break;
            case RANDOM:
                handleRandom();
                break;
            case SMART:
                break;
            default:

        }

    }

    private void handleSmart() {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
        msg.addUserDefinedParameter("AGENT_TYPE", AgentType.COMPANY.toString());
        msg.addReceiver(new AID("ControlTower", AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Weather-forecast-ontology");
        msg.setContent(company.getLocalName() + " Query plane numbers");
        company.send(msg);

    }

    private void handleRandom() {
        Random rand = new Random();
        // Generate random integers in range 0 to 5
        int priority = rand.nextInt(5);

        company.sendMessagePriority(priority);
    }

}
