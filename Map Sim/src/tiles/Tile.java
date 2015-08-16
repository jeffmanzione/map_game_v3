package tiles;

import generic.Discrete;
import item.structures.Structure;
import item.structures.cities.Settlement;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import nations.Nation;
import nations.Region;
import maps.Drawable;
import maps.GameMap;
import units.Unit;

/**
 * Tile that will make up all of the locations on the map
 * 
 * @author Jeff
 *
 */
public class Tile implements Drawable, Discrete {

	private Continent continent;

	private LandType[] type;
	private QuadState[] quads;

	private int unitSlots = 1;

	private List<Unit> units;

	private Structure struct = null;

	private Region owner = null;

	private double cost = 2;

	private boolean isLand = false;

	public final int x, y;

	public static final Color SHADOW = new Color(0, 0, 0, 100);

	private boolean hasRoad = false;

	/**
	 * Tile Constructor
	 * 
	 * @param type
	 *            The four LandTypes that make up the tile
	 */
	public Tile(LandType[] type, int x, int y) {
		this.x = x;
		this.y = y;
		this.type = type;
		quads = new QuadState[type.length];

		int landCount = 0;
		int shallows = 0;
		for (LandType quad : type) {
			if (quad.isLand()) {
				landCount++;
			}

			if (quad.isShallow()) {
				shallows++;
			} else if (quad.isDeepSea()) {
				cost += 5.0;
			} else if (quad.isRiver()) {
				cost += 0.5;
			} else if (quad.isLowMountain()) {
				cost += 0.5;
			} else if (quad.isMountainCap()) {
				cost += 8.0;
			} else if (quad.isMountainPeak()) {
				cost += 6.0;
			} else if (quad.isMountainType()) {
				cost += 1.0;
			} else if (quad.isForest()) {
				cost += 0.25;
			}
		}

		if (landCount > 1) {
			isLand = true;
		} else {
			cost -= ((double) shallows) * 0.25;
		}

		units = new CopyOnWriteArrayList<Unit>();
	}

	public boolean hasRoad() {
		return hasRoad;
	}

	public void setRoad(boolean val) {
		hasRoad = val;
	}

	public double getPassageCost() {
		return cost;
	}

	public void smooth(GameMap map, int x, int y) {

		Tile above = map.tileAt(x, y - 1);
		Tile below = map.tileAt(x, y + 1);
		Tile left = map.tileAt(x - 1, y);
		Tile right = map.tileAt(x + 1, y);
		Tile aboveLeft = map.tileAt(x - 1, y - 1);
		Tile aboveRight = map.tileAt(x + 1, y - 1);
		Tile belowLeft = map.tileAt(x - 1, y + 1);
		Tile belowRight = map.tileAt(x + 1, y + 1);

		calculateSmoothEdges(TileGroup.createForUpperLeft(this, above, left, aboveLeft),
				TileGroup.createForUpperRight(this, above, right, aboveRight),
				TileGroup.createForLowerLeft(this, below, left, belowLeft),
				TileGroup.createForLowerRight(this, below, right, belowRight));
	}

	/**
	 * Sets the structure for the tile. Should call {@link #hasStructure} first.
	 * 
	 * @param struct
	 *            The structure to place
	 * @throws TileAlreadyHasStructureException
	 *             If the tile already has a structure
	 */
	public void placeStructure(Structure struct) throws TileAlreadyHasStructureException {

		if (hasStructure()) {
			throw new TileAlreadyHasStructureException();
		} else {
			this.struct = struct;
			struct.setLocation(this);
		}
	}

	/**
	 * Checks if the tile has a structure
	 * 
	 * @return <b>true</b> if the tile has a structure <b>false</b> if the tile does not have a structure
	 */
	public boolean hasStructure() {
		return struct != null;
	}

	/**
	 * Removes the structure of the tile if it has one. Should call {@link #hasStructure} first.
	 * 
	 * @return the structure that was removed
	 * 
	 * @throws TileDoesNotHaveAStructureException
	 *             If the tile does not have a structure
	 */
	public Structure removeStructure() throws TileDoesNotHaveAStructureException {

		if (struct == null) {
			throw new TileDoesNotHaveAStructureException();
		}
		Structure tmp = struct;
		struct = null;
		return tmp;
	}

	/**
	 * Replaces the current structure with a new one. Should call {@link #hasStructure} first.
	 * 
	 * @param newStructure
	 *            The structure to replace the existing structure on this tile.
	 * @return The previous structure
	 * @throws TileDoesNotHaveAStructureException
	 *             If the tile does not have a structure
	 */
	public Structure replaceStructureWith(Structure newStructure) throws TileDoesNotHaveAStructureException {

		Structure tmp = struct;
		struct = newStructure;
		return tmp;
	}

	/**
	 * Gets whether or not a unit can be put on this tile.
	 * 
	 * @return <b>true</b> if a unit can be put on this tile, <b>false</b> if there is no space for the unit.
	 */
	public boolean canPutUnit() {
		return units.size() < unitSlots;
	}

	/**
	 * Puts a unit on this tile. Should call {@link canPutUnit} first.
	 * 
	 * @param unit
	 *            The unit to place on the tile.
	 * @throws TileHasMaxNumberOfUnitsException
	 *             If the tile cannot hold any more units.
	 */
	public void put(Unit unit) throws TileHasMaxNumberOfUnitsException {

		if (!canPutUnit()) {
			throw new TileHasMaxNumberOfUnitsException();
		}

		if (unit.getCurrent() != null) {
			unit.getCurrent().remove(unit);
		}

		units.add(unit);
		unit.setCurrent(this);

	}

	public void remove(Unit unit) {
		units.remove(unit);
	}

	/**
	 * Gets an iterable of the units on the tile.
	 * 
	 * @return An iterable of the units on the tile.
	 */
	public Iterable<Unit> getUnits() {
		return units;
	}

	/**
	 * Removes the unit from the specified index.
	 * 
	 * @param index
	 *            The index for the unit to be removed.
	 * @return The unit removed
	 * @throws NoUnitAtSpecifiedIndexException
	 *             If there is no unit at that index
	 */
	public Unit remove(int index) throws NoUnitAtSpecifiedIndexException {
		if (units.size() > index) {
			return units.remove(index);
		} else {
			throw new NoUnitAtSpecifiedIndexException();
		}
	}

