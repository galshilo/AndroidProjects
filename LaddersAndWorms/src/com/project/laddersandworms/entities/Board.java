package com.project.laddersandworms.entities;

import java.util.Random;

import com.project.laddersandworms.controller.Game;

import android.graphics.Point;

public class Board {
	private static final int BOARD_ROWS = 10;
	private static final int BOARD_COLUMNS = 7;
	private final int BOARD_SQUARE_RATIO_X = 110;
	private final int BOARD_SQUARE_RATIO_Y = 90;
	private final int MIDDLE_SQUARE_X = 20;
	private final int MIDDLE_SQUARE_Y = 17;
	private final int DIFFECULTY_RATIO = 20;
	private Position[] board;
	private Game.Level level;
	private Random rand;

	public Board(final Game.Level level) {
		this.board = new Position[BOARD_ROWS * BOARD_COLUMNS + 1];
		this.level = level;
		this.rand = new Random();
		initBoard();
		initWeapon();
	}

	public void initBoard() {
		int counter = 1;
		for (int y = 1; y <= BOARD_ROWS; y++) {
			for (int x = 0; x < BOARD_COLUMNS; x++) {
				board[counter] = new Position(counter, y % 2 == 1 ? x
						* BOARD_SQUARE_RATIO_X + MIDDLE_SQUARE_X
						: ((BOARD_COLUMNS - 1) * BOARD_SQUARE_RATIO_X)
								- (x * BOARD_SQUARE_RATIO_X) + MIDDLE_SQUARE_X,
						(BOARD_ROWS * BOARD_SQUARE_RATIO_Y)
								- (y * BOARD_SQUARE_RATIO_Y) + MIDDLE_SQUARE_Y);
				counter++;
			}
		}

	}

	private void initWeapon() {
		for (int i = 0; i < getFinalPosition() ; i++) {
			generateWeapon();
		}
	}

	public int generateWeapon() {
		if (rand.nextInt((int) (DIFFECULTY_RATIO / level.getNumVal())) == 0) {
			int position = rand.nextInt((BOARD_ROWS * BOARD_COLUMNS) - 1) + 2;
			if (board[position].getObstacle() == Obstacle.NONE) {
				board[position].setObstacle(Obstacle.BAZOOKA);
			}
			return position;
		}
		return -1;
	}

	public int getColumsNumber(){
		return BOARD_COLUMNS;
	}
	
	public static int getFinalPosition() {
		return BOARD_ROWS * BOARD_COLUMNS;
	}

	public Position getPositionAt(int posNum) {
		return board[posNum];
	}

	public Point getPointnAt(int posNum) {
		return board[posNum].getPoint();
	}

	public Position[] getPositions() {
		return board;
	}
}
