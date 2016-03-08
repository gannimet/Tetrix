package de.riwo.tetrix;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import de.riwo.tetrix.tetris.Game;
import de.riwo.tetrix.tetris.LineClearListener;
import de.riwo.tetrix.tetris.stone.Stone;
import de.riwo.tetrix.uiutils.GridPanelTapStrategy;

public class GridPanel extends View implements LineClearListener, OnTouchListener {

	private Paint paint = new Paint();
	
	private int cellWidth = 20;
	private int cellHeight = 20;
	private Game game;
	private int gridWidth, gridHeight;
	
	private Stone stone;
	private int stoneX, stoneY;
	private boolean[][] box;
	
	private GridPanelTapStrategy tapStrategy;
	
	private int touchX, touchY;
	
	public GridPanel(Context context) {
		super(context);
		init(context);
	}

	public GridPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public GridPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public void init(Context context) {
		setOnTouchListener(this);
		
		WindowManager wmng = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wmng.getDefaultDisplay();
		
		// convert dip into px
		if(display.getHeight() > 400) {
			cellWidth = (int) Math.round(16.0 * getResources().getDisplayMetrics().density);
		} else {
			cellWidth = (int) Math.round(12.0 * getResources().getDisplayMetrics().density);
		}
		cellHeight = cellWidth;
	}
	
	public void setGame(Game game) {
		this.game = game;
		game.addLineClearListener(this);
		gridWidth = game.getGridWidth();
		gridHeight = game.getGridHeight();
	}

	public void setTapStrategy(GridPanelTapStrategy tapStrategy) {
		this.tapStrategy = tapStrategy;
		this.tapStrategy.setDimensions(
			gridWidth * cellWidth,
			gridHeight * cellHeight
		);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		paint.setStrokeWidth(1);
		paint.setStyle(Style.STROKE);
		
		paint.setColor(Color.GRAY);
		canvas.drawRect(0, 0, cellWidth * gridWidth,
			cellHeight * gridHeight, paint);
		
		if(game == null || !game.isStarted()) {
			invalidate();
			return;
		}
		
		// paint the grid
		paint.setStyle(Style.FILL);
		synchronized(game) {
			for(int y = 0; y < gridHeight; y++) {
				for(int x = 0; x < gridWidth; x++) {
					paint.setColor(game.getGridColorAt(x, y));
					canvas.drawRect(
						x * cellWidth, // left
						y * cellHeight, // top
						(x+1) * cellWidth - 1, // right
						(y+1) * cellHeight - 1, // bottom
						paint
					);
				}
			}
		}
		
		// invalidate only the "dirty" area in which the stone has moved
		stone = game.getCurrentStone();
		stoneX = stone.getX();
		stoneY = stone.getY();
		box = stone.getAllocationInBoundingBox();
		invalidate(
			(stoneX - 1) * cellWidth,
			(stoneY - 2) * cellHeight,
			(stoneX + box.length + 1) * cellWidth,
			(stoneY + box[0].length + 1) * cellHeight
		);
	}

	@Override
	public void onLinesCleared(List<Integer> lines) {
		postInvalidate();	// redraw panel
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if(tapStrategy == null)
			return false;
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			touchX = Math.round(event.getX(event.getPointerCount()-1));
			touchY = Math.round(event.getY(event.getPointerCount()-1));
			
			tapStrategy.handleTap(touchX, touchY);
		}
		
		return true;
	}

}