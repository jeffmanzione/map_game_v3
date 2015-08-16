package item.structures.cities;

import nations.Nation;
import maps.GameMap;

public class Camp extends Settlement {

	public Camp(String name, int population, int x, int y, GameMap map, Nation owner) {
		super(name, population, 1, 1.0, x, y, map, owner);
	}

	@Override
	public String getType() {
		return "Camp";
	}


}
