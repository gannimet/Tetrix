package de.riwo.tetrix.tetris.stone;


public class RightChairStone extends Stone {

	protected boolean[][] initialAllocation;
	
	public RightChairStone(int color, int x) {
		super(color, x);
	}

	@Override
	protected void initializeAllocation() {
		if(initialAllocation == null) {
			initialAllocation = new boolean[][] {
				{false, true, false},
				{true, true, false},
				{true, false, false}
			};
		}
		
		setAllocationInBoundingBox(initialAllocation);
	}

	@Override
	public boolean isRotatable() {
		return true;
	}

	@Override
	public StoneType getType() {
		return StoneType.RIGHT_CHAIR_STONE;
	}

}
