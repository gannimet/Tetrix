package de.riwo.tetrix.uiutils;

import de.riwo.tetrix.tetris.Game;

public class MoveSidewaysTapStrategy extends GridPanelTapStrategy {

	public MoveSidewaysTapStrategy(Game game) {
		super(game);
	}

	@Override
	public void handleTap(int x, int y) {
		if(x <= panelWidth / 2) {
			game.moveStoneLeft();
		} else {
			game.moveStoneRight();
		}
	}

}