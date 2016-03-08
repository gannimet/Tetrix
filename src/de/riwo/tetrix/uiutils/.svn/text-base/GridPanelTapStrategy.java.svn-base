package de.riwo.tetrix.uiutils;

import de.riwo.tetrix.tetris.Game;

public abstract class GridPanelTapStrategy {

	protected Game game;
	protected int panelWidth, panelHeight;
	
	public GridPanelTapStrategy(Game game) {
		this.game = game;
	}
	
	public void setDimensions(int panelWidth, int panelHeight) {
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
	}

	public abstract void handleTap(int x, int y);
	
}