package Environment;

import Agents.Airplane;
import Agents.ControlTower;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class JadeServiceLaucher {

	public static final boolean SEPARATE_CONTAINERS = false;

	private ContainerController mainContainer;
	private ContainerController agentContainer;
	private int N_AIRPLANES_PER_COMPANY = 2;

	protected void launchJADE() {
		
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);
		
		if(SEPARATE_CONTAINERS) {
			Profile p2 = new ProfileImpl();
			agentContainer = rt.createAgentContainer(p2);
		} else {
			agentContainer = mainContainer;
		}
		
		launchAgents();
	}

    protected void launchAgents() {

		try {

			ControlTower controlTower = new ControlTower();
			mainContainer.acceptNewAgent("ControlTower", controlTower).start();

			// Ryanair airplanes
			for(int i = 0; i < N_AIRPLANES_PER_COMPANY; i++) {
				Airplane ap = new Airplane("Ryanair" + (i + 1) + " " + i + " 3 4 5 12");
				agentContainer.acceptNewAgent("Ryanair" + i, ap).start();
			}

			// TAP airplanes
			for(int i = 0; i < N_AIRPLANES_PER_COMPANY; i++) {
				Airplane ap = new Airplane("Tap" + (i + 1) + " " + i + " 13 14 15 20");
				agentContainer.acceptNewAgent("Tap" + i, ap).start();
			}

		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
    }

    public static void main(String[] args) {
		new JadeServiceLaucher().launchJADE();
	}

}