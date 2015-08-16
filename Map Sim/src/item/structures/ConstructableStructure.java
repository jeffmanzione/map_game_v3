package item.structures;

import item.structures.cities.Settlement;
import item.structures.outposts.HabitedLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nations.Nation;
import nations.Region;
import maps.GameMap;
import tiles.Tile;
import generic.Constructable;

public class ConstructableStructure extends HabitedLocation implements Constructable {
	
	public ConstructableStructure(int unitSlots, double radius, int x, int y, GameMap map, Nation owner) {
		super(unitSlots, x, y, map, owner);
		this.radius = radius;

		region = new Region(this);
	}

	private final double radius;

	public final Region region;
	
	public double getRadius() {
		return radius;
	}


	public boolean validForOwnership(GameMap map, int x, int y) {
		Tile tile = map.tileAt(x, y);
		return x >= 0 && x < map.map_width && y >= 0 && y < map.map_height && !tile.hasOwner() && tile.isLand();
	}

	public boolean valid(GameMap map, int x, int y) {

		Tile tile = map.tileAt(x, y);
		return map.valid(x, y) && tile.getOwner() == this.getOwner()
				&& this.location.getContinent() == tile.getContinent() && tile.isLand() && !tile.hasRoad()
				&& !(tile.hasStructure() && tile.getStructure() instanceof Settlement);
	}

	public Nation getOwner() {
		return owner;
	}

	public Nation getNation() {
		return owner;
	}

	public void passOrder(Tile tile) {

	}

	public void advanceMonth() {
		super.advanceMonth();
	}

	private List<Tile> tiles = new ArrayList<>();

	public List<Tile> getAvailableTiles() {

		tiles.clear();

		Collection<Tile> weHave = getOwner().getTiles();

		for (Tile tile : weHave) {
			if (tile.getContinent() == this.getLocation().getContinent()) {
				tiles.add(tile);
			}
		}

		return tiles;
	}

	private boolean constructing = false;

	public boolean isConstructing() {
		return constructing;
	}

	@Override
	public void setConstructing(boolean val) {
		constructing = val;

		System.out.println(this.getStructuresSameContinent());
	}

	public Collection<Tile> getTiles() {
		return region.getTiles();
	}

	@Override
	public String getType() {
		return null;
	}


	@Override
	public String getName() {
		return null;
	}


	@Override
	public void placeStructure(int x, int y) {
		
	}
}
