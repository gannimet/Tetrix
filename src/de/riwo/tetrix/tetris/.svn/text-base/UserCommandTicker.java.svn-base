package de.riwo.tetrix.tetris;

import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;
import de.riwo.tetrix.tetris.command.StoneCommand;
import de.riwo.tetrix.tetris.stone.Stone;

class UserCommandTicker implements Runnable {

	private Queue<StoneCommand> commands = new LinkedList<StoneCommand>();
	private boolean paused = false;
	private boolean stopped = false;
	private Stone stone;
	
	public UserCommandTicker(Stone stone) {
		this.stone = stone;
	}
	
	public void queue(StoneCommand command) {
		commands.offer(command);
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
	
	public void changeStone(Stone stone) {
		this.stone = stone;
	}
	
	@Override
	public void run() {
		StoneCommand command;
		while(!stopped) {
			if(!paused) {
				if(!commands.isEmpty()) {
					try {
						command = commands.poll();
						command.setStone(stone);
						command.execute();
					} catch (IllegalFixRequestException e) {
						Log.w(getClass().getName(), e.getMessage(), e);
					}
				}
			}
		}
	}

}