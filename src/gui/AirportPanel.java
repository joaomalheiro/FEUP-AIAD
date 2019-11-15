package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class AirportPanel extends JPanel {
	
	private HashMap<String, BufferedImage> loadedImages;
	
	AirportPanel() {
        loadedImages = new HashMap<>();
        try {
            loadedImages.put("background", ImageIO.read(new File("images/background.png")));
            loadedImages.put("controlTower", ImageIO.read(new File("images/controltower.png")));
            loadedImages.put("airplane", ImageIO.read(new File("images/airplane.png")));
        } catch(IOException e) {
            e.printStackTrace();
        }
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Character[][] map = new Character[50][50];
        for (Character[] row: map)
            Arrays.fill(row, '*');
        
        map[15][15] = 'C';
        drawBackground(g, map);
        drawAgents(g, map);
    }
	
	private void drawBackground(Graphics g, Character[][] map) {
		System.out.println(map.length);
        for (int i = 0; i < map.length; ++i)
            for (int j = 0; j < map[i].length; ++j) {
                g.drawImage(loadedImages.get("background"), j * 8, i * 8, null);
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
		  		g.drawImage(loadedImages.get("controlTower"), j * 8, i * 8, null);
		  		break;
			case 'A':
		  		g.drawImage(loadedImages.get("airplane"), j * 8, i * 8, null);
		  		break;
		  }
	}
}
