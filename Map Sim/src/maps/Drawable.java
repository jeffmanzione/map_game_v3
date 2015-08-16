package maps;
import java.awt.Graphics2D;


public interface Drawable {
	/** Draws this object on the map at the specified location and at the
	 * specified scale.
	 * @param g					The graphics on which to draw
	 * @param x					The x coordinate
	 * @param y					The y coordinate
	 * @param scale				The object scale size
	 * @param map				The GameMap Object
	 * @return					<b>true</b> if the draw was successful,
	 * 							<b>false</b> if the draw failed.
	 */
	public abstract boolean draw(Graphics2D g, int x, int y, int drawX, 
			int drawY, int scale, GameMap map, boolean hover, boolean selected, boolean fancy);
	
	public abstract boolean drawLabel(Graphics2D g, int x, int y, int drawX,
			int drawY, int scale, GameMap map, boolean hover, boolean selected, boolean fancy);
}
