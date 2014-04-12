package com.project.laddersandworms.controller;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.laddersandworms.MainActivity;
import com.project.laddersandworms.R;
import com.project.laddersandworms.entities.Board;
import com.project.laddersandworms.entities.Dice;
import com.project.laddersandworms.entities.Obstacle;
import com.project.laddersandworms.entities.Player;
import com.project.laddersandworms.entities.Player.Type;
import com.project.laddersandworms.entities.Position;

public class Game implements Observer, OnLoadCompleteListener {

	private final String 				RAW_IMAGE = "drawable/dice_";
	private final String 				RAW_SOUNDS = "raw/sound_dice_";
	private final int 					NUMBER_OF_PLAYERS = 2;
	private final int 					ANIMATION_DELAY =	1000;
	private final int 					TURN_DELAY = 2500;
	private final int 					DICE_DELAY = 700;
	
	private Player 						mCurrentPlayer;
	private Dice 						mDice;
	private ArrayList<Player> 			mPlayers;
	private MainActivity 				mView;
	private Board 						mBoard;
	private SoundPool 					mSoundPool;
	private Level 						mLevel; 

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

	public Game() {
		this.mPlayers = new ArrayList<Player>(NUMBER_OF_PLAYERS);
		this.mLevel = Level.HARD;//to set when we dealing with options menu
		this.mDice = new Dice();
		this.mBoard = new Board(mLevel);
		this.mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		this.mSoundPool.setOnLoadCompleteListener(this);
	}

	public void setView(MainActivity view) {
		this.mView = view;
	}

	public MainActivity GetView(){
		return this.mView;
	}
	
	public void init() {
		initPlayers();
		initObstacles();
		initFinishLine();
		playSound(R.raw.sound_startround);
	}

	private void initFinishLine(){
		Point point = new Point(mBoard.getPointnAt(Board.getFinalPosition()).x - 18,
				mBoard.getPointnAt(Board.getFinalPosition()).y);
		drawImage(point, generateImageView(R.drawable.img_finish));
	}
	
	private void initPlayers() {
		addPlayer(new Player("Gal", Player.Type.HUMAN, mLevel));
		addPlayer(new Player("Dor", Player.Type.MACHINE, mLevel));
		for (Player p : mPlayers) {
			p.setPosition(1);
			setImagePosition(mView.getPlayerView(p.getType()),
					mBoard.getPointnAt(p.getPosition()));
		}
		setActivePlayer(mPlayers.get(0));
	}

	public void rollDice() throws InterruptedException {
		animateDice();
		generateWeapon();
		movePlayer(mCurrentPlayer, mDice.getValue());
	}

	private void animateDice() {
		int soundResource = getResourceId(RAW_SOUNDS + mDice.getSound());
		playSound(soundResource);
		rotateDice();	
		mDice.roll();
		mView.getDiceView().setImageResource(
				getResourceId(RAW_IMAGE + mDice.getValue()));
	}
	
	private void rotateDice(){
		RotateAnimation anim = new RotateAnimation(0f, 360f,
				mView.getDiceView().getWidth()/2, mView.getDiceView().getHeight()/2);
	    anim.setRepeatCount(Animation.ABSOLUTE);
	    anim.setDuration(DICE_DELAY);
	    mView.getDiceView().setAnimation(anim);
	}

	private boolean positionHasObstacle(int pos, Obstacle obs) {
		return mBoard.getPositionAt(pos).getObstacle() == obs;
	}

