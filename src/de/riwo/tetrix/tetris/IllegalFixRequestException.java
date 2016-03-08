package de.riwo.tetrix.tetris;

public class IllegalFixRequestException extends Exception {

	private static final long serialVersionUID = 8109653464422404789L;
	
	public IllegalFixRequestException(String message) {
		super(message);
	}

}