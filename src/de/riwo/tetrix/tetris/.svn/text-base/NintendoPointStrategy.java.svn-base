package de.riwo.tetrix.tetris;

import java.util.List;

import android.util.Log;

/**
 * Strategy class implementing the point counting algorithm used by
 * Nintendo's versions of tetris.
 * @author richard
 *
 */
public class NintendoPointStrategy implements PointStrategy {

	@Override
	public int getLinePoints(final int level, final List<Integer> lines) {
		switch(lines.size()) {
			case 1:
				return 40 * (level + 1);
			case 2:
				return 100 * (level + 1);
			case 3:
				return 300 * (level + 1);
			case 4:
				return 1200 * (level + 1);
			default:
				Log.w(getClass().getName(), "Impossible number of cleared lines: "
					+ lines.size());
				throw new IllegalStateException("Impossible number of cleared lines: "
					+ lines.size());
		}
	}

}