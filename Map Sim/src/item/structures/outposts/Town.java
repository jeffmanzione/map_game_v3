package item.structures.outposts;

import maps.GameMap;
import nations.Region;

public class Town extends Outpost {

	public Town(int x, int y, GameMap map, Region region) {
		super(2, x, y, map, region);
		REQ_GARRISON = 20;
		MAX_GARRISON = 60;
	}

	public String getType() {
		return "Town";
	}

	public String getName() {
		return "Town";
	}

}
