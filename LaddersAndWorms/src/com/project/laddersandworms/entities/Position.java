package com.project.laddersandworms.entities;

import android.graphics.Point;
import android.view.View;
import android.widget.ImageView;

public class Position {
	
	private int 				mId;
	private Point 				mPoint;
	private Obstacle			mObstacle;
	private ImageView 			mView;

	public Position(int id, int x, int y) {
		this.mId = id;
		this.mPoint = new Point(x, y);
		this.mObstacle = Obstacle.NONE;
	}
	
	public int getId(){
		return this.mId;
	}
	
	public void setView(ImageView view){
		this.mView = view;
	}
	
	public ImageView getView(){
		return this.mView;
	}
	
	public Obstacle getObstacle(){
		return this.mObstacle;
	}
	
	public void setPoint(final int x, final int y){
		this.mPoint.set(x, y);
	}
	
	public void setObstacle(Obstacle obs){
		this.mObstacle = obs;
	}
	
	public Point getPoint(){
		return this.mPoint;
	}
	
	public void freeObstacle(){
		this.mView.setVisibility(View.GONE);
		this.mView.setImageDrawable(null);
		setView(null);
		setObstacle(Obstacle.NONE);
	}

}
