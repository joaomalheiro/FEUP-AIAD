package gui;

import javax.swing.*;

import Agents.ControlTower;

import java.awt.*;
import java.awt.event.*;

public class AirportGUI extends JPanel {
	
	private ControlTower controlTower;
	private JFrame frame;
	private AirportPanel panel;
	
	public AirportGUI(ControlTower ct)  {
		
		controlTower = ct;
		initializeFrame();
		initializePanel();
		
		while(true) {
			panel.repaint();
			panel.setFocusable(true);
			panel.requestFocusInWindow();
		}
	}
	
	private void initializeFrame() {
		
		frame = new JFrame("Airport");
		frame.setBounds(100, 100, 640, 530);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.getContentPane().setBackground(new Color(32,32,32));
	}
	
	private void initializePanel() {
		//game box
		panel = new AirportPanel();
		panel.setBounds(50, 50, 360, 400);
		panel.setVisible(true);
		panel.setFocusable(true);
		frame.add(panel);
	}
}
