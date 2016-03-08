package de.riwo.tetrix.tetris.stone;


public class PodiumStone extends Stone {

	protected boolean[][] initialAllocation;
	
	public PodiumStone(int color, int x) {
		super(color, x);
	}

	@Override
	protected void initializeAllocation() {
		if(initialAllocation == null) {
			initialAllocation = new boolean[][] {
				{false, true, false},
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
		return StoneType.PODIUM_STONE;
	}

}