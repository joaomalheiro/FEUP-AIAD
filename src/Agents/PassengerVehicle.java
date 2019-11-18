package Agents;

import AuxiliarClasses.AgentType;
import AuxiliarClasses.TransportTask;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
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
import java.util.Vector;

public class PassengerVehicle extends Agent {

    private int id;
    private int capacity;
    private TransportTask current_task = null;
    private int speed; //  meters/tick
    // private int fuel;

    public PassengerVehicle(int id, int capacity, int speed){
        this.id = id;
        this.capacity = capacity;
        this.speed = speed;
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

            // TODO
            // Only after receiving confirmation from CT, does it start working
            this.current_task = acceptNewIndividualTask(msg);

            // Task is going to be executed (each tick is 1 s = 1000 ms
            addBehaviour(new ExecuteTask(this, 1000, current_task.getDrive_distance(), this.getSpeed()));

            // DEBUG
            System.out.println("ACCEPTED: " + this.current_task.getAirplane_name() + " By" + this.getLocalName());
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

    private TransportTask acceptNewIndividualTask(ACLMessage msg) {

        ACLMessage reply = msg.createReply();
        TransportTask task_aux = null;
        try {
            task_aux = (TransportTask) msg.getContentObject();
            task_aux.addVehicleToTask(this.getAID().getLocalName(), this.getCapacity());
            reply.setContentObject(task_aux);
            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            reply.addUserDefinedParameter("AGENT_TYPE", AgentType.PASSENGER_VEHICLE.toString());
            this.send(reply);
        } catch (UnreadableException | IOException e) {
            e.printStackTrace();
        }

        return task_aux;
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

    private class ExecuteTask extends TickerBehaviour {

        private int no_total_ticks;
        public ExecuteTask(Agent a, long period, int distance, int speed) {
            super(a, period);
            no_total_ticks = distance / speed;
        }

        @Override
        protected void onTick() {
            no_total_ticks--;

            if(no_total_ticks <= 0){
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setSender(myAgent.getAID());
                msg.addUserDefinedParameter("AGENT_TYPE", AgentType.PASSENGER_VEHICLE.toString());
                msg.addReceiver(new AID("ControlTower", AID.ISLOCALNAME));
                myAgent.send(msg);
                stop();
            }
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

    public int getSpeed() {
        return this.speed;
    }
}
