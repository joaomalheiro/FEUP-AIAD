package AgentBehaviours;

import Agents.ControlTower;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;

public class LandingTicker extends TickerBehaviour {

    private ControlTower ct;
    boolean f;

    public LandingTicker(Agent a, long period) {
        super(a, period);
        this.ct = (ControlTower) a;
        f = true;

    }

    @Override
    protected void onTick() {
        int tmp = 0;

        System.out.println("LANDINGTICKER: counters -> " + ct.getTransports_available_counter() + " | " + tmp);
        while (ct.getTransports_available_counter() - tmp > 0 && !ct.getAirplanes().isEmpty() && ct.getAirplanes().first().getTimeToTower() == 0) {
            System.out.println("LANDINGTICKER: Airplane " + ct.getAirplanes().first().getLocalName());
            ct.landAirplane(ct.getAirplanes().first());
            tmp++;
        }
    }

}
