package parsers;

import generic.Discrete;
import item.structures.cities.Camp;
import item.structures.cities.City;
import item.structures.cities.LargeCity;
import item.structures.cities.Settlement;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import units.Farmer;
/*import units.Settler;*/
import units.Unit;
import units.army.Army;
import units.sea.ColonyShip;
import units.sea.SpacePirate;
import units.sea.Transport;
import units.sea.Trireme;
import maps.GameMap;
import nations.Nation;

public class NationParser extends Parser {

	private NationParser(File file) throws FileNotFoundException {
		super(file);
	}

	private Map<String, Class<Unit>> units = new HashMap<>();


	@SuppressWarnings("unchecked")
	private static final Class<Unit>[] UNIT_CLASSES = new Class[] { /*Settler.class,*/ Transport.class, Trireme.class, ColonyShip.class, SpacePirate.class, Farmer.class, Army.class};

	protected Map<String, Nation> parse (GameMap gameMap) throws IOException {

		for (Class<Unit> c : UNIT_CLASSES) {
			units.put(c.getSimpleName(), c);
		}

		Map<String, Nation> hash = new HashMap<String, Nation>();
		while (lookahead (CHAR)) {
			Module mod = parseAssignment();
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) mod.ref;
			String cityLevel = (String) map.get("type");
			Nation.Type type;
			if (cityLevel.equals("city")) {
				type = Nation.Type.CITY;
			} else if (cityLevel.equals("kingdom")) {
				type = Nation.Type.KINGDOM;
			} else {
				type = Nation.Type.EMPIRE;
			}

			Nation nat = new Nation(mod.id, (String) map.get("text"), type, 
					(Color) map.get("primary"), (Color) map.get("secondary"));
			hash.put(mod.id, nat);

			@SuppressWarnings("unchecked")
			Map<String, Map<String, Object>> settlements = 
			(Map<String, Map<String, Object>>) map.get("settlements");

			Comparator<Map.Entry<String, Map<String, Object>>> comp = new Comparator<Map.Entry<String, Map<String, Object>>>() {

				public int compare(Entry<String, Map<String, Object>> e1, Entry<String, Map<String, Object>> e2) {
					return Integer.compare((int) e2.getValue().get("population"), (int) e1.getValue().get("population")); 
				}

			};
			List<Map.Entry<String, Map<String, Object>>> entries = new LinkedList<Map.Entry<String, Map<String, Object>>>(settlements.entrySet());
			Collections.sort(entries, comp);

			for (Entry<String, Map<String, Object>> e : entries) {
				Map<String, Object> details = e.getValue();

				int x = (int) details.get("x");
				int y = (int) details.get("y");
				int population = (int) details.get("population");
				String text = details.get("text").toString();
				String lvl = details.get("lvl").toString();

				Settlement city = null;

				switch (lvl) {
				case "camp":
					city = new Camp(text, population, x, y, gameMap, nat);
					break;
				case "village":
					System.err.println(lvl + " not yet implemented!");
					break;
				case "town":
					System.err.println(lvl + " not yet implemented!");
					break;
				case "city":
					city = new City(text, population, x, y, gameMap, nat);
					break;
				case "large_city":
					city = new LargeCity(text, population, x, y, gameMap, nat);
					break;
				default:
					System.err.println("Invalid lvl for city (" + e.getKey() + 
							") with value \"" + lvl + "\".");
				}

				if (city != null) {
					nat.addStructure(city);
					gameMap.put(city, x, y);



					@SuppressWarnings("unchecked")
					Map<String, Map<String, Object>> unit = 
					(Map<String, Map<String, Object>>) details.get("units");

					if (unit != null) {

						for (Entry<String, Map<String, Object>> ent : unit.entrySet()) {
							Map<String, Object> dets = ent.getValue();
							int xi = (int) dets.get("x");
							int yi = (int) dets.get("y");
							String typ = (String) dets.get("type");

							try {
								Unit u = units.get(typ).getConstructor(new Class[] {Discrete.class}).newInstance(city);
								gameMap.put(u, xi, yi);
							} catch (Exception ex) {
								ex.printStackTrace();
							}

						}
					}
				}

			}

		}
		return hash;
	}

	public static Map<String, Nation> loadNationsFromFile(String fileName, GameMap map) throws IOException {
		File file = new File(fileName);
		NationParser parser = new NationParser(file);
		return parser.parse(map);

		/*for (Nation nat : map.values()) {
			System.out.println(nat.getID());
			System.out.println(nat.getName());
			System.out.println(nat.primary);
			System.out.println(nat.secondary);
			System.out.println(nat.getType());
			System.out.println();
		}*/

	}

}
