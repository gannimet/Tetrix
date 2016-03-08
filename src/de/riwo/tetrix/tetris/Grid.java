package de.riwo.tetrix.tetris;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.Point;
import de.riwo.tetrix.tetris.stone.Stone;

/**
 * Class for representing the tetris grid. Its internal status
 * stores the grid's allocation, i. e., which of its cells
 * are occupied by {@link Stone}s.
 * @author richard
 *
 */
public class Grid {

	private int width;
	private int height;
	private int[][] allocation;
	
	private List<LineClearListener> lineClearListeners;
	private List<StoneFixListener> stoneFixListeners;
	
	private List<Integer> lineCheckLines = new ArrayList<Integer>();
	
	/**
	 * Creates a grid of the given width and height.
	 * @param width number of columns
	 * @param height number of rows
	 */
	public Grid(int width, int height) {
		this.width = width;
		this.height = height;
		allocation = new int[width][height];
		lineClearListeners = new ArrayList<LineClearListener>();
		stoneFixListeners = new ArrayList<StoneFixListener>();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	/**
	 * Whether the grid position {@code (x, y)} is free.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return {@code true}, if there is no brick allocated at
	 * grid position {@code (x, y)}; {@code false}, if there is
	 * a brick allocated at grid position {@code (x, y)} or
	 * the provided position exceeds the grid's limits.
	 * @see #isFree(Point)
	 */
	public boolean isFree(int x, int y) {
		if(x >= width || x < 0) {
			return false;
		}
		if(y >= height || y < 0) {
			return false;
		}
		
		return allocation[x][y] == 0;
	}
	
	/**
	 * Whether the grid position described by {@code point} is free.
	 * @param point {@link Point} to be checked
	 * @return {@code true}, if there is no brick allocated at
	 * grid position {@code (point.x, point.y)}; {@code false},
	 * if there is a brick allocated at grid position
	 * {@code (point.x, point.y)} or the provided position
	 * exceeds the grid's limits.
	 * @see #isFree(int, int)
	 */
	public boolean isFree(Point point) {
		return isFree(point.x, point.y);
	}
	
	/**
	 * Returns the most natural x-coordinate for stones to be initially
	 * placed on the grid.
	 * @return x-coordinate depending on the grid's width which clients
	 * can use as the initial position for new stones.
	 */
	public int stoneIndent() {
		return (width / 2) - 2;
	}
	
	/**
	 * Takes over the given stone's allocation into this grid.
	 * 
	 * Uses position and bounding box of the stone to determine
	 * which bricks are being allocated with the stone's color.
	 * This method also performs a {@link #lineCheck()} and
	 * calls {@link #notifyStoneFixListeners(Stone)}
	 * when the fix was successful.
	 * @param stone {@link Stone} to be fixed.
	 * @throws IllegalFixRequestException thrown when a brick
	 * demanded by the stone is not free
	 */
	public void fixStone(Stone stone) throws IllegalFixRequestException {
		boolean[][] stoneAlloc = stone.getAllocationInBoundingBox();
		int stoneX = stone.getX();
		int stoneY = stone.getY();
		int color = stone.getColor();
		
		for(int yOff = 0; yOff < stoneAlloc.length; yOff++) {
			for(int xOff = 0; xOff < stoneAlloc[0].length; xOff++) {
				// walk through the stone
				
				if(stoneAlloc[xOff][yOff]) {
					// is there a brick at this position in the stone?
					
					if(isFree(stoneX + xOff, stoneY + yOff)) {
						// is this position on the grid free?
						allocation[stoneX + xOff][stoneY + yOff] =
							color;
					} else {
						throw new IllegalFixRequestException(
							"Cannot fix stone: Grid position (" +
							(stoneX + xOff) + ", " + (stoneY + yOff) +
							") is not free.");
					}
					
				}
				
			}
		}
		
		// fixing was successful,
		// then check for lines
		lineCheck();
		// and notify interested listeners
		notifyStoneFixListeners(stone);
	}
	
	/**
	 * Called whenever a stone has been fixed. Checks the grid
	 * for full lines and clears them if any. This method also
	 * calls {@see #notifyLineClearListeners(List)} if at least
	 * one line has been cleared.
	 */
	private void lineCheck() {
		lineCheckLines.clear();
		// a counting variable needed when several lines
		// get cleared at a time, because the indices change
		int soFarCleared = 0;
		
		for(int y = height - 1; y >= 0;) {
			if(isLineFull(y)) {
				if(clearLine(y)) {
					// if the current line is full, remove
					// it and add it to the list to inform
					// the clearListeners
					lineCheckLines.add(y - soFarCleared);
					soFarCleared++;
				}
			} else {
				y--;
			}
		}
		
		if(lineCheckLines.size() > 0)
			notifyLineClearListeners(lineCheckLines);
	}
	
	/**
	 * Whether this line is full.
	 * @param y zero-based index (topmost line has index 0)
	 * @return {@code true}, if the line with index {@code y}
	 * is full, i. e., if every brick in it is allocated.
	 */
	public boolean isLineFull(int y) {
		for(int x = 0; x < width; x++) {
			if(isFree(x, y))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Remove the bricks from this line and update the allocation.
	 * 
	 * In more detail: the allocation of each line above the given
	 * line will be copied to the line below, i. e., all bricks
	 * above "fall" down one line
	 * @param y zero-based index of the line to be cleared
	 * (topmost line has index 0)
	 * @return Whether the clearing was successful, i. e., if
	 * the given line was even full.
	 */
	public boolean clearLine(int y) {
		if(isLineFull(y)) {
			for(y = y-1; y >= 0; y--) {
				// for each line above the line
				// that is to be cleared
				for(int x = 0; x < width; x++) {
					// walk through this line and
					// copy it to the line below
					allocation[x][y+1] = allocation[x][y];
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the color of the brick allocated at the
	 * specified position.
	 * @param x x coordinate (zero-based)
	 * @param y y coordinate (zero-based)
	 * @return int value of the {@link Color}
	 * or {@code 0}, if there is no brick allocated or the provided
	 * position exceeds the grid's limits
	 */
	public int getColorAt(int x, int y) {
		if(x < 0 || x >= width)
			return 0;
		if(y < 0 || y >= height)
			return 0;
		
		return allocation[x][y];
	}

	public void addLineClearListener(LineClearListener lineClearListener) {
		lineClearListeners.add(lineClearListener);
	}

	public void removeLineClearListener(LineClearListener lineClearListener) {
		lineClearListeners.remove(lineClearListener);
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
	
	public void addStoneFixListener(StoneFixListener stoneFixListener) {
		stoneFixListeners.add(stoneFixListener);
	}

	public void removeStoneFixListener(StoneFixListener stoneFixListener) {
		stoneFixListeners.remove(stoneFixListener);
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
	
}