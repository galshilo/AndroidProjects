package com.project.laddersandworms.entities;
import java.util.Observable;
import java.util.Random;

import android.util.Log;

public class Player extends Observable {
	
	final int MAX_HIT_MOVES = 30;
	
	public enum Type { HUMAN, MACHINE }
	private String name;
	private int position;
	private Type type;
	private Random rand;
	
	public Player(String name, Type type){
		this.position = 1;
		this.name = name;
		this.type = type;
		this.rand = new Random();
	}
	
	public int hitRival(){
		return -(this.rand.nextInt(MAX_HIT_MOVES)+1);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}
	
	public Type getType(){
		return this.type;
	}
	
	public void setType(Type type){
		this.type = type;
	}

	public void setPosition(final int position) {
		if (position > Board.getFinalPosition()){
			Log.i("Worms", "Trying to get to outer position: " + position);
			this.position = Board.getFinalPosition();
			Log.i("Worms", "Postion was set to :" + Board.getFinalPosition());
		}else if (position <= 1){
				this.position = 1;
		}else {
			this.position = position;
		}
		setChanged();
	}
	
	public boolean isMachine(){
		return this.type == Type.MACHINE;
	}

}
