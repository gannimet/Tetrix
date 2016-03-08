package de.riwo.tetrix.tetris.command;

public class IllegalCommandTypeException extends Exception {

	private static final long serialVersionUID = -7929366692387862788L;
	
	public IllegalCommandTypeException(String message) {
		super(message);
	}

}