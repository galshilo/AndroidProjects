package com.project.laddersandworms;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.project.laddersandworms.controller.Game;
import com.project.laddersandworms.entities.InvalidPlayerException;
import com.project.laddersandworms.entities.Player;

public class MainActivity extends Activity implements OnClickListener {

	private ImageView imageViewDice, imageViewPlayerSoldier, imageViewPlayerPC, imageViewTurn;
	private Handler handler;
	private Game _controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		handler = new Handler();
		imageViewDice = (ImageView) findViewById(R.id.imageViewDice);
		imageViewPlayerSoldier = (ImageView) findViewById(R.id.imageViewPlayer);
		imageViewPlayerPC = (ImageView) findViewById(R.id.imageViewPC);
		imageViewTurn = (ImageView) findViewById(R.id.imageViewTurn);;
		imageViewDice.setOnClickListener(this);
		_controller = Game.getInstance();
		_controller.setView(this);
		try {
			_controller.init();
		} catch (InvalidPlayerException e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public Handler getHandler(){
		return this.handler;
	}
	
	public ImageView getPlayerView(Player.Type type) {
		switch (type) {
		case MACHINE:
			return this.imageViewPlayerPC;
		case HUMAN:
			return this.imageViewPlayerSoldier;
		default:
			return null;
		}
	}

	public ImageView getDiceView() {
		return this.imageViewDice;
	}
	
	public ImageView getTurnView(){
		return this.imageViewTurn;
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imageViewDice) {

			try {
				_controller.rollDice();

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvalidPlayerException e) {
				e.printStackTrace();
}
		}
	}
}
