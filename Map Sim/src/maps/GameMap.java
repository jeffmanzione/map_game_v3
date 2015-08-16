package maps;

import generic.Constructable;
import generic.Discrete;
import gfx.GameWindow;
import gfx.ResourceManager;
import gfx.maps.MiniMap;
import item.Item;
import item.structures.Structure;
import item.structures.cities.Settlement;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import parsers.NationParser;
import nations.Nation;
import roads.RoadBuilder;
import tiles.Colors;
import tiles.Continent;
import tiles.LandType;
import tiles.Tile;
import tiles.TileAlreadyHasStructureException;
import tiles.TileHasMaxNumberOfUnitsException;
import time.TimeManager;
import units.Unit;

public class GameMap extends JPanel {
	private static final long serialVersionUID = -8642741473346038152L;

	private Tile[][] tiles;
	public int map_width, map_height;
	private int comp_width, comp_height;

	private static final int[] SCALES = { 4, 8, 16, 32, 64, 128 };
	private int scale_index = 2, scale = 16;

	private MouseInterpreter mi;
	private Point old_panel_center;
	private Point panel_center;

	private static int NUDGE_MAX = 48, NUDGE_MIN = 16;
	private static final int SCROLL_BORDER = 25, NUDGE_INTERVAL = 1;
	public static final int ITERATION = 40;

	private int coorX = 0, coorY = 0, oldCoorX = 0, oldCoorY = 0;

	private int x_offset = 0;
	private int y_offset = 0;

	private Map<String, Nation> nations;

	private MiniMap mini = null;

	private volatile int selectedX = -1, selectedY = -1;

	private Map<Integer, BufferedImage> maps;
	private BufferedImage current;

	private TimeManager tm;

	private Nation player;

	public static final int HYBRID = 0, REGION = 1, TERRAIN = 2, STRUCTURES = 3, CLIMATE = 4;
	private int mode = HYBRID;

	private boolean[][] discovered;

	private Lock lock;

	private boolean FOW = true, fancy = true;

	private GameWindow window;

	private List<Tile> constructableRegion = new ArrayList<>();

	private List<Continent> continents = new ArrayList<>();

	// private boolean rescaling = false;

	private Rectangle discoveredBounds;

	private String mapName = "default";

	public GameMap(File mapFile, File climateFile, int comp_width, int comp_height) throws InvalidMapSizeException {

		super();

		this.comp_width = comp_width;
		this.comp_height = comp_height;

		mi = new MouseInterpreter();

		addMouseListener(mi);
		addMouseMotionListener(mi);
		addMouseWheelListener(mi);

		readMapFile(mapFile, climateFile);
		System.out.println("Read Map.");

		repaint();

	}

