package de.riwo.tetrix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import de.riwo.tetrix.tetris.Game;
import de.riwo.tetrix.tetris.GameOverListener;
import de.riwo.tetrix.tetris.LevelChangeListener;
import de.riwo.tetrix.tetris.LevelStrategy;
import de.riwo.tetrix.tetris.LineClearListener;
import de.riwo.tetrix.tetris.NintendoPointStrategy;
import de.riwo.tetrix.tetris.PointStrategy;
import de.riwo.tetrix.tetris.PointsChangeListener;
import de.riwo.tetrix.tetris.SNESLevelStrategy;
import de.riwo.tetrix.tetris.StoneFixListener;
import de.riwo.tetrix.tetris.command.RotateCommand;
import de.riwo.tetrix.tetris.command.StoneCommand;
import de.riwo.tetrix.tetris.stone.Stone;
import de.riwo.tetrix.tetris.stone.StoneType;
import de.riwo.tetrix.uiutils.ColorStrategy;
import de.riwo.tetrix.uiutils.GridPanelTapStrategy;
import de.riwo.tetrix.uiutils.MoveSidewaysTapStrategy;
import de.riwo.tetrix.uiutils.NullTapStrategy;
import de.riwo.tetrix.uiutils.PauseTapStrategy;
import de.riwo.tetrix.uiutils.RotateTapStrategy;
import de.riwo.tetrix.uiutils.ShuffleColorStrategy;
import de.riwo.tetrix.uiutils.SingleColorStrategy;
import de.riwo.tetrix.uiutils.StandardColorStrategy;

