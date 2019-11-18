package AgentBehaviours;

import Agents.ControlTower;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class LandingTicker extends TickerBehaviour {

    private ControlTower ct;
    public LandingTicker(Agent a, long period) {
        super(a, period);
        this.ct = (ControlTower) a;

    }
    @Override
    protected void onTick() {
        if(ct.getTransports_available_counter() > 0 && !ct.getAirplanes().isEmpty() && ct.getAirplanes().first().getTimeToTower() == 0)
            ct.landAirplane(ct.getAirplanes().first());
    }
}
