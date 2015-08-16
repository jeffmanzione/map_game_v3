package units;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;

import nations.Nation;
import maps.Drawable;
import maps.GameMap;
import tiles.Colors;
import tiles.Tile;
import generic.Discrete;
import generic.Garrisonable;
import gfx.ResourceManager;

public abstract class Unit implements Discrete, Drawable {

	protected Tile current;

	protected GameMap map;

	private int direction = 0;

	protected Discrete owner;

	public Unit(Discrete owner) {
		current = null;
		this.owner = owner;
		owner.addUnit(this);
	}

	public void setGameMap(GameMap map) {
		this.map = map;
	}

	public Tile getCurrent() {
		return current;
	}

	protected BufferedImage getImage(int scale) {
		return ResourceManager.get(getType(), scale / 2);
	}

	private void drawGraphic(Graphics2D g, int drawX, int drawY, int scale,
			GameMap map, boolean hover, boolean selected, int xOff, int yOff,
			double fraction) {

		BufferedImage img = getImage(scale);

		if (img != null) {

			if ((direction >= 0 && direction < 180)) {
				g.drawImage(
						img,
						(int) (drawX + (scale - img.getWidth() / 2) + (xOff * fraction))
								+ img.getWidth(),
						(int) (drawY + (scale - img.getHeight() / 2) - (yOff * fraction)),
						(int) (drawX + (scale - img.getWidth() / 2) + (xOff * fraction)),
						(int) (drawY + (scale - img.getHeight() / 2) - (yOff * fraction))
								+ img.getHeight(), 0, 0, img.getWidth(),
						img.getHeight(), null);
			} else {
				g.drawImage(
						img,
						(int) (drawX + (scale - img.getWidth() / 2) + (xOff * fraction)),
						(int) (drawY + (scale - img.getHeight() / 2) - (yOff * fraction)),
						null);
			}
		}
	}

