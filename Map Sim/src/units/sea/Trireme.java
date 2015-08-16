
package units.sea;

import generic.Discrete;
import generic.Garrisonable;

public class Trireme extends SeaUnit implements Garrisonable {

	public Trireme(Discrete owner) {
		super(owner);
	}
	
	private int garrison = 0;
	
	private final int REQ_GARRISON = 50;
	
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
		return 20;
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

	public int unfilledGarrisonSlots() {
		return getMaxGarrison() - garrison;
	}
	
	public boolean canAddGarrison() {
		return garrison < getMaxGarrison() && 
				getMoneyCostPerUnitInGarrison() * 10 <= owner.getNation().getExactMoney() && 
				getManpowerCostPerUnitInGarrison() * 10 <= owner.getNation().getExactManpower() && 
				getAdministrativeCostGarrison() * 10 <= owner.getNation().getExactAdminPower() &&
				getDiplomaticCostGarrison() * 10 <= owner.getNation().getExactDiplomaticPower() &&
				getMilitaryCostPerUnitInGarrison() * 10 <= owner.getNation().getExactMilitaryPower();
	}
}
