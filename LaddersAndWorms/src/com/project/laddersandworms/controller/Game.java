package com.project.laddersandworms.controller;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
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
	private final int TURN_DELAY = 1500;
	
	public static Object mutex = new Object();

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

	public void init() throws InvalidPlayerException {
		initPlayers();
		initObstacles();
	}

	private void initPlayers() throws InvalidPlayerException {
		addPlayer(new Player("Gal", Player.Type.HUMAN));
		addPlayer(new Player("Dor", Player.Type.MACHINE));
		for (Player p : _players) {
			p.setPosition(1);
			setImagePosition(_view.getPlayerView(p.getType()), _board.getPointnAt(p.getPosition()));
		}
		setActivePlayter(_players.get(0));
	}

	public void rollDice() throws InterruptedException, InvalidPlayerException {
		animateDice();
		generateWeapon();
		movePlayer(_currentPlayer, _dice.getValue());
	}

	private void animateDice() {
		int soundResource = getResourceId(RAW_SOUNDS + _dice.getSound());
		playSound(soundResource);
		_dice.roll();
		_view.getDiceView().setImageResource(
				getResourceId(RAW_IMAGE + _dice.getValue()));
	}

	private boolean positionHasObstacle(int pos, Obstacle obs) {
		return _board.getPositionAt(pos).getObstacle() == obs;
	}

	private boolean handlePositionAction(Player player) throws InvalidPlayerException {
		if (positionHasObstacle(player.getPosition(), Obstacle.BAZOOKA)) {
			Position pos = _board.getPositionAt(player.getPosition());
			Log.i("Worms", player.getName() + " stepped on a bazooka!");
			pos.freeObstacle();
			hitRival();
			return true;
		}
		return false;
	}
	
	private void hitRival() throws InvalidPlayerException{
		Player rival = _players.get((_players.indexOf(_currentPlayer) + 1)
				% NUMBER_OF_PLAYERS);
		int moves = _currentPlayer.hitRival();
		Log.i("Worms", _currentPlayer.getName() + " hit the rival with " + moves);
		movePlayer(rival, moves);
	}

	public void switchPlayer() throws InterruptedException,
			InvalidPlayerException {
		_currentPlayer = _players.get((_players.indexOf(_currentPlayer) + 1)
				% NUMBER_OF_PLAYERS);

		setImage(
				_view.getTurnView(),
				_currentPlayer.getType() == Type.HUMAN ? R.drawable.soldier_player
						: R.drawable.soldier_pc);
		highlightTurn();

		if (_currentPlayer.isMachine()) {
			rollDice();
		}
	}

	private void setImage(final ImageView view, final int resource) {
		view.setImageResource(resource);
	}

	private ImageView generateImageView(int resource) {
		ImageView view = new ImageView(_view);
		setImage(view, resource);
		return view;
	}

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

	private void drawImage(final Position pos, final ImageView view) {
		final RelativeLayout layout = (RelativeLayout) _view
				.findViewById(R.id.layoutGameBoard);
		view.setMaxHeight(30);
		view.setMaxWidth(30);
		setImagePosition(view, pos.getPoint());
		layout.post(new Runnable() {

			@Override
			public void run() {
				layout.addView(view);
				layout.invalidate();
				Log.i("Worms", "Added bazooka to point: " + pos.getId());
			}
		});
	}

	private void generateWeapon() {
		int square = _board.generateWeapon();
		if (square == -1) // weapon was not added
			return;

		ImageView view = generateImageView(R.drawable.img_bazooka);
		drawImage(_board.getPositionAt(square), view);
		_board.getPositionAt(square).setView(view);
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

	public void movePlayer(final Player player, int moves) throws InvalidPlayerException {
		final ImageView currentView = _view.getPlayerView(player.getType());
		if (currentView == null) {
			throw new InvalidPlayerException();
		}
		final int lastPosition = player.getPosition();
		player.setPosition(player.getPosition()	+ moves);
		final int currentPosition = player.getPosition();
//		final Point playerPosition = _board.getPositionAt(
//				player.getPosition()).getPoint();
		_view.getHandler().post(new Runnable() {
			
			@Override
			public void run() {
				Log.i("Worms", "moving player " + player.getName() + " to " + player.getPosition());
				//setImagePosition(currentView, playerPosition);
				try {
					moveImage(currentView,lastPosition,currentPosition);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					if (handlePositionAction(player)){
						return;
					}
				} catch (InvalidPlayerException e) {
					e.printStackTrace();
				}	
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
	
	public void moveImage(final ImageView img, int lastPosistion, int currentPosition) throws InterruptedException{
		int sourceX = _board.getPointnAt(lastPosistion).x;
		int sourceY = _board.getPointnAt(lastPosistion).y;
		int destenationX = _board.getPointnAt(currentPosition).x;
		int destenationY = _board.getPointnAt(currentPosition).y;
		final int dx = (destenationX - sourceX)/10;
		final int dy = (destenationY - sourceY)/10;
		new AsyncTask<Integer, Integer, Integer>() {

			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}

			@Override
			protected Integer doInBackground(Integer... params) {
				for (int i = 0;i < 6;i++){
					int posX = params[0];
					int posY = params[1];
					posX += dx;
					posY += dy;
					onProgressUpdate(posX, posY);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
				setImagePosition(img ,new Point(values[0], values[1]));

			}
		}.execute(sourceX,sourceY);
		
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

	public void setActivePlayter(Player p) {
		this._currentPlayer = p;
	}

	@Override
	public void update(Observable observable, Object data) {
		try {
			Thread.sleep(TURN_DELAY);
			if (_currentPlayer.getPosition() == Board.getFinalPosition()){
				playSound(R.raw.sound_victory);
			}
			switchPlayer();		
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvalidPlayerException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		soundPool.play(sampleId, 1, 1, 0, 0, 1);
	}

}
