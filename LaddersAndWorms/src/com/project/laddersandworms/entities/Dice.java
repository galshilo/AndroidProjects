package com.project.laddersandworms.entities;

import java.util.Random;


public class Dice {
	
	private final int NUMBER_OF_SOUNDS = 4;
	private final int MAX_DICE_VALUE = 6;
	
	private Random rand;
	
	public Dice(){
		this.rand = new Random();
	}
	
	public int roll(){
    	return  rand.nextInt(MAX_DICE_VALUE) + 1;
	}
	
	public int getSound(){
		return (rand.nextInt(NUMBER_OF_SOUNDS + 1) % NUMBER_OF_SOUNDS) + 1;
	}

}
