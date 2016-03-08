package de.riwo.tetrix.tetris.stone;

public class InversedLStone extends Stone {

	protected boolean[][] initialAllocation;
	
	public InversedLStone(int color, int x) {
		super(color, x);
	}

	@Override
	protected void initializeAllocation() {
		if(initialAllocation == null) {
			initialAllocation = new boolean[][] {
				{true, true, false},
				{false, true, false},
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
		return StoneType.INVERSED_L_STONE;
	}

}