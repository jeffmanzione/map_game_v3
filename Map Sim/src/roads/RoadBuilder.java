package roads;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import nations.Nation;
import maps.GameMap;
import tiles.Tile;

public class RoadBuilder {

	private static final double SQRT2 = Math.sqrt(2);

	public static List<Tile> getPath(final GameMap map, final Nation owner,
			Tile start, Tile destination) {

		List<Tile> path = new ArrayList<Tile>();

		// the pos is the current position being checked
		Tile pos = start;

		// the arrays that store stuff
		final Tile[][] tiles = new Tile[map.map_width][map.map_height];
		final double[][] costs = new double[map.map_width][map.map_height];
		final Tile[][] preds = new Tile[map.map_width][map.map_height];

		// this set holds all of the visited tiles
		final Set<Tile> visited = new HashSet<Tile>();

		// this holds the tiles
		final TreeSet<Tile> cheapest = new TreeSet<Tile>(
				new Comparator<Tile>() {
					public int compare(Tile t1, Tile t2) {
						if (t1 == t2 || t1.equals(t2))
							return 0;

						boolean ct1 = visited.contains(t1);
						boolean ct2 = visited.contains(t2);

						if (ct1 && !ct2)
							return 1;
						else if (ct2 && !ct1)
							return -1;
						else {
							double num = costs[t1.x][t1.y] - costs[t2.x][t2.y];

							if (num == 0) {
								// Should never get into the first
								// if (EVER)
								if (t1.hashCode() == t2.hashCode())
									return 0;
								else if (t1.hashCode() > t2.hashCode())
									return 1;
								else
									return -1;
							} else if (num > 0)
								return 1;
							else
								return -1;
						}
					}
				});

		tiles[pos.x][pos.y] = pos;
		costs[pos.x][pos.y] = 0;
		preds[pos.x][pos.y] = null;

		// loop to iterate
		while (true) {
			// adds the current position to the visited set

			int x = pos.x;
			int y = pos.y;

			cheapest.remove(pos);
			visited.add(pos);

			// if it turns out that the pos is the destination,
			// do something
			if (pos == destination) {
				path = new ArrayList<Tile>();
				// double bestCost = costs[pos.x][pos.y];
				// basically adds the path with the smallest cost.
				while (pos != start) {
					path.add(pos);
					pos = preds[pos.x][pos.y];
				}

				path.add(start);

				return path;
			}

			// The neighbor tiles
			List<Tile> neighbors = getPassableTilesAround(map, owner, pos);

			// looping through all of the neighbor tiles
			for (Tile neighbor : neighbors) {

				int nX = neighbor.x;
				int nY = neighbor.y;
				
				// if the tile has not been visited
				if (valid(map, owner, nX, nY)) {

					double factor = neighbor.getPassageCost();

					// if(neighbor. instanceof River ||
					// neighbor.getItem() instanceof Forest)
					// factor*=1.5;
					// else if(neighbor.getItem() instanceof
					// Settlement) factor*=0.8;

					// double elevationDifference =
					// (world.elevation[nX][nY] -
					// world.elevation[x][y])/256;

					// if(elevationDifference < 0)
					// elevationDifference = 0;

					// calculates the cost from this position to
					// this neighbor
					
					
					double dcost = neighbor.hasRoad() ? 0 : (((Math.abs(x - nX) + Math.abs(y - nY)) % 2 == 0) ? SQRT2
							: 1.000) * (factor /*
									 * + elevationDifference
									 */);
					
					double cost = costs[x][y]
							+ dcost;
//					if (neighbor.hasRoad()) {
//						cost = 0.001;
//					}
					
					// System.out.println(factor);
					// if neighbor already has data
					if (tiles[nX][nY] != null) {
						// Calculates the cost already in the list
						double existingCost = costs[nX][nY];
						// if the new cost is less than the old one,
						// change the values to the new one
						if (existingCost > cost) {
							cheapest.remove(neighbor);
							costs[nX][nY] = cost;
							preds[nX][nY] = pos;
							cheapest.add(neighbor);
						}
					} /* otherwise add a new entry */else {
						tiles[nX][nY] = neighbor;
						costs[nX][nY] = cost;
						preds[nX][nY] = pos;
						cheapest.add(neighbor);
					}
				}
			}

			if (cheapest.size() == 0) {
				System.out.println("FAIL");
				return new ArrayList<>();
			}

			// go to the next cheapest one

			pos = cheapest.first();

		}

	}

	private static boolean valid(GameMap map, Nation owner, int x, int y) {
		return map.valid(x, y) && map.tileAt(x, y).getOwner() == owner;
	}

	public static List<Tile> getPassableTilesAround(GameMap map, Nation owner, Tile t) {

		List<Tile> tiles = new ArrayList<Tile>();

		int x = t.x;
		int y = t.y;

		if (valid(map, owner, x + 1, y)) {
			tiles.add(map.tileAt(x + 1, y));
		}

		if (valid(map, owner, x, y + 1)) {
			tiles.add(map.tileAt(x, y + 1));
		}

		if (valid(map, owner, x, y - 1)) {
			tiles.add(map.tileAt(x, y - 1));
		}

		if (valid(map, owner, x - 1, y)) {
			tiles.add(map.tileAt(x - 1, y));
		}

		if (valid(map, owner, x + 1, y + 1)) {
			tiles.add(map.tileAt(x + 1, y + 1));
		}

		if (valid(map, owner, x - 1, y + 1)) {
			tiles.add(map.tileAt(x - 1, y + 1));
		}

		if (valid(map, owner, x + 1, y - 1)) {
			tiles.add(map.tileAt(x + 1, y - 1));
		}

		if (valid(map, owner, x - 1, y - 1)) {
			tiles.add(map.tileAt(x - 1, y - 1));
		}

		//System.out.println(tiles.size());

		return tiles;
	}

}
