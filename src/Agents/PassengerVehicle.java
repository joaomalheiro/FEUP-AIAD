package Agents;

import AuxiliarClasses.AgentType;
import AuxiliarClasses.Pair;
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

    public PassengerVehicle(int id, int capacity){
        this.id = id;
        this.capacity = capacity;
    }

    public void setup() {
        System.out.println("New Agents.PassengerVehicle!");

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

            if(current_task == null && msg != null && !msg.getContent().equals("Got your message!")) {
                try {
                    System.out.println(msg.getContentObject());
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }

                if(msg.getPerformative() == ACLMessage.CFP)
                    addBehaviour(new TaskHandler(myAgent, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
                else if(msg.getPerformative() == ACLMessage.REQUEST)
                    handleNewTaskFromControlTower(msg);
            }
            else {
                block();
            }
        }
    }

    private void handleNewTaskFromControlTower(ACLMessage msg) {

        TransportTask task = null;
        ACLMessage forward_request = null;
        try {
            task = (TransportTask) msg.getContentObject();
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

        //When the vehicle can handle the request alone
        if(task.getPassenger_number() <= this.getCapacity()) {
            acceptNewIndividualTask(msg);
            return;
        }

        else {
            try {
                task = (TransportTask) msg.getContentObject();
                forward_request = new ACLMessage(ACLMessage.CFP);
                task.addVehicleToTask(this.getLocalName(), this.getCapacity());
                forward_request.setContentObject(task);
            } catch (UnreadableException | IOException e) {
                e.printStackTrace();
            }
            // DEBUG
            System.out.println("FORWARDED: " + task.getAirplane_name() + " By" + this.getLocalName());
            addBehaviour(new TaskPreparation(this, forward_request));
        }
    }

    private void acceptNewIndividualTask(ACLMessage msg) {

        ACLMessage reply = msg.createReply();
        try {
            TransportTask task_aux = (TransportTask) msg.getContentObject();
            task_aux.addVehicleToTask(this.getAID().getLocalName(), this.getCapacity());
            reply.setContentObject(task_aux);
            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            this.send(reply);

            this.current_task = task_aux;
            // DEBUG
            System.out.println("ACCEPTED: " + task_aux.getAirplane_name() + " By" + this.getLocalName());
        } catch (UnreadableException | IOException e) {
            e.printStackTrace();
        }
    }


    private class TaskPreparation extends ContractNetInitiator {

        public TaskPreparation(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        @Override
        protected Vector prepareCfps(ACLMessage cfp) {
            Vector<ACLMessage> vec = new Vector<>();
            AID[] aids = searchDF("PASSENGER_VEHICLE");

            for (AID aid : aids) {
                if(aid.getLocalName() == myAgent.getLocalName())
                    continue;

                cfp.addReceiver(aid);
            }

            vec.add(cfp);

            return vec;
        }

        @Override
        protected void handleAllResponses(Vector responses, Vector acceptances) {
            // TODO
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
