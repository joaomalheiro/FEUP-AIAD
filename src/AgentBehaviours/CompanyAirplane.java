package AgentBehaviours;

import Agents.Airplane;
import Agents.Company;
import Environment.JadeServiceLaucher;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.StaleProxyException;

public class CompanyAirplane extends TickerBehaviour {
    private Company company;
    public CompanyAirplane(Company company, long period) {
        super(company, period);
        this.company = company;
    }

    @Override
    protected void onTick() {
        Airplane ap = new Airplane(company.getLocalName() + (company.getAirplanes().size() + 1) + " " + company.getAirplanes().size() + " 20 40 35 12");
        try {
            JadeServiceLaucher.agentContainer.acceptNewAgent(company.getLocalName() + (company.getAirplanes().size() + 1), ap).start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
        company.addAirplane(ap);
        System.out.println(ap);
    }
}
