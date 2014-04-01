package com.project.laddersandworms;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.project.laddersandworms.controller.GameController;
import com.project.laddersandworms.entities.InvalidPlayerException;
import com.project.laddersandworms.entities.Player;
import com.project.laddersandworms.entities.Player.Type;

public class MainActivity extends Activity implements OnClickListener {

	private ImageView imageViewDice, imageViewPlayerSoldier, imageViewPlayerPC, imageViewTurn;
	private GameController _controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageViewDice = (ImageView) findViewById(R.id.imageViewDice);
		imageViewPlayerSoldier = (ImageView) findViewById(R.id.imageViewPlayer);
		imageViewPlayerPC = (ImageView) findViewById(R.id.imageViewPC);
		imageViewTurn = (ImageView) findViewById(R.id.imageViewTurn);
		imageViewDice.setOnClickListener(this);
		_controller = GameController.getInstance();
		_controller.setView(this);
		_controller.init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
