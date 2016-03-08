package de.riwo.tetrix.tetris;

import java.util.List;

public interface LineClearListener {

	/**
	 * Called whenever one or several lines in the grid
	 * were full and have therefor been cleared.
	 * @param lines A {@link List} containing the indices
	 * of the removed lines.
	 */
	public void onLinesCleared(List<Integer> lines);
	
}