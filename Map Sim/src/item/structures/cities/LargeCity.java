package item.structures.cities;

import nations.Nation;
import maps.GameMap;


public class LargeCity extends Settlement {

	public LargeCity(String name, int population, int x, int y, GameMap map, Nation owner) {
		super(name, population, 3, 5.0, x, y, map, owner);
		TEXT_SZ = 14;
		
		MAX_GARRISON = 250;
		REQ_GARRISON = 100;
	}

	@Override
	public String getType() {
		return "LargeCity";
	}


}
