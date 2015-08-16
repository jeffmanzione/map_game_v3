package item.structures.cities;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import item.structures.ConstructableStructure;
import item.structures.NumberFormatter;
import generic.Constructable;
import gfx.ResourceManager;
import tiles.Colors;
import tiles.Tile;
import maps.GameMap;
import nations.Nation;

public abstract class Settlement extends ConstructableStructure implements Constructable {

	private String name;
	private volatile double population;

	private static final String POPULATION_ID = "pop", MANPOWER_ID = "man";
	protected Image popIcon, manIcon;

	public Settlement(String name, int population, int unitSlots, double radius, int x, int y, GameMap map, Nation owner) {
		super(unitSlots, radius, x, y, map, owner);
		this.name = name;
		this.population = population;

		setUpRegion(map);
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}

	public int getPopulation() {
		return (int) population;
	}

	private void setUpRegion(GameMap map) {

		map.tileAt(x, y).setRoad(true);

		int rad = (int) getRadius();

		region.add(map.tileAt(x, y));

		for (int i = 1; i <= rad; i++) {

			if (validForOwnership(map, x + i, y)) {
				region.add(map.tileAt(x + i, y));
			}

			if (validForOwnership(map, x, y + i)) {
				region.add(map.tileAt(x, y + i));
			}

			if (validForOwnership(map, x, y - i)) {
				region.add(map.tileAt(x, y - i));
			}

			if (validForOwnership(map, x - i, y)) {
				region.add(map.tileAt(x - i, y));
			}

		}

		for (int i = 1; i <= rad; i++) {

			int len = (int) Math.sqrt(Math.pow(getRadius(), 2) - Math.pow(i, 2));

			for (int j = 1; j <= len; j++) {

				if (validForOwnership(map, x + i, y + j)) {
					region.add(map.tileAt(x + i, y + j));
				}

				if (validForOwnership(map, x - i, y + j)) {
					region.add(map.tileAt(x - i, y + j));
				}

				if (validForOwnership(map, x + i, y - j)) {
					region.add(map.tileAt(x + i, y - j));
				}

				if (validForOwnership(map, x - i, y - j)) {
					region.add(map.tileAt(x - i, y - j));
				}
			}
		}

		map.revalidateDiscovered();

	}

	public void advanceMonth() {
		super.advanceMonth();

		// setting manpower growth
		moneyGrowth += population * 0.01 - this.getMoneyCostGarrison();
		manpowerGrowth += population * 0.01 - this.getManpowerCostGarrison();
		population += population * 0.0007;
	}

	@Override
	public void placeStructure(int x, int y) {
		for (Tile t : map.getRoad()) {
			t.setRoad(true);
		}
	}

	@Override
	public boolean drawLabel(Graphics2D g, int x, int y, int drawX, int drawY, int scale, GameMap map, boolean hover,
			boolean selected, boolean fancy) {

		if (scale > 4 && (selected || hover)) {
			String pop = NumberFormatter.format(population, 3), man = NumberFormatter.formatDecimalPlaces(
					manpowerGrowth, 1);
			g.setFont(g.getFont().deriveFont((float) TEXT_SZ));

			FontMetrics met = g.getFontMetrics();

			Color bg, fg;

			Rectangle2D bounds = g.getFontMetrics().getStringBounds("                            ", g);
			int strWidth = (int) bounds.getWidth();
			int strAsc = met.getAscent();
			int strHeight = (int) bounds.getHeight();

			if (popIcon == null) {
				popIcon = ResourceManager.get(POPULATION_ID, 32).getScaledInstance(strHeight, strHeight,
						Image.SCALE_SMOOTH);
				manIcon = ResourceManager.get(MANPOWER_ID, 32).getScaledInstance(strHeight, strHeight,
						Image.SCALE_SMOOTH);

			}

			int yOffset = Math.min(scale * 6 / 4 + strHeight, scale * 2);
			int xOffset = scale - strWidth / 2;

			int space = strHeight / 5;

			if (selected) {
				if (hover) {
					bg = Colors.HOVER_SELECT_BG;
				} else {
					bg = Colors.SELECT_BG;
				}

			} else if (hover) {
				bg = Colors.HOVER_BG;
			} else {
				bg = Colors.NORM_BG;
			}

			fg = Colors.NORM_FG;

			g.setColor(bg);
			g.fillRect(drawX + xOffset - 1, drawY + yOffset - strAsc + strHeight + space, strWidth + 2, strHeight);

			g.drawImage(popIcon, drawX + xOffset, drawY + yOffset - strAsc + strHeight + space, null);

			Rectangle2D popString = g.getFontMetrics().getStringBounds(pop, g);
			int strWidthPop = (int) popString.getWidth();

			g.drawImage(manIcon, drawX + xOffset + popIcon.getWidth(null) + 2 + strWidthPop + 8, drawY + yOffset
					- strAsc + strHeight + space, null);

			g.setColor(fg);
			g.drawString(pop, drawX + xOffset + popIcon.getWidth(null) + 2, drawY + yOffset + strHeight + space - 1);

			g.drawString(man, drawX + xOffset + popIcon.getWidth(null) + 2 + strWidthPop + 8 + manIcon.getWidth(null)
					+ 2, drawY + yOffset + strHeight + space - 1);

		}

		return super.drawLabel(g, x, y, drawX, drawY, scale, map, hover, selected, fancy);
	}

}
