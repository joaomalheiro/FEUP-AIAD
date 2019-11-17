package gui;

import javax.swing.*;

import Agents.ControlTower;

import java.awt.*;
import java.awt.event.*;

public class AirportGUI extends JPanel {
	
	private ControlTower controlTower;
	private JFrame frame;
	private AirportPanel panel;

	public AirportPanel getPanel() {
		return panel;
	}
	
	public AirportGUI(ControlTower ct)  {
		
		controlTower = ct;
		initializeFrame();
		initializePanel();
		
		this.controlTower.setGui(this);
	}
	
	private void initializeFrame() {
		
		frame = new JFrame("Airport");
		frame.setBounds(100, 100, 1000, 800);
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
		vehiclePanel.setBounds(700, 200, 160, 96);
		vehiclePanel.setVisible(true);
		vehiclePanel.setFocusable(true);
		frame.add(vehiclePanel);

	}
}
