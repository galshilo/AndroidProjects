package com.project.laddersandworms.controller;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AlphaAnimation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.project.laddersandworms.MainActivity;
import com.project.laddersandworms.R;
import com.project.laddersandworms.entities.Board;
import com.project.laddersandworms.entities.Dice;
import com.project.laddersandworms.entities.InvalidPlayerException;
import com.project.laddersandworms.entities.Obstacle;
import com.project.laddersandworms.entities.Player;
import com.project.laddersandworms.entities.Player.Type;
import com.project.laddersandworms.entities.Position;

public class Game implements Observer, OnLoadCompleteListener {

	private static Game _instrance = null;
	private final String RAW_IMAGE = "drawable/dice_";
	private final String RAW_SOUNDS = "raw/sound_dice_";
	private final int NUMBER_OF_PLAYERS = 2;
	private final int ANIMATION_DELAY = 1000;
	private final int TURN_DELAY = 2500;
	private final int DICE_DELAY = 700;
	private Player _currentPlayer;
	private Dice _dice;
	private ArrayList<Player> _players;
	private MainActivity _view;
	private Board _board;
	private SoundPool _soundPool;

	
	public enum Level {
		EASY(1), MEDIUM(2), HARD(3);

		private int numVal;

		Level(int numVal) {
			this.numVal = numVal;
		}

		public int getNumVal() {
			return numVal;
		}
	}

	private Game() {
		this._players = new ArrayList<Player>(NUMBER_OF_PLAYERS);
		this._dice = new Dice();
		this._board = new Board(Level.HARD);
		this._soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		this._soundPool.setOnLoadCompleteListener(this);
	}

	public static Game getInstance() {
		if (_instrance == null) {
			_instrance = new Game();
		}

		return _instrance;
	}

	public void setView(MainActivity view) {
		this._view = view;
	}

	public MainActivity GetView(){
		return this._view;
	}
	
	public void init() throws InvalidPlayerException {
		initPlayers();
		initObstacles();
		initFinishLine();
		playSound(R.raw.sound_startround);
	}

	private void initFinishLine(){
		drawImage(_board.getPositionAt(Board.getFinalPosition()), 
				generateImageView(R.drawable.img_finish));
	}
	
	private void initPlayers() throws InvalidPlayerException {
		addPlayer(new Player("Gal", Player.Type.HUMAN));
		addPlayer(new Player("Dor", Player.Type.MACHINE));
		for (Player p : _players) {
			p.setPosition(1);
			setImagePosition(_view.getPlayerView(p.getType()),
					_board.getPointnAt(p.getPosition()));
		}
		setActivePlayer(_players.get(0));
	}

	public void rollDice() throws InterruptedException, InvalidPlayerException {
		animateDice();
		generateWeapon();
		movePlayer(_currentPlayer, _dice.getValue());
	}

	private void animateDice() {
		int soundResource = getResourceId(RAW_SOUNDS + _dice.getSound());
		playSound(soundResource);
		rotateDice();	
		_dice.roll();
		_view.getDiceView().setImageResource(
				getResourceId(RAW_IMAGE + _dice.getValue()));
	}
	
	private void rotateDice(){
		RotateAnimation anim = new RotateAnimation(0f, 360f,
				_view.getDiceView().getWidth()/2, _view.getDiceView().getHeight()/2);
	    anim.setRepeatCount(Animation.ABSOLUTE);
	    anim.setDuration(DICE_DELAY);
	    _view.getDiceView().setAnimation(anim);
	}

	private boolean positionHasObstacle(int pos, Obstacle obs) {
		return _board.getPositionAt(pos).getObstacle() == obs;
	}

