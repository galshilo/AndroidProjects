package com.project.laddersandworms;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity implements OnClickListener {

	private Button 				mBtnNewGame;
	private Button 				mBtnOptions;
	private Button 				mBtnScores;
	private MediaPlayer			mMediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		bindView();
		playSoundtrack();
	}

	private void bindView() {
		mBtnNewGame = (Button) findViewById(R.id.btnNewGame);
		mBtnOptions = (Button) findViewById(R.id.btnOptions);
		mBtnScores = (Button) findViewById(R.id.btnScores);
		mBtnNewGame.setOnClickListener(this);
		mBtnOptions.setOnClickListener(this);
		mBtnScores.setOnClickListener(this);
	}

	private void playSoundtrack() {
		if (mMediaPlayer == null){
			mMediaPlayer = MediaPlayer.create(this, R.raw.sound_menu_soundtrack);
		}
		
		if (mMediaPlayer.isPlaying()) {
			return;
		}

		mMediaPlayer.setLooping(true);
		mMediaPlayer.start();
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

	private void releaseSoundtrack() {
		if (mMediaPlayer != null){
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		releaseSoundtrack();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	private void switchActivity(Class<?> cls){
		Intent intent = new Intent(this, cls);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNewGame:
			switchActivity(MainActivity.class);
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mBtnNewGame.setBackgroundResource(R.drawable.btn_newgame_hl);
			mBtnNewGame.postDelayed(new Runnable() {
				@Override
				public void run() {
					mBtnNewGame.setBackgroundResource(R.drawable.btn_newgame);
				}
			}, 250);
			break;
		case R.id.btnOptions:
			switchActivity(OptionsActivity.class);
			mBtnOptions.setBackgroundResource(R.drawable.btn_options_hl);
			mBtnOptions.postDelayed(new Runnable() {
				@Override
				public void run() {
					mBtnOptions.setBackgroundResource(R.drawable.btn_options);
				}
			}, 250);
			break;
		case R.id.btnScores:
			mBtnScores.setBackgroundResource(R.drawable.btn_scores_hl);
			mBtnScores.postDelayed(new Runnable() {
				@Override
				public void run() {
					mBtnScores.setBackgroundResource(R.drawable.btn_scores);
				}
			}, 250);
			break;
		default:
		}

	}
}