	private boolean handlePositionAction(Player player) {
		if (positionHasObstacle(player.getPosition(), Obstacle.BAZOOKA)) {
			Log.i("Worms", player.getName() + " stepped on a bazooka!");
			mView.getHandler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					hitRival();
				}
			}, ANIMATION_DELAY);

			return true;
		}
		return false;
	}

	@SuppressLint("NewApi")
	private void hitRival() {
		final Player rivalPlayer = mPlayers.get((mPlayers.indexOf(mCurrentPlayer) + 1) % NUMBER_OF_PLAYERS);
		final int steps = mCurrentPlayer.hitRival();
		final Position  bazookaPosition = mBoard.getPositionAt(mCurrentPlayer.getPosition());
		final ImageView bazookaView = mBoard.getPositionAt(mCurrentPlayer.getPosition()).getView();
		final Point rivalPosition = mBoard.getPositionAt(rivalPlayer.getPosition()).getPoint();
		bazookaView.animate().x(rivalPosition.x).setDuration(ANIMATION_DELAY  * 2);
		bazookaView.animate().y(rivalPosition.y).setDuration(ANIMATION_DELAY * 2);
		bazookaView.animate().rotation(1080).setDuration(ANIMATION_DELAY * 2);
		vibrate(ANIMATION_DELAY, ANIMATION_DELAY);
		Log.i("Worms", mCurrentPlayer.getName() + " hit the rival with " + steps);
		bombAnimation(rivalPosition);
		playSound(R.raw.sound_airstrike);
		
		mView.getHandler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				bazookaPosition.freeObstacle();
			}
		}, ANIMATION_DELAY * 2);
		
		mView.getHandler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				movePlayer(rivalPlayer, steps);
			}
		}, (int)(ANIMATION_DELAY * 2.5));
		
		mView.getHandler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				playHitPlayerSound(mCurrentPlayer);				
			}
		}, (int)(ANIMATION_DELAY * 3.5));
	}
	
	private void playHitPlayerSound(Player player) {
		if (player.isMachine()){
			playSound(R.raw.sound_ouch);
		} else {
			playSound(R.raw.sound_laugh);
		}
	}

	@SuppressLint("NewApi")
	private void bombAnimation(Point rivalPosition) {
		final RelativeLayout layout = (RelativeLayout) mView.findViewById(R.id.layoutGameBoard);
		final ImageView img = generateImageView(R.drawable.explosion);
		setImagePosition(img, new Point(rivalPosition.x - 5, rivalPosition.y - 5));
		layout.postDelayed(new Runnable() {
				
			@Override
			public void run() {
				playSound(R.raw.sound_shoot);
				img.animate().scaleX(2f).setDuration((int)(ANIMATION_DELAY/1.5));
				img.animate().scaleY(2f).setDuration((int)(ANIMATION_DELAY/1.5));
				layout.addView(img);
			}
		}, (int)(ANIMATION_DELAY*1.7));
		layout.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				img.setVisibility(View.GONE);
				img.setImageDrawable(null);	
			}
		}, (int)(ANIMATION_DELAY*1.7+ANIMATION_DELAY/1.5));
		
	}

	public void switchPlayer() throws InterruptedException {
		mCurrentPlayer = mPlayers.get((mPlayers.indexOf(mCurrentPlayer) + 1) % NUMBER_OF_PLAYERS);
		setImage(mView.getTurnView(),mCurrentPlayer.getType() == Type.HUMAN ?
				R.drawable.soldier_player :  R.drawable.soldier_pc);
		highlightTurn();

		if (mCurrentPlayer.isMachine()) {
			rollDice();
		} else {
			mView.getDiceView().setEnabled(true);
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
		ImageView view = new ImageView(mView);
		setImage(view, resource);
		return view;
	}

	@SuppressLint("NewApi")
	private void initObstacles() {
		for (Position pos : mBoard.getPositions()) {
			if (pos != null) {
				if (pos.getObstacle() == Obstacle.BAZOOKA) {
					pos.setView(generateImageView(R.drawable.img_bazooka));
					drawImage(pos.getPoint(), pos.getView());
				}
			}
		}
	}

	/***
	 * draw a given ImageView object to a specific view location
	 * @param pos Position object with x and y values
	 * @param img ImageView object 
	 */
	private void drawImage(final Point point, final ImageView img) {//to pass imageSize ass argument for more generic method
		final int imageSize = 30;
		final RelativeLayout layout = (RelativeLayout) mView
				.findViewById(R.id.layoutGameBoard);
		img.setMaxHeight(imageSize);
		img.setMaxWidth(imageSize);
		setImagePosition(img, point);
		layout.post(new Runnable() {

			@Override
			public void run() {
				fireFadeInAnimation(img, ANIMATION_DELAY * 2);
				layout.addView(img);
				Log.i("Worms", "Added bazooka to coordinate: " + point);
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
		int square = mBoard.generateWeapon();
		if (square == -1) {// weapon was not added
			return;
		}

		ImageView view = generateImageView(R.drawable.img_bazooka);
		drawImage(mBoard.getPointnAt(square), view);
		mBoard.getPositionAt(square).setView(view);
		playSound(R.raw.sound_teleport);
	}

	private void highlightTurn() {
		final TextView txtTurn = (TextView) mView.findViewById(R.id.txtBoard);
		txtTurn.setTextColor(Color.RED);
		txtTurn.setText(String.format("%s, Your turn!",
				mCurrentPlayer.getName()));
		txtTurn.postDelayed(new Runnable() {

			@Override
			public void run() {
				txtTurn.setTextColor(Color.BLACK);
			}
		}, 250);
	}

	public void movePlayer(final Player player, int steps) {
		final ImageView currentView = mView.getPlayerView(player.getType());		
		final int basePosition = player.getPosition(); 
		player.setPosition(player.getPosition() + steps);		
		final int currentPosition = player.getPosition();
		
		mView.getHandler().post(new Runnable() {

			@Override
			public void run() {
				Log.i("Worms", "moving player " + player.getName() + " to "+ player.getPosition());
				try {
					fireImageMovementAnimation(currentView, basePosition, currentPosition);
				} catch (InterruptedException e1) {}

					if (handlePositionAction(player)) {
						return;
					}

				player.notifyObservers();
			}
		});
	}

	public void addPlayer(Player p) {
		p.addObserver(this);
		this.mPlayers.add(p);
	}

	public void removePlayer(Player p) {
		this.mPlayers.remove(p);
	}

	private void playSound(int id) {
		mSoundPool.load(mView, id, 1);
	}

	private int getResourceId(String path) {
		int id = mView.getResources().getIdentifier(path, null,
				mView.getPackageName());
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
		if (distance == 0)
			return;
		final int delay = ANIMATION_DELAY / distance;
		int delta = (toPosition - fromPosition) / distance;
		int tempPosition = fromPosition;
		
		for (int i = 0;i < distance;i++){
			tempPosition += delta;
			final int newTempPosition = tempPosition;// final attribute needed to use inside postDelayed
			final int destX = mBoard.getPointnAt(tempPosition).x;
			final int destY = mBoard.getPointnAt(tempPosition).y;
			mView.getHandler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					img.animate().x(destX).setDuration(delay);
					img.animate().y(destY).setDuration(delay);
					setImageDirection(img ,newTempPosition);
				}
			}, delay * i);
		}
	}

	private void setImageDirection(ImageView img, int tempPosition) {// FUCKING SWEET TESTICLES <3 <3 <3 <3 <3 
		if (img == null) 
			return;
		if ((Math.ceil((double)tempPosition / (double)mBoard.getColumsNumber())) % 2 == 0){
			switch ((Integer)img.getTag()){ 
				case R.drawable.soldier_pc_fliped:
					setImage(img, R.drawable.soldier_pc);
					img.setTag(R.drawable.soldier_pc);
					break;
				case R.drawable.soldier_player_fliped:
					setImage(img, R.drawable.soldier_player);
					img.setTag(R.drawable.soldier_player);
					break;
				default:
			}	
		}
		else {
			switch ((Integer)img.getTag()){ 
				case R.drawable.soldier_pc:
					setImage(img, R.drawable.soldier_pc_fliped);
					img.setTag(R.drawable.soldier_pc_fliped);
					break;
				case R.drawable.soldier_player:
					setImage(img, R.drawable.soldier_player_fliped);
					img.setTag(R.drawable.soldier_player_fliped);
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
		this.mCurrentPlayer = p;
	}

	@SuppressLint("NewApi")
	@Override
	public void update(Observable observable, Object data) {
		if (mCurrentPlayer.getPosition() == Board.getFinalPosition()) {
			playSound(R.raw.sound_victory);
		}
		mView.getHandler().postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					switchPlayer();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, TURN_DELAY);
	}

	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		soundPool.play(sampleId, 1, 1, 0, 0, 1);
	}
	
	private void vibrate(final int duration,int delay) {

		mView.getHandler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Vibrator v = (Vibrator) mView
						.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(duration);
			}
		}, delay);
		
	}
}
