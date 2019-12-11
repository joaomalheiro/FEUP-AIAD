package AgentBehaviours;

import Agents.ControlTower;
import AuxiliarClasses.AirplaneInfo;
import AuxiliarClasses.TransportTask;
import AuxiliarClasses.TransportVehicleAvailability;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.Map;

public class AssignTransportationTask extends TickerBehaviour {

    private int max_value;
    private int counter = 0;
    private TransportTask task;

    public AssignTransportationTask(Agent a, TransportTask task, int max_value) {
        super(a, 500);
        this.max_value = max_value;
        this.task = task;
    }

    @Override
    public void onTick() {
        counter++;
        System.out.println("Attempt to assign the task no. " + counter + " airplane " + task.getAirplane_name());

        if(assignNewTransportTask()){
            System.out.println("AssignTransportationTask: Found a FREE transport");
            stop();
        }
    }

    private boolean assignNewTransportTask() {

        // TODO
        // Have a drive distance list
        System.out.println("CONTROLTOWER: Assigning new passenger transportation task");

        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        msg.setSender(myAgent.getAID());
        msg.addUserDefinedParameter("AGENT_TYPE", "CONTROLTOWER");
        try {
            msg.setContentObject(this.task);
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean found = false;
        for (Map.Entry<String, TransportVehicleAvailability> entry : ((ControlTower) myAgent).getPassenger_vehicles_availability().entrySet()) {
            String k = entry.getKey();
            TransportVehicleAvailability v = entry.getValue();

            if (!v.equals(TransportVehicleAvailability.FREE))
                continue;

            // DEBUG
            System.out.println("CONTROLTOWER: Found an available vehicle");
            ((ControlTower) myAgent).getPassenger_vehicles_availability().put(k, TransportVehicleAvailability.WAITING_REPLY);
            found = true;
            AID aux = new AID(k, AID.ISLOCALNAME);
            msg.addReceiver(aux);
            break;
        }

        if (found)
            myAgent.send(msg);

        return found;

    }



}
