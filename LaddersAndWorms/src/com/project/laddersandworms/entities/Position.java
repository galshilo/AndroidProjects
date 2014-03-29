package com.project.laddersandworms.entities;

import java.util.Random;
import android.graphics.Point;

public class Position {

	private final int MAX_LEVEL = 3;
	private final int DIFFECULTY_MULTIPLY = 5;
	
	private Point point;
	private Obstacle obstacle;
	private Player player;
	private Random rand;

	public Position(int x, int y, int level) {
		this.point = new Point(x, y);
		int randomObstacle = rand.nextInt((MAX_LEVEL + 1 - level)
				* DIFFECULTY_MULTIPLY);
		if (randomObstacle < 2)
			this.obstacle = Obstacle.values()[randomObstacle];
		else
			this.obstacle = Obstacle.NONE;
	}
	
	public Obstacle getObstacle(){
		return this.obstacle;
	}
	
	public void setPoint(int x, int y){
		this.point.set(x, y);
	}
	
	public void removePlayer(){
		this.player = null;
	}
	
	public void addPlayer(Player player){
		this.player = player;
	}
	
	public Player getPlayer(){
		return this.player;
	}

}
