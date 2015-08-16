package item;

import generic.Discrete;
import gfx.ResourceManager;
import item.structures.Instruction;

import java.awt.Font;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import tiles.Tile;
import maps.GameMap;

public abstract class Item implements Discrete {
	private Queue<Instruction> instructions;
	
	public Item() {
		instructions = new ConcurrentLinkedQueue<>();
	}
	
	public abstract String getName();
	
	/** Does the move for a unit.
	 * 
	 * @param map					The map on which to perform the action
	 * @return						The number of actions performed.
	 */
	public int doMove(GameMap map) {
		int numPerformed = 0;
		try {
			while (!instructions.isEmpty()) {
				if (instructions.remove().perform(map)) {
					numPerformed++;
				} else {
					System.err.println("Instruction failed to perform.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return numPerformed;
		
	}
	
	public void passOrder(Tile tile) {
		
	}
}
