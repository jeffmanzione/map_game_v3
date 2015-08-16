package generic;

public interface Garrisonable {
	
	public void addGarrison();
	public void subtractGarrison();
	
	public int getGarrison();
	public int getRequiredGarrison();
	public int getMaxGarrison();
	
	public int unfilledGarrisonSlots();
	
	public double getMoneyCostPerUnitInGarrison();
	public double getManpowerCostPerUnitInGarrison();
	public double getAdministrativeCostPerUnitInGarrison();
	public double getDiplomaticCostPerUnitInGarrison();
	public double getMilitaryCostPerUnitInGarrison();
	
	public double getMoneyCostGarrison();
	public double getManpowerCostGarrison();
	public double getAdministrativeCostGarrison();
	public double getDiplomaticCostGarrison();
	public double getMilitaryCostGarrison();

	public boolean canAddGarrison();
	
}
