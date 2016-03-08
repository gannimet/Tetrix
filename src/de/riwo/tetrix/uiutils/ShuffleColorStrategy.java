package de.riwo.tetrix.uiutils;

import android.graphics.Color;
import de.riwo.tetrix.tetris.stone.StoneType;

public class ShuffleColorStrategy extends ColorStrategy {

	private int threshold = 180;
	
	@Override
	public int getColor(StoneType stoneType) {
		int r = (int) (Math.random() * 255.0);
		if(r > threshold)
			r = 180;
		int g = (int) (Math.random() * 255.0);
		if(g > threshold)
			g = 180;
		int b = (int) (Math.random() * 255.0);
		if(b > threshold)
			b = 180;
		
		return Color.rgb(r, g, b);
	}

}