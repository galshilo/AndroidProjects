package com.project.laddersandworms.entities;
import java.util.Observable;

public class Player extends Observable {
	
	public enum Type { HUMAN, MACHINE }
	private String name;
	private int position;
	private Type type;
	
	public Player(String name, Type type){
		this.position = 1;
		this.name = name;
		this.type = type;
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
		this.position = position;
		setChanged();
	}
	
	public boolean isMachine(){
		return this.type == Type.MACHINE;
	}

}
