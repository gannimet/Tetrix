package de.riwo.tetrix.tetris.stone;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import de.riwo.tetrix.tetris.Grid;
import de.riwo.tetrix.tetris.StoneFixListener;

public abstract class Stone implements StoneFixListener {

	protected int x;
	protected int startX;
	protected int y;
	protected int startY;
	protected int color;
	protected Orientation orientation;
	protected boolean[][] allocation;
	protected boolean[][] standardAllocation;
	
	protected Rect box;
	
	protected StoneIterator iterator;
	
	public enum Orientation {
		TOPUP, TOPRIGHT, TOPDOWN, TOPLEFT;
	}
	
	public Stone(int color, int x) {
		this.color = color;
		this.x = x;
		startX = x;
		initializeAllocation();
		y = calculateStartY();
		startY = y;
		box = new Rect(
			x,
			y,
			x + allocation.length - 1,
			y + allocation[0].length - 1
		);
	}
	
	private int calculateStartY() {
		int result = 0;
		
		for(int yCount = 0; yCount < allocation[0].length; yCount++) {
			for(int xCount = 0; xCount < allocation.length; xCount++) {
				// if there's a brick in the current line,
				// then this brick has to be at y = 0
				if(allocation[xCount][yCount]) {
					return result;
				}	
			}
			// every time the loop survives one row, there was no brick
			// which means we can put the stone one row above
			result--;
		}
		return result;
	}
	
	/**
	 * Puts the stone back to the position it was once initialized with
	 * and turns it back to its original orientation.
	 */
	public void reset() {
		/* Stone is virtually put back
		 * to the top and can be reused */
		setY(startY);
		setX(startX);
		orientation = Orientation.TOPUP;
		initializeAllocation();
	}
	
	public StoneIterator iterator() {
		if(iterator == null) {
			iterator = new StoneIterator(this);
			return iterator;
		}
		iterator.setStone(this);
		return iterator;
	}
	
	public int getX() {
		return x;
	}

	public synchronized void setX(int x) {
		this.x = x;
		box.left = x;
		box.right = x + allocation.length - 1;
	}

	public int getY() {
		return y;
	}

	public synchronized void setY(int y) {
		this.y = y;
		box.top = y;
		box.bottom = y + allocation[0].length - 1;
	}
	
	public void setPosition(int x, int y) {
		setX(x);
		setY(y);
	}

	public int getColor() {
		return color;
	}
	
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	/**
	 * To be called after this stone has been rotated. Sets this
	 * stone's orientation to its logical successor.
	 * 
	 * The order is as follows: {@code TOPUP - TOPRIGHT -
	 * TOPDOWN - TOPLEFT}
	 */
	public void nextOrientation() {
		switch(orientation) {
			case TOPUP:
				setOrientation(Orientation.TOPRIGHT);
				return;
			case TOPRIGHT:
				setOrientation(Orientation.TOPDOWN);
				return;
			case TOPDOWN:
				setOrientation(Orientation.TOPLEFT);
				return;
			case TOPLEFT:
				setOrientation(Orientation.TOPUP);
				return;
		}
	}
	
	/**
	 * Returns the type of this stone, encoded as one of the
	 * {@code int} constants defined in the {@link Stone} class. 
	 * @return the appropriate {@code int} constant value for
	 * this stone's type
	 */
	public abstract StoneType getType();

	public void setColor(int color) {
		this.color = color;
	}
	
