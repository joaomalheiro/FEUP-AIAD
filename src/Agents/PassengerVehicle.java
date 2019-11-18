package Agents;

import AgentBehaviours.ListeningPassVehicleBehaviour;
import AgentBehaviours.TransportationTaskExecution;
import AuxiliarClasses.AgentType;
import AuxiliarClasses.TransportTask;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import jade.lang.acl.UnreadableException;

import java.io.IOException;

public class PassengerVehicle extends Agent {

    private int id;
    private int capacity;
    private TransportTask current_task = null;

    private boolean confirmed_task;
    private int speed; //  meters/tick

    public PassengerVehicle(int id, int capacity, int speed) {
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

        addBehaviour(new ListeningPassVehicleBehaviour(this));
        addBehaviour(new TransportationTaskExecution(this, 250));

    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getSpeed() {
        return this.speed;
    }

    public TransportTask getTask() {
        return this.current_task;
    }

    public void setCurrent_task(TransportTask t) {
        this.current_task = t;
    }

    public boolean isConfirmed_task() {
        return confirmed_task;
    }

    public void setConfirmed_task(boolean confirmed_task) {
        this.confirmed_task = confirmed_task;
    }


/*
    private class ListeningPassengerVehicleBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if (current_task == null && msg != null && !msg.getContent().equals("Got your message!")) {

                if (msg.getPerformative() == ACLMessage.REQUEST && msg.getSender().getLocalName().equals("ControlTower"))
                    handleNewTaskFromControlTower(msg);
            } else {
                block();
            }
        }
    }

    private void handleNewTaskFromControlTower(ACLMessage msg) {

        System.out.println("[T]" + this.getLocalName() + " - NEW Request" );

        this.current_task = acceptNewIndividualTask(msg);

        // Task is going to be executed (each tick is 1 s = 1000 ms
        addBehaviour(new ExecuteTask(this, 1000, current_task.getDrive_distance(), this.getSpeed()));

        // DEBUG
        // System.out.println("ACCEPTED: " + this.current_task.getAirplane_name() + " By" + this.getLocalName());
        return;


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

    private class ExecuteTask extends TickerBehaviour {

        private int no_total_ticks;

        public ExecuteTask(Agent a, long period, int distance, int speed) {
            super(a, period);
            no_total_ticks = distance / speed;
        }

        @Override
        protected void onTick() {
            no_total_ticks--;

            //System.out.println("TICKS :: " + no_total_ticks + " - " + myAgent.getLocalName());
            if (no_total_ticks <= 0) {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setSender(myAgent.getAID());
                msg.addUserDefinedParameter("AGENT_TYPE", AgentType.PASSENGER_VEHICLE.toString());
                msg.addReceiver(new AID("ControlTower", AID.ISLOCALNAME));
                msg.setContent("Finished my task");
                myAgent.send(msg);
                //System.out.println("Finish THE task " + myAgent.getLocalName());
                stop();
            }
        }
    }*/




        /* else {
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
        } */

    /* private void executeTransportTask(TransportTask task) {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);

        try {
            msg.setContentObject(task);
        } catch (IOException e) {
            e.printStackTrace();
        }

        addBehaviour(new TaskPreparation(this, msg));
    } */

    /*
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
                reply.setContentObject(Need to return the evaluator responsenull);
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
    }*/


}
