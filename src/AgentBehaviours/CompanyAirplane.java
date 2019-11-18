package AgentBehaviours;

import Agents.Airplane;
import Agents.Company;
import Environment.JadeServiceLaucher;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.StaleProxyException;

import java.util.Random;

public class CompanyAirplane extends TickerBehaviour {
    private Company company;
    private int idCounter = 1;
    public CompanyAirplane(Company company, long period) {
        super(company, period);
        this.company = company;
    }

    @Override
    protected void onTick() {
        Airplane ap = generateRandomPlane();
        try {
            JadeServiceLaucher.agentContainer.acceptNewAgent(company.getLocalName() + idCounter, ap).start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        company.addAirplane(ap);
       // System.out.println(ap);
    }

    private Airplane generateRandomPlane(){
        Random rand = new Random();

        int timeToTower = rand.nextInt(20) + 10;
        int fuelAmount = rand.nextInt(20) + timeToTower + 5;

        idCounter++;
        return new Airplane(company.getLocalName() + idCounter + " " + idCounter + " " + fuelAmount + " " + 40 + " " + 40 + " " + timeToTower);
    }
}
