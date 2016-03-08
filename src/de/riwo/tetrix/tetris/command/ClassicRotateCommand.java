package de.riwo.tetrix.tetris.command;

import de.riwo.tetrix.tetris.Grid;
import de.riwo.tetrix.tetris.stone.Stone;

public final class ClassicRotateCommand extends RotateCommand {
	
	private boolean[][] desiredAlloc;
	private int xShift, yShift;
	
	// prototype arrays for reuse to avoid memory leaks
	//private boolean[][] proto2x2 = new boolean[2][2];		// only useful for BlockStones, which are not rotatable
	//private boolean[][] proto3x3 = new boolean[3][3];
	//private boolean[][] proto4x4 = new boolean[4][4];
	
	private final boolean[][] fourBarLeftRight = new boolean[][] {
		{false, false, false, false},
		{true, true, true, true},
		{false, false, false, false},
		{false, false, false, false}
	};
	private final boolean[][] fourBarUpDown = new boolean[][] {
		{false, true, false, false},
		{false, true, false, false},
		{false, true, false, false},
		{false, true, false, false}
	};
	private final boolean[][] straightLRight = new boolean[][] {
		{false, false, false},
		{true, true, true},
		{false, false, true}
	};
	private final boolean[][] straightLDown = new boolean[][] {
		{false, true, true},
		{false, true, false},
		{false, true, false}
	};
	private final boolean[][] straightLLeft = new boolean[][] {
		{true, false, false},
		{true, true, true},
		{false, false, false}
	};
	private final boolean[][] straightLUp = new boolean[][] {
		{false, true, false},
		{false, true, false},
		{true, true, false}
	};
	private final boolean[][] inversedLRight = new boolean[][] {
		{false, false, false},
		{true, true, true},
		{true, false, false}
	};
	private final boolean[][] inversedLDown = new boolean[][] {
		{false, true, false},
		{false, true, false},
		{false, true, true}
	};
	private final boolean[][] inversedLLeft = new boolean[][] {
		{false, false, true},
		{true, true, true},
		{false, false, false}
	};
	private final boolean[][] inversedLUp = new boolean[][] {
		{true, true, false},
		{false, true, false},
		{false, true, false}
	};
	private final boolean[][] podiumRight = new boolean[][] {
		{false, false, false},
		{true, true, true},
		{false, true, false}
	};
	private final boolean[][] podiumDown = new boolean[][] {
		{false, true, false},
		{false, true, true},
		{false, true, false}
	};
	private final boolean[][] podiumLeft = new boolean[][] {
		{false, true, false},
		{true, true, true},
		{false, false, false}
	};
	private final boolean[][] podiumUp = new boolean[][] {
		{false, true, false},
		{true, true, false},
		{false, true, false}
	};
	private final boolean[][] leftChairRightLeft = new boolean[][] {
		{false, true, true},
		{true, true, false},
		{false, false, false}
	};
	private final boolean[][] leftChairDownUp = new boolean[][] {
		{true, false, false},
		{true, true, false},
		{false, true, false}
	};
	private final boolean[][] rightChairRightLeft = new boolean[][] {
		{true, true, false},
		{false, true, true},
		{false, false, false}
	};
	private final boolean[][] rightChairDownUp = new boolean[][] {
		{false, true, false},
		{true, true, false},
		{true, false, false}
	};

	public ClassicRotateCommand(Stone stone, Grid grid) {
		super(stone, grid);
	}

