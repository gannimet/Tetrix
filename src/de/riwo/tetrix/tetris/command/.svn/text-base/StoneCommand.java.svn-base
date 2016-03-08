package de.riwo.tetrix.tetris.command;

import de.riwo.tetrix.tetris.Grid;
import de.riwo.tetrix.tetris.IllegalFixRequestException;
import de.riwo.tetrix.tetris.stone.Stone;

public abstract class StoneCommand {

	protected Stone stone;
	protected Grid grid;
	
	public static final int MOVE_DOWN_COMMAND = 0;
	public static final int MOVE_LEFT_COMMAND = 1;
	public static final int MOVE_RIGHT_COMMAND = 2;
	public static final int ROTATE_COMMAND = 3;
	
	public StoneCommand(Stone stone, Grid grid) {
		this.stone = stone;
		this.grid = grid;
	}
	
	public void setStone(Stone stone) {
		this.stone = stone;
	}
	
	public abstract void execute() throws IllegalFixRequestException;
	
}