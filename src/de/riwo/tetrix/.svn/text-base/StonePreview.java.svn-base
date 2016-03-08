package de.riwo.tetrix;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import de.riwo.tetrix.tetris.stone.Stone;

public class StonePreview extends View {

	private Paint paint = new Paint();
	private int cellWidth = 20;
	private int cellHeight = 20;
	private Stone stone;
	private boolean[][] stoneAlloc;
	
	public StonePreview(Context context) {
		super(context);
	}

	public StonePreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StonePreview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setStone(Stone stone) {
		this.stone = stone;
		stoneAlloc = stone.getInitialAllocationInBoundingBox();
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(stone == null)
			return;
		
		paint.setColor(stone.getColor());
		
		for(int y = 0; y < stoneAlloc[0].length; y++) {
			for(int x = 0; x < stoneAlloc.length; x++) {
				if(stoneAlloc[x][y]) {
					canvas.drawRect(
						x * cellWidth,
						y * cellHeight,
						(x + 1) * cellWidth - 1,
						(y + 1) * cellHeight - 1,
						paint
					);
				}
			}
		}
	}

}