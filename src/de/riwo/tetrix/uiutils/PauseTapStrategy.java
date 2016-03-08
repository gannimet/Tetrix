package de.riwo.tetrix.uiutils;

import de.riwo.tetrix.tetris.Game;

public class PauseTapStrategy extends GridPanelTapStrategy {

	public PauseTapStrategy(Game game) {
		super(game);
	}

	@Override
	public void handleTap(int x, int y) {
		if(game.isPaused())
			game.resume();
		else
			game.pause();
	}

}