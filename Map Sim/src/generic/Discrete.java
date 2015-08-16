package generic;

import nations.Nation;
import tiles.Tile;
import units.Unit;

/** Anything that is separate or distinct. This implies that the object has a
 * type.
 * 
 * @author Jeff
 *
 */
public interface Discrete {
	/** Gets the type of the object.
	 */
	public abstract String getType();
	
	//public abstract String get
	
	public abstract void passOrder(Tile tile);

	public abstract Nation getNation();
	
	public abstract void advance();

	public abstract void advanceMonth();

	public abstract void advanceYear();

	public abstract void addUnit(Unit unit);

	
}
