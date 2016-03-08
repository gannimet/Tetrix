package de.riwo.tetrix.uiutils;

import de.riwo.tetrix.tetris.Game;

public class RotateTapStrategy extends GridPanelTapStrategy {

	public RotateTapStrategy(Game game) {
		super(game);
	}

	@Override
	public void handleTap(int x, int y) {
		game.rotateStone();
	}

}