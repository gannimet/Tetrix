package de.riwo.tetrix.tetris.command;

import android.util.Log;
import de.riwo.tetrix.tetris.Grid;
import de.riwo.tetrix.tetris.IllegalFixRequestException;
import de.riwo.tetrix.tetris.stone.Stone;

public class MoveDownCommand extends MoveCommand {

	private boolean[][] stoneAlloc = stone.getAllocationInBoundingBox();
	private int stoneX = stone.getX();
	private int stoneY = stone.getY();
	private int stoneWidth = stoneAlloc.length;
	private int stoneHeight = stoneAlloc[0].length;
	
	public MoveDownCommand(Stone stone, Grid grid) {
		super(stone, grid);
	}

	@Override
	public void execute() throws IllegalFixRequestException {
		synchronized(stone) {
			stoneX = stone.getX();
			stoneY = stone.getY();
			if(isMovePossible()) {
				// if possible, move stone downwards
				stone.setY(stoneY + 1);
			} else {
				// if not, there's no other option than to fix it
				try {
					grid.fixStone(stone);
				} catch (IllegalFixRequestException e) {
					Log.w(getClass().getName(), e.getMessage(), e);
					throw e;
				}
			}
		}
	}
	
	private synchronized boolean isMovePossible() {
		// for each column
		for(int x = 0; x < stoneWidth; x++) {
			// iterate through column backwards
			for(int y = stoneHeight-1; y >= 0; y--) {
				// is there a brick at bounding box position (x,y)?
				if(stoneAlloc[x][y]) {
					/* when we get here, it means that y is the
					 * y-coordinate within the bounding box at
					 * which the downmost brick of the stone is
					 */
					
					// determine whether the cell below the
					// last brick is free
					if(!grid.isFree(
						stoneX + x, // x position of stone + x-offset
						stoneY + y + 1 // y position of stone + y-offset
						// + 1, because the stone shall move down by 1
					)) {
						return false;
					} else {
						// cell below is free, what more could we ask for?
						break;
					}
				}
			}
		}
		
		/* if we survived the loop, there was no reason not to allow
		 * the stone to move downwards, i. e., there are no bricks
		 * in the way and we're inside the grid's bounds */
		return true;
	}

	@Override
	public synchronized void onStoneRotated(Stone stone) {
		stoneAlloc = stone.getAllocationInBoundingBox();
	}
	
	@Override
	public void setStone(Stone stone) {
		super.setStone(stone);
		stoneAlloc = stone.getAllocationInBoundingBox();
		stoneWidth = stoneAlloc.length;
		stoneHeight = stoneAlloc[0].length;
	}

}