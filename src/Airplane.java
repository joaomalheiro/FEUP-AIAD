import jade.core.*;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;


public class Airplane extends Agent {
    private int id;
    private float fuel;
    private int capacity;
    private int passengers;

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

    public void setup() {
        System.out.println("New Airplane!");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            id = Integer.parseInt(args[0].toString());
            fuel = Float.parseFloat(args[1].toString());
            capacity = Integer.parseInt(args[2].toString());
            passengers = Integer.parseInt(args[3].toString());
        }
        addBehaviour(new StartingMessage());
    }
    public class StartingMessage extends OneShotBehaviour {
        public void action() {
            jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID("ControlTower", AID.ISLOCALNAME));
            msg.setLanguage("English");
            msg.setOntology("Weather-forecast-ontology");
            msg.setContent("Today it’s raining");
            send(msg);
        }
    }

}
