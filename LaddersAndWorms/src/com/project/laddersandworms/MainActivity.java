package com.project.laddersandworms;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.project.laddersandworms.controller.Game;
import com.project.laddersandworms.entities.Player;

public class MainActivity extends Activity implements OnClickListener {

	private ImageView			mIvDice;
	private ImageView 			mIvPlayerSoldier;
	private ImageView 			mIvPlayerPC;
	private ImageView			mIvTurn;
	private Handler 			mHandler;
	private MediaPlayer 		mMediaPlayer;
	private Game 				mController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.mHandler = new Handler();
		bindView();
		playSoundtrack();
		mController = new Game();
		mController.setView(this);
		mController.init();
	}

	private void bindView() {
		mIvDice = (ImageView) findViewById(R.id.imageViewDice);
		mIvPlayerSoldier = (ImageView) findViewById(R.id.imageViewPlayer);
		mIvPlayerSoldier.setTag(R.drawable.soldier_player);
		mIvPlayerPC = (ImageView) findViewById(R.id.imageViewPC);
		mIvPlayerPC.setTag(R.drawable.soldier_pc);
		mIvTurn = (ImageView) findViewById(R.id.imageViewTurn);
		mIvDice.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		playSoundtrack();
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseSoundtrack();
	}

	@Override
	protected void onStop() {
		super.onStop();
		releaseSoundtrack();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void playSoundtrack() {
		if (mMediaPlayer == null){
			mMediaPlayer = MediaPlayer.create(this, R.raw.sound_gameplay);
		}
		
		if (mMediaPlayer.isPlaying()) {
			return;
		}

		mMediaPlayer.setLooping(true);
		mMediaPlayer.start();
	}

	private void releaseSoundtrack() {
		if (mMediaPlayer != null){
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
	
	public Handler getHandler() {
		return this.mHandler;
	}

	public ImageView getPlayerView(Player.Type type) {
		switch (type) {
		case MACHINE:
			return this.mIvPlayerPC;
		case HUMAN:
			return this.mIvPlayerSoldier;
		default:
			return null;
		}
	}

	public ImageView getDiceView() {
		return this.mIvDice;
	}

	public ImageView getTurnView() {
		return this.mIvTurn;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.imageViewDice:
			this.mIvDice.setEnabled(false);
			try {
				mController.rollDice();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
