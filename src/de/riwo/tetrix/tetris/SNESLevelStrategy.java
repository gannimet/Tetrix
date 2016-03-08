package de.riwo.tetrix.tetris;

public class SNESLevelStrategy implements LevelStrategy {

	@Override
	public int getLevel(final int points, final int lines) {
		return (int) ((((double) lines) / 10.0) + 1);
	}

}