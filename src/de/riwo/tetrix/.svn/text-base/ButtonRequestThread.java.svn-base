package de.riwo.tetrix;

import de.riwo.tetrix.tetris.Game;
import de.riwo.tetrix.tetris.command.StoneCommand;

public class ButtonRequestThread implements Runnable {

	private int interval;
	private Game game;
	
	private int commandType = Integer.MAX_VALUE;
	private boolean paused = true;
	private boolean stopped = false;
	
	public ButtonRequestThread(Game game, int interval) {
		this.game = game;
		this.interval = interval;
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public void setCommandType(int commandType) {
		this.commandType = commandType;
	}
	
	public int getCommandType() {
		return commandType;
	}
	
	public void stop() {
		stopped = true;
	}
	
	@Override
	public void run() {
		while(!stopped) {
			if(!paused) {
				if(commandType == StoneCommand.MOVE_DOWN_COMMAND)
					game.moveStoneDown();
				if(commandType == StoneCommand.MOVE_LEFT_COMMAND)
					game.moveStoneLeft();
				if(commandType == StoneCommand.MOVE_RIGHT_COMMAND)
					game.moveStoneRight();
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {}
			}
		}
	}

}