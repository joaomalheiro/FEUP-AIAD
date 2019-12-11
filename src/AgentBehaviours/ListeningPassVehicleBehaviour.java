package AgentBehaviours;

import Agents.PassengerVehicle;
import AuxiliarClasses.AgentType;
import AuxiliarClasses.TransportTask;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

public class ListeningPassVehicleBehaviour extends CyclicBehaviour {

    public ListeningPassVehicleBehaviour(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();

        if (msg != null && !msg.getContent().equals("Got your message!")) {
            Object tmp = msg.getAllUserDefinedParameters().get("AGENT_TYPE");
            if (tmp != null) {
                switch (tmp.toString()) {
                    case "CONTROLTOWER":
                        controlTowerMessage(msg);
                        break;
                    default:
                        System.out.println("ListeningPassVehicleBehaviour - ERROR: agent type " + tmp.toString() + " unknown");
                }
            }
        } else {
            block();
        }
    }

    private void controlTowerMessage(ACLMessage msg) {

        if (msg.getPerformative() == ACLMessage.CONFIRM) {
            System.out.println(myAgent.getLocalName() + ": received CONFIRM message");
            ((PassengerVehicle) myAgent).setConfirmed_task(true);
        } else if (((PassengerVehicle) myAgent).getTask() != null) {
            ACLMessage reply = msg.createReply();
            reply.clearUserDefinedParameter("AGENT_TYPE");
            reply.addUserDefinedParameter("AGENT_TYPE", AgentType.PASSENGER_VEHICLE.toString());
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            reply.setContent("Busy");
            // DEBUG
            System.out.println(myAgent.getLocalName() + ": already BUSY");

            myAgent.send(reply);
        } else if (msg.getPerformative() == ACLMessage.PROPOSE) {
            TransportTask task_proposed;

            try {
                task_proposed = (TransportTask) msg.getContentObject();
                ACLMessage reply = msg.createReply();
                reply.addUserDefinedParameter("AGENT_TYPE", AgentType.PASSENGER_VEHICLE.toString());

                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                task_proposed.addVehicleToTask(myAgent.getLocalName(), ((PassengerVehicle) myAgent).getCapacity());
                ((PassengerVehicle) myAgent).setCurrent_task(task_proposed);
                reply.setContentObject(task_proposed);
                System.out.println(myAgent.getLocalName() + ": Accepting Task for " + task_proposed.getAirplane_name());

                myAgent.send(reply);
            } catch (UnreadableException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
