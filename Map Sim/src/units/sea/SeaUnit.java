package units.sea;

import generic.Discrete;
import gfx.ResourceManager;

import java.awt.image.BufferedImage;
import java.util.List;

import tiles.Tile;
import units.Unit;

public class SeaUnit extends Unit {
	public SeaUnit(Discrete owner) {
		super(owner);
	}
	
	protected BufferedImage getImage(int scale) {
		//System.out.println(getType() + (isEnRoute() ? "_moving" : "_still"));
		return ResourceManager.get(getType() + (isEnRoute() ? "_moving" : "_still"), scale);
	}

	public List<Tile> getPassableTilesAround(Tile tile) {
		return map.getPassableSeaTilesAround(tile);
	}
	
	public boolean canPass(Tile tile) {
		return !tile.isLand();
	}
	
	
}
