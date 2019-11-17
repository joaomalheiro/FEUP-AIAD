package gui;

import Agents.ControlTower;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PassengerVehiclePanel extends JPanel {

    private HashMap<String, BufferedImage> loadedImages;
    private ControlTower controlTower;

    PassengerVehiclePanel(ControlTower ct) {
        controlTower = ct;
        loadedImages = new HashMap<>();
        try {
            loadedImages.put("backgroundPassenger", ImageIO.read(new File("images/backgroundPassenger.png")));
            loadedImages.put("controlTower", ImageIO.read(new File("images/controltower.png")));
            loadedImages.put("airplane", ImageIO.read(new File("images/airplane.png")));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g, controlTower.getVehicleMap());
        drawAgents(g, controlTower.getVehicleMap());
    }

    private void drawBackground(Graphics g, Character[][] map) {
        for (int i = 0; i < map.length; ++i)
            for (int j = 0; j < map[i].length; ++j) {
                g.drawImage(loadedImages.get("backgroundPassenger"), j * 32, i * 32, null);
            }
    }

    private void drawAgents(Graphics g, Character[][] map) {
        for (int i = 0; i < map.length; ++i)
            for (int j = 0; j < map[i].length; ++j){
                chooseImage(g, map[i][j], i, j);
            }
    }

    private void chooseImage(Graphics g, Character character, int i, int j) {
        switch(character) {
            case 'C':
                g.drawImage(loadedImages.get("controlTower"), j * 16, i * 16, null);
                break;
            case 'A':
                g.drawImage(loadedImages.get("airplane"), j * 16, i * 16, null);
                break;
        }
    }
}