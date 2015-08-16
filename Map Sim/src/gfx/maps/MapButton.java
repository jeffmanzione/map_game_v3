package gfx.maps;

import gfx.Action;
import gfx.buttons.GenericGameButton;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MapButton extends GenericGameButton {
	private static final long serialVersionUID = -5543224843530487495L;

	private BufferedImage icon;

	public MapButton(BufferedImage icon, Action act) throws IOException {

		super(ImageIO.read(new File("gfx/gui/map_up.png")), ImageIO.read(new File("gfx/gui/map_down.png")), ImageIO
				.read(new File("gfx/gui/map_hover.png")), ImageIO.read(new File("gfx/gui/map_disabled.png")), act);
		this.icon = icon;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(icon, (width() - icon.getWidth()) / 2, (height() - icon.getHeight()) / 2, null);

	}

}
