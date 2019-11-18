package AgentBehaviours;

import Agents.PassengerVehicle;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

import static java.lang.Math.round;

public class TransportationTaskExecution extends TickerBehaviour {

    private boolean executing_task = false;
    private boolean task_is_over = false;
    private int task_time = 0;

    public TransportationTaskExecution(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {

        if(((PassengerVehicle) myAgent).getTask() != null && task_time == 0) {
            task_time = round(((PassengerVehicle) myAgent).getTask().getDrive_distance() / ((PassengerVehicle) myAgent).getSpeed() * (1000 / getPeriod())) ;
            System.out.println( myAgent.getLocalName() + ": Calculated task time " + task_time + "ticks for " + ((PassengerVehicle) myAgent).getTask().getAirplane_name() );
        }

        if(((PassengerVehicle) myAgent).getTask() != null && ((PassengerVehicle) myAgent).isConfirmed_task()){
            System.out.println( myAgent.getLocalName() + ": Executing task for " + ((PassengerVehicle) myAgent).getTask().getAirplane_name() );
            executing_task = true;
            task_is_over = executeTask();
        }

        if(task_is_over) {
            System.out.println( myAgent.getLocalName() + ": Terminated task for " + ((PassengerVehicle) myAgent).getTask().getAirplane_name() );
            executing_task = false;
            task_time = 0;
            informControlTower();

            //Reset values
            ((PassengerVehicle) myAgent).setCurrent_task(null);
            ((PassengerVehicle) myAgent).setConfirmed_task(false);
            task_is_over = false;
        }
    }

    private boolean executeTask() {
        task_time--;
        return task_time == 0;
    }

    private void informControlTower() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("ControlTower", AID.ISLOCALNAME));
        msg.setSender(myAgent.getAID());
        msg.addUserDefinedParameter("AGENT_TYPE", "PASSENGER_VEHICLE");
        try {
            msg.setContentObject(((PassengerVehicle) myAgent).getTask());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("PASSENGER_VEHICLE: Informing Control Tower of task end");
        myAgent.send(msg);
    }
}