	/**
	 * Draws the tile on the specified graphics location.
	 * 
	 */
	public boolean draw(Graphics2D g, int x, int y, int drawX, int drawY, int scale, GameMap map, boolean hover,
			boolean selected, boolean fancy) {
		try {

			drawQuad(0, g, x, y, drawX, drawY, scale, fancy, map);
			drawQuad(1, g, x, y, drawX + scale, drawY, scale, fancy, map);
			drawQuad(2, g, x, y, drawX, drawY + scale, scale, fancy, map);
			drawQuad(3, g, x, y, drawX + scale, drawY + scale, scale, fancy, map);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void drawQuad(int which, Graphics2D g, int x, int y, int drawX, int drawY, int scale, boolean fancy,
			GameMap map) {
		QuadState quad = quads[which];
		// System.out.println(scale);
		Color maj;
		Color min;
		QuadState.Corner corner;

		corner = quad.corners[0];
		maj = corner.maj.disp;

		if (corner.state == QuadState.State.CONVEX) {
			min = corner.min.disp;

			g.setColor(min);
			g.fillRect(drawX, drawY, scale / 2, scale / 2);

			g.setColor(maj);
			// if (scale >= 8) {
			g.fillArc(drawX, drawY, scale, scale, 90, 90);
			// }

		} else {
			g.setColor(maj);
			g.fillRect(drawX, drawY, scale / 2, scale / 2);

		}

		corner = quad.corners[1];
		maj = corner.maj.disp;
		if (corner.state == QuadState.State.CONVEX) {
			min = corner.min.disp;

			g.setColor(min);
			g.fillRect(drawX + scale / 2, drawY, scale / 2, scale / 2);

			g.setColor(maj);
			// if (scale >= 8) {
			g.fillArc(drawX, drawY, scale, scale, 0, 90);
			// }
		} else {
			g.setColor(maj);
			g.fillRect(drawX + scale / 2, drawY, scale / 2, scale / 2);
		}

		corner = quad.corners[2];
		maj = corner.maj.disp;
		if (corner.state == QuadState.State.CONVEX) {
			min = corner.min.disp;

			g.setColor(min);
			g.fillRect(drawX, drawY + scale / 2, scale / 2, scale / 2);

			g.setColor(maj);
			// if (scale >= 8) {
			g.fillArc(drawX, drawY, scale, scale, 180, 90);
			// }
		} else {
			g.setColor(maj);
			g.fillRect(drawX, drawY + scale / 2, scale / 2, scale / 2);
		}

		corner = quad.corners[3];
		maj = corner.maj.disp;
		if (corner.state == QuadState.State.CONVEX) {
			min = corner.min.disp;

			g.setColor(min);
			g.fillRect(drawX + scale / 2, drawY + scale / 2, scale / 2, scale / 2);

			g.setColor(maj);
			// if (scale >= 8) {
			g.fillArc(drawX, drawY, scale, scale, 270, 90);
			// }
		} else {
			g.setColor(maj);
			g.fillRect(drawX + scale / 2, drawY + scale / 2, scale / 2, scale / 2);
		}

	}

	private void calculateSmoothEdges(TileGroup ul, TileGroup ur, TileGroup ll, TileGroup lr) {

		quads[0] = new QuadState(type[0], ul);
		quads[1] = new QuadState(type[1], ur);
		quads[2] = new QuadState(type[2], ll);
		quads[3] = new QuadState(type[3], lr);

	}

	public LandType getUpperLeft() {
		return type[0];
	}

	public LandType getUpperRight() {
		return type[1];
	}

	public LandType getLowerLeft() {
		return type[2];
	}

	public LandType getLowerRight() {
		return type[3];
	}

	public Structure getStructure() {
		return struct;
	}

	public boolean drawLabel(Graphics2D g, int x, int y, int drawX, int drawY, int scale, GameMap map, boolean hover,
			boolean selected, boolean fancy) {
		if (hasStructure()) {
			struct.drawLabel(g, x, y, drawX, drawY, scale, map, hover, selected, fancy);
		}

		if (units.size() > 0 && this.isInFullView(map)) {
			units.get(0).drawLabel(g, x, y, drawX, drawY, scale, map, hover, selected, fancy);
		}

		return true;
	}

	public boolean drawOwnership(Graphics2D g, int x, int y, int drawX, int drawY, int scale, GameMap map,
			boolean hover, boolean selected, boolean fancy) {

		g.setStroke(new BasicStroke(scale == 1 ? 1 : 2));

		Tile northy = map.getTileInDirectionOf(x, y, GameMap.NORTH);
		Tile easty = map.getTileInDirectionOf(x, y, GameMap.EAST);
		Tile southy = map.getTileInDirectionOf(x, y, GameMap.SOUTH);
		Tile westy = map.getTileInDirectionOf(x, y, GameMap.WEST);
		Tile nwy = map.getTileInDirectionOf(x, y, GameMap.NORTH_WEST);
		Tile ney = map.getTileInDirectionOf(x, y, GameMap.NORTH_EAST);
		Tile swy = map.getTileInDirectionOf(x, y, GameMap.SOUTH_WEST);
		Tile sey = map.getTileInDirectionOf(x, y, GameMap.SOUTH_EAST);

		if (fancy) {

			if (hasOwner()) {
				Color primary;
				if (map.getMapMode() == GameMap.HYBRID) {
					primary = owner.getOwner().getOwner().primary;
				} else {
					primary = this.owner.color;
				}
				primary = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 100);

				Color secondary = owner.getOwner().getOwner().secondary;
				secondary = new Color(secondary.getRed(), secondary.getGreen(), secondary.getBlue(), 100);

				int count = 0;

				boolean north = false, south = false, east = false, west = false, nw = false, ne = false, sw = false, se = false;

				if (northy != null && northy.getOwner() == this.getOwner()) {
					count++;
					north = true;
				}
				if (southy != null && southy.getOwner() == this.getOwner()) {
					count++;
					south = true;
				}
				if (easty != null && easty.getOwner() == this.getOwner()) {
					count++;
					east = true;
				}
				if (westy != null && westy.getOwner() == this.getOwner()) {
					count++;
					west = true;
				}
				if (nwy != null && nwy.getOwner() == this.getOwner()) {
					nw = true;
				}
				if (ney != null && ney.getOwner() == this.getOwner()) {
					ne = true;
				}
				if (swy != null && swy.getOwner() == this.getOwner()) {
					sw = true;
				}
				if (sey != null && sey.getOwner() == this.getOwner()) {
					se = true;
				}

				g.setColor(primary);
				if (count > 2 || (north && south) || (east && west) || (north && east && sw) || (north && west && se)
						|| (south && east && nw) || (south && west && ne)) {
					g.fillRect(drawX, drawY, scale * 2, scale * 2);

					// if (fancy) {

					g.setColor(secondary);
					if (!west && !nw) {
						g.drawLine(drawX, drawY, drawX, drawY + scale);
					}
					if (!south && !se) {
						g.drawLine(drawX + scale, drawY + scale * 2 - 1, drawX + scale * 2, drawY + scale * 2 - 1);
					}
					if (!south && !sw) {
						g.drawLine(drawX, drawY + scale * 2 - 1, drawX + scale, drawY + scale * 2 - 1);
					}
					if (!east && !ne) {
						g.drawLine(drawX + scale * 2 - 1, drawY, drawX + scale * 2 - 1, drawY + scale);
					}
					if (!north && !ne) {
						g.drawLine(drawX + scale, drawY, drawX + scale * 2, drawY);
					}
					if (!west && !sw) {
						g.drawLine(drawX, drawY + scale, drawX, drawY + scale * 2);
					}
					if (!north && !nw) {
						g.drawLine(drawX, drawY, drawX + scale, drawY);
					}
					if (!east && !se) {
						g.drawLine(drawX + scale * 2 - 1, drawY + scale, drawX + scale * 2 - 1, drawY + scale * 2);
					}
					// }

				} else if (north && east || north && se || east && nw) {
					g.fillRect(drawX, drawY, scale * 2, scale);
					g.fillRect(drawX + scale, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 90);
					// }

					// if (fancy) {
					g.setColor(secondary);
					// if (fancy) {
					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 180, 90);
					// }
					if (!nw) {
						g.drawLine(drawX, drawY, drawX, drawY + scale);
					}
					if (!ne && !north) {
						g.drawLine(drawX + scale, drawY, drawX + scale * 2, drawY);
					}
					if (!se) {
						g.drawLine(drawX + scale, drawY + scale * 2 - 1, drawX + scale * 2, drawY + scale * 2 - 1);
					}
					// }

				} else if (north && west || north && sw || west && ne) {
					g.fillRect(drawX, drawY, scale * 2, scale);
					g.fillRect(drawX, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 270, 90);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 270, 90);

					if (!sw) {
						g.drawLine(drawX, drawY + scale * 2 - 1, drawX + scale, drawY + scale * 2 - 1);
					}
					if (!ne) {
						g.drawLine(drawX + scale * 2 - 1, drawY, drawX + scale * 2 - 1, drawY + scale);
					}
					// }

				} else if (south && east || south && ne || east && sw) {
					g.fillRect(drawX, drawY + scale, scale * 2, scale);
					g.fillRect(drawX + scale, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 90);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 90, 90);

					if (!ne) {
						g.drawLine(drawX + scale, drawY, drawX + scale * 2, drawY);
					}
					if (!sw) {
						g.drawLine(drawX, drawY + scale, drawX, drawY + scale * 2);
					}
					// }

				} else if (south && west || south && nw || west && se) {
					g.fillRect(drawX, drawY + scale, scale * 2, scale);
					g.fillRect(drawX, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 90);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 0, 90);

					if (!nw) {
						g.drawLine(drawX, drawY, drawX + scale, drawY);
					}
					if (!se) {
						g.drawLine(drawX + scale * 2 - 1, drawY + scale, drawX + scale * 2 - 1, drawY + scale * 2);
					}
					// }
				} else if (!sw && !north && !south && !east && !west && !se && !ne && !nw) {
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 360);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 0, 360);

					// }

				} else if (sw && !north && !south && !east && !west && !se && !ne && !nw) {
					g.fillRect(drawX, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, -90, 270);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, -90, 270);

					// }

				} else if (nw && !north && !south && !east && !west && !se && !ne && !sw) {
					g.fillRect(drawX, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 270);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 180, 270);

					// }

				} else if (se && !north && !south && !east && !west && !nw && !ne && !sw) {
					g.fillRect(drawX + scale, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 270);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 0, 270);
					// }

				} else if (ne && !north && !south && !east && !west && !nw && !se && !sw) {
					g.fillRect(drawX + scale, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 270);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 90, 270);
					// }

				} else if (north || (ne && nw)) {
					g.fillRect(drawX, drawY, scale * 2, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 180);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 180, 180);

					if (!east && !ne) {
						g.drawLine(drawX + scale * 2 - 1, drawY, drawX + scale * 2 - 1, drawY + scale);
					}
					if (!west && !nw) {
						g.drawLine(drawX, drawY, drawX, drawY + scale);
					}
					// }

				} else if (south || (se && sw)) {
					g.fillRect(drawX, drawY + scale, scale * 2, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 180);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 0, 180);

					if (!east && !se) {
						g.drawLine(drawX + scale * 2 - 1, drawY + scale, drawX + scale * 2 - 1, drawY + scale * 2);
					}
					if (!west && !sw) {
						g.drawLine(drawX, drawY + scale, drawX, drawY + scale * 2);
					}
					// }

				} else if (east || (ne && se)) {
					g.fillRect(drawX + scale, drawY, scale, scale * 2);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 180);
					// }

					g.setColor(secondary);
					// if (fancy) {
					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 90, 180);
					// }

					if (!north && !ne) {
						g.drawLine(drawX + scale, drawY, drawX + scale * 2, drawY);
					}
					if (!south && !se) {
						g.drawLine(drawX + scale, drawY + scale * 2 - 1, drawX + scale * 2, drawY + scale * 2 - 1);
					}

				} else if (west || (nw && sw)) {
					g.fillRect(drawX, drawY, scale, scale * 2);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 270, 180);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 270, 180);

					if (!north && !nw) {
						g.drawLine(drawX, drawY, drawX + scale, drawY);
					}
					if (!south && !sw) {
						g.drawLine(drawX, drawY + scale * 2 - 1, drawX + scale, drawY + scale * 2 - 1);
					}
					// }
				} else if (ne && sw) {
					g.fillRect(drawX + scale, drawY, scale, scale);
					g.fillRect(drawX, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 270, 90);
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 90);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2, scale * 2, 270, 90);
					g.drawArc(drawX, drawY, scale * 2, scale * 2, 90, 90);
					// }
				} else if (nw && se) {
					g.fillRect(drawX + scale, drawY + scale, scale, scale);
					g.fillRect(drawX, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 90);
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 90);

					g.setColor(secondary);

					g.drawArc(drawX, drawY, scale * 2, scale * 2, 0, 90);
					g.drawArc(drawX, drawY, scale * 2, scale * 2, 180, 90);
					// }
				}

			}

			Color primary = null;

			Area region = new Area(new Rectangle(drawX, drawY, scale * 2, scale * 2));

			if (northy != null && northy.hasOwner() && northy.getOwner() != getOwner()
					&& northy.getOwner() == southy.getOwner() && northy.getOwner() == easty.getOwner()
					&& northy.getOwner() == westy.getOwner()) {
				if (map.getMapMode() == GameMap.HYBRID) {
					primary = northy.getOwner().primary;
				} else {
					primary = northy.owner.color;
				}
				// if (fancy) {

				primary = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 100);
				g.setColor(primary);

				Area ellipse = new Area(new Ellipse2D.Double(drawX, drawY, scale * 2 - 1, scale * 2 - 1));

				region.subtract(ellipse);
				g.fill(region);

				// if (fancy) {
				Color secondary = northy.getOwner().secondary;
				secondary = new Color(secondary.getRed(), secondary.getGreen(), secondary.getBlue(), 100);

				g.setColor(secondary);

				g.draw(ellipse);
				// }
				// }
			} else if (northy != null && northy.hasOwner() && northy.getOwner() != getOwner()
					&& northy.getOwner() == easty.getOwner() && northy.getOwner() == westy.getOwner()) {
				if (map.getMapMode() == GameMap.HYBRID) {
					primary = northy.getOwner().primary;
				} else {
					primary = northy.owner.color;
				}
				primary = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 100);

				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale * 2, scale)));
				// if (fancy) {
				g.setColor(primary);
				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 0, 180, Arc2D.PIE)));
				// }

				g.fill(region);

				// if (fancy) {
				Color secondary = northy.getOwner().secondary;
				secondary = new Color(secondary.getRed(), secondary.getGreen(), secondary.getBlue(), 100);

				g.setColor(secondary);

				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 0, 180);
				// }

			} else if (northy != null && northy.hasOwner() && northy.getOwner() != getOwner()
					&& northy.getOwner() == southy.getOwner() && northy.getOwner() == westy.getOwner()) {
				if (map.getMapMode() == GameMap.HYBRID) {
					primary = northy.getOwner().primary;
				} else {
					primary = northy.owner.color;
				}
				primary = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 100);
				g.setColor(primary);

				region.subtract(new Area(new Rectangle2D.Double(drawX + scale, drawY, scale, scale * 2)));
				// if (fancy) {
				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 90, 180, Arc2D.PIE)));
				// }

				g.fill(region);

				// if (fancy) {
				Color secondary = northy.getOwner().secondary;
				secondary = new Color(secondary.getRed(), secondary.getGreen(), secondary.getBlue(), 100);
				g.setColor(secondary);

				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 90, 180);
				// }

			} else if (northy != null && northy.hasOwner() && northy.getOwner() != getOwner()
					&& northy.getOwner() == southy.getOwner() && northy.getOwner() == easty.getOwner()) {
				if (map.getMapMode() == GameMap.HYBRID) {
					primary = northy.getOwner().primary;
				} else {
					primary = northy.owner.color;
				}
				primary = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 100);
				g.setColor(primary);

				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale, scale * 2)));
				// if (fancy) {
				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 270, 180, Arc2D.PIE)));
				// }

				g.fill(region);

				// if (fancy) {
				Color secondary = northy.getOwner().secondary;
				secondary = new Color(secondary.getRed(), secondary.getGreen(), secondary.getBlue(), 100);
				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 270, 180);
				// }

			} else if (southy != null && southy.hasOwner() && southy.getOwner() != getOwner()
					&& southy.getOwner() == easty.getOwner() && southy.getOwner() == westy.getOwner()) {
				if (map.getMapMode() == GameMap.HYBRID) {
					primary = southy.getOwner().primary;
				} else {
					primary = southy.owner.color;
				}
				primary = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 100);
				g.setColor(primary);

				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale * 2, scale)));
				// if (fancy) {
				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 180, 180, Arc2D.PIE)));
				// }

				g.fill(region);

				// if (fancy) {
				Color secondary = southy.getOwner().secondary;
				secondary = new Color(secondary.getRed(), secondary.getGreen(), secondary.getBlue(), 100);
				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 180, 180);
				// }

			} else if (northy != null && northy.hasOwner() && northy.getOwner() != getOwner()
					&& northy.getOwner() == easty.getOwner()) {
				if (map.getMapMode() == GameMap.HYBRID) {
					primary = northy.getOwner().primary;
				} else {
					primary = northy.owner.color;
				}
				primary = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 100);
				g.setColor(primary);

				// if (fancy) {
				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 0, 90, Arc2D.PIE)));
				// }
				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale, scale)));
				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale * 2, scale)));

				g.fill(region);

				// if (fancy) {
				Color secondary = northy.getOwner().secondary;
				secondary = new Color(secondary.getRed(), secondary.getGreen(), secondary.getBlue(), 100);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 0, 90);
				// }

			} else if (northy != null && northy.hasOwner() && northy.getOwner() != getOwner()
					&& northy.getOwner() == westy.getOwner()) {
				if (map.getMapMode() == GameMap.HYBRID) {
					primary = northy.getOwner().primary;
				} else {
					primary = northy.owner.color;
				}
				primary = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 100);
				g.setColor(primary);

				// if (fancy) {
				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 90, 90, Arc2D.PIE)));
				// }
				region.subtract(new Area(new Rectangle2D.Double(drawX + scale, drawY, scale, scale)));
				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale * 2, scale)));

				g.fill(region);

				// if (fancy) {
				Color secondary = northy.getOwner().secondary;
				secondary = new Color(secondary.getRed(), secondary.getGreen(), secondary.getBlue(), 100);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 90, 90);
				// }

			} else if (southy != null && southy.hasOwner() && southy.getOwner() != getOwner()
					&& southy.getOwner() == easty.getOwner()) {
				if (map.getMapMode() == GameMap.HYBRID) {
					primary = southy.getOwner().primary;
				} else {
					primary = southy.owner.color;
				}
				primary = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 100);
				g.setColor(primary);

				// if (fancy) {
				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 270, 90, Arc2D.PIE)));
				// }
				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale, scale)));
				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale * 2, scale)));

				g.fill(region);

				// if (fancy) {
				Color secondary = southy.getOwner().secondary;
				secondary = new Color(secondary.getRed(), secondary.getGreen(), secondary.getBlue(), 100);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 270, 90);
				// }

			} else if (southy != null && southy.hasOwner() && southy.getOwner() != getOwner()
					&& southy.getOwner() == westy.getOwner()) {
				if (map.getMapMode() == GameMap.HYBRID) {
					primary = southy.getOwner().primary;
				} else {
					primary = southy.owner.color;
				}
				primary = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 100);
				g.setColor(primary);

				// if (fancy) {
				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 180, 90, Arc2D.PIE)));
				// }
				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale * 2, scale)));
				region.subtract(new Area(new Rectangle2D.Double(drawX + scale, drawY + scale, scale, scale)));

				g.fill(region);

				// if (fancy) {
				Color secondary = southy.getOwner().secondary;
				secondary = new Color(secondary.getRed(), secondary.getGreen(), secondary.getBlue(), 100);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 180, 90);
				// }

			}

		} else if (this.hasOwner()) {
			Color primary;
			if (map.getMapMode() == GameMap.HYBRID) {
				primary = owner.getOwner().getOwner().primary;
			} else {
				primary = this.owner.color;
			}
			primary = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 150);
			g.setColor(primary);
			g.fillRect(drawX, drawY, scale * 2, scale * 2);
		}
		// return drawItem(g, x, y, drawX, drawY, scale, map, hover, selected);
		return true;

	}

	public boolean drawConstructionRange(Graphics2D g, int x, int y, int drawX, int drawY, int scale, GameMap map,
			boolean hover, boolean selected, boolean fancy) {

		g.setStroke(new BasicStroke(scale == 1 ? 1 : 2));

		Tile northy = map.getTileInDirectionOf(x, y, GameMap.NORTH);
		Tile easty = map.getTileInDirectionOf(x, y, GameMap.EAST);
		Tile southy = map.getTileInDirectionOf(x, y, GameMap.SOUTH);
		Tile westy = map.getTileInDirectionOf(x, y, GameMap.WEST);
		Tile nwy = map.getTileInDirectionOf(x, y, GameMap.NORTH_WEST);
		Tile ney = map.getTileInDirectionOf(x, y, GameMap.NORTH_EAST);
		Tile swy = map.getTileInDirectionOf(x, y, GameMap.SOUTH_WEST);
		Tile sey = map.getTileInDirectionOf(x, y, GameMap.SOUTH_EAST);

		Color primary = new Color(0, 255, 0, 100);
		Color secondary = new Color(0, 255, 75, 100);

		int count = 0;

		boolean north = false, south = false, east = false, west = false, nw = false, ne = false, sw = false, se = false;

		List<Tile> selectedRegion = map.getConstructableRegion();

		if (northy != null && selectedRegion.contains(northy)) {
			count++;
			north = true;
		}
		if (southy != null && selectedRegion.contains(southy)) {
			count++;
			south = true;
		}
		if (easty != null && selectedRegion.contains(easty)) {
			count++;
			east = true;
		}
		if (westy != null && selectedRegion.contains(westy)) {
			count++;
			west = true;
		}
		if (nwy != null && selectedRegion.contains(nwy)) {
			nw = true;
		}
		if (ney != null && selectedRegion.contains(ney)) {
			ne = true;
		}
		if (swy != null && selectedRegion.contains(swy)) {
			sw = true;
		}
		if (sey != null && selectedRegion.contains(sey)) {
			se = true;
		}

		if (selectedRegion.contains(this)) {

			g.setColor(primary);
			if (count > 2 || (north && south) || (east && west) || (north && east && sw) || (north && west && se)
					|| (south && east && nw) || (south && west && ne)) {
				g.fillRect(drawX, drawY, scale * 2, scale * 2);

				g.setColor(secondary);
				if (!west && !nw) {
					g.drawLine(drawX, drawY, drawX, drawY + scale);
				}
				if (!south && !se) {
					g.drawLine(drawX + scale, drawY + scale * 2 - 1, drawX + scale * 2, drawY + scale * 2 - 1);
				}
				if (!south && !sw) {
					g.drawLine(drawX, drawY + scale * 2 - 1, drawX + scale, drawY + scale * 2 - 1);
				}
				if (!east && !ne) {
					g.drawLine(drawX + scale * 2 - 1, drawY, drawX + scale * 2 - 1, drawY + scale);
				}
				if (!north && !ne) {
					g.drawLine(drawX + scale, drawY, drawX + scale * 2, drawY);
				}
				if (!west && !sw) {
					g.drawLine(drawX, drawY + scale, drawX, drawY + scale * 2);
				}
				if (!north && !nw) {
					g.drawLine(drawX, drawY, drawX + scale, drawY);
				}
				if (!east && !se) {
					g.drawLine(drawX + scale * 2 - 1, drawY + scale, drawX + scale * 2 - 1, drawY + scale * 2);
				}

			} else if (north && east || north && se || east && nw) {
				g.fillRect(drawX, drawY, scale * 2, scale);
				g.fillRect(drawX + scale, drawY + scale, scale, scale);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 90);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 180, 90);
				if (!nw) {
					g.drawLine(drawX, drawY, drawX, drawY + scale);
				}
				if (!ne && !north) {
					g.drawLine(drawX + scale, drawY, drawX + scale * 2, drawY);
				}
				if (!se) {
					g.drawLine(drawX + scale, drawY + scale * 2 - 1, drawX + scale * 2, drawY + scale * 2 - 1);
				}

			} else if (north && west || north && sw || west && ne) {
				g.fillRect(drawX, drawY, scale * 2, scale);
				g.fillRect(drawX, drawY + scale, scale, scale);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 270, 90);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 270, 90);
				if (!sw) {
					g.drawLine(drawX, drawY + scale * 2 - 1, drawX + scale, drawY + scale * 2 - 1);
				}
				if (!ne) {
					g.drawLine(drawX + scale * 2 - 1, drawY, drawX + scale * 2 - 1, drawY + scale);
				}

			} else if (south && east || south && ne || east && sw) {
				g.fillRect(drawX, drawY + scale, scale * 2, scale);
				g.fillRect(drawX + scale, drawY, scale, scale);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 90);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 90, 90);
				if (!ne) {
					g.drawLine(drawX + scale, drawY, drawX + scale * 2, drawY);
				}
				if (!sw) {
					g.drawLine(drawX, drawY + scale, drawX, drawY + scale * 2);
				}

			} else if (south && west || south && nw || west && se) {
				g.fillRect(drawX, drawY + scale, scale * 2, scale);
				g.fillRect(drawX, drawY, scale, scale);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 90);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 0, 90);
				if (!nw) {
					g.drawLine(drawX, drawY, drawX + scale, drawY);
				}
				if (!se) {
					g.drawLine(drawX + scale * 2 - 1, drawY + scale, drawX + scale * 2 - 1, drawY + scale * 2);
				}

			} else if (!sw && !north && !south && !east && !west && !se && !ne && !nw) {
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 360);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 0, 360);

			} else if (sw && !north && !south && !east && !west && !se && !ne && !nw) {
				g.fillRect(drawX, drawY + scale, scale, scale);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, -90, 270);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, -90, 270);

			} else if (nw && !north && !south && !east && !west && !se && !ne && !sw) {
				g.fillRect(drawX, drawY, scale, scale);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 270);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 180, 270);

			} else if (se && !north && !south && !east && !west && !nw && !ne && !sw) {
				g.fillRect(drawX + scale, drawY + scale, scale, scale);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 270);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 0, 270);

			} else if (ne && !north && !south && !east && !west && !nw && !se && !sw) {
				g.fillRect(drawX + scale, drawY, scale, scale);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 270);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 90, 270);

			} else if (north && (ne && nw)) {
				g.fillRect(drawX, drawY, scale * 2, scale);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 180);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 180, 180);

				if (!east && !ne) {
					g.drawLine(drawX + scale * 2 - 1, drawY, drawX + scale * 2 - 1, drawY + scale);
				}
				if (!west && !nw) {
					g.drawLine(drawX, drawY, drawX, drawY + scale);
				}

			} else if (south || (se && sw)) {
				g.fillRect(drawX, drawY + scale, scale * 2, scale);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 180);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 0, 180);

				if (!east && !se) {
					g.drawLine(drawX + scale * 2 - 1, drawY + scale, drawX + scale * 2 - 1, drawY + scale * 2);
				}
				if (!west && !sw) {
					g.drawLine(drawX, drawY + scale, drawX, drawY + scale * 2);
				}

			} else if (east || (ne && se)) {
				g.fillRect(drawX + scale, drawY, scale, scale * 2);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 180);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 90, 180);

				if (!north && !ne) {
					g.drawLine(drawX + scale, drawY, drawX + scale * 2, drawY);
				}
				if (!south && !se) {
					g.drawLine(drawX + scale, drawY + scale * 2 - 1, drawX + scale * 2, drawY + scale * 2 - 1);
				}

			} else if (west || (nw && sw)) {
				g.fillRect(drawX, drawY, scale, scale * 2);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 270, 180);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 270, 180);

				if (!north && !nw) {
					g.drawLine(drawX, drawY, drawX + scale, drawY);
				}
				if (!south && !sw) {
					g.drawLine(drawX, drawY + scale * 2 - 1, drawX + scale, drawY + scale * 2 - 1);
				}
			} else if (ne && sw) {
				g.fillRect(drawX + scale, drawY, scale, scale);
				g.fillRect(drawX, drawY + scale, scale, scale);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 270, 90);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 90);

				g.setColor(secondary);

				g.drawArc(drawX, drawY, scale * 2, scale * 2, 270, 90);
				g.drawArc(drawX, drawY, scale * 2, scale * 2, 90, 90);

			} else if (nw && se) {
				g.fillRect(drawX + scale, drawY + scale, scale, scale);
				g.fillRect(drawX, drawY, scale, scale);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 90);
				g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 90);

				g.setColor(secondary);
				g.drawArc(drawX, drawY, scale * 2, scale * 2, 0, 90);
				g.drawArc(drawX, drawY, scale * 2, scale * 2, 180, 90);
			}

			if (hover) {

				g.setColor(new Color(255, 255, 0, 150));
				g.fillArc(drawX + scale / 2, drawY + scale / 2, scale, scale, 0, 360);
				g.drawArc(drawX + scale / 2, drawY + scale / 2, scale, scale, 0, 360);
			}

		} else {

			Area region = new Area(new Rectangle(drawX, drawY, scale * 2, scale * 2));

			if (north && south && east && west) {

				g.setColor(primary);

				Area ellipse = new Area(new Ellipse2D.Double(drawX, drawY, scale * 2 - 1, scale * 2 - 1));

				region.subtract(ellipse);
				g.fill(region);

				g.setColor(secondary);

				g.draw(ellipse);

			} else if (north && east && west) {

				g.setColor(primary);

				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale * 2, scale)));
				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 0, 180, Arc2D.PIE)));

				g.fill(region);

				g.setColor(secondary);

				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 0, 180);

			} else if (north && west && south) {

				g.setColor(primary);

				region.subtract(new Area(new Rectangle2D.Double(drawX + scale, drawY, scale, scale * 2)));
				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 90, 180, Arc2D.PIE)));

				g.fill(region);

				g.setColor(secondary);

				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 90, 180);

			} else if (north && south && east) {

				g.setColor(primary);

				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale, scale * 2)));
				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 270, 180, Arc2D.PIE)));

				g.fill(region);

				g.setColor(secondary);

				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 270, 180);

			} else if (south && east && west) {

				g.setColor(primary);

				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale * 2, scale)));
				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 180, 180, Arc2D.PIE)));

				g.fill(region);

				g.setColor(secondary);

				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 180, 180);

			} else if (north && east) {

				g.setColor(primary);

				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 0, 90, Arc2D.PIE)));
				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale, scale)));
				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale * 2, scale)));

				g.fill(region);

				g.setColor(secondary);

				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 0, 90);

			} else if (north && west) {

				g.setColor(primary);

				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 90, 90, Arc2D.PIE)));
				region.subtract(new Area(new Rectangle2D.Double(drawX + scale, drawY, scale, scale)));
				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale * 2, scale)));

				g.fill(region);

				g.setColor(secondary);

				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 90, 90);

			} else if (south && east) {

				g.setColor(primary);

				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 270, 90, Arc2D.PIE)));
				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale, scale)));
				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale * 2, scale)));

				g.fill(region);

				g.setColor(secondary);

				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 270, 90);

			} else if (south && west) {

				g.setColor(primary);

				region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 180, 90, Arc2D.PIE)));
				region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale * 2, scale)));
				region.subtract(new Area(new Rectangle2D.Double(drawX + scale, drawY + scale, scale, scale)));

				g.fill(region);

				g.setColor(secondary);

				g.drawArc(drawX, drawY, scale * 2 - 1, scale * 2 - 1, 180, 90);

			}
		}

		// return drawItem(g, x, y, drawX, drawY, scale, map, hover, selected);
		return true;

	}

	public boolean drawItem(Graphics2D g, int x, int y, int drawX, int drawY, int scale, GameMap map, boolean hover,
			boolean selected, boolean fancy) {

		if (hasStructure()) {
			struct.draw(g, x, y, drawX, drawY, scale, map, hover, selected, fancy);
		}

		if (units.size() > 0 && this.isInFullView(map)) {
			units.get(0).draw(g, x, y, drawX, drawY, scale, map, hover, selected, fancy);
		}

		return true;
	}

	public boolean isInFullView(GameMap map) {
		return (map.getPlayer() != null && map.getPlayer() == this.getNation())
				|| this.hasUnitOfNation(map.getPlayer()) || hasNeighborOfNation(map, map.getPlayer())
				|| hasNeighborUnitOfNation(map, map.getPlayer());
	}

	private boolean hasUnitOfNation(Nation player) {
		for (Unit u : units) {
			if (u.getNation() == player) {
				return true;
			}
		}

		return false;
	}

	public boolean hasNeighborUnitOfNation(GameMap map, Nation nat) {
		for (Tile tile : map.getTilesAround(this)) {
			if (tile.hasUnitOfNation(nat)) {
				return true;
			}
		}

		return false;
	}

	public boolean drawShadow(Graphics2D g, int x, int y, int drawX, int drawY, int scale, GameMap map, boolean hover,
			boolean selected, boolean fancy) {

		Tile northy = map.getTileInDirectionOf(x, y, GameMap.NORTH);
		Tile easty = map.getTileInDirectionOf(x, y, GameMap.EAST);
		Tile southy = map.getTileInDirectionOf(x, y, GameMap.SOUTH);
		Tile westy = map.getTileInDirectionOf(x, y, GameMap.WEST);
		Tile nwy = map.getTileInDirectionOf(x, y, GameMap.NORTH_WEST);
		Tile ney = map.getTileInDirectionOf(x, y, GameMap.NORTH_EAST);
		Tile swy = map.getTileInDirectionOf(x, y, GameMap.SOUTH_WEST);
		Tile sey = map.getTileInDirectionOf(x, y, GameMap.SOUTH_EAST);

		g.setColor(SHADOW);

		if (fancy) {

			if (!this.isInFullView(map)) {

				int count = 0;

				boolean north = false, south = false, east = false, west = false, nw = false, ne = false, sw = false, se = false;

				if (northy != null && !northy.isInFullView(map)) {
					count++;
					north = true;
				}
				if (southy != null && !southy.isInFullView(map)) {
					count++;
					south = true;
				}
				if (easty != null && !easty.isInFullView(map)) {
					count++;
					east = true;
				}
				if (westy != null && !westy.isInFullView(map)) {
					count++;
					west = true;
				}
				if (nwy != null && !nwy.isInFullView(map)) {
					nw = true;
				}
				if (ney != null && !ney.isInFullView(map)) {
					ne = true;
				}
				if (swy != null && !swy.isInFullView(map)) {
					sw = true;
				}
				if (sey != null && !sey.isInFullView(map)) {
					se = true;
				}

				if (count > 2 || (north && south) || (east && west) || (north && east && sw) || (north && west && se)
						|| (south && east && nw) || (south && west && ne)) {
					g.fillRect(drawX, drawY, scale * 2, scale * 2);

				} else if (north && east || north && se || east && nw) {
					g.fillRect(drawX, drawY, scale * 2, scale);
					g.fillRect(drawX + scale, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 90);
					// }
				} else if (north && west || north && sw || west && ne) {
					g.fillRect(drawX, drawY, scale * 2, scale);
					g.fillRect(drawX, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 270, 90);
					// }

				} else if (south && east || south && ne || east && sw) {
					g.fillRect(drawX, drawY + scale, scale * 2, scale);
					g.fillRect(drawX + scale, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 90);
					// }

				} else if (south && west || south && nw || west && se) {
					g.fillRect(drawX, drawY + scale, scale * 2, scale);
					g.fillRect(drawX, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 90);
					// }

				} else if (!sw && !north && !south && !east && !west && !se && !ne && !nw) {
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 360);
					// }

				} else if (sw && !north && !south && !east && !west && !se && !ne && !nw) {
					g.fillRect(drawX, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, -90, 270);
					// }

				} else if (nw && !north && !south && !east && !west && !se && !ne && !sw) {
					g.fillRect(drawX, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 270);
					// }

				} else if (se && !north && !south && !east && !west && !nw && !ne && !sw) {
					g.fillRect(drawX + scale, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 270);
					// }

				} else if (ne && !north && !south && !east && !west && !nw && !se && !sw) {
					g.fillRect(drawX + scale, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 270);
					// }

				} else if (north || (ne && nw)) {
					g.fillRect(drawX, drawY, scale * 2, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 180);
					// }

				} else if (south || (se && sw)) {
					g.fillRect(drawX, drawY + scale, scale * 2, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 180);
					// }

				} else if (east || (ne && se)) {
					g.fillRect(drawX + scale, drawY, scale, scale * 2);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 180);
					// }

				} else if (west || (nw && sw)) {
					g.fillRect(drawX, drawY, scale, scale * 2);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 270, 180);
					// }
				} else if (ne && sw) {
					g.fillRect(drawX + scale, drawY, scale, scale);
					g.fillRect(drawX, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 270, 90);
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 90);
					// }
				} else if (nw && se) {
					g.fillRect(drawX + scale, drawY + scale, scale, scale);
					g.fillRect(drawX, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 90);
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 90);
					// }
				}

			} else {

				Area region = new Area(new Rectangle(drawX, drawY, scale * 2, scale * 2));

				if (northy != null && !northy.isInFullView(map) && southy != null && !southy.isInFullView(map)
						&& easty != null && !easty.isInFullView(map) && westy != null && !westy.isInFullView(map)) {

					// if (fancy) {
					Area ellipse = new Area(new Ellipse2D.Double(drawX, drawY, scale * 2 - 1, scale * 2 - 1));

					region.subtract(ellipse);
					g.fill(region);
					// }
				} else if (northy != null && !northy.isInFullView(map) && easty != null && !easty.isInFullView(map)
						&& westy != null && !westy.isInFullView(map)) {

					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale * 2, scale)));
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 0, 180, Arc2D.PIE)));
					// }

					g.fill(region);

				} else if (northy != null && !northy.isInFullView(map) && southy != null && !southy.isInFullView(map)
						&& westy != null && !westy.isInFullView(map)) {
					region.subtract(new Area(new Rectangle2D.Double(drawX + scale, drawY, scale, scale * 2)));
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 90, 180, Arc2D.PIE)));
					// }

					g.fill(region);

				} else if (northy != null && !northy.isInFullView(map) && southy != null && !southy.isInFullView(map)
						&& easty != null && !easty.isInFullView(map)) {
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale, scale * 2)));
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 270, 180, Arc2D.PIE)));
					// }

					g.fill(region);

				} else if (southy != null && !southy.isInFullView(map) && easty != null && !easty.isInFullView(map)
						&& westy != null && !westy.isInFullView(map)) {
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale * 2, scale)));
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 180, 180, Arc2D.PIE)));
					// }

					g.fill(region);

				} else if (northy != null && !northy.isInFullView(map) && easty != null && !easty.isInFullView(map)) {
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 0, 90, Arc2D.PIE)));
					// }
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale, scale)));
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale * 2, scale)));

					g.fill(region);

				} else if (northy != null && !northy.isInFullView(map) && westy != null && !westy.isInFullView(map)) {
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 90, 90, Arc2D.PIE)));
					// }
					region.subtract(new Area(new Rectangle2D.Double(drawX + scale, drawY, scale, scale)));
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale * 2, scale)));

					g.fill(region);

				} else if (southy != null && !southy.isInFullView(map) && easty != null && !easty.isInFullView(map)) {
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 270, 90, Arc2D.PIE)));
					// }
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale, scale)));
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale * 2, scale)));

					g.fill(region);

				} else if (southy != null && !southy.isInFullView(map) && westy != null && !westy.isInFullView(map)) {
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 180, 90, Arc2D.PIE)));
					// }
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale * 2, scale)));
					region.subtract(new Area(new Rectangle2D.Double(drawX + scale, drawY + scale, scale, scale)));

					g.fill(region);

				}
			}
		} else if (!this.isInFullView(map)) {
			g.fillRect(drawX, drawY, scale * 2, scale * 2);
		}

		return true;
	}

	public boolean drawFOW(Graphics2D g, int x, int y, int drawX, int drawY, int scale, GameMap map, boolean hover,
			boolean selected, boolean fancy) {

		Tile northy = map.getTileInDirectionOf(x, y, GameMap.NORTH);
		Tile easty = map.getTileInDirectionOf(x, y, GameMap.EAST);
		Tile southy = map.getTileInDirectionOf(x, y, GameMap.SOUTH);
		Tile westy = map.getTileInDirectionOf(x, y, GameMap.WEST);
		Tile nwy = map.getTileInDirectionOf(x, y, GameMap.NORTH_WEST);
		Tile ney = map.getTileInDirectionOf(x, y, GameMap.NORTH_EAST);
		Tile swy = map.getTileInDirectionOf(x, y, GameMap.SOUTH_WEST);
		Tile sey = map.getTileInDirectionOf(x, y, GameMap.SOUTH_EAST);

		g.setColor(Color.BLACK);

		if (fancy) {

			if (!map.discovered(x, y)) {

				int count = 0;

				boolean north = false, south = false, east = false, west = false, nw = false, ne = false, sw = false, se = false;

				if (northy != null && !map.discovered(northy)) {
					count++;
					north = true;
				}
				if (southy != null && !map.discovered(southy)) {
					count++;
					south = true;
				}
				if (easty != null && !map.discovered(easty)) {
					count++;
					east = true;
				}
				if (westy != null && !map.discovered(westy)) {
					count++;
					west = true;
				}
				if (nwy != null && !map.discovered(nwy)) {
					nw = true;
				}
				if (ney != null && !map.discovered(ney)) {
					ne = true;
				}
				if (swy != null && !map.discovered(swy)) {
					sw = true;
				}
				if (sey != null && !map.discovered(sey)) {
					se = true;
				}

				if (count > 2 || (north && south) || (east && west) || (north && east && sw) || (north && west && se)
						|| (south && east && nw) || (south && west && ne)) {
					g.fillRect(drawX, drawY, scale * 2, scale * 2);

				} else if (north && east || north && se || east && nw) {
					g.fillRect(drawX, drawY, scale * 2, scale);
					g.fillRect(drawX + scale, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 90);
					// }

				} else if (north && west || north && sw || west && ne) {
					g.fillRect(drawX, drawY, scale * 2, scale);
					g.fillRect(drawX, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 270, 90);
					// }

				} else if (south && east || south && ne || east && sw) {
					g.fillRect(drawX, drawY + scale, scale * 2, scale);
					g.fillRect(drawX + scale, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 90);
					// }

				} else if (south && west || south && nw || west && se) {
					g.fillRect(drawX, drawY + scale, scale * 2, scale);
					g.fillRect(drawX, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 90);
					// }

				} else if (!sw && !north && !south && !east && !west && !se && !ne && !nw) {
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 360);
					// }

				} else if (sw && !north && !south && !east && !west && !se && !ne && !nw) {
					g.fillRect(drawX, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, -90, 270);
					// }

				} else if (nw && !north && !south && !east && !west && !se && !ne && !sw) {
					g.fillRect(drawX, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 270);
					// }

				} else if (se && !north && !south && !east && !west && !nw && !ne && !sw) {
					g.fillRect(drawX + scale, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 270);
					// }

				} else if (ne && !north && !south && !east && !west && !nw && !se && !sw) {
					g.fillRect(drawX + scale, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 270);
					// }

				} else if (north || (ne && nw)) {
					g.fillRect(drawX, drawY, scale * 2, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 180);
					// }

				} else if (south || (se && sw)) {
					g.fillRect(drawX, drawY + scale, scale * 2, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 180);
					// }

				} else if (east || (ne && se)) {
					g.fillRect(drawX + scale, drawY, scale, scale * 2);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 180);
					// }

				} else if (west || (nw && sw)) {
					g.fillRect(drawX, drawY, scale, scale * 2);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 270, 180);
					// }

				} else if (ne && sw) {
					g.fillRect(drawX + scale, drawY, scale, scale);
					g.fillRect(drawX, drawY + scale, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 270, 90);
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 90, 90);
					// }
				} else if (nw && se) {
					g.fillRect(drawX + scale, drawY + scale, scale, scale);
					g.fillRect(drawX, drawY, scale, scale);
					// if (fancy) {
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 0, 90);
					g.fillArc(drawX, drawY, scale * 2, scale * 2, 180, 90);
					// }
				}

			} else {

				Area region = new Area(new Rectangle(drawX, drawY, scale * 2, scale * 2));

				if (northy != null && !map.discovered(northy) && southy != null && !map.discovered(southy)
						&& easty != null && !map.discovered(easty) && westy != null && !map.discovered(westy)) {
					// if (fancy) {
					Area ellipse = new Area(new Ellipse2D.Double(drawX, drawY, scale * 2 - 1, scale * 2 - 1));

					region.subtract(ellipse);
					g.fill(region);
					// }

				} else if (northy != null && !map.discovered(northy) && easty != null && !map.discovered(easty)
						&& westy != null && !map.discovered(westy)) {

					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale * 2, scale)));
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 0, 180, Arc2D.PIE)));
					// }

					g.fill(region);

				} else if (northy != null && !map.discovered(northy) && southy != null && !map.discovered(southy)
						&& westy != null && !map.discovered(westy)) {
					region.subtract(new Area(new Rectangle2D.Double(drawX + scale, drawY, scale, scale * 2)));
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 90, 180, Arc2D.PIE)));
					// }

					g.fill(region);

				} else if (northy != null && !map.discovered(northy) && southy != null && !map.discovered(southy)
						&& easty != null && !map.discovered(easty)) {
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale, scale * 2)));
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 270, 180, Arc2D.PIE)));
					// }

					g.fill(region);

				} else if (southy != null && !map.discovered(southy) && easty != null && !map.discovered(easty)
						&& westy != null && !map.discovered(westy)) {
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale * 2, scale)));
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 180, 180, Arc2D.PIE)));
					// }

					g.fill(region);

				} else if (northy != null && !map.discovered(northy) && easty != null && !map.discovered(easty)) {
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 0, 90, Arc2D.PIE)));
					// }
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale, scale)));
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale * 2, scale)));

					g.fill(region);

				} else if (northy != null && !map.discovered(northy) && westy != null && !map.discovered(westy)) {
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 90, 90, Arc2D.PIE)));
					// }
					region.subtract(new Area(new Rectangle2D.Double(drawX + scale, drawY, scale, scale)));
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale * 2, scale)));

					g.fill(region);

				} else if (southy != null && !map.discovered(southy) && easty != null && !map.discovered(easty)) {
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 270, 90, Arc2D.PIE)));
					// }
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY + scale, scale, scale)));
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale * 2, scale)));

					g.fill(region);

				} else if (southy != null && !map.discovered(southy) && westy != null && !map.discovered(westy)) {
					// if (fancy) {
					region.subtract(new Area(new Arc2D.Double(drawX, drawY, scale * 2, scale * 2, 180, 90, Arc2D.PIE)));
					// }
					region.subtract(new Area(new Rectangle2D.Double(drawX, drawY, scale * 2, scale)));
					region.subtract(new Area(new Rectangle2D.Double(drawX + scale, drawY + scale, scale, scale)));

					g.fill(region);

				}
			}
		} else if (!map.discovered(x, y)) {
			g.fillRect(drawX, drawY, scale * 2, scale * 2);
		}

		return true;
	}

	public boolean drawPaths(Graphics2D g, int x, int y, int drawX, int drawY, int scale, GameMap map, boolean hover,
			boolean selected, boolean drawHover) {

		Tile northy = map.getTileInDirectionOf(x, y, GameMap.NORTH);
		Tile easty = map.getTileInDirectionOf(x, y, GameMap.EAST);
		Tile southy = map.getTileInDirectionOf(x, y, GameMap.SOUTH);
		Tile westy = map.getTileInDirectionOf(x, y, GameMap.WEST);
		Tile nwy = map.getTileInDirectionOf(x, y, GameMap.NORTH_WEST);
		Tile ney = map.getTileInDirectionOf(x, y, GameMap.NORTH_EAST);
		Tile swy = map.getTileInDirectionOf(x, y, GameMap.SOUTH_WEST);
		Tile sey = map.getTileInDirectionOf(x, y, GameMap.SOUTH_EAST);

		boolean north = false, south = false, east = false, west = false, nw = false, ne = false, sw = false, se = false;

		if (northy != null && (northy.hasRoad() || map.getRoad().contains(northy))) {
			north = true;
		}
		if (southy != null && (southy.hasRoad() || map.getRoad().contains(southy))) {
			south = true;
		}
		if (easty != null && (easty.hasRoad() || map.getRoad().contains(easty))) {
			east = true;
		}
		if (westy != null && (westy.hasRoad() || map.getRoad().contains(westy))) {
			west = true;
		}
		if (nwy != null && (nwy.hasRoad() || map.getRoad().contains(nwy))) {
			nw = true;
		}
		if (ney != null && (ney.hasRoad() || map.getRoad().contains(ney))) {
			ne = true;
		}
		if (swy != null && (swy.hasRoad() || map.getRoad().contains(swy))) {
			sw = true;
		}
		if (sey != null && (sey.hasRoad() || map.getRoad().contains(sey))) {
			se = true;
		}

		if (drawHover) {
			g.setColor(Color.GREEN.darker().darker());
		} else {
			g.setColor(Color.GRAY);
		}
		g.setStroke(new BasicStroke(scale / 8));

		// g.fillOval(drawX + scale / 2, drawY + scale / 2, scale, scale);

		if (north) {
			g.drawLine(drawX + scale, drawY, drawX + scale, drawY + scale);
		}
		if (south) {
			g.drawLine(drawX + scale, drawY + scale, drawX + scale, drawY + scale * 2);
		}
		if (east) {
			g.drawLine(drawX + scale, drawY + scale, drawX + scale * 2, drawY + scale);
		}
		if (west) {
			g.drawLine(drawX, drawY + scale, drawX + scale, drawY + scale);
		}
		if (ne) {
			g.drawLine(drawX + scale, drawY + scale, drawX + scale * 2, drawY);
		}
		if (nw) {
			g.drawLine(drawX + scale, drawY + scale, drawX, drawY);
		}
		if (se) {
			g.drawLine(drawX + scale, drawY + scale, drawX + scale * 2, drawY + scale * 2);
		}
		if (sw) {
			g.drawLine(drawX + scale, drawY + scale, drawX, drawY + scale * 2);
		}

		g.setStroke(new BasicStroke(1));

		return true;
	}

	public boolean hasNeighborOfNation(GameMap map, Nation nat) {
		for (Tile tile : map.getPassableLandTilesAround(this)) {
			if (tile.getNation() == nat) {
				return true;
			}
		}

		return false;
	}

	public void setOwner(Settlement owner) {
		this.owner = owner.region;
	}

	public void setOwner(Region owner) {
		this.owner = owner;
	}

	public Nation getOwner() {
		if (owner == null || owner.getOwner() == null) {
			return null;
		}
		return owner.getOwner().getOwner();
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public boolean isLand() {
		return isLand;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public List<Discrete> getElements(GameMap map) {
		List<Discrete> ranked = new ArrayList<Discrete>();

		for (Unit u : units) {
			if (u.getNation() == map.getPlayer())
				ranked.add(u);
		}

		if (this.hasStructure() && struct.getNation() == map.getPlayer()) {
			ranked.add(struct);
		}

		if (this.getOwner() == map.getPlayer()) {
			ranked.add(this);
		}

		return ranked;
	}

	public Discrete getProminent(GameMap map) {
		List<Discrete> ranked = getElements(map);

		if (ranked.isEmpty()) {
			return null;
		} else {
			return ranked.get(0);
		}

	}

	@Override
	public String getType() {
		return "Tile";
	}

	public void passOrder(Tile tile) {

	}

	@Override
	public Nation getNation() {
		if (owner == null || owner.getOwner() == null) {
			return null;
		} else {
			return owner.getOwner().getOwner();
		}
	}

	public void advance() {

	}

	public void advanceMonth() {

	}

	public void advanceYear() {

	}

	public void addUnit(Unit unit) {
		try {
			put(unit);
		} catch (TileHasMaxNumberOfUnitsException e) {
			e.printStackTrace();
		}
	}

	public Continent getContinent() {
		return continent;
	}

	public void setContinent(Continent continent) {
		this.continent = continent;
	}

}
