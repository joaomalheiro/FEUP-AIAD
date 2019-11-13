package AgentBehaviours;

import Agents.PassengerVehicle;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class PassengerVehicleStartWork extends OneShotBehaviour {
    public PassengerVehicleStartWork(PassengerVehicle vehicle) { super(vehicle); }

    @Override
    public void action() {
        PassengerVehicle vehicle = (PassengerVehicle) getAgent();
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addUserDefinedParameter("AGENT_TYPE", "PASSENGERVEHICLE");
        msg.addReceiver(new AID("ControlTower", AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setContent("START " + vehicle.getID());
        vehicle.send(msg);
    }
}
