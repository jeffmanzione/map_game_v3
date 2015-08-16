package tiles;

public class TileGroup {
	public LandType above, below, left, right, aboveLeft, aboveRight, 
			belowLeft, belowRight;
	
	private TileGroup() {
		
	}
	
	public static TileGroup createForUpperLeft(final Tile curr, final Tile upper, 
			final Tile leftTile, final Tile upperLeft) {

		return new TileGroup() {
			{
				above 	   = upper == null ? curr.getUpperLeft() : upper.getLowerLeft();
				aboveRight = upper == null ? curr.getUpperLeft() : upper.getLowerRight();
				right 	   = curr.getUpperRight();
				belowRight = curr.getLowerRight();
				below 	   = curr.getLowerLeft();
				belowLeft  = leftTile == null ? curr.getUpperLeft() : leftTile.getLowerRight();
				left 	   = leftTile == null ? curr.getUpperLeft() : leftTile.getUpperRight();
				aboveLeft  = upperLeft == null ? curr.getUpperLeft() : upperLeft.getLowerRight();
			}
		};
	}
	
	public static TileGroup createForUpperRight(final Tile curr, final Tile upper,
			final Tile rightTile, final Tile upperRight) {
		
		return new TileGroup() {
			{
				above 	   = upper == null ? curr.getUpperRight() : upper.getLowerRight();
				aboveRight = upperRight == null ? curr.getUpperRight() : upperRight.getLowerLeft();
				right 	   = rightTile == null ? curr.getUpperRight() : rightTile.getUpperLeft();
				belowRight = rightTile == null ? curr.getUpperRight() : rightTile.getLowerLeft();
				below 	   = curr.getLowerRight();
				belowLeft  = curr.getLowerLeft();
				left 	   = curr.getUpperLeft();
				aboveLeft  = upper == null ? curr.getUpperRight() : upper.getLowerLeft();
			}
		};
	}
	
	public static TileGroup createForLowerLeft(final Tile curr, final Tile lower,
			final Tile leftTile, final Tile lowerLeft) {
		
		return new TileGroup() {
			{
				above 	   = curr.getUpperLeft();
				aboveRight = curr.getUpperRight();
				right 	   = curr.getLowerRight();
				belowRight = lower == null ? curr.getLowerLeft() : lower.getUpperRight();
				below 	   = lower == null ? curr.getLowerLeft() : lower.getUpperLeft();
				belowLeft  = lowerLeft == null ? curr.getLowerLeft() : lowerLeft.getUpperRight();
				left 	   = leftTile == null ? curr.getLowerLeft() : leftTile.getLowerRight();
				aboveLeft  = leftTile == null ? curr.getLowerLeft() : leftTile.getUpperRight();
			}
		};
	}
	
	public static TileGroup createForLowerRight(final Tile curr, final Tile lower,
			final Tile rightTile, final Tile lowerRight) {
		return new TileGroup() {
			{
				above 	   = curr.getUpperRight();
				aboveRight = rightTile == null ? curr.getLowerRight() : rightTile.getUpperLeft();
				right 	   = rightTile == null ? curr.getLowerRight() : rightTile.getLowerLeft();
				belowRight = lowerRight == null ? curr.getLowerRight() : lowerRight.getUpperLeft();
				below 	   = lower == null ? curr.getLowerRight() : lower.getUpperRight();
				belowLeft  = lower == null ? curr.getLowerRight() : lower.getUpperLeft();
				left 	   = curr.getLowerLeft();
				aboveLeft  = curr.getUpperLeft();
			}
		};
	}
}
