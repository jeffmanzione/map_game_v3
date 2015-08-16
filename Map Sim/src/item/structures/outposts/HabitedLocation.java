package item.structures.outposts;

import item.structures.Structure;
import generic.Garrisonable;
import maps.GameMap;
import nations.Nation;

public abstract class HabitedLocation extends Structure implements Garrisonable {

	public final int x, y;
	
	protected Nation owner;
	
	protected GameMap map;
	
	public HabitedLocation(int unitSlots, int x, int y, GameMap map, Nation owner) {
		
		super(unitSlots);

		this.x = x;
		this.y = y;
		this.map = map;
		this.owner = owner;
	}
	
	private int garrison = 0;
	protected int REQ_GARRISON = 20;
	protected int MAX_GARRISON = 50;
	
	public Nation getOwner() {
		return owner;
	}
	
	public Nation getNation() {
		return owner;
	}
	
	public void addGarrison() {
		this.getNation().incurCost(getMoneyCostPerUnitInGarrison() * 10, 
				getManpowerCostPerUnitInGarrison() * 10, 
				getAdministrativeCostPerUnitInGarrison() * 10, 
				getDiplomaticCostPerUnitInGarrison() * 10, 
				getMilitaryCostPerUnitInGarrison() * 10);
		garrison++;
	}

	public void subtractGarrison() {
		garrison--;
	}

	public int getGarrison() {
		return garrison + REQ_GARRISON;
	}
	public int getRequiredGarrison() {
		return REQ_GARRISON;
	}

	public int getMaxGarrison() {
		return MAX_GARRISON;
	}
	
	public int unfilledGarrisonSlots() {
		return MAX_GARRISON - garrison;
	}
	
	public double getMoneyCostPerUnitInGarrison() {
		return 0.2;
	}

	public double getManpowerCostPerUnitInGarrison() {
		return 0.1;
	}

	public double getAdministrativeCostPerUnitInGarrison() {
		return 0;
	}

	public double getDiplomaticCostPerUnitInGarrison() {
		return 0;
	}

	public double getMilitaryCostPerUnitInGarrison() {
		return 0.1;
	}
	
	public double getMoneyCostGarrison() {
		return this.getMoneyCostPerUnitInGarrison() * garrison;
	}

	public double getManpowerCostGarrison() {
		return this.getManpowerCostPerUnitInGarrison() * garrison;
	}

	public double getAdministrativeCostGarrison() {
		return this.getAdministrativeCostPerUnitInGarrison() * garrison;
	}

	public double getDiplomaticCostGarrison() {
		return this.getDiplomaticCostPerUnitInGarrison() * garrison;
	}

	public double getMilitaryCostGarrison() {
		return this.getMilitaryCostPerUnitInGarrison() * garrison;
	}
	
	public boolean canAddGarrison() {
		return garrison < MAX_GARRISON && 
				getMoneyCostPerUnitInGarrison() * 10 <= owner.getExactMoney() && 
				getManpowerCostPerUnitInGarrison() * 10 <= owner.getExactManpower() && 
				getAdministrativeCostGarrison() * 10 <= owner.getExactAdminPower() &&
				getDiplomaticCostGarrison() * 10<= owner.getExactDiplomaticPower() &&
				getMilitaryCostPerUnitInGarrison() * 10 <= owner.getExactMilitaryPower();
	}
}
