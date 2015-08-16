package gfx.buttons;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JPanel;

public abstract class AbstractGameButton extends JPanel {
	private static final long serialVersionUID = 424837871590295709L;

	private boolean hover = false, pressed = false, enabled = true, firstDraw = true;

	private int width, height;

	protected AbstractGameButton(int width, int height) throws IOException {
		this.width = width;
		this.height = height;
		setUp();
	}

	private void setUp() {
		setOpaque(false);
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

		setPreferredSize(new Dimension(width, height));

	}

	protected void resizeButton(int width, int height) {
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width, height));
	}
	
	private void click() {
		if (enabled) {
			whenClicked();
			repaint();
		}
	}

	public void enable() {
		enabled = true;
	}

	public void disable() {
		enabled = false;
	}

	public abstract void whenClicked();

	protected abstract void drawPressed(Graphics g);

	protected abstract void drawHover(Graphics g);

	protected abstract void drawEnabled(Graphics g);

	protected abstract void drawDisabled(Graphics g);

	public void drawButton(Graphics g) {
		if (firstDraw) {
			firstDraw(g);
		}
		if (isEnabled()) {
			if (isPressed()) {
				drawPressed(g);
			} else if (isHover()) {
				drawHover(g);
			} else {
				drawEnabled(g);
			}
		} else {
			drawDisabled(g);
		}

	}

	protected void firstDraw(Graphics g) {
		firstDraw = false;
	}

	public void paintComponent(Graphics g) {
		drawButton(g);
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public boolean isHover() {
		return hover;
	}

	public boolean isPressed() {
		return pressed;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
