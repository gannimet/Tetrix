package de.riwo.tetrix.uiutils;

import android.graphics.Color;
import de.riwo.tetrix.tetris.stone.StoneType;

public class StandardColorStrategy extends ColorStrategy {

	@Override
	public int getColor(StoneType stoneType) {
		switch(stoneType) {
			case FOUR_BAR_STONE:
				return Color.rgb(2, 222, 222);
			case BLOCK_STONE:
				return Color.rgb(255, 238, 0);
			case STRAIGHT_L_STONE:
				return Color.rgb(255, 151, 20);
			case INVERSED_L_STONE:
				return Color.rgb(0, 137, 222);
			case LEFT_CHAIR_STONE:
				return Color.rgb(255, 62, 48);
			case RIGHT_CHAIR_STONE:
				return Color.rgb(11, 212, 57);
			case PODIUM_STONE:
				return Color.rgb(128, 6, 177);
			case SINGLE_STONE:
				return Color.BLACK;
			default:
				throw new AssertionError("Unknown stone type");
		}
	}

}