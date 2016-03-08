package de.riwo.tetrix.tetris.stone;

public class SingleStone extends Stone {

	protected boolean[][] initialAllocation;
	
	public SingleStone(int color, int x) {
		super(color, x);
	}

	@Override
	public StoneType getType() {
		return StoneType.SINGLE_STONE;
	}

	@Override
	protected void initializeAllocation() {
		if(initialAllocation == null) {
			initialAllocation = new boolean[][] {
				{true}
			};
		}
		
		setAllocationInBoundingBox(initialAllocation);
	}

	@Override
	public boolean isRotatable() {
		return false;
	}

}