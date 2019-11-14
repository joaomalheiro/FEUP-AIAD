package Environment;

import Agents.Airplane;
import Agents.Company;
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
	public static ContainerController agentContainer;
	private int N_AIRPLANES_PER_COMPANY = 2;

	protected void launchJADE() throws StaleProxyException {
		
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
			Company cp = new Company(1000);
			agentContainer.acceptNewAgent("Ryanair" , cp).start();

		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
    }

    public static void main(String[] args) throws StaleProxyException {
		new JadeServiceLaucher().launchJADE();
	}

}