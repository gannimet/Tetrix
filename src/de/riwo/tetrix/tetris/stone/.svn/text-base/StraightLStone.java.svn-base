package de.riwo.tetrix.tetris.stone;


public class StraightLStone extends Stone {

	protected boolean[][] initialAllocation;
	
	public StraightLStone(int color, int x) {
		super(color, x);
	}

	@Override
	public void initializeAllocation() {
		if(initialAllocation == null) {
			initialAllocation = new boolean[][] {
				{false, true, false},
				{false, true, false},
				{true, true, false}
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
		return StoneType.STRAIGHT_L_STONE;
	}

}