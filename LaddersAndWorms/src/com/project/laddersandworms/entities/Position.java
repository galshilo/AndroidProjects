package com.project.laddersandworms.entities;

import java.util.Random;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class Position {

	
	private int id;
	private Point point;
	private Obstacle obstacle;
	private ImageView view;

	public Position(int id, int x, int y) {
		this.id = id;
		this.point = new Point(x, y);
		this.obstacle = Obstacle.NONE;
	}
	
	public int getId(){
		return this.id;
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
