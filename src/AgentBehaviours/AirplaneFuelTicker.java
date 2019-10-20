package AgentBehaviours;

import Agents.Airplane;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class AirplaneFuelTicker extends TickerBehaviour {

    private Airplane airplane;

    public AirplaneFuelTicker(Airplane airplane, long period) {
        super(airplane, period);
        this.airplane = airplane;
    }

    @Override
    protected void onTick() {
        this.airplane.decrementFuel();
    }
}
