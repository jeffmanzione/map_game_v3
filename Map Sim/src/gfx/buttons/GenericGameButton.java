package gfx.buttons;

import gfx.Action;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GenericGameButton extends AbstractGameButton {
	private static final long serialVersionUID = 8926429400512428752L;

	private BufferedImage up, down, hov, disabled;

	private final Action act;
	
	public GenericGameButton(BufferedImage up, BufferedImage down, BufferedImage hov, BufferedImage disabled,
			final Action act) throws IOException {
		super(up.getWidth(), up.getHeight());
		this.act = act;
		this.up = up;
		this.down = down;
		this.hov = hov;
		this.disabled = disabled;
	}

	public void whenClicked() {
		act.perform();
	}

	protected void drawPressed(Graphics g) {
		g.drawImage(down, 0, 0, null);
	}
	
	protected void drawHover(Graphics g) {
		g.drawImage(hov, 0, 0, null);
	}
	
	protected void drawEnabled(Graphics g) {
		g.drawImage(up, 0, 0, null);
	}
	
	protected void drawDisabled(Graphics g) {
		g.drawImage(disabled, 0, 0, null);
	}

	public void paintComponent(Graphics g) {
		drawButton(g);
	}

	private static BufferedImage up_down_buttons = null;

	static {
		try {
			up_down_buttons = ImageIO.read(new File("gfx/gui/inc_dec_buttons.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static GenericGameButton createUpButton(final Action act) throws IOException {
		return new GenericGameButton(up_down_buttons.getSubimage(0, 0, 30, 20), up_down_buttons.getSubimage(60, 0, 30,
				20), up_down_buttons.getSubimage(30, 0, 30, 20), up_down_buttons.getSubimage(90, 0, 30, 20), act);
	}

	public static GenericGameButton createDownButton(final Action act) throws IOException {
		return new GenericGameButton(up_down_buttons.getSubimage(0, 20, 30, 20), up_down_buttons.getSubimage(60, 20,
				30, 20), up_down_buttons.getSubimage(30, 20, 30, 20), up_down_buttons.getSubimage(90, 20, 30, 20), act);
	}

}
