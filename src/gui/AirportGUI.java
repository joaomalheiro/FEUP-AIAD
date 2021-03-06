package gui;

import javax.swing.*;

import Agents.ControlTower;
import Agents.PassengerVehicle;

import java.awt.*;
import java.awt.event.*;

public class AirportGUI extends JPanel {
	
	private ControlTower controlTower;
	private JFrame frame;
	private AirportPanel panel;
	private PassengerVehiclePanel vehiclePanel;
	private JLabel planeLabel;
	private JLabel passengerLabel;

	public AirportPanel getPanel() {
		return panel;
	}

	public PassengerVehiclePanel getVehiclePanel() {
		return vehiclePanel;
	}

	public AirportGUI(ControlTower ct)  {
		
		controlTower = ct;
		initializeFrame();
		initializeLabels();
		initializePanel();
		initializePassengerVehiclePanel();

		panel.repaint();
		
		this.controlTower.setGui(this);
	}

	private void initializeLabels() {

		planeLabel = new JLabel("Airplanes landing");
		planeLabel.setBounds(300, 450, 200, 20);
		planeLabel.setForeground(Color.white);
		frame.add(planeLabel);

		passengerLabel = new JLabel("Passenger Transport");
		passengerLabel.setBounds(800, 450, 200, 20);
		passengerLabel.setForeground(Color.white);
		frame.add(passengerLabel);
	}
	
	private void initializeFrame() {
		
		frame = new JFrame("Airport");
		frame.setBounds(100, 100, 1100, 800);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.getContentPane().setBackground(new Color(32,32,32));
	}
	
	private void initializePanel() {
		//game box
		panel = new AirportPanel(controlTower);
		panel.setBounds(50, 50, 640, 352);
		panel.setVisible(true);
		panel.setFocusable(true);
		frame.add(panel);
	}

	private void initializePassengerVehiclePanel(){

		vehiclePanel = new PassengerVehiclePanel(controlTower);
		vehiclePanel.setBounds(700, 200, 320, 96);
		vehiclePanel.setVisible(true);
		vehiclePanel.setFocusable(true);
		frame.add(vehiclePanel);

	}
}
