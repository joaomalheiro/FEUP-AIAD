package Agents;

import AuxiliarClasses.AgentType;
import AuxiliarClasses.TransportTask;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class PassengerVehicle extends Agent {

    private int id;
    private int capacity;
    private TransportTask current_task = null;
    // private int speed;
    // private int fuel;

    public void setup() {
        System.out.println("New Agents.PassengerVehicle!");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            id = Integer.parseInt(args[0].toString());
            capacity = Integer.parseInt(args[1].toString());
            // speed = Integer.parseInt(args[2].toString());
            // fuel = Integer.parseInt(args[3].toString());
        }

        // Register in DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AgentType.PASSENGER_VEHICLE.toString());
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new ListeningPassengerVehicleBehaviour());
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

    private void executeTransportTask(TransportTask task) {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);

        try {
            msg.setContentObject(task);
        } catch (IOException e) {
            e.printStackTrace();
        }

        addBehaviour(new TaskPreparation(this, msg));
    }

    private class ListeningPassengerVehicleBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if(current_task != null && msg != null && !msg.getContent().equals("Got your message!")) {
                try {
                    System.out.println(msg.getContentObject());
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
            }
            else {
                block();
            }
        }
    }

    private void handleNewTask(TransportTask task) {

        if(task.getPassenger_number() <= this.getCapacity())
            return; //Send accept message back to the control tower

        // Ask help to other ones
    }

    private void acceptNewTask() {


    }


    private class TaskPreparation extends ContractNetInitiator {

        public TaskPreparation(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        @Override
        protected Vector prepareCfps(ACLMessage cfp) {
            Vector<ACLMessage> vec = new Vector<>();
            AID[] aids = searchDF("PASSENGER_VEHICLE");

            for (AID aid : aids)
                cfp.addReceiver(aid);

            vec.add(cfp);

            return vec;
        }

        @Override
        protected void handleAllResponses(Vector responses, Vector acceptances) {
            super.handleAllResponses(responses, acceptances);
        }
    }

    private class TaskHandler extends ContractNetResponder {

        public TaskHandler(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        @Override
        protected ACLMessage handleCfp(ACLMessage cfp) {
            PassengerVehicle pv = (PassengerVehicle) myAgent;
            try {
                TransportTask transT = (TransportTask) cfp.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            // TODO
            // If the bus is busy, it must deny the request here

            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);
            try {
                reply.setContentObject(/* Need to return the evaluator response*/null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reply;
        }

        @Override
        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {

            // TODO
            // This handler must send the ct the accept message
            System.out.println(myAgent.getLocalName() + ": proposal accepted");
            try {
                TransportTask task = (TransportTask) cfp.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            ACLMessage res = accept.createReply();
            res.setPerformative(ACLMessage.INFORM);
            res.setContent("Will be done ");

            return res;
        }
    }

    private AID[] searchDF(String service) {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(service);
        dfd.addServices(sd);

        SearchConstraints ALL = new SearchConstraints();
        ALL.setMaxResults(new Long(-1));

        try {
            DFAgentDescription[] result = DFService.search(this, dfd, ALL);
            AID[] agents = new AID[result.length];
            for (int i = 0; i < result.length; i++)
                agents[i] = result[i].getName();
            return agents;

        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        return null;
    }

    public int getID() {
        return this.id;
    }

    public int getCapacity() {
        return this.capacity;
    }
}
