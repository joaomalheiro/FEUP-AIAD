package Environment;

import AgentBehaviours.CompanyPriorityStrategy;
import Agents.Company;
import Agents.ControlTower;
import Agents.PassengerVehicle;
import gui.AirportGUI;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class JadeServiceLaucher {

	public static final boolean SEPARATE_CONTAINERS = false;

	private ContainerController mainContainer;
	public static ContainerController agentContainer;
	private int N_AIRPLANES_PER_COMPANY = 2;

	protected void launchJADE(int scenario) throws StaleProxyException {
		
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);
		
		if(SEPARATE_CONTAINERS) {
			Profile p2 = new ProfileImpl();
			agentContainer = rt.createAgentContainer(p2);
		} else {
			agentContainer = mainContainer;
		}

		ControlTower ct = launchAgents(scenario);
		launchGUI(ct);
		/*try (PrintWriter writer = new PrintWriter(new File("Information.csv"))) {

			StringBuilder sb = new StringBuilder();
			sb.append("id");
			sb.append(',');
			sb.append("fuel");
			sb.append(',');
			sb.append("passengers");
			sb.append(',');
			sb.append("planesAtThatTime");
			sb.append(',');
			sb.append("priority");
			sb.append(',');
			sb.append("timeToArrive");
			sb.append(',');
			sb.append("timeWaited");
			sb.append('\n');

			writer.write(sb.toString());

			System.out.println("Csv Created!");

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}*/
	}
	
	protected void launchGUI(ControlTower ct) {
		AirportGUI airport = new AirportGUI(ct);
		ct.setAvailability();
	}

    protected ControlTower launchAgents(int scenario) {

		try {

			ControlTower controlTower = new ControlTower();
			mainContainer.acceptNewAgent("ControlTower", controlTower).start();

			for(int i = 0; i < 3; i++){
				PassengerVehicle pv = new PassengerVehicle(20 + i, 30, 10);
				agentContainer.acceptNewAgent("Vehicle" + (i+1), pv).start();
			}

			switch (scenario){
				case 1:
					Company cp = new Company(1000, CompanyPriorityStrategy.Strategy.RANDOM);
					agentContainer.acceptNewAgent("Ryanair" , cp).start();
					Company cp2 = new Company(1000, CompanyPriorityStrategy.Strategy.SMART);
					agentContainer.acceptNewAgent("TAP" , cp2).start();
					Company cp3 = new Company(1000, CompanyPriorityStrategy.Strategy.MEDIUM);
					agentContainer.acceptNewAgent("Luftansa" , cp3).start();
					break;
				case 2:
					Company cp27 = new Company(1000, CompanyPriorityStrategy.Strategy.SMART);
					agentContainer.acceptNewAgent("AirAsia" , cp27).start();
					Company cp28 = new Company(1000, CompanyPriorityStrategy.Strategy.SMART);
					agentContainer.acceptNewAgent("QatarAirways" , cp28).start();
					Company cp29 = new Company(1000, CompanyPriorityStrategy.Strategy.SMART);
					agentContainer.acceptNewAgent("SingapureAirlines" , cp29).start();
                    Company cp21 = new Company(1000, CompanyPriorityStrategy.Strategy.RANDOM);
                    agentContainer.acceptNewAgent("Ryanair" , cp21).start();
                    Company cp22 = new Company(1000, CompanyPriorityStrategy.Strategy.RANDOM);
                    agentContainer.acceptNewAgent("TAP" , cp22).start();
                    Company cp23 = new Company(1000, CompanyPriorityStrategy.Strategy.RANDOM);
                    agentContainer.acceptNewAgent("Luftansa" , cp23).start();
                    Company cp24 = new Company(1000, CompanyPriorityStrategy.Strategy.MEDIUM);
                    agentContainer.acceptNewAgent("EasyGet" , cp24).start();
                    Company cp25 = new Company(1000, CompanyPriorityStrategy.Strategy.MEDIUM);
                    agentContainer.acceptNewAgent("AmericanAirlines" , cp25).start();
                    Company cp26 = new Company(1000, CompanyPriorityStrategy.Strategy.MEDIUM);
                    agentContainer.acceptNewAgent("Emirates" , cp26).start();

					break;
				case 3:
					break;
				case 4:
					break;
				default:

			}

			return controlTower;

		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		return null;
    }

    public static void main(String[] args) throws StaleProxyException {
		new JadeServiceLaucher().launchJADE(Integer.parseInt(args[0]));
	}

}