package units.sea;

import item.structures.cities.Colony;

import java.util.ArrayList;
import java.util.List;

import maps.GameMap;
import tiles.Tile;
import generic.Constructable;
import generic.Discrete;

public class ColonyShip extends SeaUnit implements Constructable {

	public ColonyShip(Discrete owner) {
		super(owner);	
	}

	public boolean valid(GameMap map, int x, int y) {
		Tile tile = map.tileAt(x, y);
		return x >= 0 && x < map.map_width && y >= 0 && y < map.map_height && !tile.hasOwner() && tile.isLand();
	}

	private List<Tile> tiles = new ArrayList<Tile>();
	
	public List<Tile> getAvailableTiles() {
		tiles.clear();
		int rad = 2;
		
		for (int i = 1; i <= rad; i++) {
			
			if (valid(map, current.x + i, current.y)) {
				tiles.add(map.tileAt(current.x + i, current.y));
			}
			
			if (valid(map, current.x, current.y + i)) {
				tiles.add(map.tileAt(current.x, current.y + i));
			}
			
			if (valid(map, current.x, current.y - i)) {
				tiles.add(map.tileAt(current.x, current.y - i));
			}
			
			if (valid(map, current.x - i, current.y)) {
				tiles.add(map.tileAt(current.x - i, current.y));
			}
			
		}

		for (int i = 1; i <= rad; i++) {
			
			int len = (int) Math.sqrt(Math.pow(rad, 2) - Math.pow(i, 2));

			for (int j = 1; j <= len; j++) {
				
				if (valid(map, current.x + i, current.y + j)) {
					tiles.add(map.tileAt(current.x + i, current.y + j));
				}
				
				if (valid(map, current.x - i, current.y + j)) {
					tiles.add(map.tileAt(current.x - i, current.y + j));
				}
				
				if (valid(map, current.x + i, current.y - j)) {
					tiles.add(map.tileAt(current.x + i, current.y - j));
				}
				
				if (valid(map, current.x - i, current.y - j)) {
					tiles.add(map.tileAt(current.x - i, current.y - j));
				}
			}
		}
	
		return tiles;
	}
	
	private boolean constructing = false;
	
	public boolean isConstructing() {
		return constructing;
	}
	
	public String toString() {
		return this.getNation().getName() + " Colony Ship";
	}
	
	public void advance() {
		super.advance();
		
		if (map.getSelected() == this) {
			getAvailableTiles();
		}
	}

	@Override
	public void placeStructure(int x, int y) {
		this.setConstructing(false);
		Colony colony = new Colony(x, y, map, this.getNation());
		this.getNation().addStructure(colony);
		map.put(colony, x, y);
		current.remove(this);
	}

	@Override
	public void setConstructing(boolean val) {
		constructing = val;
	}

}