	@Override
	public synchronized void execute() { //if(true) return;
		if(!stone.isRotatable())
			return;
		
		synchronized(stone) {
			// prepare desiredAlloc and shift params depending
			// on the type of stone
			switch(stone.getType()) {
				case FOUR_BAR_STONE:
					prepareFourBarRotation();
					break;
				case BLOCK_STONE:
					throw new AssertionError("BlockStone shouldn't be rotatable");
				case INVERSED_L_STONE:
					prepareInversedLRotation();
					break;
				case LEFT_CHAIR_STONE:
					prepareLeftChairRotation();
					break;
				case PODIUM_STONE:
					preparePodiumRotation();
					break;
				case RIGHT_CHAIR_STONE:
					prepareRightChairRotation();
					break;
				case STRAIGHT_L_STONE:
					prepareStraightLRotation();
					break;
				default:
					throw new AssertionError("Unknown stoneType");
			}
			
			assert(desiredAlloc != null);
			
			// when the preparation is done, do the rotation
			if(isRotationPossible(desiredAlloc, xShift, yShift)) {
				stone.setAllocationInBoundingBox(desiredAlloc);
				stone.setX(stone.getX() + xShift);
				stone.setY(stone.getY() + yShift);
				stone.nextOrientation();
				notifyStoneRotationListeners();
			}
		}
	}
	
	/*private void clearArray(boolean[][] which) {
		for(int y = 0; y < which[0].length; y++) {
			for(int x = 0; x < which.length; x++) {
				which[x][y] = false;
			}
		}
	}*/

	private void prepareFourBarRotation() {
		switch(stone.getOrientation()) {
			case TOPUP:
			case TOPDOWN:
				desiredAlloc = fourBarLeftRight;
				xShift = 0;
				yShift = 0;
				return;
			case TOPRIGHT:
			case TOPLEFT:
				desiredAlloc = fourBarUpDown;
				xShift = 0;
				yShift = 0;
				return;
		}
	}
	
	private void prepareStraightLRotation() {
		switch(stone.getOrientation()) {
			case TOPUP:
				desiredAlloc = straightLRight;
				xShift = 0;
				yShift = 0;
				return;
			case TOPRIGHT:
				desiredAlloc = straightLDown;
				xShift = 0;
				yShift = 0;
				return;
			case TOPDOWN:
				desiredAlloc = straightLLeft;
				xShift = 0;
				yShift = 0;
				return;
			case TOPLEFT:
				desiredAlloc = straightLUp;
				xShift = 0;
				yShift = 0;
				return;
		}
	}
	
	private void prepareInversedLRotation() {
		switch(stone.getOrientation()) {
			case TOPUP:
				desiredAlloc = inversedLRight;
				xShift = 0;
				yShift = 0;
				return;
			case TOPRIGHT:
				desiredAlloc = inversedLDown;
				xShift = 0;
				yShift = 0;
				return;
			case TOPDOWN:
				desiredAlloc = inversedLLeft;
				xShift = 0;
				yShift = 0;
				return;
			case TOPLEFT:
				desiredAlloc = inversedLUp;
				xShift = 0;
				yShift = 0;
				return;
		}
	}
	
	private void preparePodiumRotation() {
		switch(stone.getOrientation()) {
			case TOPUP:
				desiredAlloc = podiumRight;
				xShift = 0;
				yShift = 0;
				return;
			case TOPRIGHT:
				desiredAlloc = podiumDown;
				xShift = 0;
				yShift = 0;
				return;
			case TOPDOWN:
				desiredAlloc = podiumLeft;
				xShift = 0;
				yShift = 0;
				return;
			case TOPLEFT:
				desiredAlloc = podiumUp;
				xShift = 0;
				yShift = 0;
				return;
		}
	}
	
	private void prepareLeftChairRotation() {
		switch(stone.getOrientation()) {
			case TOPUP:
			case TOPDOWN:
				desiredAlloc = leftChairRightLeft;
				xShift = 0;
				yShift = -1;
				return;
			case TOPRIGHT:
			case TOPLEFT:
				desiredAlloc = leftChairDownUp;
				xShift = 0;
				yShift = 1;
				return;
		}
	}
	
	private void prepareRightChairRotation() {
		switch(stone.getOrientation()) {
			case TOPUP:
			case TOPDOWN:
				desiredAlloc = rightChairRightLeft;
				xShift = 0;
				yShift = -1;
				return;
			case TOPRIGHT:
			case TOPLEFT:
				desiredAlloc = rightChairDownUp;
				xShift = 0;
				yShift = 1;
				return;
		}
	}

}