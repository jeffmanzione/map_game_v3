package gfx.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public abstract class TimeButton extends JPanel {

	private static final long serialVersionUID = 552964127664724979L;

	private BufferedImage pauseUp, pauseHover, pauseDown, goUp, goHover, goDown;

	private boolean paused = true, hover = false, pressed = false;

	public TimeButton() throws IOException {
		setOpaque(false);
		
		pauseUp = ImageIO.read(new File("gfx/gui/pause_up.png"));
		pauseHover = ImageIO.read(new File("gfx/gui/pause_hover.png"));
		pauseDown = ImageIO.read(new File("gfx/gui/pause_down.png"));
		goUp = ImageIO.read(new File("gfx/gui/start_up.png"));
		goHover = ImageIO.read(new File("gfx/gui/start_hover.png"));
		goDown = ImageIO.read(new File("gfx/gui/start_down.png"));

		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				click();
			}

			public void mouseEntered(MouseEvent e) {
				hover = true;
				repaint();
			}

			public void mouseExited(MouseEvent e) {
				hover = false;
				repaint();
			}

			public void mousePressed(MouseEvent e) {
				pressed = true;
				repaint();
			}

			public void mouseReleased(MouseEvent e) {
				pressed = false;
				repaint();
			}
		});
		
		setPreferredSize(new Dimension(pauseUp.getWidth(), pauseUp.getHeight()));
	}

	public void click() {
		if (paused) {
			unpause();
		} else {
			pause();
		}

		paused = !paused;
		repaint();
	}


	public abstract void unpause();
	public abstract void pause();

	public void paintComponent(Graphics g) {
		if (!paused) {
			if (pressed) {
				g.drawImage(pauseDown, 0, -2, null);
			} else if (hover) {
				g.drawImage(pauseHover, 0, -2, null);
			} else {
				g.drawImage(pauseUp, 0, 0, null);
			}
		} else {
			if (pressed) {
				g.drawImage(goDown, 0, -2, null);
			} else if (hover) {
				g.drawImage(goHover, 0, -2, null);
			} else {
				g.drawImage(goUp, 0, -2, null);
			}
		}
		
		int width = this.getPreferredSize().width;
		int height = this.getPreferredSize().height;
		int interval = width / 5;
		int startHeight = height - 5;
		int startInterval = interval / 2;
		
		g.setColor(Color.BLACK);
		g.fillRect(startInterval - 1, startHeight - 1, interval * 4 + 6, 6);
		
		for (int i = 1; i < 6; i++) {
			if (i <= ticks) {
				g.setColor(Color.GREEN);
			} else {
				g.setColor(Color.GRAY);
			}

			g.fillRect(startInterval, startHeight, 4, 2);
			
			startInterval += interval;
		}
		
		
	}
	
	private int ticks = 1;

	public void subTick() {
		if (ticks > 0) {
			ticks--;
		}
	}

	public void addTick() {
		if (ticks < 5) {
			ticks++;
		}
	}
}