	public boolean draw(Graphics2D g, int x, int y, int drawX, int drawY,
			int scale, GameMap map, boolean hover, boolean selected, boolean fancy) {

		try {

			int xOff = 0, yOff = 0;

			double fraction = 0;
			if (isEnRoute) {
				xOff = getXOffset(scale);
				yOff = getYOffset(scale);
				fraction = determineFraction();
			}

			if (map.getSelected() == this) {
				BufferedImage select = ResourceManager.get("selection", scale);
				AffineTransform at = AffineTransform.getRotateInstance(
						Math.toRadians(map.getCounter() * 360 / 64 / 4),
						select.getWidth() / 2, select.getHeight() / 2);
				AffineTransformOp op = new AffineTransformOp(at,
						AffineTransformOp.TYPE_BILINEAR);

				g.drawImage(op.filter(select, null),
						(int) (drawX + (xOff * fraction)),
						(int) (drawY - (yOff * fraction)), null);
			}

			this.drawGraphic(g, drawX, drawY, scale, map, hover, selected,
					xOff, yOff, fraction);

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	public Nation getNation() {
		if (owner != null) {
			return owner.getNation();
		} else {
			return null;
		}
	}

	public String toString() {
		return this.getNation().getName() + " " + this.getType();
	}

	protected int TEXT_SZ = 10;

	public boolean drawLabel(Graphics2D g, int x, int y, int drawX, int drawY,
			int scale, GameMap map, boolean hover, boolean selected, boolean fancy) {

		String str = toString();

		g.setFont(g.getFont().deriveFont((float) TEXT_SZ));

		FontMetrics met = g.getFontMetrics();

		int xOff = 0, yOff = 0;

		double fraction = 0;

		if (isEnRoute) {
			xOff = getXOffset(scale);
			yOff = getYOffset(scale);
			fraction = determineFraction();
		}

		Color bg, fg;

		if (hover) {

			Rectangle2D bounds = g.getFontMetrics().getStringBounds(str, g);
			int strWidth = (int) bounds.getWidth();
			int strHeight = met.getHeight();
			int strAsc = met.getAscent();

			int yOffset = Math.min(scale * 6 / 4 + strHeight, scale * 2);
			int xOffset = scale - strWidth / 2;

			bg = Colors.SELECT_BG;
			fg = Colors.HOVER_FG;

			g.setColor(bg);
			g.fillRect((int) (drawX + xOffset - 1 + (xOff * fraction)),
					(int) (drawY + yOffset - strAsc - (yOff * fraction)),
					strWidth + 2, strHeight);

			g.setColor(fg);
			g.drawString(str, (int) (drawX + xOffset + (xOff * fraction)),
					(int) (drawY + yOffset - (yOff * fraction)));

		} else if (scale > 8) {

			if (this instanceof Garrisonable
					&& !(current.hasStructure() && current.getStructure() instanceof Garrisonable)) {

				Garrisonable gu = (Garrisonable) this;
				String garrison;

				int num = gu.getGarrison();

				if (num >= 1000) {
					garrison = (num / 1000) + "K";
				} else {
					garrison = num + "";
				}

				Rectangle2D bounds = g.getFontMetrics().getStringBounds(
						garrison, g);
				int strWidth = (int) bounds.getWidth();
				int strHeight = met.getHeight();
				int strAsc = met.getAscent();

				int yOffset = Math.min(scale * 6 / 4 + strHeight, scale * 2);
				int xOffset = scale - strWidth / 2;

				bg = Colors.SELECT_BG;
				fg = Colors.HOVER_FG;

				g.setColor(bg);
				g.fillRect((int) (drawX + xOffset - 2 + (xOff * fraction)),
						(int) (drawY + yOffset - strAsc - (yOff * fraction)),
						strWidth + 3, strHeight);

				g.setColor(fg);
				g.drawString(garrison,
						(int) (drawX + xOffset + (xOff * fraction)),
						(int) (drawY + yOffset - (yOff * fraction)));

			}
		}

		return true;
	}

	public int getXOffset(int scale) {
		int angle = simpleAngle(direction);
		int xOff = 0;
		if (angle > 0 && angle < 180)
			xOff = scale * 2;
		else if (angle > 180 && angle < 360)
			xOff = -scale * 2;

		return xOff;
	}

	public int getYOffset(int scale) {
		int angle = simpleAngle(direction);
		int yOff = 0;
		if (angle > 270 || angle < 90) {
			yOff = scale * 2;
		} else if (angle > 90 && angle < 270) {
			yOff = -scale * 2;
		}
		return yOff;

	}

	public int simpleAngle(int d) {
		return d % 360;
	}

	public String getType() {
		return this.getClass().getSimpleName();
	}

	private Tile destination;
	private List<Tile> path = new ArrayList<Tile>();
	private boolean isEnRoute = false;

	public boolean isEnRoute() {
		return isEnRoute;
	}

	private static final double SQRT2 = Math.sqrt(2);

	public void createPathTo(Tile t) {
		if (canPass(t) && (isEnRoute && destination != t)
				|| (!isEnRoute && current != t)) {

			// stops the unit
			// map.removeEnRoute(this);

			// sets the destination to the parameter tile
			destination = t;

			// starts a thread for the method to not waste time
			Thread pathFind = new Thread() {
				public void run() {
					// the pos is the current position being checked
					Tile pos = current;

					// the arrays that store stuff
					final Tile[][] tiles = new Tile[map.map_width][map.map_height];
					final double[][] costs = new double[map.map_width][map.map_height];
					final Tile[][] preds = new Tile[map.map_width][map.map_height];

					// this set holds all of the visited tiles
					final Set<Tile> visited = new HashSet<Tile>();

					// this holds the tiles
					final TreeSet<Tile> cheapest = new TreeSet<Tile>(
							new Comparator<Tile>() {
								public int compare(Tile t1, Tile t2) {
									if (t1 == t2 || t1.equals(t2))
										return 0;

									boolean ct1 = visited.contains(t1);
									boolean ct2 = visited.contains(t2);

									if (ct1 && !ct2)
										return 1;
									else if (ct2 && !ct1)
										return -1;
									else {
										double num = costs[t1.x][t1.y]
												- costs[t2.x][t2.y];

										if (num == 0) {
											// Should never get into the first
											// if (EVER)
											if (t1.hashCode() == t2.hashCode())
												return 0;
											else if (t1.hashCode() > t2
													.hashCode())
												return 1;
											else
												return -1;
										} else if (num > 0)
											return 1;
										else
											return -1;
									}
								}
							});

					tiles[pos.x][pos.y] = pos;
					costs[pos.x][pos.y] = 0;
					preds[pos.x][pos.y] = null;

					// loop to iterate
					while (true) {
						// adds the current position to the visited set

						int x = pos.x;
						int y = pos.y;

						cheapest.remove(pos);
						visited.add(pos);

						// if it turns out that the pos is the destination,
						// do something
						if (pos == destination) {
							path = new ArrayList<Tile>();
							// double bestCost = costs[pos.x][pos.y];
							// basically adds the path with the smallest cost.
							while (pos != current) {
								path.add(pos);
								pos = preds[pos.x][pos.y];
							}

							path.add(current);

							// System.out.println("PATH: " +
							// String.format("%1$.2f", bestCost) + ": " + path);

							if (path.size() != 0) {
								isEnRoute = true;

								if (path.size() > 1) {
									travelCost = (int) path
											.get(path.size() - 2)
											.getPassageCost();
									direction = map.getDirectionFrom(current,
											path.get(path.size() - 2));
									// System.out.println(direction);
								}
							}

							// adds the order;
							// map.addEnRoute(Unit.this);
							// alertPath();

							map.repaint();
							;

							return;
						}

						// The neighbor tiles
						List<Tile> neighbors = getPassableTilesAround(pos);

						// looping through all of the neighbor tiles
						for (Tile neighbor : neighbors) {
							// if the tile has not been visited
							if (canPass(neighbor)) {
								int nX = neighbor.x;
								int nY = neighbor.y;

								double factor = neighbor.getPassageCost();

								// if(neighbor. instanceof River ||
								// neighbor.getItem() instanceof Forest)
								// factor*=1.5;
								// else if(neighbor.getItem() instanceof
								// Settlement) factor*=0.8;

								// double elevationDifference =
								// (world.elevation[nX][nY] -
								// world.elevation[x][y])/256;

								// if(elevationDifference < 0)
								// elevationDifference = 0;

								// calculates the cost from this position to
								// this neighbor
								double cost = costs[x][y]
										+ (((Math.abs(x - nX) + Math
												.abs(y - nY)) % 2 == 0) ? SQRT2
												: 1.000) * (factor /*
																	 * +
																	 * elevationDifference
																	 */);
								// System.out.println(factor);
								// if neighbor already has data
								if (tiles[nX][nY] != null) {
									// Calculates the cost already in the list
									double existingCost = costs[nX][nY];
									// if the new cost is less than the old one,
									// change the values to the new one
									if (existingCost > cost) {
										cheapest.remove(neighbor);
										costs[nX][nY] = cost;
										preds[nX][nY] = pos;
										cheapest.add(neighbor);
									}
								} /* otherwise add a new entry */else {
									tiles[nX][nY] = neighbor;
									costs[nX][nY] = cost;
									preds[nX][nY] = pos;
									cheapest.add(neighbor);
								}
							}
						}

						if (cheapest.size() == 0) {
							System.out.println("FAIL");
							return;
						}

						// go to the next cheapest one

						pos = cheapest.first();

					}
				}
			};
			pathFind.start();
		}
	}

	public void addWaypoint(Tile t) {
		if (!isEnRoute) {
			createPathTo(t);
		} else {
			if (canPass(t) && destination != t) {
				// sets the destination to the parameter tile

				final Tile currentDest = destination;
				destination = t;

				// starts a thread for the method to not waste time
				Thread pathFind = new Thread() {
					public void run() {
						// the pos is the current position being checked
						Tile pos = path.get(0);

						// the arrays that store stuff
						final Tile[][] tiles = new Tile[map.map_width][map.map_height];
						final double[][] costs = new double[map.map_width][map.map_height];
						final Tile[][] preds = new Tile[map.map_width][map.map_height];

						// this set holds all of the visited tiles
						final Set<Tile> visited = new HashSet<Tile>();

						// this holds the tiles
						final TreeSet<Tile> cheapest = new TreeSet<Tile>(
								new Comparator<Tile>() {
									public int compare(Tile t1, Tile t2) {
										if (t1 == t2 || t1.equals(t2))
											return 0;

										boolean ct1 = visited.contains(t1);
										boolean ct2 = visited.contains(t2);

										if (ct1 && !ct2)
											return 1;
										else if (ct2 && !ct1)
											return -1;
										else {
											double num = costs[t1.x][t1.y]
													- costs[t2.x][t2.y];

											if (num == 0) {
												// Should never get into the
												// first if (EVER)
												if (t1.hashCode() == t2
														.hashCode())
													return 0;
												else if (t1.hashCode() > t2
														.hashCode())
													return 1;
												else
													return -1;
											} else if (num > 0)
												return 1;
											else
												return -1;
										}
									}
								});

						tiles[pos.x][pos.y] = pos;
						costs[pos.x][pos.y] = 0;
						preds[pos.x][pos.y] = null;

						// loop to iterate
						while (true) {
							// adds the current position to the visited set

							int x = pos.x;
							int y = pos.y;

							cheapest.remove(pos);
							visited.add(pos);

							// if it turns out that the pos is the destination,
							// do something
							if (pos == destination) {
								// double bestCost = costs[pos.x][pos.y];
								// basically adds the path with the smallest
								// cost.
								List<Tile> olds = path;
								path = new ArrayList<Tile>();
								while (pos != currentDest) {
									path.add(pos);
									pos = preds[pos.x][pos.y];
								}

								path.addAll(olds);

								travelStart = map.getTime();

								map.repaint();

								// System.out.println("PATH: " +
								// String.format("%1$.2f", bestCost) + ": " +
								// path);

								return;
							}

							// The neighbor tiles
							List<Tile> neighbors = getPassableTilesAround(pos);

							// looping through all of the neighbor tiles
							for (Tile neighbor : neighbors) {
								// if the tile has not been visited
								if (canPass(neighbor)) {
									int nX = neighbor.x;
									int nY = neighbor.y;

									double factor = neighbor.getPassageCost();

									// if(neighbor. instanceof River ||
									// neighbor.getItem() instanceof Forest)
									// factor*=1.5;
									// else if(neighbor.getItem() instanceof
									// Settlement) factor*=0.8;

									// double elevationDifference =
									// (world.elevation[nX][nY] -
									// world.elevation[x][y])/256;

									// if(elevationDifference < 0)
									// elevationDifference = 0;

									// calculates the cost from this position to
									// this neighbor
									double cost = costs[x][y]
											+ (((Math.abs(x - nX) + Math.abs(y
													- nY)) % 2 == 0) ? SQRT2
													: 1.000) * (factor /*
																		 * +
																		 * elevationDifference
																		 */);
									// System.out.println(factor);
									// if neighbor already has data
									if (tiles[nX][nY] != null) {
										// Calculates the cost already in the
										// list
										double existingCost = costs[nX][nY];
										// if the new cost is less than the old
										// one, change the values to the new one
										if (existingCost > cost) {
											cheapest.remove(neighbor);
											costs[nX][nY] = cost;
											preds[nX][nY] = pos;
											cheapest.add(neighbor);
										}
									} /* otherwise add a new entry */else {
										tiles[nX][nY] = neighbor;
										costs[nX][nY] = cost;
										preds[nX][nY] = pos;
										cheapest.add(neighbor);
									}
								}
							}

							if (cheapest.size() == 0) {
								System.out.println("FAIL");
								return;
							}

							// go to the next cheapest one

							pos = cheapest.first();

						}
					}
				};
				pathFind.start();
			}
		}
	}

	public List<Tile> getPassableTilesAround(Tile tile) {
		return map.getPassableLandTilesAround(tile);
	}

	public boolean canPass(Tile tile) {
		return tile.isLand();
	}

	public void setCurrent(Tile tile) {
		current = tile;
	}

	public void passOrder(Tile tile) {
		createPathTo(tile);
	}

	int currentTraveled = 0;
	int travelCost = 0;

	double prevFraction = 0;

	long travelStart;

	public double determineFraction() {
		// double fraction;

		// fraction =
		// Double.valueOf((double)Math.round(map.getCounterForMoving()) /
		// map.getMaxCounter());
		//
		// if (fraction != 1) {
		// prevFraction = ((double) currentTraveled + fraction) / travelCost;
		// } else if (fraction >= 1.0) {
		// prevFraction = ((double) currentTraveled) / travelCost;
		// }
		//
		// return prevFraction;

		prevFraction = (((currentTraveled * 1.0 + (map.getTime() - travelStart) * 1.0 / 8)) / travelCost) 
				;

		//System.out.println(currentTraveled + "\t" + map.getTime() + "\t" + travelStart + "\t" + travelCost + "\t" + prevFraction);

		return prevFraction;
	}

	public void advance() {
		travelStart = map.getTime();
		if (isEnRoute) {

			try {

				if (path.size() > 0) {

					currentTraveled += 1;

					Tile next = path.get(path.size() - 2);
					travelCost = (int) next.getPassageCost();

					if (travelCost <= currentTraveled) {

						path.remove(path.size() - 1);

						if (path.size() > 1) {
							direction = map.getDirectionFrom(next,
									path.get(path.size() - 2));
							direction = simpleAngle(direction);
						} else {
							isEnRoute = false;
							path.clear();
						}

						currentTraveled = 0;
						map.put(this, next);
						travelCost = (int) path.get(path.size() - 2)
								.getPassageCost();
					}
				}

			} catch (Exception e) {
				isEnRoute = false;
				// map.removeEnRoute(this);
				path.clear();
				currentTraveled = 0;
			}
		}
	}

	public void advanceMonth() {
		manpowerGrowth = -5;
		moneyGrowth = -5;
		if (this instanceof Garrisonable) {
			Garrisonable gu = (Garrisonable) this;
			manpowerGrowth -= gu.getManpowerCostGarrison();
			moneyGrowth -= gu.getMoneyCostGarrison();
		}
	}

	public void advanceYear() {

	}

	public List<Tile> getPath() {
		return path;
	}

	public void addUnit(Unit u) {
		try {
			throw new Exception("Cannot Add Unit to Unit");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected double manpowerGrowth = 0;
	protected double moneyGrowth = 0;
	protected double adminGrowth = 0;
	protected double diploGrowth = 0;
	protected double militGrowth = 0;

	public double getExactManpowerGrowth() {
		return manpowerGrowth;
	}

	public int getManpowerGrowth() {
		return (int) manpowerGrowth;
	}

	public double getExactMoneyGrowth() {
		return moneyGrowth;
	}

	public int getMoneyGrowth() {
		return (int) moneyGrowth;
	}

}
