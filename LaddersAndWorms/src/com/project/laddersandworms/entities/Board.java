package com.project.laddersandworms.entities;

public class Board {
	private final int BOARD_ROWS = 10;
	private final int BOARD_COLUMNS = 7;
	private Position[] board;
	private int level;

	public Board(int level) {
		this.board = new Position[BOARD_ROWS * BOARD_COLUMNS + 1];
		this.level = level;
		init();
	}

	public void init() {
		for (int x = 1; x <= BOARD_ROWS; x++) {
			for (int y = 1; y <= BOARD_COLUMNS; y++) {
				board[x * y] = new Position(x, y, this.level);
			}
		}
	}
	
	public void removePlayerFromPosition(int position){
		this.board[position].removePlayer();
	}
	
	public void addPlayerToPosition(Player player, int position){
		this.board[position].addPlayer(player);
	}
	
	public Player getPlayerInPosition(int position){
		return this.board[position].getPlayer();
	}
	
	public int getFinalPosition(){
		return BOARD_ROWS * BOARD_COLUMNS + 1;
	}
}
