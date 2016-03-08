package de.riwo.tetrix.tetris.stone;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory class that allows you to share {@link Stone} objects.
 * @author richard
 *
 */
public class StoneFactory {

	private Map<StoneType, Stone> stones = new HashMap<StoneType, Stone>();
	// for memory consumption's sake, we don't wanna allocate a stone
	// object every time a client requests on via getStone()
	private Stone workingStone;
	
	/**
	 * Returns the shared {@link Stone} object specified by
	 * {@code stoneType} or creates it if necessary. Two calls to
	 * this method with the same {@code stoneType} attribute will
	 * return the same object (in the sense of {@code ==}).
	 * @param stoneType type of stone to be created, as defined
	 * by the constants in {@link Stone}
	 * @param color int value of {@link android.graphics.Color} for
	 * the requested stone
	 * @param x x-coordinate of the stone.
	 * @return Existing or newly created {@link Stone} object
	 * @throws IllegalStoneTypeException if the provided {@code stoneType}
	 * is unknown
	 * @see Stone
	 */
	public Stone getStone(StoneType stoneType, int color, int x)
	throws IllegalStoneTypeException {
		workingStone = stones.get(stoneType);
		if(workingStone != null) {
			// a stone of this type did already exist
			// and we can simply reuse it
			workingStone.setColor(color);
			workingStone.reset();
			workingStone.setX(x);
			return workingStone;
		} else {
			// stone didn't exist before -> create one
			switch(stoneType) {
				case BLOCK_STONE:
					workingStone = new BlockStone(color, x);
					break;
				case FOUR_BAR_STONE:
					workingStone = new FourBarStone(color, x);
					break;
				case INVERSED_L_STONE:
					workingStone = new InversedLStone(color, x);
					break;
				case LEFT_CHAIR_STONE:
					workingStone = new LeftChairStone(color, x);
					break;
				case PODIUM_STONE:
					workingStone = new PodiumStone(color, x);
					break;
				case RIGHT_CHAIR_STONE:
					workingStone = new RightChairStone(color, x);
					break;
				case STRAIGHT_L_STONE:
					workingStone = new StraightLStone(color, x);
					break;
				case SINGLE_STONE:
					workingStone = new SingleStone(color, x);
					break;
				default:
					throw new IllegalStoneTypeException(
						"Requested stone type " + stoneType +
						" does not exist.");
			}
			stones.put(stoneType, workingStone);
			workingStone.reset();
			return workingStone;
		}
	}
	
}