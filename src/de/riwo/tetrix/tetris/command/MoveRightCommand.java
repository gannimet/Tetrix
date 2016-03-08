package de.riwo.tetrix.tetris.command;

import de.riwo.tetrix.tetris.Grid;
import de.riwo.tetrix.tetris.IllegalFixRequestException;
import de.riwo.tetrix.tetris.stone.Stone;

public class MoveRightCommand extends MoveCommand {

	private boolean[][] stoneAlloc = stone.getAllocationInBoundingBox();
	private int stoneX = stone.getX();
	private int stoneY = stone.getY();
	private int stoneWidth = stoneAlloc.length;
	private int stoneHeight = stoneAlloc[0].length;
	
	public MoveRightCommand(Stone stone, Grid grid) {
		super(stone, grid);
	}

	@Override
	public void execute() throws IllegalFixRequestException {
		synchronized(stone) {
			stoneX = stone.getX();
			stoneY = stone.getY();
			if(isMovePossible()) {
				// if possible, move stone to the right
				stone.setX(stoneX + 1);
			}
		}
	}
	
	private boolean isMovePossible() {
		// for each row
		for(int y = 0; y < stoneHeight; y++) {
			// iterate through this row backwards
			for(int x = stoneWidth-1; x >= 0; x--) {
				// is there a brick at bounding box position (x,y)?
				if(stoneAlloc[x][y]) {
					/* when we get here, it means that y is the
					 * y-coordinate within the bounding box at
					 * which the rightmost brick of the stone is
					 */
					
					// determine whether the cell to the right
					// of the last brick is free
					if(!grid.isFree(
						stoneX + x + 1, /* x position of stone + x-offset
						+ 1, because the stone shall move to the rightt */
						stoneY + y // y position of stone + y-offset
					)) {
						return false;
					} else {
						// cell to the right is free, what more could we ask for?
						break;
					}
				}
			}
		}
		
		/* if we survived the loop, there was no reason not to allow
		 * the stone to move rightwards, i. e., there are no bricks
		 * in the way and we're inside the grid's bounds */
		return true;
	}

	@Override
	public void onStoneRotated(Stone stone) {
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