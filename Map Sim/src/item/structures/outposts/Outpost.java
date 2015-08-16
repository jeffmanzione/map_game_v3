package item.structures.outposts;

import maps.GameMap;
import nations.Region;

public abstract class Outpost extends HabitedLocation {
	
	public final Region region;
	
	public Outpost(int unitSlots, int x, int y, GameMap map, Region region) {
		super(unitSlots, x, y, map, region.getOwner().getNation());
		this.region = region;	
		REQ_GARRISON = 5;
		MAX_GARRISON = 20;
	}

}
