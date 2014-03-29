package com.project.laddersandworms.controller;

import java.util.ArrayList;
import java.util.Random;

import android.media.MediaPlayer;
import android.widget.ImageView;

import com.project.laddersandworms.MainActivity;
import com.project.laddersandworms.entities.Dice;
import com.project.laddersandworms.entities.Player;

public class GameController {

	private final String RAW_IMAGE = "drawable/dice_";
	private final String RAW_SOUNDS = "raw/sound_dice_";
	private static GameController _instrance = null;

	private Dice _dice;
	private ArrayList<Player> _players;
	private MainActivity _view;

	private GameController() {
		this._players = new ArrayList<Player>(2);
		this._dice = new Dice();
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

	public void rollDice() throws InterruptedException {
		final int DICE_SPINS = 6;
		int soundResource = getResourceId(RAW_SOUNDS + _dice.getSound());
		playSound(soundResource);

		new Thread() {
			public void run() {
				for (int i = 0; i < DICE_SPINS; i++) {
					try {
						Thread.sleep(100);
						_view.getDiceView().post(new Runnable() {
							@Override
							public void run() {
								_view.getDiceView()
										.setImageResource(
												getResourceId(RAW_IMAGE
														+ _dice.roll()));
							}
						});
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();

		_view.getDiceView().postDelayed(new Runnable() {
			@Override
			public void run() {
				_view.getDiceView().setImageResource(
						getResourceId(RAW_IMAGE + _dice.roll()));
			}
		}, 700);
	}

	public void addPlayer(Player p) {
		this._players.add(p);
	}

	public void removePlayer(Player p) {
		this._players.remove(p);
	}

	private void playSound(int id) {
		MediaPlayer player = MediaPlayer.create(_view, id);
		player.start();
	}

	private int getResourceId(String path) {
		int id = _view.getResources().getIdentifier(path, null,
				_view.getPackageName());
		return id;
	}

	public void setImagePosition(ImageView img, float x, float y) {
		img.setX(x);
		img.setY(y);
	}

}
