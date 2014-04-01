package com.project.laddersandworms.controller;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
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

public class GameController implements Observer, OnLoadCompleteListener {

	private static GameController _instrance = null;
	private final String RAW_IMAGE = "drawable/dice_";
	private final String RAW_SOUNDS = "raw/sound_dice_";
	private final int NUMBER_OF_PLAYERS = 2;

	private Player _currentPlayer;
	private Dice _dice;
	private ArrayList<Player> _players;
	private MainActivity _view;
	private Board _board;
	private SoundPool _player;

	private GameController() {
		this._players = new ArrayList<Player>(NUMBER_OF_PLAYERS);
		this._dice = new Dice();
		this._board = new Board(1);
		this._player = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		this._player.setOnLoadCompleteListener(this);
	}

	public static GameController getInstance() {
		if (_instrance == null) {
			_instrance = new GameController();
		}

		return _instrance;
	}

	public void setView(MainActivity view) {
		this._view = view;
	}

	public void init() {
		initPlayers();
		initObstacles();
	}

	private void initPlayers() {
		addPlayer(new Player("Gal", Player.Type.HUMAN));
		addPlayer(new Player("Dor", Player.Type.MACHINE));
		for (Player p : _players) {
			p.setPosition(1);
		}
		this._currentPlayer = _players.get(0);

	}

	public void rollDice() throws InterruptedException, InvalidPlayerException {
		animateDice();
		generateWeapon();
		movePlayer();
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

	private void handlePositionAction(Player player) {
		if (positionHasObstacle(player.getPosition(), Obstacle.BAZOOKA)) {
			Position pos = _board.getPositionAt(player.getPosition());
			/* shoot logic here */
			pos.freeObstacle();
		}
	}

	private void switchPlayer() throws InterruptedException,
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
					drawImage(pos.getPoint(), pos.getView());
				}
			}
		}
	}

	private void drawImage(final Point point, final ImageView view) {
		final RelativeLayout layout = (RelativeLayout) _view
				.findViewById(R.id.layoutGameBoard);
		view.setMaxHeight(30);
		view.setMaxWidth(30);
		setImagePosition(view, point);
		layout.post(new Runnable() {

			@Override
			public void run() {
				layout.addView(view);
				layout.invalidate();
				Log.i("Worms", "Added bazooka to point: " + point.toString());
			}
		});
	}

	private void generateWeapon() {
		int square = _board.generateWeapon();
		if (square == -1) // weapon was not added
			return;

		ImageView view = generateImageView(R.drawable.img_bazooka);
		drawImage(_board.getPointnAt(square), view);
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

	public void movePlayer() throws InvalidPlayerException {
		ImageView currentView = _view.getPlayerView(_currentPlayer.getType());
		if (currentView == null) {
			throw new InvalidPlayerException();
		}

		Log.i("Worms", "moving player " + _currentPlayer.getName());
		_currentPlayer.setPosition(_currentPlayer.getPosition()
				+ _dice.getValue());
		Point playerPosition = _board.getPositionAt(
				_currentPlayer.getPosition()).getPoint();
		setImagePosition(currentView, playerPosition);
		handlePositionAction(_currentPlayer);
		_currentPlayer.notifyObservers();
	}

	public void addPlayer(Player p) {
		p.addObserver(this);
		this._players.add(p);
	}

	public void removePlayer(Player p) {
		this._players.remove(p);
	}

	private void playSound(int id) {
		_player.load(_view, id, 1);
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
