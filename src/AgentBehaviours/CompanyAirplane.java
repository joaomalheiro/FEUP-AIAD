package AgentBehaviours;

import Agents.Airplane;
import Agents.Company;
import Environment.JadeServiceLaucher;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.StaleProxyException;

public class CompanyAirplane extends TickerBehaviour {
    private Company company;
    private int idCounter = 1;
    public CompanyAirplane(Company company, long period) {
        super(company, period);
        this.company = company;
    }

    @Override
    protected void onTick() {
        Airplane ap = new Airplane(company.getLocalName() + idCounter + " " + idCounter + " 20 40 35 12");
        try {
            JadeServiceLaucher.agentContainer.acceptNewAgent(company.getLocalName() + idCounter, ap).start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
        idCounter++;
        company.addAirplane(ap);
        System.out.println(ap);
    }
}
