import jade.core.*;
import jade.core.behaviours.Behaviour;

public class ControlTower extends Agent{
    public void setup() {
        System.out.println("Hello world!");
    }
    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

    public class OverbearingBehaviour extends Behaviour {
        public void action() {
            while (true) {
                // do something
            }
        }
        public boolean done() {
            return true;
        }
    }
}
