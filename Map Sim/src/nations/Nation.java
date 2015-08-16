package nations;

import item.structures.Structure;
import item.structures.cities.Settlement;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tiles.Tile;

public class Nation {
	private final String id;
	private String name;

	public enum Type {
		CITY, KINGDOM, EMPIRE;
	}

	private Type type;

	private Map<String, Structure> structures;

	public final Color primary, secondary;

	private double money = 50, manpower = 50, admin = 50, diplo = 50,
			milit = 50;

	public Nation(String id, String name, Type type, Color primary,
			Color secondary) {
		this.id = id;
		this.name = name;
		this.type = type;

		structures = new HashMap<String, Structure>();

		this.primary = primary;
		this.secondary = secondary;

	}

	public int getMoney() {
		return (int) money;
	}

	public double getExactMoney() {
		return money;
	}

	public void addMoney(double amount) {
		money += amount;
	}

	public void subtractMoney(double amount) {
		money -= amount;
	}

	public int getManpower() {
		return (int) manpower;
	}

	public double getExactManpower() {
		return manpower;
	}

	public int getAdminPower() {
		return (int) admin;
	}

	public double getExactAdminPower() {
		return admin;
	}

	public int getDiplomaticPower() {
		return (int) diplo;
	}

	public double getExactDiplomaticPower() {
		return diplo;
	}

	public int getMilitaryPower() {
		return (int) milit;
	}

	public double getExactMilitaryPower() {
		return milit;
	}

	public String getName() {
		return name;
	}

	public String getID() {
		return id;
	}

	public Type getType() {
		return type;
	}

	/* Do not modify this map!!! */
	public Map<String, Structure> getStructures() {
		return structures;
	}

	public void addStructure(Structure city) {
		structures.put(city.getName(), city);
	}

	private double manpowerGrowth = 0;
	private double moneyGrowth = 0;

	public double getExactManpowerGrowthPerMonth() {
		return manpowerGrowth;
	}

	public int getManpowerGrowthPerMonth() {
		return (int) manpowerGrowth;
	}

	public double getExactMoneyGrowthPerMonth() {
		return moneyGrowth;
	}

	public int getMoneyGrowthPerMonth() {
		return (int) moneyGrowth;
	}

	public void advance() {
		double tmpMoneyGrowth = 0, tmpManpowerGrowth = 0;
		for (Structure city : structures.values()) {
			city.advance();
			tmpMoneyGrowth += city.getExactMoneyGrowth();
			tmpManpowerGrowth += city.getExactManpowerGrowth();
		}
		manpowerGrowth = tmpManpowerGrowth;
		moneyGrowth = tmpMoneyGrowth;
	}

	public void advanceMonth() {
		for (Structure city : structures.values()) {
			city.advanceMonth();
			manpower += city.getExactManpowerGrowth();
			money += city.getExactMoneyGrowth();
		}
	}

	public void advanceYear() {
		for (Structure city : structures.values()) {
			city.advanceYear();
		}
	}

	public void incurCost(double money, double manpower, double admin,
			double diplo, double milit) {
		this.money -= money;
		this.manpower -= manpower;
		this.admin -= admin;
		this.diplo -= diplo;
		this.milit -= milit;
	}

	public void gainBenefit(double money, double manpower, double admin,
			double diplo, double milit) {
		this.money += money;
		this.manpower += manpower;
		this.admin += admin;
		this.diplo += diplo;
		this.milit += milit;
	}

	public Collection<Tile> getTiles() {
		Set<Tile> tiles = new HashSet<Tile>();

		for (Structure struct : this.structures.values()) {
			if (struct instanceof Settlement) {
				Settlement set = (Settlement) struct;
				tiles.addAll(set.getTiles());
			}
		}
		
		return tiles;

	}
}
