package gfx.maps;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;

import maps.GameMap;
import gfx.Action;

public class MiniMap extends MapComponent {
	private static final long serialVersionUID = 322931591199381528L;

	private GameMap map;

	private BufferedImage tmp, current;
	private Graphics2D graphics, currentGraphics;

	private int startWidth = 0, startHeight = 0, endWidth = 0, endHeight = 0;

	private MapButton hybrid, region, terrain, structures;

	public static final int DRAW_WIDTH = 248, DRAW_HEIGHT = 160;

	public MiniMap(final GameMap map) throws IOException {
		super(ImageIO.read(new File("gfx/gui/minimap_skin.png")), 266, 210);

		this.map = map;

		// BufferedImage mainImage = map.drawMap(8);

		// img = mainImage.getScaledInstance(DRAW_WIDTH, DRAW_HEIGHT, Image.SCALE_SMOOTH);

		tmp = new BufferedImage(map.map_width, map.map_height, BufferedImage.TYPE_4BYTE_ABGR);
		graphics = tmp.createGraphics();
		current = new BufferedImage(DRAW_WIDTH, DRAW_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		currentGraphics = current.createGraphics();

		addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				resetMapPosition(e);
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
				resetMapPosition(e);
			}

			public void mouseReleased(MouseEvent e) {
			}

		});

		addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent e) {
				resetMapPosition(e);
			}

			public void mouseMoved(MouseEvent e) {
			}

		});

		this.setLayout(null);

		hybrid = new MapButton(ImageIO.read(new File("gfx/gui/map_icon_hybrid.png")), new Action() {
			public void perform() {
				map.setMapMode(GameMap.HYBRID);
				map.repaint();
			}
		});

		region = new MapButton(ImageIO.read(new File("gfx/gui/map_icon_region.png")), new Action() {
			public void perform() {
				map.setMapMode(GameMap.REGION);
				map.repaint();
			}
		});

		terrain = new MapButton(ImageIO.read(new File("gfx/gui/map_icon_terrain.png")), new Action() {
			public void perform() {
				map.setMapMode(GameMap.TERRAIN);
				map.repaint();
			}
		});

		structures = new MapButton(ImageIO.read(new File("gfx/gui/map_icon_structures.png")), new Action() {
			public void perform() {
				map.setMapMode(GameMap.STRUCTURES);
				map.repaint();
			}
		});

		this.add(hybrid);
		hybrid.setBounds(16, 6, hybrid.getPreferredSize().width, hybrid.getPreferredSize().height);
		this.add(region);
		region.setBounds(60, 6, region.getPreferredSize().width, region.getPreferredSize().height);
		this.add(terrain);
		terrain.setBounds(104, 6, terrain.getPreferredSize().width, terrain.getPreferredSize().height);
		this.add(structures);
		structures.setBounds(148, 6, structures.getPreferredSize().width, structures.getPreferredSize().height);

		lock = new ReentrantLock();

		Thread thread = new Thread() {
			public void run() {
				while (true) {
					lock.lock();

					map.drawMiniMap(graphics);
					currentGraphics.drawImage(
							tmp.getScaledInstance(DRAW_WIDTH, DRAW_HEIGHT, Image.SCALE_AREA_AVERAGING), 0, 0, null);

					lock.unlock();
					try {
						sleep(250);
						repaint();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();

	}

	private void resetMapPosition(MouseEvent e) {
		int scale = map.getScale() * 2;
		int width = map.map_width * scale;
		int height = map.map_height * scale;

		int x = e.getX() - 8 - (endWidth - startWidth) / 2;
		if (x < 0) {
			x = 0;
		}
		if (x + (endWidth - startWidth) >= DRAW_WIDTH) {
			x = DRAW_WIDTH - (endWidth - startWidth);
		}

		int y = e.getY() - 41 - (endHeight - startHeight) / 2;
		if (y < 0) {
			y = 0;
		}
		if (y + (endHeight - startHeight) >= DRAW_HEIGHT) {
			y = DRAW_HEIGHT - (endHeight - startHeight);
		}

		map.setComponentOrigin(x * width / DRAW_WIDTH, y * height / DRAW_HEIGHT);

	}

	private Lock lock;

	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		g.drawImage(current, 8, 41, null);
		g.setColor(Color.RED);
		g.drawRect(8 + startWidth, 41 + startHeight, endWidth - startWidth, endHeight - startHeight);

	}

	public void updateMini(int width, int height, int startWidth, int startHeight, int endWidth, int endHeight) {
		this.startWidth = startWidth * DRAW_WIDTH / width;
		this.startHeight = startHeight * DRAW_HEIGHT / height;
		this.endWidth = endWidth * DRAW_WIDTH / width;
		this.endHeight = endHeight * DRAW_HEIGHT / height;
	}

}