	/**
	 * Tests whether a brick of this stone is allocated at the
	 * provided point. 
	 * @param x x-coordinate within the {@link Grid}
	 * @param y y-coordinate within the {@link Grid}
	 * @return {@code true}, if there is a brick at {@code (x, y)}
	 * @see #hitTest(Point)
	 */
	public synchronized boolean hitTest(int x, int y) {
		/* we canNOT use Rect#contains(int, int) here, because
		 * the coordinates to be tested would have to be less than
		 * (and not equal to) right and bottom of the Rect */
		
		// If we're not even in the bounding box, we definitely
		// don't hit this stone
		if(x < box.left || x > box.right)
			return false;
		if(y < box.top || y > box.bottom)
			return false;
		
		// If we get here, it means we are definitely within
		// the bounding box, so we look for the allocation
		// at the respective offset
		try {
			return allocation[x - this.x][y - this.y];
		} catch(ArrayIndexOutOfBoundsException e) {
			Log.w(getClass().getName(),
				"x = " + x + "; this.x = " + this.x +
				"; y = " + y + "; this.y = " + this.y,
				e);
			System.exit(1);
			return false;
		}
	}
	
	/**
	 * Tests whether a brick of this stone is allocated at the
	 * provided point. 
	 * @param point {@link Point} to be tested
	 * @return {@code true}, if there is a brick at the position
	 * described by {@code point}
	 * @see #hitTest(int, int)
	 */
	public boolean hitTest(Point point) {
		return hitTest(point.x, point.y);
	}
	
	/**
	 * The smallest rectangle completely surrounding
	 * this stone regardless of its orientation.
	 * @return The smallest surrounding
	 * {@link android.graphics.Rect} {@code r} such that
	 * each coordinate of the rectangle is the respective
	 * coordinate of the outermost brick in that
	 * corresponding direction.
	 */
	public final Rect boundingBox() {
		return box;
	}
	
	protected abstract void initializeAllocation();
	
	/**
	 * Whether this stone is rotatable, i. e., whether it makes sense
	 * to rotate it.
	 * 
	 * Technically every stone can be rotated, but you might want to
	 * avoid this behavior for stones that don't change their appearance
	 * when rotated to improve runtime performance, as it is the case
	 * for {@link BlockStone}s.
	 * @return {@code true}, if it is necessary to (really) rotate this
	 * stone when user input requests so.
	 */
	public abstract boolean isRotatable();
	
	/**
	 * Sets this stone's allocation within its bounding box.
	 * 
	 * The parameter {@code allocation} has therefor got to have
	 * the same dimensions as the bounding box, such that
	 * {@code allocation[x][y]} points to the {@code x}th column
	 * within the {@code y}th row of the bounding box. An element
	 * of the {@allocation} array which evaluates to {@code true}
	 * indicates a brick at that very position; a {@code false}
	 * element is a position without a brick.
	 * @param allocation A two-dimensional array of {@code boolean}s
	 * indicating where inside the bounding box the bricks are located.
	 * @see #getAllocationInBoundingBox()
	 * @see #boundingBox()
	 */
	public void setAllocationInBoundingBox(boolean[][] allocation) {
		this.allocation = allocation;
		
		if(standardAllocation == null)
			standardAllocation = allocation;
	}
	
	/**
	 * Returns this stone's standard allocation, before any rotations
	 * were applied.
	 * 
	 * Clients can use this method to display stones in their initial
	 * appearance, for example to show a preview of the next stone of the game.
	 * @return A two-dimensional array of {@code boolean}s
	 * indicating where inside the bounding box the bricks are located,
	 * as described in {@link #setAllocationInBoundingBox(boolean[][])}.
	 * @see #getAllocationInBoundingBox()
	 * @see #boundingBox()
	 */
	public boolean[][] getInitialAllocationInBoundingBox() {
		return standardAllocation;
	}
	
	/**
	 * Get the stone's allocation within its bounding box,
	 * describing the essential structure of the stone.
	 * @return A two-dimensional array of {@code boolean}s
	 * indicating where inside the bounding box the bricks are located,
	 * as described in {@link #setAllocationInBoundingBox(boolean[][])}.
	 * @see #setAllocationInBoundingBox(boolean[][])
	 * @see #boundingBox()
	 */
	public boolean[][] getAllocationInBoundingBox() {
		return allocation;
	}
	
	@Override
	public void onStoneFixed(Stone stone) {
		reset();
	}
	
}