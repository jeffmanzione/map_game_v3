package item.structures.outposts;

import maps.GameMap;
import nations.Region;

public class Hamlet extends Outpost {

	public Hamlet(int x, int y, GameMap map, Region region) {
		super(1, x, y, map, region);
	}

	@Override
	public String getType() {
		return "Hamlet";
	}

	@Override
	public String getName() {
		return "Hamlet";
	}

}
