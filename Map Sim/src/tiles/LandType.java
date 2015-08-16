package tiles;

import java.awt.Color;


public enum LandType {

	GRASS(Colors.GRASS_COLOR, Colors.GRASS_DISP), 
	FOREST(Colors.FOREST_COLOR, Colors.FOREST_DISP),
	GRASS_LOW(Colors.GRASS_COLOR, Colors.GRASSL_DISP),
	FOREST_LOW(Colors.FOREST_COLOR, Colors.FORESTL_DISP),
	BEACH(Colors.BEACH_COLOR, Colors.BEACH_DISP), 
	PLAIN(Colors.PLAIN_COLOR, Colors.BEACH_COLOR), 
	DESERT(Colors.DESERT_COLOR, Colors.BEACH_DISP), 
	SWAMP(Colors.SWAMP_COLOR, Colors.SWAMP_DISP), 
	MOUNTAIN(Colors.MOUNTAIN_COLOR, Colors.MOUNTAIN_DISP),
	MT_LOW(Colors.MT_LOW_COLOR, Colors.MT_LOW_DISP),
	MT_PEAK(Colors.MT_PEAK_COLOR, Colors.MT_PEAK_DISP),
	MT_CAP(Colors.MT_CAP_COLOR, Colors.MT_CAP_DISP),
	RIVER(Colors.RIVER_COLOR, Colors.RIVER_DISP), 
	SEA(Colors.SEA_COLOR, Colors.SEA_DISP),
	SHALLOW(Colors.SHALLOW_COLOR, Colors.SHALLOW_DISP), 
	DEEP_SEA(Colors.DEEP_SEA_COLOR, Colors.DEEP_SEA_DISP),
	NONE(Colors.NONE_COLOR, Colors.NONE_DISP);

	
	public Color col, disp;
	
	private LandType(int col, int disp) {
		this.col = new Color(col);
		this.disp = new Color(disp);
	}
	
	public static LandType parseFromColor(int color, int climate) {
		
		switch (color) {
			case Colors.GRASS_COLOR:
				return GRASS;
				/*if (climate == Colors.LIGHT_COLOR) {
					return GRASS_LOW;
				} else {
					return GRASS;
				}*/
			case Colors.FOREST_COLOR:
				return FOREST;
				/*if (climate == Colors.LIGHT_COLOR) {
					return FOREST_LOW;
				} else {
					return FOREST;
				}*/
			case Colors.BEACH_COLOR:
				return BEACH;
			case Colors.PLAIN_COLOR:
				return PLAIN;
			case Colors.DESERT_COLOR:
				return DESERT;
			case Colors.SWAMP_COLOR:
				return SWAMP;
			case Colors.MOUNTAIN_COLOR:
				return MOUNTAIN;
			case Colors.MT_LOW_COLOR:
				return MT_LOW;
			case Colors.MT_PEAK_COLOR:
				return MT_PEAK;
			case Colors.MT_CAP_COLOR:
				return MT_CAP;
			case Colors.RIVER_COLOR:
				return RIVER;
			case Colors.SEA_COLOR:
				return SEA;
			case Colors.SHALLOW_COLOR:
				return SHALLOW;
			case Colors.DEEP_SEA_COLOR:
				return DEEP_SEA;
			default:
				return NONE;
		}
		
	}
	
	public boolean isLand() {
		return !(this == SEA || this == RIVER || this == SHALLOW || this == NONE);
	}
	
	public boolean isWater() {
		return this == SEA || this == SHALLOW || this == RIVER;
	}
	
	public boolean isRiver() {
		return this == RIVER;
	}
	
	public boolean isSea() {
		return this == SEA || this == SHALLOW;
	}
	
	public boolean isDeepSea() {
		return this == DEEP_SEA;
	}
	
	public boolean isShallow() {
		return this == SHALLOW;
	}
	
	public boolean isForest() {
		return this == FOREST;
	}
	
	public boolean isMountain() {
		return this == MOUNTAIN;
	}
	
	public boolean isMountainPeak() {
		return this == MT_PEAK;
	}
	
	public boolean isBeach() {
		return this == BEACH;
	}
	
	public boolean isLandNotBeach() {
		return isLand() && !isBeach();
	}

	public boolean isMountainCap() {
		return this == MT_CAP;
	}
	
	public boolean isLowMountain() {
		return this == MT_LOW;
	}

	public boolean isMountainBetterThanLow() {
		return !isLowMountain() && (isMountain() || isLowMountain() || isMountainCap());
	}
	
	public boolean isMountainType() {
		return (isLowMountain() || isMountain() || isLowMountain() || isMountainCap());
	}
}
