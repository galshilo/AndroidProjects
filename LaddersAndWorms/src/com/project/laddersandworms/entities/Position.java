package com.project.laddersandworms.entities;

import java.util.Random;
import android.graphics.Point;
import android.view.View;
import android.widget.ImageView;

public class Position {

	private final int MAX_LEVEL = 3;
	private final int DIFFICULTY = 3;
	
	private Point point;
	private Obstacle obstacle;
	private Random rand;
	private ImageView view;

	public Position(int x, int y, int level) {
		this.rand = new  Random();
		this.point = new Point(x, y);
		int randomObstacle = rand.nextInt((MAX_LEVEL + 1 - level)
				* DIFFICULTY);
		this.obstacle = (randomObstacle == 0) ? Obstacle.BAZOOKA : Obstacle.NONE;
	}
	
	public void setView(ImageView view){
		this.view = view;
	}
	
	public ImageView getView(){
		return this.view;
	}
	
	public Obstacle getObstacle(){
		return this.obstacle;
	}
	
	public void setPoint(final int x, final int y){
		this.point.set(x, y);
	}
	
	public void setObstacle(Obstacle obs){
		this.obstacle = obs;
	}
	
	public Point getPoint(){
		return this.point;
	}
	
	public void freeObstacle(){
		this.view.setVisibility(View.GONE);
		this.view.setImageDrawable(null);
		setView(null);
		setObstacle(Obstacle.NONE);
	}

}
