package de.riwo.tetrix.tetris.command;

import java.util.ArrayList;
import java.util.List;

import de.riwo.tetrix.tetris.Grid;
import de.riwo.tetrix.tetris.stone.Stone;

public abstract class RotateCommand extends StoneCommand {

	public static final int CLASSIC_ROTATION = 1;
	
	private List<StoneRotationListener> listeners =
		new ArrayList<StoneRotationListener>();
	
	public RotateCommand(Stone stone, Grid grid) {
		super(stone, grid);
	}
	
	public void addStoneRotationListener(StoneRotationListener listener) {
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeStoneRotationListener(StoneRotationListener listener) {
		listeners.remove(listener);
	}
	
	public void notifyStoneRotationListeners() {
		if(listeners.isEmpty())
			return;
		
		if(listeners.size() == 1) {
			listeners.get(0).onStoneRotated(stone);
			return;
		}
		
		if(listeners.size() == 2) {
			listeners.get(0).onStoneRotated(stone);
			listeners.get(1).onStoneRotated(stone);
			return;
		}
		
		if(listeners.size() == 3) {
			listeners.get(0).onStoneRotated(stone);
			listeners.get(1).onStoneRotated(stone);
			listeners.get(2).onStoneRotated(stone);
			return;
		}
		
		for(StoneRotationListener listener : listeners) {
			listener.onStoneRotated(stone);
		}
	}
	
	/**
	 * Whether a rotation of the current {@link Stone} is possible considering
	 * the provided conditions.
	 * @param desiredAllocation the allocation which the stone would have
	 * after the rotation
	 * @param xShift the number of cells that the stone has to move in
	 * x direction relative to the previous orientation to ensure a
	 * smooth rotation (may be negative)
	 * @param yShift the number of cells that the stone has to move in
	 * y direction relative to the previous orientation to ensure a
	 * smooth rotation (may be negative)
	 * @return {@code true}, if a rotation under the provided conditions is
	 * possible
	 */
	protected synchronized boolean isRotationPossible(boolean[][] desiredAllocation, int xShift, int yShift) {
		// iterate through the desired allocation
		for(int y = 0; y < desiredAllocation[0].length; y++) {
			for(int x = 0; x < desiredAllocation.length; x++) {
				
				if(desiredAllocation[x][y]) {
					// is there a brick at position (x, y)?
					if(!grid.isFree(
						stone.getX() + xShift + x,
						stone.getY() + yShift + y
					)) {
						// is the translated grid position
						// occupied? then the rotation is impossible
						return false;
					}
					
				}
				
			}
		}
		
		// if we survived the loop, everything is fine to rotate
		return true;
	}

}