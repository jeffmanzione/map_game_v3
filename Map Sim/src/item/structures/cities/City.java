package item.structures.cities;

import nations.Nation;
import maps.GameMap;

public class City extends Settlement {

	public City(String name, int population, int x, int y, GameMap map, Nation owner) {
		super(name, population, 2, 3.0, x, y, map, owner);
		MAX_GARRISON = 100;
		REQ_GARRISON = 50;
	}

	
	public String getType() {
		return "City";
	}

}
