package de.riwo.tetrix.tetris;

/**
 * Interface that clients can implement if they want to be
 * notified when the game is over for the player. 
 * @author richard
 *
 */
public interface GameOverListener {

	/**
	 * Called when the next stone doesn't fit on the grid anymore,
	 * indicating that the game is effectively over.
	 */
	public void onGridFull();
	
}