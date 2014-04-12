package com.project.laddersandworms.entities;

import java.util.Random;


public class Dice {
	
	private final int 			NUMBER_OF_SOUNDS = 4;
	private final int			MAX_DICE_VALUE = 6;
	private int 				mValue;
	private Random 				mRand;
	
	public Dice(){
		this.mRand = new Random();
	}
	
	public void roll(){
    	this.mValue = mRand.nextInt(MAX_DICE_VALUE) + 1;
	}
	
	public int getValue(){
		return this.mValue;
	}
	
	public int getSound(){
		return (mRand.nextInt(NUMBER_OF_SOUNDS + 1) % NUMBER_OF_SOUNDS) + 1;
	}

}
