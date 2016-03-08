package de.riwo.tetrix.tetris.stone;


public class FourBarStone extends Stone {

	protected boolean[][] initialAllocation;
	
	public FourBarStone(int color, int x) {
		super(color, x);
	}

	@Override
	protected void initializeAllocation() {
		if(initialAllocation == null) {
			initialAllocation = new boolean[][] {
				{false, true, false, false},
				{false, true, false, false},
				{false, true, false, false},
				{false, true, false, false}
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
		return StoneType.FOUR_BAR_STONE;
	}

}