	public void loadStrucures() {
		ResourceManager.importComponent(this);
		try {
			nations = NationParser.loadNationsFromFile("maps/" + mapName + "/text/nations/nations.txt", this);

			player = nations.get("attica");

			for (int x = 0; x < map_width; x++) {
				for (int y = 0; y < map_height; y++) {
					if (tiles[x][y].isInFullView(this)) {
						discover(x, y);
					} else {
						undiscover(x, y);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (lock == null) {
			lock = new ReentrantLock();
		}
		
		Thread mouseThread = new Thread() {
			boolean nudgingUp, nudgingDown, nudgingLeft, nudgingRight;

			int x_nudge = NUDGE_MIN, x_nudge_interval = 0;
			int y_nudge = NUDGE_MIN, y_nudge_interval = 0;

			public void run() {

				while (true) {

					// boolean shouldRepaint = false;
					// boolean redrawForMouse = false;

					lock.lock();

					updateCounter(counter + 1);

					int x = mi.x;
					int y = mi.y;
					int zoom = -mi.zoom;

					if (zoom != 0) {
						toggleRescaling();

						if (zoom + scale_index >= SCALES.length - 1) {
							scale_index = SCALES.length - 1;
						} else if (zoom + scale_index <= 0) {
							scale_index = 0;
						} else {
							scale_index += zoom;
						}

						mi.zoom = 0;

						int x_on_map = x - (GameMap.this.comp_width / 2) + panel_center.x;
						int y_on_map = y - (GameMap.this.comp_height / 2) + panel_center.y;

						int mouse_x_offset_from_center = x - GameMap.this.comp_width / 2;
						int mouse_y_offset_from_center = y - GameMap.this.comp_height / 2;

						int newX = (x_on_map * SCALES[scale_index] / scale) - mouse_x_offset_from_center;
						int newY = (y_on_map * SCALES[scale_index] / scale) - mouse_y_offset_from_center;

						scale = SCALES[scale_index];

						if (zoom < 0) {
							int scaled_map_width = map_width * scale * 2;
							int scaled_map_height = map_height * scale * 2;

							int max = scaled_map_height - GameMap.this.comp_height / 2;

							if (newY < GameMap.this.comp_height / 2) {
								newY = GameMap.this.comp_height / 2;
							} else if (newY > max) {
								newY = max;
							}

							max = scaled_map_width - GameMap.this.comp_width / 2;

							if (newX < GameMap.this.comp_width / 2) {
								newX = GameMap.this.comp_width / 2;
							} else if (newX > max) {
								newX = max;
							}
						}

						panel_center.x = newX;
						panel_center.y = newY;

						current = maps.get(scale);

						// shouldRepaint = true;
					}

					boolean redrawX = false, redrawY = false;

					// System.out.println(x + " " + y);

					if (x != -1 && y != -1) {
						if (x < SCROLL_BORDER) {
							if (nudgingRight) {
								x_nudge = 0;
								x_nudge_interval = 0;
								nudgingRight = false;
							}

							nudgingLeft = true;

							x_nudge_interval++;
							if (x_nudge_interval % NUDGE_INTERVAL == 0) {
								x_nudge_interval = 0;
								x_nudge++;
							}

							redrawX = nudgeLeft(x_nudge = Math.min(NUDGE_MAX, x_nudge));

						} else if (x > GameMap.this.comp_width - SCROLL_BORDER && x < GameMap.this.comp_width) {
							if (nudgingLeft) {
								x_nudge = 0;
								x_nudge_interval = 0;
								nudgingLeft = false;
							}
							nudgingRight = true;

							x_nudge_interval++;
							if (x_nudge_interval % NUDGE_INTERVAL == 0) {
								x_nudge_interval = 0;
								x_nudge++;
							}

							redrawX = nudgeRight(x_nudge = Math.min(NUDGE_MAX, x_nudge));
						}

						if (y < SCROLL_BORDER) {
							if (nudgingDown) {
								y_nudge = 0;
								y_nudge_interval = 0;
								nudgingDown = false;
							}
							nudgingUp = true;

							y_nudge_interval++;
							if (y_nudge_interval % NUDGE_INTERVAL == 0) {
								y_nudge_interval = 0;
								y_nudge++;
							}

							redrawY = nudgeUp(y_nudge = Math.min(NUDGE_MAX, y_nudge));

						} else if (y > GameMap.this.comp_height - SCROLL_BORDER && y < GameMap.this.comp_height) {
							if (nudgingUp) {
								y_nudge = 0;
								y_nudge_interval = 0;
								nudgingUp = false;
							}
							nudgingDown = true;

							y_nudge_interval++;
							if (y_nudge_interval % NUDGE_INTERVAL == 0) {
								y_nudge_interval = 0;
								y_nudge++;
							}

							redrawY = nudgeDown(y_nudge = Math.min(NUDGE_MAX, y_nudge));
						} else {

							if (!redrawX && x_nudge > 0) {
								if (nudgingLeft) {

									x_nudge_interval--;
									if (x_nudge_interval == -1) {
										x_nudge_interval = NUDGE_INTERVAL;
										x_nudge -= 2;
									}

									if (nudgingLeft = nudgeLeft(x_nudge)) {
										// shouldRepaint = true;
									} else {
										x_nudge = NUDGE_MIN;
									}
								} else if (nudgingRight) {

									x_nudge_interval--;
									if (x_nudge_interval == -1) {
										x_nudge_interval = NUDGE_INTERVAL;
										x_nudge -= 2;
									}

									if (nudgingRight = nudgeRight(x_nudge)) {
										// shouldRepaint = true;
									} else {
										x_nudge = NUDGE_MIN;
									}
								}
							}

							if (!redrawY && y_nudge > 0) {
								if (nudgingUp) {

									y_nudge_interval--;
									if (y_nudge_interval == -1) {
										y_nudge_interval = NUDGE_INTERVAL;
										y_nudge -= 2;
									}

									if (nudgingUp = nudgeUp(y_nudge)) {
										// shouldRepaint = true;
									} else {
										y_nudge = NUDGE_MIN;
									}
								} else if (nudgingDown) {

									y_nudge_interval--;
									if (y_nudge_interval == -1) {
										y_nudge_interval = NUDGE_INTERVAL;
										y_nudge -= 2;
									}

									if (nudgingDown = nudgeDown(y_nudge)) {
										// shouldRepaint = true;
									} else {
										y_nudge = NUDGE_MIN;
									}
								}
							}
						}

						oldCoorX = coorX;
						oldCoorY = coorY;

						coorX = (mi.x + x_offset) / scale / 2;
						coorY = (mi.y + y_offset) / scale / 2;

						// if (coorX != oldCoorX || coorY != oldCoorY) {
						// redrawForMouse = true;
						// }

						MouseInterpreter.MouseState state = mi.getMouseClicked();

						if (state != MouseInterpreter.MouseState.NONE) {

							int clickX = (mi.click_x + x_offset) / scale / 2 + startX;
							int clickY = (mi.click_y + y_offset) / scale / 2 + startY;

							if (state == MouseInterpreter.MouseState.LEFT) {
								selectedX = clickX;
								selectedY = clickY;

								if (selected instanceof Constructable) {

									if (((Constructable) selected).isConstructing()
											&& ((Constructable) selected).getAvailableTiles().contains(
													tiles[selectedX][selectedY])) {
										((Constructable) selected).placeStructure(selectedX, selectedY);
									}

								}

								GameMap.this.selected = GameMap.this.tileAt(selectedX, selectedY).getProminent(
										GameMap.this);
								window.setSelected(GameMap.this.selected);

								if (selected instanceof Constructable) {
									constructableRegion = ((Constructable) selected).getAvailableTiles();
								}

								// shouldRepaint = true;
							} else if (selected != null
									&& state == MouseInterpreter.MouseState.RIGHT
									|| (state == MouseInterpreter.MouseState.RIGHT_SHIFT && !(selected instanceof Unit))) {
								selected.passOrder(tileAt(clickX, clickY));
							} else if (selected != null && state == MouseInterpreter.MouseState.RIGHT_SHIFT) {
								((Unit) selected).addWaypoint(tileAt(clickX, clickY));
							}
						}

					}

					lock.unlock();

					updatePlayerInfo();

					// shouldRedrawAll = false;

					// if (shouldRepaint || redrawX || redrawY) {
					// shouldRedrawAll = true;
					repaint();
					// } else if (redrawForMouse) {
					// shouldRedrawAll = false;
					// repaint();
					// }

					try {
						sleep(ITERATION);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		};
		mouseThread.start();
	}

	// private boolean shouldRedrawAll = true;

	// private List<Point> toUpdate = new ArrayList<Point>();

	private void calculateMapBounds() {
		startX = panel_center.x / scale / 2 - comp_width / 2 / scale / 2;
		startY = panel_center.y / scale / 2 - comp_height / 2 / scale / 2;

		x_offset = (panel_center.x == comp_width / 2) ? 0 : (panel_center.x) % (2 * scale);
		y_offset = (panel_center.y == comp_height / 2) ? 0 : (panel_center.y) % (2 * scale);

		endX = (panel_center.x + comp_width / 2) / scale / 2;
		endY = (panel_center.y + comp_height / 2) / scale / 2;

		if (endX < tiles.length - 2) {
			endX += 2;
		} else if (endX < tiles.length - 1) {
			endX += 1;
		}

		if (endY < tiles[0].length - 2) {
			endY += 2;
		} else if (endY < tiles[0].length - 1) {
			endY += 1;
		}

		if (startX < 0) {
			startX = 0;
		}

		if (startY < 0) {
			startY = 0;
		}

		x_tile = startX + coorX;
		y_tile = startY + coorY;

	}

	public void toggleFOW() {
		FOW = !FOW;
		repaint();
	}

	public void toggleFancy() {
		fancy = !fancy;
		repaint();
		// System.out.println("fancy=" + fancy);
	}

	public void setTimeManager(TimeManager tm) {
		this.tm = tm;
	}

	public TimeManager getTimeManager() {
		return tm;
	}

	public void setComponentSize(int width, int height) {
		comp_width = width;
		comp_height = height;
	}

	private int startX, startY, endX, endY, x_tile, y_tile;

	private void readMapFile(File mapFile, File climateFile) throws InvalidMapSizeException {
		try {

			BufferedImage mapImg = ImageIO.read(mapFile);
			BufferedImage climateImg = ImageIO.read(climateFile);
			map_width = mapImg.getWidth();
			map_height = mapImg.getHeight();

			if (map_width % 2 != 0 || map_height % 2 != 0 || climateImg.getWidth() != map_width
					|| climateImg.getHeight() != map_height) {
				throw new InvalidMapSizeException();
			}

			tiles = new Tile[map_width / 2][map_height / 2];
			discovered = new boolean[map_width / 2][map_height / 2];

			LandType[] landTypes;
			for (int x = 0; x < map_width; x += 2) {
				for (int y = 0; y < map_height; y += 2) {
					landTypes = new LandType[4];
					landTypes[0] = LandType.parseFromColor(mapImg.getRGB(x, y), climateImg.getRGB(x, y));

					landTypes[1] = LandType.parseFromColor(mapImg.getRGB(x + 1, y), climateImg.getRGB(x + 1, y));

					landTypes[2] = LandType.parseFromColor(mapImg.getRGB(x, y + 1), climateImg.getRGB(x, y + 1));

					landTypes[3] = LandType
							.parseFromColor(mapImg.getRGB(x + 1, y + 1), climateImg.getRGB(x + 1, y + 1));

					Tile tile = new Tile(landTypes, x / 2, y / 2);
					tiles[x / 2][y / 2] = tile;
				}
			}

			for (int x = 0; x < map_width / 2; x++) {
				for (int y = 0; y < map_height / 2; y++) {

					tiles[x][y].smooth(this, x, y);
				}
			}

			// CONTINENTS
			for (int x = 0; x < map_width / 2; x++) {
				for (int y = 0; y < map_height / 2; y++) {
					Tile tile = tiles[x][y];

					List<Tile> around = this.getPassableLandTilesAround(tile);

					boolean newContinent = true;

					for (Tile neighbor : around) {
						if (neighbor.getContinent() != null) {
							tile.setContinent(neighbor.getContinent());
							newContinent = false;
							break;
						}
					}

					if (newContinent) {
						Continent continent = new Continent();
						tile.setContinent(continent);
						continents.add(continent);
					}
				}
			}

			maps = new HashMap<Integer, BufferedImage>();
			for (int scale : SCALES) {
				if (scale <= 32) {
					maps.put(scale, drawMap(scale));
				}
			}

			/* Not the scale from inside the loop, the global one. */
			current = maps.get(scale);

			setSize(comp_width, comp_height);
			setPreferredSize(new Dimension(comp_width, comp_height));

			panel_center = new Point(map_width * scale / 2, map_height * scale / 2);
			old_panel_center = new Point(0, 0);

			map_width = map_width / 2;
			map_height = map_height / 2;

			repaint();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public static final int MAX_COUNTER = 64;

	public int getMaxCounter() {
		return 25;
	}

	public Nation getPlayer() {
		return player;
	}

	public void drawMiniMap(Graphics2D g) {

		g.drawImage(current, 0, 0, map_width, map_height, null);

		for (int x = 0; x < map_width; x++) {
			for (int y = 0; y < map_height; y++) {
				if (FOW && !discovered[x][y]) {
					tiles[x][y].drawFOW(g, x, y, x, y, 1, this, false, false, false);
				} else {
					tiles[x][y].drawOwnership(g, x, y, x, y, 1, this, false, false, false);
				}
			}
		}

	}

	List<Tile> road;
	{
		road = new ArrayList<>();
	}

	public List<Tile> getRoad() {
		return road;
	}

	public void paintMap(Graphics2D g) {
		super.paintComponent(g);

		calculateMapBounds();

		if ((coorX != oldCoorX || coorY != oldCoorY) && selected instanceof Settlement
				&& selected instanceof Constructable && ((Constructable) selected).isConstructing()) {

			road = RoadBuilder.getPath(this, selected.getNation(), ((Settlement) selected).getLocation(),
					this.tileAt(coorX + startX, coorY + startY));
		}

		if (scale > 32) {

			for (int x = startX; x <= endX && x < tiles.length; x++) {
				for (int y = startY; y <= endY && x < tiles.length && y < tiles[x].length; y++) {

					if (!FOW || (discovered[x][y] || this.adjacentToDiscovered(x, y))) {
						tiles[x][y].draw(g, x, y, (x - startX) * scale * 2 - x_offset, (y - startY) * scale * 2
								- y_offset, scale, this, (x == x_tile && y == y_tile), selected == tiles[x][y], fancy);
					}

				}
			}

		} else {

			g.drawImage(current, 0, 0, (endX * 2 * scale) - (startX * 2 * scale), (endY * 2 * scale)
					- (startY * 2 * scale), (startX * 2 * scale) + x_offset, (startY * 2 * scale) + y_offset,
					(endX * 2 * scale) + x_offset, (endY * 2 * scale) + y_offset, null);

		}

		if (this.mode != TERRAIN) {
			for (int x = startX; x <= endX && x < tiles.length; x++) {
				if (valid(x, 0)) {
					for (int y = startY; y <= endY && y < tiles[x].length; y++) {
						if (valid(x, y)) {

							if (this.mode != STRUCTURES) {
								if (!FOW || (discovered[x][y] || this.adjacentToDiscovered(x, y))) {
									tiles[x][y].drawOwnership(g, x, y, (x - startX) * scale * 2 - x_offset,
											(y - startY) * scale * 2 - y_offset, scale, this,
											(x == x_tile && y == y_tile), selectedX == x && selectedY == y, fancy);

								}
							}

							if (selected instanceof Settlement && selected instanceof Constructable
									&& ((Constructable) selected).isConstructing() && road != null
									&& road.contains(tiles[x][y])) {
								tiles[x][y].drawPaths(g, x, y, (x - startX) * scale * 2 - x_offset, (y - startY)
										* scale * 2 - y_offset, scale, this, (x == x_tile && y == y_tile),
										selectedX == x && selectedY == y, true);
							}

							if (selected instanceof Constructable && ((Constructable) selected).isConstructing()) {
								// System.out.println(tiles[x][y]);
								tiles[x][y].drawConstructionRange(g, x, y, (x - startX) * scale * 2 - x_offset,
										(y - startY) * scale * 2 - y_offset, scale, this, (x == x_tile && y == y_tile),
										selectedX == x && selectedY == y, fancy);
							}
						}
					}
				}
			}
		}
		if (this.mode != TERRAIN) {
			for (int x = startX; x <= endX && x < tiles.length; x++) {
				if (valid(x, 0)) {
					for (int y = startY; y <= endY && y < tiles[x].length; y++) {
						if (valid(x, y) && (!FOW || discovered[x][y])) {

							if (tiles[x][y].hasRoad()) {
								tiles[x][y].drawPaths(g, x, y, (x - startX) * scale * 2 - x_offset, (y - startY)
										* scale * 2 - y_offset, scale, this, (x == x_tile && y == y_tile),
										selectedX == x && selectedY == y, false);
							}

							tiles[x][y].drawItem(g, x, y, (x - startX) * scale * 2 - x_offset, (y - startY) * scale * 2
									- y_offset, scale, this, (x == x_tile && y == y_tile), selectedX == x
									&& selectedY == y, fancy);
						}
					}
				}
			}

		}
		if (scale > 32) {

			for (int x = startX; x <= endX && x < tiles.length; x++) {
				for (int y = startY; y <= endY && x < tiles.length && y < tiles[x].length; y++) {

					if (FOW && (!discovered[x][y] || this.adjacentToDiscovered(x, y))) {
						tiles[x][y].drawFOW(g, x, y, (x - startX) * scale * 2 - x_offset, (y - startY) * scale * 2
								- y_offset, scale, this, (x == x_tile && y == y_tile) ? true : false,
								selected == tiles[x][y], fancy);
					}
				}
			}

		} else {

			if (FOW) {

				for (int x = startX; x <= endX && x < tiles.length; x++) {
					if (valid(x, 0)) {
						for (int y = startY; y <= endY && x < tiles.length && y < tiles[x].length; y++) {
							if (!discovered[x][y] || this.adjacentToDiscovered(x, y)) {
								tiles[x][y].drawFOW(g, x, y, (x - startX) * scale * 2 - x_offset, (y - startY) * scale
										* 2 - y_offset, scale, this, (x == x_tile && y == y_tile) ? true : false,
										selected == tiles[x][y], fancy);
							}
						}
					}
				}
			}

		}

		int drawX = coorX * scale * 2 - x_offset;
		int drawY = coorY * scale * 2 - y_offset;

		g.setColor(Color.WHITE);
		g.drawRoundRect(drawX, drawY, scale * 2 - 2, scale * 2 - 2, scale, scale);
		g.setColor(Color.BLACK);
		g.drawRoundRect(drawX - 1, drawY - 1, scale * 2, scale * 2, scale, scale);
		g.setColor(Colors.TRANS_GREEN);
		g.fillRoundRect(drawX + 1, drawY + 1, scale * 2 - 4, scale * 2 - 4, scale - 2, scale - 2);

		if (this.mode != TERRAIN) {
			for (int x = startX; x <= endX && x < tiles.length; x++) {
				if (valid(x, 0)) {
					for (int y = startY; y <= endY && y < tiles[x].length; y++) {
						if (valid(x, y) && (!FOW || discovered[x][y])) {
							tiles[x][y].drawLabel(g, x, y, (x - startX) * scale * 2 - x_offset, (y - startY) * scale
									* 2 - y_offset, scale, this, (x == x_tile && y == y_tile) ? true : false,
									selectedX == x && selectedY == y, fancy);
						}
					}
				}
			}
		}

		if (FOW) {

			// System.out.println(scale + " " + x_offset + " " + y_offset + " " + startX + " " + startY);

			for (int x = startX; x <= endX && x < tiles.length; x++) {
				if (valid(x, 0)) {
					for (int y = startY; y <= endY && y < tiles[x].length; y++) {
						if (valid(x, y) && discovered[x][y] || this.adjacentToDiscovered(x, y)) {
							tiles[x][y].drawShadow(g, x, y, (x - startX) * scale * 2 - x_offset, (y - startY) * scale
									* 2 - y_offset, scale, this, (x == x_tile && y == y_tile) ? true : false,
									selectedX == x && selectedY == y, fancy);
						}
					}
				}
			}
		}

		if (selected != null && selected instanceof Unit) {
			drawPath(g, (Unit) selected, startX, startY, x_offset, y_offset);
		}

		if (showUI) {

			String coor = "(" + x_tile + "," + y_tile + ") (" + selectedX + "," + selectedY + ")";
			Font font = new Font("SANS SERIF", Font.BOLD, 12);
			FontMetrics metrics = g.getFontMetrics(font);
			g.setFont(font);
			g.setColor(new Color(0, 0, 0, 100));

			g.fillRect(comp_width - 266, comp_height - 12 - metrics.getHeight(), metrics.stringWidth(coor),
					metrics.getHeight());
			g.setColor(Color.WHITE);
			g.drawString(coor, comp_width - 266, comp_height - 16);

		}

	}

	private Lock rescaling = new ReentrantLock();
	private AtomicBoolean isRescaling = new AtomicBoolean(false);

	private void toggleRescaling() {
		rescaling.lock();
		isRescaling.set(!isRescaling.get());
		rescaling.unlock();
	}

	public void paintComponent(Graphics graphics) {

		rescaling.lock();

		Graphics2D g = (Graphics2D) graphics;

		// if (shouldRedrawAll) {
		paintMap(g);
		// } else {
		// repaintMouse(g);
		// }

		updateMiniMap();

		if (isRescaling.get()) {
			toggleRescaling();
		}
		rescaling.unlock();

	}

	private void drawPath(Graphics2D g, Unit u, int startX, int startY, int x_offset, int y_offset) {
		if (u.isEnRoute()) {
			List<Tile> tiles = u.getPath();
			if (tiles.size() > 2) {
				Point2D prev = null;
				Tile curr = tiles.get(0);
				Tile next;
				for (int i = 1; i < tiles.size(); i++) {
					next = tiles.get(i);

					g.setStroke(new BasicStroke(2));

					int xOff = 0, yOff = 0;

					double fraction = 0;
					if (i == tiles.size() - 1) {
						xOff = u.getXOffset(scale);
						yOff = u.getYOffset(scale);
						fraction = u.determineFraction();

					}

					Point2D to;

					if (prev == null) {
						prev = new Point2D.Double((curr.x - startX) * scale * 2 + scale - x_offset, (curr.y - startY)
								* scale * 2 + scale - y_offset);
					}

					int dir = this.getDirectionFrom(curr, next);

					int to_x;
					int to_y;

					switch (dir) {
						case 0:
							to_x = (curr.x - startX) * scale * 2 + scale;
							to_y = (curr.y - startY) * scale * 2;
							break;
						case 45:
							to_x = (curr.x - startX) * scale * 2 + scale * 2;
							to_y = (curr.y - startY) * scale * 2;
							break;
						case 90:
							to_x = (curr.x - startX) * scale * 2 + scale * 2;
							to_y = (curr.y - startY) * scale * 2 + scale;
							break;
						case 135:
							to_x = (curr.x - startX) * scale * 2 + scale * 2;
							to_y = (curr.y - startY) * scale * 2 + scale * 2;
							break;
						case 180:
							to_x = (curr.x - startX) * scale * 2 + scale;
							to_y = (curr.y - startY) * scale * 2 + scale * 2;
							break;
						case 225:
							to_x = (curr.x - startX) * scale * 2;
							to_y = (curr.y - startY) * scale * 2 + scale * 2;
							break;
						case 270:
							to_x = (curr.x - startX) * scale * 2;
							to_y = (curr.y - startY) * scale * 2 + scale;
							break;
						case 315:
							to_x = (curr.x - startX) * scale * 2;
							to_y = (curr.y - startY) * scale * 2;
							break;
						default:
							to_x = 0;
							to_y = 0;
					}

					to = new Point2D.Double(to_x - x_offset, to_y - y_offset);

					Path2D path = new Path2D.Double();

					path.moveTo(prev.getX(), prev.getY());

					if (i == tiles.size() - 1) {
						path.quadTo((curr.x - startX) * scale * 2 + scale - x_offset, (curr.y - startY) * scale * 2
								+ scale - y_offset, (u.getCurrent().x - startX) * scale * 2 + scale - x_offset
								+ (fraction * xOff), (u.getCurrent().y - startY) * scale * 2 + scale - y_offset
								- (fraction * yOff));
					} else {
						path.quadTo((curr.x - startX) * scale * 2 + scale - x_offset, (curr.y - startY) * scale * 2
								+ scale - y_offset, to_x - x_offset, to_y - y_offset);
					}
					g.setColor(Color.green);
					g.draw(path);

					// g.setColor(Color.green);
					// g.drawLine((curr.x - startX) * scale * 2 - x_offset +
					// scale, (curr.y - startY) * scale * 2 - y_offset + scale,
					// (int) ((next.x - startX) * scale * 2 - x_offset + scale -
					// (xOff * fraction)), (int) ((next.y - startY) * scale * 2
					// - y_offset + scale + (yOff * fraction)));

					if (i == 1) {
						int angle = getDirectionFrom(next, curr);

						AffineTransform at = new AffineTransform();
						BufferedImage img = ResourceManager.get("arrow", scale);
						at.translate((curr.x - startX) * scale * 2 - x_offset + scale - img.getWidth() / 2,
								(curr.y - startY) * scale * 2 - y_offset + scale - img.getHeight() / 2);
						at.rotate(Math.toRadians(angle), img.getWidth() / 2, img.getHeight() / 2);
						g.drawImage(img, at, null);
					}

					prev = to;
					curr = next;
				}
			}
		}
	}

	/*
	 * private void drawPath(Graphics2D g, Unit u, int startX, int startY, int x_offset, int y_offset) {
	 * 
	 * if (u.isEnRoute()) { List<Tile> tiles = u.getPath(); Tile one = tiles.get(0); Tile two; for (int i = 1; i <
	 * tiles.size() - 1; i++) { two = tiles.get(i);
	 * 
	 * g.setStroke(new BasicStroke(2));
	 * 
	 * int xOff = 0, yOff = 0;
	 * 
	 * double fraction = 0; if (i == tiles.size() - 2) { xOff = u.getXOffset(scale); yOff = u.getYOffset(scale);
	 * fraction = 1 - u.determineFraction();
	 * 
	 * }
	 * 
	 * 
	 * g.setColor(Color.green); g.drawLine((one.x - startX) * scale * 2 - x_offset + scale, (one.y - startY) * scale * 2
	 * - y_offset + scale, (int) ((two.x - startX) * scale * 2 - x_offset + scale - (xOff * fraction)), (int) ((two.y -
	 * startY) * scale * 2 - y_offset + scale + (yOff * fraction)));
	 * 
	 * if (i == 1) { int angle = getDirectionFrom(two, one);
	 * 
	 * AffineTransform at = new AffineTransform(); BufferedImage img = ResourceManager.get("arrow", scale);
	 * at.translate((one.x - startX) * scale * 2 - x_offset + scale - img.getWidth()/2, (one.y - startY) * scale * 2 -
	 * y_offset + scale - img.getHeight()/2); at.rotate(Math.toRadians(angle), img.getWidth()/2, img.getHeight()/2);
	 * g.drawImage(img, at, null); }
	 * 
	 * one = two; } } }
	 */

	public int getDirectionFrom(Tile a, Tile b) {
		int dx = b.x - a.x;
		int dy = b.y - a.y;
		int angle = (int) Math.toDegrees(Math.atan2(-dy, dx));

		int compassAngle = 90 - angle;

		compassAngle += 45 / 2;

		if (compassAngle < 0)
			compassAngle += 360;

		return (compassAngle / 45) * 45;
	}

	public void setMiniMap(MiniMap mini) {
		this.mini = mini;
		updateMiniMap();
	}

	public void updateMiniMap() {
		mini.updateMini(map_width * scale * 2, map_height * scale * 2, (panel_center.x - comp_width / 2),
				(panel_center.y - comp_height / 2), (panel_center.x + comp_width / 2),
				(panel_center.y + comp_height / 2));
	}

	public void setMapMode(int mode) {
		this.mode = mode;
	}

	public int getMapMode() {
		return mode;
	}

	public BufferedImage drawMap(int scale) {
		BufferedImage map = new BufferedImage(tiles.length * scale * 2, tiles[0].length * scale * 2,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = map.createGraphics();

		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				tiles[x][y].draw(g, x, y, x * scale * 2 - x_offset, y * scale * 2 - y_offset, scale, this, false,
						false, fancy);
			}
		}

		g.dispose();

		return map;
	}

	public boolean nudgeLeft(int amount) {
		return nudge(Direction.LEFT, amount);
	}

	public boolean nudgeRight(int amount) {
		return nudge(Direction.RIGHT, amount);
	}

	public boolean nudgeUp(int amount) {
		return nudge(Direction.UP, amount);
	}

	public boolean nudgeDown(int amount) {
		return nudge(Direction.DOWN, amount);
	}

	private enum Direction {
		UP, DOWN, LEFT, RIGHT;
	}

	private boolean nudge(Direction dir, int amount) {
		int newX = old_panel_center.x = panel_center.x;
		int newY = old_panel_center.y = panel_center.y;

		int scaled_map_width = map_width * scale * 2;
		int scaled_map_height = map_height * scale * 2;
		int max;
		switch (dir) {
			case UP:
				newY = Math.max(comp_height / 2, newY - amount);
				break;
			case DOWN: /* ?????? */
				max = scaled_map_height - comp_height / 2 /*- (scale * 7 / 4)*/;
				newY = Math.min(max, newY + amount);
				break;
			case RIGHT: /* ?????? */
				max = scaled_map_width - comp_width / 2 /*- (scale * 3 / 4)*/;
				newX = Math.min(max, newX + amount);
				break;
			case LEFT:
				newX = Math.max(comp_width / 2, newX - amount);
				break;
			default:
				return false;
		}

		if (newX == panel_center.x && newY == panel_center.y) {
			return false;
		} else {

			panel_center.x = newX;
			panel_center.y = newY;

			// System.out.println(amount);

			return true;
		}
	}

	public Tile tileAt(int x, int y) {
		if (x >= 0 && y >= 0 && x < tiles.length && y < tiles[0].length) {
			return tiles[x][y];
		} else {
			return null;
		}
	}

	public boolean put(Item i, int x, int y) {
		Tile tile = tiles[x][y];
		if (i instanceof Structure) {
			if (tile.hasStructure()) {
				return false;
			} else {
				try {
					tile.placeStructure((Structure) i);
					return true;
				} catch (TileAlreadyHasStructureException e) {
					e.printStackTrace();
					return false;
				}
			}
		} else {
			System.err.println("NO INFO FOR ANY ITEM BESIDES A STRUCTURE!");
			return false;
		}

	}

	public boolean put(Unit unit, int x, int y) {
		Tile tile = tiles[x][y];
		return put(unit, tile);
	}

	public boolean discovered(int x, int y) {
		return discovered[x][y];
	}

	public boolean discovered(Tile tile) {
		return discovered[tile.x][tile.y];
	}

	public boolean adjacentToDiscovered(Tile pos) {
		for (int i = NORTH; i <= SOUTH_WEST; i++) {
			Tile tile = getTileInDirectionOf(pos.x, pos.y, i);
			if (tile != null && discovered[tile.x][tile.y]) {
				return true;
			}
		}
		return false;
	}

	public boolean adjacentToDiscovered(int x, int y) {
		for (int i = NORTH; i <= SOUTH_WEST; i++) {
			Tile tile = getTileInDirectionOf(x, y, i);
			if (tile != null && discovered[tile.x][tile.y]) {
				return true;
			}
		}
		return false;
	}

	private void discover(int x, int y) {
		discovered[x][y] = true;

		if (discoveredBounds == null) {
			discoveredBounds = new Rectangle(x, y, 0, 0);
		}
		//
		// System.out.printf("%f,%f %f,%f\n", discoveredBounds.getMinX(),
		// discoveredBounds.getMinY(), discoveredBounds.getMaxX(),
		// discoveredBounds.getMaxY());

		int minX = (int) Math.min(discoveredBounds.getMinX(), x);
		int minY = (int) Math.min(discoveredBounds.getMinY(), y);
		int wid = (int) Math.max(discoveredBounds.getMaxX(), x) - minX;
		int hei = (int) Math.max(discoveredBounds.getMaxY(), y) - minY;

		discoveredBounds.setBounds(minX, minY, wid, hei);

		// System.out.printf("%d,%d %d,%d\n", minX, minY, minX + wid, minY +
		// hei);
	}

	public void undiscover(int x, int y) {
		discovered[x][y] = false;
	}

	public boolean put(Unit unit, Tile tile) {
		if (tile.canPutUnit()) {
			try {
				unit.setGameMap(this);
				tile.put(unit);

				if (unit.getNation() == player) {
					for (Tile t : this.getTilesAround(tile)) {
						discover(t.x, t.y);
					}
				}
				return true;
			} catch (TileHasMaxNumberOfUnitsException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3, NORTH_EAST = 4, NORTH_WEST = 5, SOUTH_EAST = 6,
			SOUTH_WEST = 7;

	public boolean valid(int x, int y) {
		return x >= 0 && x < map_width && y >= 0 && y < map_height;
	}

	public Tile getTileInDirectionOf(int x, int y, int dir) {
		if (dir == NORTH) {
			if (valid(x, y - 1)) {
				return tileAt(x, y - 1);
			}
		} else if (dir == SOUTH) {
			if (valid(x, y + 1)) {
				return tileAt(x, y + 1);
			}
		} else if (dir == EAST) {
			if (valid(x + 1, y)) {
				return tileAt(x + 1, y);
			}
		} else if (dir == WEST) {
			if (valid(x - 1, y)) {
				return tileAt(x - 1, y);
			}
		} else if (dir == NORTH_EAST) {
			if (valid(x, y - 1)) {
				return tileAt(x + 1, y - 1);
			}
		} else if (dir == NORTH_WEST) {
			if (valid(x, y + 1)) {
				return tileAt(x - 1, y - 1);
			}
		} else if (dir == SOUTH_EAST) {
			if (valid(x + 1, y)) {
				return tileAt(x + 1, y + 1);
			}
		} else {
			if (valid(x - 1, y)) {
				return tileAt(x - 1, y + 1);
			}
		}

		return null;
	}

	public int getScale() {
		return scale;
	}

	public void setComponentOrigin(int centerX, int centerY) {
		panel_center.x = centerX + comp_width / 2;
		panel_center.y = centerY + comp_height / 2;

		repaint();
	}

	public List<Tile> getPassableLandTilesAround(Tile pos) {
		List<Tile> around = new ArrayList<Tile>();

		for (int i = NORTH; i <= SOUTH_WEST; i++) {
			Tile tile = getTileInDirectionOf(pos.x, pos.y, i);
			if (tile != null && tile.isLand()) {
				around.add(tile);
			}
		}
		return around;
	}

	public List<Tile> getPassableSeaTilesAround(Tile pos) {
		List<Tile> around = new ArrayList<Tile>();

		for (int i = NORTH; i <= SOUTH_WEST; i++) {
			Tile tile = getTileInDirectionOf(pos.x, pos.y, i);
			if (tile != null && !tile.isLand()) {
				around.add(tile);
			}
		}
		return around;
	}

	public List<Tile> getTilesAround(Tile pos) {
		List<Tile> around = new ArrayList<Tile>();

		for (int i = NORTH; i <= SOUTH_WEST; i++) {
			Tile tile = getTileInDirectionOf(pos.x, pos.y, i);
			if (tile != null) {
				around.add(tile);
			}
		}
		return around;
	}

	private Discrete selected = null;

	public void setSelected(Discrete thing) {
		selected = thing;
	}

	public Discrete getSelected() {
		return selected;
	}

	public void advance() {
		for (Nation nat : nations.values()) {
			nat.advance();
		}
	}

	public void advanceMonth() {
		for (Nation nat : nations.values()) {
			nat.advanceMonth();
		}
	}

	public void advanceYear() {
		for (Nation nat : nations.values()) {
			nat.advanceYear();
		}
	}

	private int counter = 0;

	public void updateCounter(int iter) {
		counter = iter;
	}

	public int getCounter() {
		return counter;
	}

	public Map<String, Nation> getNations() {
		return nations;
	}

	private boolean showUI = true;

	public void toggleUI() {
		showUI = !showUI;
		repaint();
	}

	public void updatePlayerInfo() {
		window.updatePlayerStats(player);
	}

	public void setWindow(GameWindow window) {
		this.window = window;
	}

	public List<Tile> getConstructableRegion() {
		return constructableRegion;
	}

	public int getMouseX() {
		return mi.x;
	}

	public int getMouseY() {
		return mi.y;
	}

	public void revalidateDiscovered() {

		if (this.getPlayer() != null) {

			for (int x = 0; x < map_width; x++) {
				for (int y = 0; y < map_height; y++) {
					if (!discovered[x][y]) {
						if (tiles[x][y].isInFullView(this)) {
							discover(x, y);
						} else {
							undiscover(x, y);
						}
					}
				}
			}
		}
	}

	public long getTime() {
		return tm.getTime();
	}

}
