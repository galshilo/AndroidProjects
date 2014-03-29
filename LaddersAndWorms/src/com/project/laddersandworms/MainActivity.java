package com.project.laddersandworms;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.project.laddersandworms.controller.GameController;

public class MainActivity extends Activity {

	private ImageView imageViewDice, imageViewPlayerSoldier, imageViewPlayerPC;
	private GameController _controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		_controller = GameController.getInstance();
		_controller.setView(this);
		imageViewDice = (ImageView) findViewById(R.id.imageViewDice);
		imageViewPlayerSoldier = (ImageView) findViewById(R.id.imageViewPlayer);
		imageViewPlayerPC = (ImageView) findViewById(R.id.imageViewPC);
		imageViewDice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					_controller.rollDice();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				_controller.setImagePosition(imageViewPlayerPC,
						imageViewPlayerPC.getX() + 104,
						imageViewPlayerPC.getY());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public ImageView getDiceView() {
		return this.imageViewDice;
	}

	public ImageView getPlayerView() {
		return this.imageViewPlayerSoldier;
	}

	public ImageView getPCView() {
		return this.imageViewPlayerPC;
	}
}
