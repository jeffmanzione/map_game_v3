package item.structures;

import generic.Discrete;
//import generic.Garrisonable;
import gfx.ResourceManager;
import item.Item;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nations.Nation;
import maps.Drawable;
import maps.GameMap;
import tiles.Colors;
import tiles.Tile;
import units.Unit;

public abstract class Structure extends Item implements Drawable {

	protected Tile location;

	protected int unitSlots = 0;

	protected Discrete owner;

	public Structure(int unitSlots) {
		this.unitSlots = unitSlots;
	}

	public boolean draw(Graphics2D g, int x, int y, int drawX, int drawY, int scale, GameMap map, boolean hover,
			boolean selected, boolean fancy) {

		try {
			BufferedImage img = ResourceManager.get(getType(), scale);

			if (selected) {
				g.setColor(Colors.SELECT_HIGH);
				g.fillRoundRect(drawX, drawY, scale * 2, scale * 2, scale, scale);
				g.setColor(Colors.SELECT_FG);
				g.drawRoundRect(drawX, drawY, scale * 2, scale * 2, scale, scale);

			}

			if (img != null) {
				g.drawImage(img, drawX, drawY, null);
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected int TEXT_SZ = 10;

	public boolean drawLabel(Graphics2D g, int x, int y, int drawX, int drawY, int scale, GameMap map, boolean hover,
			boolean selected, boolean fancy) {

		String str;
		g.setFont(g.getFont().deriveFont((float) TEXT_SZ));

		FontMetrics met = g.getFontMetrics();
		Color bg, fg;

		/*
		 * if (hover && this instanceof Garrisonable && scale > 1) { bg = Colors.HOVER_SELECT_BG; fg =
		 * Colors.HOVER_SELECT_FG;
		 * 
		 * Garrisonable gu = (Garrisonable) this;
		 * 
		 * int num = gu.getGarrison();
		 * 
		 * if (num >= 1000) { str = (num / 1000) + "K"; } else { str = num + ""; }
		 * 
		 * } else {
		 */
		str = getName();
		// }

		Rectangle2D bounds = g.getFontMetrics().getStringBounds(str, g);
		int strWidth = (int) bounds.getWidth();
		int strHeight = (int) bounds.getHeight();
		int strAsc = met.getAscent();

		int yOffset = Math.min(scale * 6 / 4 + strHeight, scale * 2);
		int xOffset = scale - strWidth / 2;

		if (selected) {
			if (hover) {
				bg = Colors.HOVER_SELECT_BG;
				fg = Colors.HOVER_SELECT_FG;
			} else {
				bg = Colors.SELECT_BG;
				fg = Colors.SELECT_FG;
			}
			g.setColor(Colors.SELECT_FG);
			g.drawRect(drawX + xOffset - 2, drawY + yOffset - strAsc - 1, strWidth + 3, strHeight + 1);
		} else if (hover) {
			bg = Colors.HOVER_BG;
			fg = Colors.HOVER_FG;
		} else {
			bg = Colors.NORM_BG;
			fg = Colors.NORM_FG;
		}

		g.setColor(bg);
		g.fillRect(drawX + xOffset - 1, drawY + yOffset - strAsc, strWidth + 2, strHeight);

		g.setColor(fg);
		g.drawString(str, drawX + xOffset, drawY + yOffset - 1);

		return true;
	}

	public Nation getNation() {
		return owner.getNation();
	}

	private List<Unit> units = new ArrayList<Unit>();

	public void advance() {
		for (Unit u : units) {
			u.advance();
		}
	}

	public void advanceMonth() {
		// setting manpower growth
		moneyGrowth = 0;
		manpowerGrowth = 0;
		adminGrowth = 0;
		diploGrowth = 0;
		militGrowth = 0;
		for (Unit u : units) {
			u.advanceMonth();
			manpowerGrowth += u.getManpowerGrowth();
			moneyGrowth += u.getMoneyGrowth();
		}
	}

	public void advanceYear() {
		for (Unit u : units) {
			u.advanceYear();
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

	public void addUnit(Unit u) {
		units.add(u);
	}

	public void setLocation(Tile tile) {
		location = tile;
	}

	public Tile getLocation() {
		return location;
	}

	public List<Structure> getStructuresSameContinent() {
		List<Structure> travelables = new ArrayList<>();
		Collection<Structure> structs = this.getNation().getStructures().values();

		for (Structure struct : structs) {
			if (struct.location.getContinent() == this.location.getContinent()) {
				travelables.add(struct);
			}
		}

		return travelables;
	}

}
