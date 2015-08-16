package gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JToolTip;

public class CustomToolTip extends JToolTip {
	private static final long serialVersionUID = 683869049831995446L;
	
	public CustomToolTip() {
		
	}
	
	private String[] texts;
	private Color[] colors;
	private Font[] fonts;
	
	public void setTipText(String[] texts, Color[] colors, Font[] fonts) {
		this.texts = texts;
		this.colors = colors;
		this.fonts = fonts;
	}
	
	public void paintComponent(Graphics g) {
		int width = 0;
		for (int i = 0; i < texts.length; i++) {
			g.setColor(colors[i]);
			g.setFont(fonts[i]);
			FontMetrics context = g.getFontMetrics();
			
			g.drawString(texts[i], width, 8);
			
			width += (int) context.getStringBounds(texts[i], g).getWidth();

		}
	}

}
