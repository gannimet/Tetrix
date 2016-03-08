package de.riwo.tetrix.tetris.stone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Point;
import de.riwo.tetrix.tetris.Grid;

/**
 * Class to iterate through the allocated bricks of a {@link Stone}.
 * The iterator is listing all bricks of the provided
 * {@link Stone} as {@link Point} objects, where {@code (0, 0)}
 * is the top-left position of the corresponding {@link Grid}.
 * 
 * The order of iteration is line by line.
 * @author richard
 *
 */
public class StoneIterator implements Iterator<Point> {

	private Iterator<Point> iter;
	
	// avoid creating garbage, reuse this List
	private List<Point> content = new ArrayList<Point>();

	/**
	 * Creates an {@link Iterator} listing all bricks of the provided
	 * {@link Stone}.
	 * @param stone {@link Stone} for which the iterator should be
	 * created
	 */
	public StoneIterator(Stone stone) {
		flatten(stone);
	}
	
	private void flatten(Stone stone) {
		content.clear();
		boolean[][] alloc = stone.getAllocationInBoundingBox();
		int stoneX = stone.getX();
		int stoneY = stone.getY();
		for(int y = 0; y < alloc[0].length; y++) {
			for(int x = 0; x < alloc.length; x++) {
				if(alloc[x][y]) {
					// TODO look for nicer solution without creating garbage
					content.add(new Point(stoneX + x, stoneY + y));
				}
			}
		}
		iter = content.iterator();
	}
	
	/**
	 * Change the {@link Stone} object through which to iterate.
	 * @param stone {@link Stone} through which you want to iterate
	 */
	public void setStone(Stone stone) {
		flatten(stone);
	}
	
	/**
	 * Whether there is another brick in the {@link Stone}
	 * @return {@code true}, if there is another brick, i. e.,
	 * whether a call to {@link #next()} will return another
	 * {@link Point}.
	 */
	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	/**
	 * The next allocated brick within the {@link Stone}.
	 * 
	 * One is strongly recommended to verify whether there is
	 * another brick by calling {@link #hasNext()} first. A call
	 * to this method even though {@link #hasNext()} has returned
	 * or would return {@code false} will result in undefined
	 * behavior.
	 * @return 
	 */
	@Override
	public Point next() {
		return iter.next();
	}

	/**
	 * Required by the {@link Iterator} interface; this method
	 * does nothing by default.
	 */
	@Override
	public void remove() {
		// do nothing
	}

}