package de.riwo.tetrix.tetris.stone;

public class LeftChairStone extends Stone {

	protected boolean[][] initialAllocation;
	
	public LeftChairStone(int color, int x) {
		super(color, x);
	}

	@Override
	protected void initializeAllocation() {
		if(initialAllocation == null) {
			initialAllocation = new boolean[][] {
				{true, false, false},
				{true, true, false},
				{false, true, false}
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
		return StoneType.LEFT_CHAIR_STONE;
	}

}