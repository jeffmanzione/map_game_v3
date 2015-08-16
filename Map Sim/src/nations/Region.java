package nations;

import item.structures.ConstructableStructure;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import tiles.Tile;

public class Region {


	public final UUID uuid;

	public final Color color;
	
	private ConstructableStructure city;
	
	public Region(ConstructableStructure constructableStructore) {
		uuid = UUID.randomUUID();
		this.city = constructableStructore;
		Random rand = new Random(uuid.getLeastSignificantBits());
		color = new Color(Math.abs(rand.nextInt() % 255), Math.abs(rand.nextInt() % 255), Math.abs(rand.nextInt() % 255));
	}

	public void add(List<Tile> til) {
		for (Tile tile : til) {
			add(tile);
		}
	}

	public void add(Tile tile) {
		tile.setOwner(this);
		tiles.put(tile.hashCode(), tile);

	}


	public void has(Tile tile) {
		tiles.containsKey(tile.hashCode());
	}

	Map<Integer, Tile> tiles = new HashMap<Integer, Tile>();

	public ConstructableStructure getOwner() {
		return city;
	}
	
	public Collection<Tile> getTiles() {
		return tiles.values();
	}

}