public class GameActivity extends Activity implements
		GameOverListener, LevelChangeListener, LineClearListener,
		PointsChangeListener, OnClickListener, StoneFixListener, OnTouchListener {

	private GridPanel gridPanel;
	private TextView pointsLbl, levelLbl, linesLbl, nextDescLbl;
	private StonePreview nextLbl;
	private ImageButton leftBtn, rightBtn, lowerLeftBtn, lowerRightBtn, pauseBtn;
	
	private Game game;
	
	private Thread requestThread;
	private ButtonRequestThread requestTicker;
	
	private boolean lowerButtonsSwapped = false;
	private boolean enableHoldForDown, enableHoldForLeftAndRight;
	private int touchAction;
	private boolean isTouching = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		int singleColor = prefs.getInt("single_color_choice", getResources().getColor(R.color.stone_color_default));
		List<String> typesFromPrefs = Arrays.asList(prefs.getString("stone_types",
			getString(R.string.stone_types_default)).split(","));
		String colorStrategyString = prefs.getString("stone_colors", getString(R.string.stone_color_strategy_default));
		ColorStrategy colorStrategy =
			colorStrategyString.equals("single") ? new SingleColorStrategy(singleColor) :
			colorStrategyString.equals("shuffle") ? new ShuffleColorStrategy() :
			new StandardColorStrategy();
		
		Map<StoneType, Integer> stoneTypes = new HashMap<StoneType, Integer>();
		
		if(typesFromPrefs.contains("block"))
			stoneTypes.put(StoneType.BLOCK_STONE, colorStrategy.getColor(StoneType.BLOCK_STONE));
		if(typesFromPrefs.contains("four_bar"))
			stoneTypes.put(StoneType.FOUR_BAR_STONE, colorStrategy.getColor(StoneType.FOUR_BAR_STONE));
		if(typesFromPrefs.contains("inversed_l"))
			stoneTypes.put(StoneType.INVERSED_L_STONE, colorStrategy.getColor(StoneType.INVERSED_L_STONE));
		if(typesFromPrefs.contains("left_chair"))
			stoneTypes.put(StoneType.LEFT_CHAIR_STONE, colorStrategy.getColor(StoneType.LEFT_CHAIR_STONE));
		if(typesFromPrefs.contains("podium"))
			stoneTypes.put(StoneType.PODIUM_STONE, colorStrategy.getColor(StoneType.PODIUM_STONE));
		if(typesFromPrefs.contains("right_chair"))
			stoneTypes.put(StoneType.RIGHT_CHAIR_STONE, colorStrategy.getColor(StoneType.RIGHT_CHAIR_STONE));
		if(typesFromPrefs.contains("straight_l"))
			stoneTypes.put(StoneType.STRAIGHT_L_STONE, colorStrategy.getColor(StoneType.STRAIGHT_L_STONE));
		if(typesFromPrefs.contains("single"))
			stoneTypes.put(StoneType.SINGLE_STONE, colorStrategy.getColor(StoneType.SINGLE_STONE));
		
		String holdButtons = prefs.getString("hold_control_buttons", getString(R.string.hold_control_buttons_default));
		if("all".equals(holdButtons)) {
			enableHoldForDown = true;
			enableHoldForLeftAndRight = true;
		} else if("only_left_right".equals(holdButtons)) {
			enableHoldForDown = false;
			enableHoldForLeftAndRight = true;
		} else if("only_down".equals(holdButtons)) {
			enableHoldForDown = true;
			enableHoldForLeftAndRight = false;
		} else {
			enableHoldForDown = false;
			enableHoldForLeftAndRight = false;
		}
		
		if(!prefs.getBoolean("next_stone", true)) {
			nextDescLbl.setVisibility(View.INVISIBLE);
			nextLbl.setVisibility(View.INVISIBLE);
		}
		
		lowerButtonsSwapped = prefs.getBoolean("swap_lower_buttons", false);
		
		initUI();
		
		PointStrategy ps = new NintendoPointStrategy();
		LevelStrategy ls = new SNESLevelStrategy();
		int rs = RotateCommand.CLASSIC_ROTATION;
		game = new Game(10, 20, stoneTypes, ps, ls, rs);
		
		String tapStrategyString = prefs.getString("panel_tap", getString(R.string.panel_tap_default));
		GridPanelTapStrategy tapStrategy = new NullTapStrategy(game);
		if("pause_on_tap".equals(tapStrategyString))
			tapStrategy = new PauseTapStrategy(game);
		if("rotate_on_tap".equals(tapStrategyString))
			tapStrategy = new RotateTapStrategy(game);
		if("move_on_tap".equals(tapStrategyString))
			tapStrategy = new MoveSidewaysTapStrategy(game);
		
		if(enableHoldForDown || enableHoldForLeftAndRight) {
			int dropInterval = prefs.getInt("stone_velocity", 50);
			requestTicker = new ButtonRequestThread(game,
				(int) (200.0 - (double) dropInterval * 1.2));
			requestThread = new Thread(requestTicker);
			requestThread.start();
		}
		
		game.addGameOverListener(this);
		game.addLevelChangeListener(this);
		game.addLineClearListener(this);
		game.addPointsChangeListener(this);
		game.addStoneFixListener(this);
		
		gridPanel.setGame(game);
		gridPanel.setTapStrategy(tapStrategy);
		game.start();
		nextLbl.setStone(game.getNextStone());
	}
	
	private void initUI() {
		gridPanel = (GridPanel) findViewById(R.id.gridCanvas);
		pointsLbl = (TextView) findViewById(R.id.pointsLbl);
		levelLbl = (TextView) findViewById(R.id.levelLbl);
		linesLbl = (TextView) findViewById(R.id.linesLbl);
		leftBtn = (ImageButton) findViewById(R.id.moveLeftBtn);
		lowerLeftBtn = (ImageButton) findViewById(R.id.lowerLeftBtn);
		rightBtn = (ImageButton) findViewById(R.id.moveRightBtn);
		lowerRightBtn = (ImageButton) findViewById(R.id.lowerRightBtn);
		pauseBtn = (ImageButton) findViewById(R.id.pauseBtn);
		nextDescLbl = (TextView) findViewById(R.id.nextDescLbl);
		nextLbl = (StonePreview) findViewById(R.id.nextLbl);
		
		if(enableHoldForLeftAndRight) {
			// long-touching should be enabled
			leftBtn.setOnTouchListener(this);
			rightBtn.setOnTouchListener(this);
		} else {
			// only clicking for sidewards buttons
			leftBtn.setOnClickListener(this);
			rightBtn.setOnClickListener(this);
		}
		pauseBtn.setOnClickListener(this);
		
		if(lowerButtonsSwapped) {
			// left is rotate, right is down
			lowerLeftBtn.setImageResource(R.drawable.rotate_clockwise);
			lowerLeftBtn.invalidate();
			lowerRightBtn.setImageResource(R.drawable.down);
			lowerRightBtn.invalidate();
			// set the right listener for down button
			if(enableHoldForDown)
				lowerRightBtn.setOnTouchListener(this);
			else
				lowerRightBtn.setOnClickListener(this);
			// rotate button shall only react on clicks
			lowerLeftBtn.setOnClickListener(this);
		} else {
			// left is down, right is rotate
			// set right listener for down button
			if(enableHoldForDown)
				lowerLeftBtn.setOnTouchListener(this);
			else
				lowerLeftBtn.setOnClickListener(this);
			// rotate button shall only react on clicks
			lowerRightBtn.setOnClickListener(this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		terminate();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		terminate();
	}

	private void terminate() {
		game.stop();
		
		if(requestTicker != null) {
			requestTicker.stop();
			requestTicker = null;
		}
		
		if(requestThread != null) {
			requestThread.interrupt();
			requestThread = null;
		}
		
		finish();
	}
	
	@Override
	public void onGridFull() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast toast = Toast.makeText(GameActivity.this, "GAME OVER", Toast.LENGTH_LONG);
				toast.show();
			}
			
		});
	}

	@Override
	public void onClick(final View view) {
		if(view == leftBtn) {
			game.moveStoneLeft();
		}
		
		if(view == lowerLeftBtn) {
			if(lowerButtonsSwapped)
				game.rotateStone();
			else
				game.moveStoneDown();
		}
		
		if(view == rightBtn) {
			game.moveStoneRight();
		}
		
		if(view == lowerRightBtn) {
			if(lowerButtonsSwapped)
				game.moveStoneDown();
			else
				game.rotateStone();
		}
		
		if(view == pauseBtn) {
			if(game.isPaused()) {
				pauseBtn.setImageResource(R.drawable.pause);
				game.resume();
			} else {
				pauseBtn.setImageResource(R.drawable.resume);
				game.pause();
			}
		}
	}

	@Override
	public void onPointsChanged(final int points) {
		pointsLbl.post(new Runnable() {

			@Override
			public void run() {
				pointsLbl.setText(Integer.valueOf(points).toString());
			}
			
		});
	}

	@Override
	public void onLevelChanged(final int level) {
		levelLbl.post(new Runnable() {

			@Override
			public void run() {
				levelLbl.setText(Integer.valueOf(level).toString());
			}
			
		});
	}

	@Override
	public void onLinesCleared(final List<Integer> lines) {
		linesLbl.post(new Runnable() {

			@Override
			public void run() {
				linesLbl.setText(Integer.valueOf(game.getLines()).toString());
			}
			
		});
	}

	@Override
	public void onStoneFixed(final Stone stone) {
		nextLbl.setStone(game.getNextStone());
	}

	@Override
	public boolean onTouch(final View view, final MotionEvent event) {
		touchAction = event.getAction();
		
		if(!isTouching && touchAction == MotionEvent.ACTION_DOWN) {
			isTouching = true;
			
			view.setPressed(true);
			
			if(view == lowerRightBtn || view == lowerLeftBtn)
				requestTicker.setCommandType(StoneCommand.MOVE_DOWN_COMMAND);
			if(view == leftBtn)
				requestTicker.setCommandType(StoneCommand.MOVE_LEFT_COMMAND);
			if(view == rightBtn)
				requestTicker.setCommandType(StoneCommand.MOVE_RIGHT_COMMAND);
			
			// let the request thread run as long
			// as the down button is pressed
			if(requestTicker.isPaused())
				requestTicker.setPaused(false);
		}
		
		if(touchAction == MotionEvent.ACTION_UP) {
			isTouching = false;
			
			view.setPressed(false);
			
			// when the user lifts the finger from the
			// button, stop executing commands
			requestTicker.setPaused(true);
		}
		
		return true;
	}

}