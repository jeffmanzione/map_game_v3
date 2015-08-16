package tiles;

public class QuadState {
	
	public enum State {
		FULL, CONVEX;
	}
	
	public class Corner {
		
		public final LandType maj, min;
		
		public final State state;
		
		public Corner(LandType type) {
			state = State.FULL;
			maj = type;
			min = type;
		}
		
		public Corner(LandType maj, LandType min) {
			state =  State.CONVEX;
			this.maj = maj;
			this.min = min;
		}

	}
	
	public Corner[] corners;

	public QuadState(LandType quad, TileGroup around) {
		corners = new Corner[4];
		checkCorner(0, quad, around.aboveLeft, around.left, around.above);
		checkCorner(1, quad, around.aboveRight, around.right, around.above);
		checkCorner(2, quad, around.belowLeft, around.left, around.below);
		checkCorner(3, quad, around.belowRight, around.right, around.below);
	}
	
	private void checkCorner(int index, LandType quad, LandType diag, 
			LandType adj1, LandType adj2) {
		/* For smoother river/sea borders */
		if ((quad.isSea() && (adj1.isRiver() || adj2.isRiver())) ||
				
				(quad.isRiver() && (adj1.isSea() || adj2.isSea()))) {
			
			corners[index] = new Corner(quad);
			
		} else if(quad.isRiver() && diag.isWater()) {
			corners[index] = new Corner(quad);
	    } else if (
	    		/* If this quad equals none of the adjacent ones */
	    		(adj1 != quad && diag != quad && adj2 != quad) ||
	    		
				/* Diagonal sliver of Land with beach on both sides. */
				(quad.isBeach() && adj1.isLandNotBeach() && adj2.isLandNotBeach()) ||
				
				/* Deep sea and sea. */
				(quad.isDeepSea() && adj1.isSea() && adj2.isSea()) ||
				
				/* Sea and Shallow. */
				(quad.isSea() && adj1.isShallow() && adj2.isShallow()) ||
				
				/* River gets priority over land. */
				(quad.isLand() && adj1.isRiver() && adj2.isRiver()) ||	
				
				/* Land gets priority over sea. */
				(quad.isWater() && adj1.isLand() && adj2.isLand()) ||
				
				/* Mountain Cap gets priority over everything. */
				(quad.isMountainPeak() && adj1.isMountainCap() && adj2.isMountainCap()) ||
				
				/* Mountain Peak gets priority over everything. */
				(quad.isMountain() && adj1.isMountainPeak() && adj2.isMountainPeak()) ||
				
				/* Mountain gets priority over everything. */
				(quad.isLowMountain() && adj1.isMountain() && adj2.isMountain()) ||
				
				/* Low mountain gets priority over everything. */
				(!quad.isMountainType() && adj1.isLowMountain() && adj2.isLowMountain()) ||
				
				/* Forest gets priority over everything. */
				(!quad.isForest() && adj1.isForest() && adj2.isForest())) {
			
	    	LandType filler;
	    	
	    	if (adj1 != adj2) {
	    		filler = LandType.GRASS;
	    	} else {
	    		filler = ((adj1 == adj2 && (!adj2.isBeach() || quad.isWater() || (quad.isLandNotBeach() && adj1.isBeach() && adj2.isBeach() && diag.isBeach()))) ? adj1 : LandType.GRASS);
	    	}
			corners[index] = new Corner(quad, filler);
			
		} else {
			corners[index] = new Corner(quad);
		}
	}


}
