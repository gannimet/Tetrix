package de.riwo.tetrix.tetris;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import de.riwo.tetrix.tetris.command.CommandFactory;
import de.riwo.tetrix.tetris.command.IllegalCommandTypeException;
import de.riwo.tetrix.tetris.command.RotateCommand;
import de.riwo.tetrix.tetris.command.StoneCommand;
import de.riwo.tetrix.tetris.stone.IllegalStoneTypeException;
import de.riwo.tetrix.tetris.stone.Stone;
import de.riwo.tetrix.tetris.stone.StoneFactory;
import de.riwo.tetrix.tetris.stone.StoneType;

/**
 * Represents the game and its internal status like the player's points,
 * level. Works on a {@link Grid} and receives user input for moving and
 * rotating {@link Stone}s.
 * @author richard
 *
 */
public final class Game implements StoneFixListener, LineClearListener {

	private Grid grid;
	private final StoneFactory stoneFactory = new StoneFactory();
	private final CommandFactory cmdFactory;
	private Map<StoneType, Integer> enabledStoneTypes;
	// for faster access on random elements
	private List<StoneType> enabledStoneTypesList = new ArrayList<StoneType>();
	
	private int level = 1;
	private int points = 0;
	private int numLines = 0;
	
	private boolean started = false;
	private boolean finished = false;
	private boolean paused = false;
	
	private List<GameOverListener> gameOverListeners = new ArrayList<GameOverListener>();
	private List<LevelChangeListener> levelChangeListeners = new ArrayList<LevelChangeListener>();
	private List<PointsChangeListener> pointsChangeListeners = new ArrayList<PointsChangeListener>();
	private List<LineClearListener> lineClearListeners = new ArrayList<LineClearListener>();
	private List<StoneFixListener> stoneFixListeners = new ArrayList<StoneFixListener>();
	
	private DropTicker dropTicker;
	private UserCommandTicker userTicker;
	
	private Thread dropThread;
	private Thread userThread;
	
	private PointStrategy pointStrategy;
	private LevelStrategy levelStrategy;
	
	private Stone currentStone;
	private Stone nextStone;
	private Iterator<Point> stoneIter;
	
	private RotateCommand rotateCmd;
	
	/**
	 * Creates a game based on a grid with the specified width and height, using
	 * only the provided stone types and their specified colors and calculating
	 * points and level as defined by the corresponding strategy objects.
	 * @param gridWidth number of columns for this game's grid
	 * @param gridHeight number of rows for this game's grid
	 * @param enabledStoneTypes {@link Map} mapping stone types to integer
	 * values of {@link Color}.
	 * @param pointStrategy callback object used to calculate the
	 * user's points after each {@link #onLinesCleared(List)} event
	 * @param levelStrategy callback object used to calculate the
	 * level after each {@link #onLinesCleared(List)} event
	 * @see Stone
	 */
	public Game(int gridWidth, int gridHeight, Map<StoneType, Integer> enabledStoneTypes,
	PointStrategy pointStrategy, LevelStrategy levelStrategy, int rotationStrategy) {
		grid = new Grid(gridWidth, gridHeight);
		cmdFactory = new CommandFactory(rotationStrategy);
		this.enabledStoneTypes = enabledStoneTypes;
		for(StoneType stoneType : enabledStoneTypes.keySet()) {
			enabledStoneTypesList.add(stoneType);
		}
		this.pointStrategy = pointStrategy;
		this.levelStrategy = levelStrategy;
		grid.addStoneFixListener(this);
		grid.addLineClearListener(this);
	}
	
	/**
	 * Starts the game and initializes and starts the "ticker"
	 * threads.
	 * 
	 * Should the game have already been started, then calls to this
	 * method are ignored.
	 */
	public void start() {
		if(started)
			return;
		
		currentStone = randomStone();
		nextStone = randomStone();
		
		dropTicker = new DropTicker(grid, currentStone);
		userTicker = new UserCommandTicker(currentStone);
		
		dropThread = new Thread(dropTicker);
		userThread = new Thread(userTicker);
		dropThread.start();
		userThread.start();
		
		started = true;
	}
	
