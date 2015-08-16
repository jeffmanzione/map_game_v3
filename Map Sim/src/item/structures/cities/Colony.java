package item.structures.cities;

import nations.Nation;
import maps.Drawable;
import maps.GameMap;

public class Colony extends Settlement implements Drawable {

	public Colony(int x, int y, GameMap map, Nation owner) {
		super("Colony_Name", 0, 1, 1.1, x, y, map, owner);
	}
	
	@Override
	public String getType() {
		return "Colony";
	}

	@Override
	public String getName() {
		return "Colony";
	}


}
