package de.riwo.tetrix.tetris;

import android.util.Log;
import de.riwo.tetrix.tetris.command.MoveDownCommand;
import de.riwo.tetrix.tetris.command.RotateCommand;
import de.riwo.tetrix.tetris.stone.Stone;

class DropTicker implements Runnable {

	private boolean paused = false;
	private boolean stopped = false;
	private int interval;
	private MoveDownCommand command;
	private boolean listeningToRotation = false;
	
	public final static int START_INTERVAL = 600;
	
	public DropTicker(Grid grid, Stone stone) {
		this(grid, stone, START_INTERVAL);
	}
	
	public DropTicker(Grid grid, Stone stone, int interval) {
		command = new MoveDownCommand(stone, grid);
		this.interval = interval;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	public void changeStone(Stone stone) {
		command.setStone(stone);
	}
	
	public void beginPause() {
		this.paused = true;
	}
	
	public void finishPause() {
		this.paused = false;
	}
	
	public void stop() {
		stopped = true;
	}
	
	public void listenTo(RotateCommand rotCmd) {
		if(!listeningToRotation) {
			rotCmd.addStoneRotationListener(command);
			listeningToRotation = true;
		}
	}
	
	@Override
	public void run() {
		while(!stopped) {
			if(!paused) {
				try {
					Thread.sleep(interval);
				} catch (InterruptedException ie) {
					Log.w(getClass().getName(), "DropTicker interrupted");
				}
				
				try {
					if(!paused)
						command.execute();
				} catch (IllegalFixRequestException ifre) {
					Log.w(getClass().getName(), ifre.getMessage(), ifre);
					return;
				}
			}
		}
	}

}