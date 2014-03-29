package com.project.laddersandworms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity implements OnClickListener {

	Button btnNewGame, btnOptions, btnScores;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		btnNewGame = (Button) findViewById(R.id.btnNewGame);
		btnOptions = (Button) findViewById(R.id.btnOptions);
		btnScores = (Button) findViewById(R.id.btnScores);
		
		btnNewGame.setOnClickListener(this);
		btnOptions.setOnClickListener(this);
		btnScores.setOnClickListener(this);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.btnNewGame:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			btnNewGame.setBackgroundResource(R.drawable.btn_newgame_hl);			
			btnNewGame.postDelayed(new Runnable() {
				@Override
				public void run() {
					btnNewGame.setBackgroundResource(R.drawable.btn_newgame);					
				}
			}, 250);
			break;
		case R.id.btnOptions:
			btnOptions.setBackgroundResource(R.drawable.btn_options_hl);			
			btnOptions.postDelayed(new Runnable() {
				@Override
				public void run() {
					btnOptions.setBackgroundResource(R.drawable.btn_options);					
				}
			}, 250);			break;
		case R.id.btnScores:
			btnScores.setBackgroundResource(R.drawable.btn_scores_hl);			
			btnScores.postDelayed(new Runnable() {
				@Override
				public void run() {
					btnScores.setBackgroundResource(R.drawable.btn_scores);					
				}
			}, 250);			break;
		default:
		}
		
	}
}
