package de.riwo.tetrix.uiutils;

import de.riwo.tetrix.tetris.stone.StoneType;

public class SingleColorStrategy extends ColorStrategy {
	
	private int color;
	
	public SingleColorStrategy(int color) {
		this.color = color;
	}
	
	@Override
	public int getColor(StoneType stoneType) {
		return color;
	}

}