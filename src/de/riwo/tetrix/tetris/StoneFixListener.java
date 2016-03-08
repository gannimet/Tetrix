package de.riwo.tetrix.tetris;

import de.riwo.tetrix.tetris.stone.Stone;

public interface StoneFixListener {

	/**
	 * Called whenever a {@link Stone} has beend fixed on
	 * the {@link de.riwo.tetrix.tetris.Grid}
	 * @param stone The {@link Stone} that has been fixed.
	 * @see de.riwo.tetrix.tetris.Grid#fixStone(Stone)
	 */
	public void onStoneFixed(Stone stone);
	
}