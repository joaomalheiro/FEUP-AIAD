package AgentBehaviours;

import Agents.PassengerVehicle;
import AuxiliarClasses.AgentType;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class PassengerVehicleReadyToTower extends OneShotBehaviour {

    public PassengerVehicleReadyToTower(PassengerVehicle vehicle) { super(vehicle); }

    @Override
    public void action() {
        PassengerVehicle vehicle = (PassengerVehicle) getAgent();
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addUserDefinedParameter("AGENT_TYPE", AgentType.PASSENGER_VEHICLE.toString());
        msg.addReceiver(new AID("ControlTower", AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setContent("READY " + vehicle.getID());
        vehicle.send(msg);
    }
}
