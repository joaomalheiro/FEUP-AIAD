package Agents;

import jade.core.*;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;

public class ControlTower extends Agent{
    private Airplane[] airplanes;

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

    public void setup() {
        System.out.println("New control tower");
        addBehaviour(new ListeningBehaviour());
    }
    class ListeningBehaviour extends CyclicBehaviour {

        public void action() {
            ACLMessage msg = receive();
            if(msg != null) {
                System.out.println(msg);
                System.out.println("Received msg");
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("Got your message!");
                send(reply);
                System.out.println(msg.getSender());
            } else {
                block();
            }
        }
    }

}
