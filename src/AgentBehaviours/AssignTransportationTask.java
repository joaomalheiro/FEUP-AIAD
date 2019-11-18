package AgentBehaviours;

import Agents.ControlTower;
import AuxiliarClasses.AirplaneInfo;
import AuxiliarClasses.TransportTask;
import AuxiliarClasses.TransportVehicleAvailability;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.Map;

public class AssignTransportationTask extends OneShotBehaviour {

    private int max_value;
    private int counter = 0;
    private AirplaneInfo info;

    public AssignTransportationTask(Agent a, AirplaneInfo info, int max_value) {
        super(a);
        this.info = info;
        this.max_value = max_value;
    }

    @Override
    public void action() {

        while(!assignNewTransportTask(info) && counter <= max_value){
            counter++;
            System.out.println("FAILURE: No transports available, repeating");
        }


    }

    private boolean assignNewTransportTask(AirplaneInfo info) {

        // TODO
        // Have a drive distance list
        System.out.println("CONTROLTOWER: Assigning new passenger transportation task");
        TransportTask task = new TransportTask(info.getLocalName(), info.getPassengers(), 30);

        // GUIDE
        // - Create new task
        // - Create ACLMessage
        // - Gather all vehicles that need to be contacted
        // - Contact all vehicles
        // - Handle the answers
        // - Handling is now choosing the first one to answer

        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        msg.setSender(myAgent.getAID());
        msg.addUserDefinedParameter("AGENT_TYPE", "CONTROLTOWER");
        try {
            msg.setContentObject(task);
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
