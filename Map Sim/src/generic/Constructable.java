package generic;

import java.util.List;

import maps.GameMap;
import tiles.Tile;

public interface Constructable {
	
	public boolean valid(GameMap map, int x, int y);
	
	public abstract List<Tile> getAvailableTiles();
	
	public abstract boolean isConstructing();
	
	public abstract void placeStructure(int x, int y);

	public abstract void setConstructing(boolean val);

}
