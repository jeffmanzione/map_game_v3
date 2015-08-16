package gfx.maps;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public abstract class MapComponent extends JPanel {
	private static final long serialVersionUID = -3620716402931526434L;

	private BufferedImage skin = null;

	public MapComponent(BufferedImage skin, int width, int height) {
		setSize(width, height);
		setPreferredSize(new Dimension(width, height));

		this.skin = skin;

		this.setOpaque(false);
		
		setLayout(null);
		
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(skin, 0, 0, null);
	}
}
