package item.structures.peripheral;

import maps.GameMap;
import nations.Nation;
import generic.Constructable;
import item.structures.ConstructableStructure;

public class Port extends ConstructableStructure implements Constructable {

	public Port(int unitSlots, int x, int y, GameMap map, Nation owner) {
		super(unitSlots, 1, x, y, map, owner);
	}

	@Override
	public String getType() {
		return "Port";
	}

}