	/**
	 * Stops the game, i. e., calls {@code interrupt()} on the
	 * game's "ticker" threads.
	 * @see Thread#interrupt()
	 */
	public void stop() {
		dropTicker.stop();
		userTicker.stop();
		dropThread.interrupt();
		userThread.interrupt();
	}
	
	/**
	 * Whether the game has already been started.
	 * @return {@code true}, if the game is already started
	 */
	public boolean isStarted() {
		return started;
	}
	
	/**
	 * Whether the game is already finished.
	 * 
	 * This method can only return {@code true} after the
	 * {@link GameOverListener}s have been notified.
	 * @return {@code true}, if the game is already finished
	 */
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * Sets the status to {@code paused}.
	 * 
	 * The "ticker" threads will therefore not execute any more
	 * {@link StoneCommand}s.
	 */
	public void pause() {
		dropTicker.beginPause();
		userTicker.beginPause();
		paused = true;
	}
	
	/**
	 * Ends a pause.
	 * 
	 * The "ticker" threads will resume their work on executing
	 * {@link StoneCommand}s.
	 */
	public void resume() {
		dropTicker.finishPause();
		userTicker.finishPause();
		paused = false;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getPoints() {
		return points;
	}
	
	public int getLines() {
		return numLines;
	}
	
	public int getGridWidth() {
		return grid.getWidth();
	}
	
	public int getGridHeight() {
		return grid.getHeight();
	}
	
	public Map<StoneType, Integer> getEnabledStoneTypes() {
		return enabledStoneTypes;
	}
	
	public void addEnabledStoneType(StoneType enabledStoneType, int color) {
		enabledStoneTypes.put(enabledStoneType, color);
		enabledStoneTypesList.add(enabledStoneType);
	}
	
	public void removeEnabledStoneType(StoneType enabledStoneType) {
		enabledStoneTypes.remove(enabledStoneType);
		enabledStoneTypesList.remove(enabledStoneType);
	}

	public void setEnabledStoneTypes(Map<StoneType, Integer> enabledStoneTypes) {
		this.enabledStoneTypes = enabledStoneTypes;
		for(StoneType stoneType : enabledStoneTypes.keySet()) {
			enabledStoneTypesList.add(stoneType);
		}
	}

	public PointStrategy getPointStrategy() {
		return pointStrategy;
	}

	public void setPointStrategy(PointStrategy pointStrategy) {
		this.pointStrategy = pointStrategy;
	}

	public LevelStrategy getLevelStrategy() {
		return levelStrategy;
	}

	public void setLevelStrategy(LevelStrategy levelStrategy) {
		this.levelStrategy = levelStrategy;
	}

	/**
	 * Returns the {@link Stone} that is currently being operated on.
	 * @return The currently dropping stone, which has not yet
	 * been fixed.
	 */
	public Stone getCurrentStone() {
		return currentStone;
	}
	
	public Stone getNextStone() {
		return nextStone;
	}
	
	/**
	 * Rotates the current stone, if possible.
	 */
	public void rotateStone() {
		if(isFinished() || isPaused())
			return;
		
		queueCommand(StoneCommand.ROTATE_COMMAND);
	}
	
	/**
	 * Moves the current stone to the left, if possible.
	 */
	public void moveStoneLeft() {
		if(isFinished() || isPaused())
			return;
		
		queueCommand(StoneCommand.MOVE_LEFT_COMMAND);
	}
	
	/**
	 * Moves the current stone to the right, if possible.
	 */
	public void moveStoneRight() {
		if(isFinished() || isPaused())
			return;
		
		queueCommand(StoneCommand.MOVE_RIGHT_COMMAND);
	}
	
	/**
	 * Moves the current stone down, if possible.
	 */
	public void moveStoneDown() {
		if(isFinished() || isPaused())
			return;
		
		queueCommand(StoneCommand.MOVE_DOWN_COMMAND);
	}
	
	private void queueCommand(int commandType) {
		try {
			switch(commandType) {
				case StoneCommand.MOVE_DOWN_COMMAND:
				case StoneCommand.MOVE_LEFT_COMMAND:
				case StoneCommand.MOVE_RIGHT_COMMAND:
					// queue any move commands
					userTicker.queue(cmdFactory.getCommand(
						commandType, currentStone, grid));
					return;
				case StoneCommand.ROTATE_COMMAND:
					// queue rotate commands as well, but also
					// make the DropTicker's MoveDownCommand
					// listen to it
					rotateCmd = (RotateCommand) cmdFactory.getCommand(
						commandType, currentStone, grid);
					userTicker.queue(rotateCmd);
					dropTicker.listenTo(rotateCmd);
					return;
			}
		} catch (IllegalCommandTypeException e) {
			Log.w(getClass().getName(), e.getMessage(), e);
		}
	}
	
	/**
	 * Returns the color value (as defined by {@link Color})
	 * in which the given cell has to be painted.
	 * 
	 * The difference to {@link Grid#getColorAt(int, int)} is
	 * that this method also pays attention to the current stone.
	 * @param x x-coordinate (zero-based)
	 * @param y y-coordinate (zero-based)
	 * @return int value of the cell's {@link Color}, or
	 * {@code 0} if the provided position exceeds the grid's limits OR
	 * there is no brick at this position
	 * @see Grid#getColorAt(int, int)
	 */
	public int getGridColorAt(int x, int y) {
		int color = grid.getColorAt(x, y);
		
		// if there was a color, fine!
		if(color != 0)
			return color;
		
		// if not, test for the currently dropping stone
		if(!finished && currentStone.hitTest(x, y))
			return currentStone.getColor();
		
		// no fixed bricks and no dropping stone at this position?
		// well, then this grid cell is free
		assert(grid.isFree(x, y));
		return 0;
	}
	
	private Stone randomStone() throws IllegalStateException {
		int index = (int) Math.round(Math.random() * (enabledStoneTypes.size()-1));
		//StoneType stoneType = (StoneType) enabledStoneTypes.keySet().toArray()[index];
		StoneType stoneType = enabledStoneTypesList.get(index);
		
		return retrieveStone(stoneType);
	}
	
	private Stone retrieveStone(StoneType stoneType) {
		try {
			return stoneFactory.getStone(
				stoneType,	// randomly acquired stone type
				enabledStoneTypes.get(stoneType),	// color of this stone type
				grid.stoneIndent()	// x position
			);
		} catch (IllegalStoneTypeException e) {
			Log.w(getClass().getName(), e.getMessage(), e);
			throw new IllegalStateException();
		}
	}
	
	private void nextStone() {
		// retrieve next stone
		if(nextStone != null)
			currentStone = retrieveStone(nextStone.getType());
		else
			currentStone = randomStone();
		
		// can it be placed on the grid?
		stoneIter = currentStone.iterator();
		while(stoneIter.hasNext()) {
			if(!grid.isFree(stoneIter.next())) {
				// no place for the current stone -> GAME OVER!
				Log.w(getClass().getName(), "GAME OVER");
				pause();
				stop();
				finished = true;
				notifyGameOverListeners();
				return;
			}
		}
		
		dropTicker.changeStone(currentStone);
		userTicker.changeStone(currentStone);
		
		nextStone = randomStone();
	}
	
	public void addGameOverListener(GameOverListener listener) {
		gameOverListeners.add(listener);
	}
	
	public void removeGameOverListener(GameOverListener listener) {
		gameOverListeners.remove(listener);
	}
	
	public void notifyGameOverListeners() {
		if(gameOverListeners.isEmpty())
			return;
		
		if(gameOverListeners.size() == 1) {
			gameOverListeners.get(0).onGridFull();
			return;
		}
		
		if(gameOverListeners.size() == 2) {
			gameOverListeners.get(0).onGridFull();
			gameOverListeners.get(1).onGridFull();
			return;
		}
		
		for(GameOverListener listener : gameOverListeners) {
			listener.onGridFull();
		}
	}
	
	public void addLevelChangeListener(LevelChangeListener listener) {
		levelChangeListeners.add(listener);
	}
	
	public void removeLevelChangeListener(LevelChangeListener listener) {
		levelChangeListeners.remove(listener);
	}
	
	public void notifyLevelChangeListeners() {
		if(levelChangeListeners.isEmpty())
			return;
		
		if(levelChangeListeners.size() == 1) {
			levelChangeListeners.get(0).onLevelChanged(level);
			return;
		}
		
		if(levelChangeListeners.size() == 2) {
			levelChangeListeners.get(0).onLevelChanged(level);
			levelChangeListeners.get(1).onLevelChanged(level);
			return;
		}
		
		for(LevelChangeListener listener : levelChangeListeners) {
			listener.onLevelChanged(level);
		}
	}
	
	public void addPointsChangeListener(PointsChangeListener listener) {
		pointsChangeListeners.add(listener);
	}
	
	public void removePointsChangeListener(PointsChangeListener listener) {
		pointsChangeListeners.remove(listener);
	}
	
	public void notifyPointsChangeListeners() {
		if(pointsChangeListeners.isEmpty())
			return;
		
		if(pointsChangeListeners.size() == 1) {
			pointsChangeListeners.get(0).onPointsChanged(points);
			return;
		}
		
		if(pointsChangeListeners.size() == 2) {
			pointsChangeListeners.get(0).onPointsChanged(points);
			pointsChangeListeners.get(1).onPointsChanged(points);
			return;
		}
		
		for(PointsChangeListener listener : pointsChangeListeners) {
			listener.onPointsChanged(points);
		}
	}
	
	public void addLineClearListener(LineClearListener listener) {
		lineClearListeners.add(listener);
	}
	
	public void removeLineClearListener(LineClearListener listener) {
		lineClearListeners.remove(listener);
	}
	
	public void notifyLineClearListeners(List<Integer> lines) {
		if(lineClearListeners.isEmpty())
			return;
		
		if(lineClearListeners.size() == 1) {
			lineClearListeners.get(0).onLinesCleared(lines);
			return;
		}
		
		if(lineClearListeners.size() == 2) {
			lineClearListeners.get(0).onLinesCleared(lines);
			lineClearListeners.get(1).onLinesCleared(lines);
			return;
		}
		
		for(LineClearListener listener : lineClearListeners) {
			listener.onLinesCleared(lines);
		}
	}
	
	public void addStoneFixListener(StoneFixListener listener) {
		stoneFixListeners.add(listener);
	}
	
	public void removeStoneFixListener(StoneFixListener listener) {
		stoneFixListeners.remove(listener);
	}
	
	public void notifyStoneFixListeners(Stone stone) {
		if(stoneFixListeners.isEmpty())
			return;
		
		if(stoneFixListeners.size() == 1) {
			stoneFixListeners.get(0).onStoneFixed(stone);
			return;
		}
		
		if(stoneFixListeners.size() == 2) {
			stoneFixListeners.get(0).onStoneFixed(stone);
			stoneFixListeners.get(1).onStoneFixed(stone);
			return;
		}
		
		for(StoneFixListener listener : stoneFixListeners) {
			listener.onStoneFixed(stone);
		}
	}

	@Override
	public void onStoneFixed(Stone stone) {
		nextStone();
		notifyStoneFixListeners(stone);
	}

	@Override
	public void onLinesCleared(List<Integer> lines) {
		assert(lines.size() > 0 && lines.size() <= 4);
		
		// add points and notify listener
		points += pointStrategy.getLinePoints(level, lines);
		notifyPointsChangeListeners();
		
		// add number of lines and check for next level
		numLines += lines.size();
		notifyLineClearListeners(lines);
		int newLevel = levelStrategy.getLevel(points, numLines);
		if(level != newLevel) {
			// level should be changed
			level = newLevel;
			// make it experiencable for the player
			dropTicker.setInterval(DropTicker.START_INTERVAL - (level * 20));
			notifyLevelChangeListeners();
		}
	}
	
}