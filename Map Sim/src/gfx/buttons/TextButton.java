package gfx.buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public abstract class TextButton extends AbstractGameButton {
	private static final long serialVersionUID = 8925470490592533778L;

	private static final int ENABLED = 0, HOVER = 1, PRESSED = 2, DISABLED = 3;

	private static BufferedImage button_src = null;

	static {
		try {
			button_src = ImageIO.read(new File("gfx/gui/text_buttons.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame();
		frame.setSize(200, 200);
		frame.getContentPane().add(new TextButton("Dogemaster") {
			private static final long serialVersionUID = 8925924232880021957L;

			@Override
			public void whenClicked() {
				// TODO Auto-generated method stub

			}

		});

		frame.setVisible(true);
	}

	private String text;

	private BufferedImage[] drawings;

	protected TextButton(String text) throws IOException {
		super(25, 25);
		this.text = text;
		drawings = new BufferedImage[4];
	}

	protected void firstDraw(Graphics g) {
		super.firstDraw(g);
		if (g == null) {
			g = this.getGraphics();
		}
		FontMetrics met = g.getFontMetrics();

		Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);
		int strWidth = (int) bounds.getWidth();
		int strHeight = (int) bounds.getHeight();
		int strAsc = met.getAscent();

		int myWidth = strWidth + 16;
		int myHeight = strHeight + 16;

		File font = new File("fonts//GARA.TTF");
		
		for (int i = ENABLED; i < DISABLED + 1; i++) {
			drawings[i] = new BufferedImage(myWidth, myHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = drawings[i].createGraphics();
			try {
				graphics.setFont(Font.createFont(Font.TRUETYPE_FONT, font).deriveFont(14.0f));
			} catch (Exception e) {
				// Will use default if not present
				e.printStackTrace();
				
			}
			graphics.drawImage(button_src.getSubimage(0, i * 17, 8, 8), 0, 0, null);
			graphics.drawImage(button_src.getSubimage(9, i * 17, 8, 8), myWidth - 8, 0, null);
			graphics.drawImage(button_src.getSubimage(0, i * 17 + 9, 8, 8), 0, myHeight - 8, null);
			graphics.drawImage(button_src.getSubimage(9, i * 17 + 9, 8, 8), myWidth - 8, myHeight - 8, null);

			for (int j = 0; j < strWidth; j++) {
				graphics.drawImage(button_src.getSubimage(9, i * 17, 1, 8), 8 + j, 0, null);
				graphics.drawImage(button_src.getSubimage(9, i * 17 + 9, 1, 8), 8 + j, myHeight - 8, null);
			}

			for (int j = 0; j < strHeight; j++) {
				graphics.drawImage(button_src.getSubimage(0, i * 17 + 8, 8, 1), 0, 8 + j, null);
				graphics.drawImage(button_src.getSubimage(9, i * 17 + 8, 8, 1), myWidth - 8, 8 + j, null);
			}

			graphics.setColor(new Color(button_src.getRGB(8, i * 17 + 8)));
			graphics.fillRect(8, 8, myWidth - 16, myHeight - 16);
			
			TextLayout textLayout = new TextLayout(text, graphics.getFont(), graphics.getFontRenderContext());

			float width = (float) graphics.getFontMetrics().stringWidth(text);
			float height = (float) textLayout.getBounds().getHeight();

			graphics.setFont(graphics.getFont().deriveFont(Font.PLAIN));
			
			AffineTransform transform = new AffineTransform();
			transform.setToTranslation(8, 8 + height);

			Shape shape = textLayout.getOutline(transform);
			graphics.setStroke(new BasicStroke(2));
			graphics.setColor(Color.BLACK);
			//graphics.draw(shape); // outline
			graphics.setColor(Color.WHITE);
			graphics.fill(shape); // text itself

			/*
			 * graphics.setColor(Color.BLACK); graphics.drawString(text, 8, 8 + strAsc); graphics.setColor(Color.WHITE);
			 * graphics.drawString(text, 8, 8 + strAsc);
			 */
		}

		this.resizeButton(myWidth, myHeight);
	}

	@Override
	protected void drawPressed(Graphics g) {
		g.drawImage(drawings[PRESSED], 0, 0, null);
	}

	@Override
	protected void drawHover(Graphics g) {
		g.drawImage(drawings[HOVER], 0, 0, null);
	}

	@Override
	protected void drawEnabled(Graphics g) {
		g.drawImage(drawings[ENABLED], 0, 0, null);
	}

	@Override
	protected void drawDisabled(Graphics g) {
		g.drawImage(drawings[DISABLED], 0, 0, null);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		firstDraw(null);
	}
}
