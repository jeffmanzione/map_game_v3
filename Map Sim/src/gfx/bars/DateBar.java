package gfx.bars;

import gfx.buttons.GenericGameButton;
import gfx.buttons.TimeButton;
import gfx.maps.MapComponent;
import gfx.Action;

import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import time.TimeManager;

public class DateBar extends MapComponent {
	private static final long serialVersionUID = 504689551094230729L;

	private String date;

	private GenericGameButton slow, fast;
	private TimeButton pause;

	private final TimeManager time;

	public DateBar(TimeManager tm) throws IOException {
		super(ImageIO.read(new File("gfx/gui/date_bar_skin.png")), 390, 100);

		time = tm;

		time.setDateBar(this);

		pause = new TimeButton() {
			private static final long serialVersionUID = 1L;

			public void unpause() {
				time.unpause();
			}

			public void pause() {
				time.pause();
			}

		};

		slow = new GenericGameButton(ImageIO.read(new File("gfx/gui/rw_up.png")), ImageIO.read(new File(
				"gfx/gui/rw_down.png")), ImageIO.read(new File("gfx/gui/rw_hover.png")), ImageIO.read(new File(
				"gfx/gui/map_up.png")), new Action() {
			public void perform() {
				time.slow();
				pause.subTick();
			}
		});

		fast = new GenericGameButton(ImageIO.read(new File("gfx/gui/ff_up.png")), ImageIO.read(new File(
				"gfx/gui/ff_down.png")), ImageIO.read(new File("gfx/gui/ff_hover.png")), ImageIO.read(new File(
				"gfx/gui/map_up.png")), new Action() {
			public void perform() {
				time.fast();
				pause.addTick();
			}
		});

		slow.setBounds(8, 3, slow.getPreferredSize().width, slow.getPreferredSize().height);
		pause.setBounds(48, 3, pause.getPreferredSize().width, pause.getPreferredSize().height);
		fast.setBounds(88, 3, fast.getPreferredSize().width, fast.getPreferredSize().height);

		add(pause);
		add(slow);
		add(fast);
	}

	public void setDate(String addDay) {
		date = addDay;
		repaint();
	}

	public void togglePause() {
		pause.click();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setFont(g.getFont().deriveFont(Font.BOLD));
		g.drawString(date, 148, 16);
	}
}