	private boolean handlePositionAction(Player player) throws InvalidPlayerException {
		if (positionHasObstacle(player.getPosition(), Obstacle.BAZOOKA)) {
			Log.i("Worms", player.getName() + " stepped on a bazooka!");
			_view.getHandler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					try {
						hitRival();
					} catch (InvalidPlayerException e) {
						e.printStackTrace();
					}
				}
			}, ANIMATION_DELAY);

			return true;
		}
		return false;
	}

	@SuppressLint("NewApi")
	private void hitRival() throws InvalidPlayerException {
		final Player rivalPlayer = _players.get((_players.indexOf(_currentPlayer) + 1)
				% NUMBER_OF_PLAYERS);
		final int steps = _currentPlayer.hitRival();
		final Position  bazookaPosition = _board.getPositionAt(_currentPlayer.getPosition());
		final ImageView bazookaView = _board.getPositionAt(_currentPlayer.getPosition()).getView();
		final Point rivalPosition = _board.getPositionAt(rivalPlayer.getPosition()).getPoint();
		playSound(R.raw.sound_shoot);
		bazookaView.animate().x(rivalPosition.x).setDuration(ANIMATION_DELAY  * 2);
		bazookaView.animate().y(rivalPosition.y).setDuration(ANIMATION_DELAY * 2);
		bazookaView.animate().rotation(1080).setDuration(ANIMATION_DELAY * 2);
		Log.i("Worms", _currentPlayer.getName() + " hit the rival with " + steps);
		_view.getHandler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				bazookaPosition.freeObstacle();
				try {
					movePlayer(rivalPlayer, steps);
				} catch (InvalidPlayerException e) {}
			}
		}, ANIMATION_DELAY * 3);
	}

	public void switchPlayer() throws InterruptedException,	InvalidPlayerException {
		_currentPlayer = _players.get((_players.indexOf(_currentPlayer) + 1) % NUMBER_OF_PLAYERS);

		setImage(_view.getTurnView(),_currentPlayer.getType() == Type.HUMAN ?
				R.drawable.soldier_player :  R.drawable.soldier_pc);
		highlightTurn();

		if (_currentPlayer.isMachine()) {
			rollDice();
		}
		else {
			_view.getDiceView().setEnabled(true);
		}
	}

	private void setImage(final ImageView view, final int resource) {
		view.setImageResource(resource);
	}
	
	/***
	 * Generates an ImageView object with an image resource
	 * @param resource image id resource
	 * @return ImageView object
	 */
	private ImageView generateImageView(int resource) {
		ImageView view = new ImageView(_view);
		setImage(view, resource);
		return view;
	}

	@SuppressLint("NewApi")
	private void initObstacles() {
		for (Position pos : _board.getPositions()) {
			if (pos != null) {
				if (pos.getObstacle() == Obstacle.BAZOOKA) {
					pos.setView(generateImageView(R.drawable.img_bazooka));
					drawImage(pos, pos.getView());
				}
			}
		}
	}

	/***
	 * draw a given ImageView object to a specific view location
	 * @param pos Position object with x and y values
	 * @param img ImageView object 
	 */
	private void drawImage(final Position pos, final ImageView img) {
		final int imageSize = 30;
		final RelativeLayout layout = (RelativeLayout) _view
				.findViewById(R.id.layoutGameBoard);
		img.setMaxHeight(imageSize);
		img.setMaxWidth(imageSize);
		setImagePosition(img, pos.getPoint());
		layout.post(new Runnable() {

			@Override
			public void run() {
				fireFadeInAnimation(img, ANIMATION_DELAY * 2);
				layout.addView(img);
				Log.i("Worms", "Added bazooka to point: " + pos.getId());
			}
		});
	}

	/***
	 * apply fade-in animation on a view object
	 * @param view view object to perform the effect on
	 * @param duration animation duration
	 */
	private void fireFadeInAnimation(View view, int duration){
		AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(duration);
		view.startAnimation(anim);
	}
	
	private void generateWeapon() {
		int square = _board.generateWeapon();
		if (square == -1) // weapon was not added
			return;

		ImageView view = generateImageView(R.drawable.img_bazooka);
		drawImage(_board.getPositionAt(square), view);
		_board.getPositionAt(square).setView(view);
		playSound(R.raw.sound_teleport);
	}

	private void highlightTurn() {
		final TextView txtTurn = (TextView) _view.findViewById(R.id.txtBoard);
		txtTurn.setTextColor(Color.RED);
		txtTurn.setText(String.format("%s, Your turn!",
				_currentPlayer.getName()));
		txtTurn.postDelayed(new Runnable() {

			@Override
			public void run() {
				txtTurn.setTextColor(Color.BLACK);
			}
		}, 250);
	}

	public void movePlayer(final Player player, int steps) throws InvalidPlayerException {
		final ImageView currentView = _view.getPlayerView(player.getType());
		if (currentView == null) 
			throw new InvalidPlayerException();
		
		final int basePosition = player.getPosition(); 
		player.setPosition(player.getPosition() + steps);
		
		final int currentPosition = player.getPosition();
		_view.getHandler().post(new Runnable() {

			@Override
			public void run() {
				Log.i("Worms", "moving player " + player.getName() + " to "+ player.getPosition());
				try {
					fireImageMovementAnimation(currentView, basePosition, currentPosition);
				} catch (InterruptedException e1) {}
				try {
					if (handlePositionAction(player)) {
						return;
					}
				} catch (InvalidPlayerException e) {}
				player.notifyObservers();
			}
		});
	}

	public void addPlayer(Player p) {
		p.addObserver(this);
		this._players.add(p);
	}

	public void removePlayer(Player p) {
		this._players.remove(p);
	}

	private void playSound(int id) {
		_soundPool.load(_view, id, 1);
	}

	private int getResourceId(String path) {
		int id = _view.getResources().getIdentifier(path, null,
				_view.getPackageName());
		return id;
	}

	public void setImagePosition(final ImageView img, final float x,
			final float y) {
		img.post(new Runnable() {

			@Override
			public void run() {
				img.setX(x);
				img.setY(y);
			}
		});
	}

	@SuppressLint("NewApi")
	public void fireImageMovementAnimation(final ImageView img, int fromPosition, int toPosition) throws InterruptedException {
		int distance = Math.abs(toPosition - fromPosition);
		final int delay = ANIMATION_DELAY / distance;
		int delta = (toPosition - fromPosition) / distance;
		int tempPosition = fromPosition;
		
		for (int i = 0;i < distance;i++){
			tempPosition += delta;
			final int newTempPosition = tempPosition;// final attribute needed to use inside postDelayed
			final int destX = _board.getPointnAt(tempPosition).x;
			final int destY = _board.getPointnAt(tempPosition).y;
			_view.getHandler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					img.animate().x(destX).setDuration(delay);
					img.animate().y(destY).setDuration(delay);
					setImageDirection(img ,newTempPosition);
				}
			}, delay * i);
		}
	}

	private void setImageDirection(ImageView img, int tempPosition) {// FUCKING UGLY FUNCTION!!!!!!!!!!!1
		if (img == null) 
			return;
		
		if ((Math.ceil(tempPosition / _board.getColumsNumber())) % 2 == 1){
			switch (_currentPlayer.getType()){ 
				case MACHINE:
					setImage(img, R.drawable.soldier_pc);
					break;
				case HUMAN:
					setImage(img, R.drawable.soldier_player);
					break;
				default:
			}	
		}
		else {
			switch (_currentPlayer.getType()){ 
				case MACHINE:
					setImage(img, R.drawable.soldier_pc_fliped);
					break;
				case HUMAN:
					setImage(img, R.drawable.soldier_player_fliped);
					break;
				default:
			}
		}
		
	}

	public void setImagePosition(final ImageView img, final Point pos) {
		img.post(new Runnable() {

			@Override
			public void run() {
				img.setX(pos.x);
				img.setY(pos.y);
			}
		});
	}

	public void setActivePlayer(Player p) {
		this._currentPlayer = p;
	}

	@SuppressLint("NewApi")
	@Override
	public void update(Observable observable, Object data) {
		if (_currentPlayer.getPosition() == Board.getFinalPosition()) {
			playSound(R.raw.sound_victory);
		}
		_view.getHandler().postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					switchPlayer();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (InvalidPlayerException e) {
					e.printStackTrace();
				}
			}
		}, TURN_DELAY);
	}

	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		soundPool.play(sampleId, 1, 1, 0, 0, 1);
	}
}
