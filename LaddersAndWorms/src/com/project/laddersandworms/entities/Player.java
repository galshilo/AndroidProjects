package com.project.laddersandworms.entities;
import java.util.Observable;
import java.util.Random;

import com.project.laddersandworms.controller.Game;

import android.util.Log;

public class Player extends Observable {
	
	private String				mName;
	private int 				mPosition;
	private Type 				mType;
	private Random 				mRand;
	private Game.Level 			mLevel;
	
	public enum Type { HUMAN, MACHINE }

	public Player(String name, Type type, Game.Level level){
		this.mPosition = 1;
		this.mName = name;
		this.mType = type;
		this.mRand = new Random();
		this.mLevel = level;
	}
	
	public int hitRival(){
		return -(this.mRand.nextInt(this.mLevel.getNumVal()*10)+1);
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public int getPosition() {
		return mPosition;
	}
	
	public Type getType(){
		return this.mType;
	}
	
	public void setType(Type type){
		this.mType = type;
	}

	public void setPosition(final int position) {
		if (position > Board.getFinalPosition()){
			Log.i("Worms", "Trying to get to outer position: " + position);
			this.mPosition = Board.getFinalPosition();
			Log.i("Worms", "Postion was set to :" + Board.getFinalPosition());
		}else if (position <= 1){
				this.mPosition = 1;
		}else {
			this.mPosition = position;
		}
		setChanged();
	}
	
	public boolean isMachine(){
		return this.mType == Type.MACHINE;
	}

}
