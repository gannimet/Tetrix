package de.riwo.tetrix.tetris.command;

import android.util.SparseArray;
import de.riwo.tetrix.tetris.Grid;
import de.riwo.tetrix.tetris.stone.Stone;

/**
 * It is crucial for the application to use this class to obtain
 * {@link StoneCommand} instances, because only this way it is granted
 * that {@link MoveCommand}s are able to correctly listen to rotation
 * events.
 * @author richard
 *
 */
public class CommandFactory {

	private SparseArray<StoneCommand> commands = new SparseArray<StoneCommand>();
	private int rotationStrategy;
	
	// to save memory, we don't allocate new commands in every call of getCommand()
	private StoneCommand workingCommand;
	
	public CommandFactory(int rotationStrategy) {
		this.rotationStrategy = rotationStrategy;
	}
	
	public StoneCommand getCommand(int commandType, Stone stone, Grid grid)
	throws IllegalCommandTypeException {
		workingCommand = commands.get(commandType);
		if(workingCommand != null) {
			// commandType existed already
			workingCommand.setStone(stone);
			return workingCommand;
		} else {
			// lazy-create new commandType of given type
			switch(commandType) {
				case StoneCommand.MOVE_DOWN_COMMAND:
					workingCommand = new MoveDownCommand(stone, grid);
					makeItListenToRotation((MoveCommand) workingCommand);
					break;
				case StoneCommand.MOVE_LEFT_COMMAND:
					workingCommand = new MoveLeftCommand(stone, grid);
					makeItListenToRotation((MoveCommand) workingCommand);
					break;
				case StoneCommand.MOVE_RIGHT_COMMAND:
					workingCommand = new MoveRightCommand(stone, grid);
					makeItListenToRotation((MoveCommand) workingCommand);
					break;
				case StoneCommand.ROTATE_COMMAND:
					if(rotationStrategy == RotateCommand.CLASSIC_ROTATION)
						workingCommand = new ClassicRotateCommand(stone, grid);
					// make all other commands listen to this
					// RotateCommand to be able to update
					// their allocation
					for(int i = 0; i < commands.size(); i++) {
						((RotateCommand) workingCommand).addStoneRotationListener(
							(MoveCommand) commands.valueAt(i)
						);
					}
					break;
				default:
					throw new IllegalCommandTypeException(
						"Illegal commandType type requested: " + commandType);
			}
			commands.put(commandType, workingCommand);
			return workingCommand;
		}
	}
	
	private void makeItListenToRotation(MoveCommand moveCmd) {
		RotateCommand rotCmd =
			(RotateCommand) commands.get(StoneCommand.ROTATE_COMMAND);
		if(rotCmd != null)
			rotCmd.addStoneRotationListener((MoveCommand) moveCmd);
	}
	
}