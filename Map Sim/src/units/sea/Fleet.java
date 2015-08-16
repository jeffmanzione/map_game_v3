package units.sea;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import generic.Discrete;

public class Fleet extends SeaUnit {

	private Set<NavalUnit> ships = new TreeSet<NavalUnit>(new Comparator<NavalUnit>() {
		public int compare(NavalUnit u1, NavalUnit u2) {
			return Integer.compare(u1.getRank(), u2.getRank());
		}
	});
	
	public Fleet(Discrete owner) {
		super(owner);
	}

	public Set<NavalUnit> getShips() {
		return ships;
	}
	
	public void addShip(NavalUnit unit) {
		ships.add(unit);
	}
	
	public void removeShip(NavalUnit unit) {
		ships.remove(unit);
	}
	
}
