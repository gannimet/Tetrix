package de.riwo.tetrix.tetris.stone;


public class BlockStone extends Stone {

	protected boolean[][] initialAllocation;
	
	public BlockStone(int color, int x) {
		super(color, x);
	}

	@Override
	protected void initializeAllocation() {
		if(initialAllocation == null) {
			initialAllocation = new boolean[][] {
				{true, true},
				{true, true}
			};
		}
		
		setAllocationInBoundingBox(initialAllocation);
	}

	@Override
	public boolean isRotatable() {
		return false;
	}

	@Override
	public StoneType getType() {
		return StoneType.BLOCK_STONE;
	}

}