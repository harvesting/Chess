package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class RedSquare extends Piece{
	
	//constructor
	public RedSquare(Texture texture, Color color, Board board) {
		super(texture, color, board);
		setType(Piece.Type.RED_SQUARE);
	}
	
	//move method
	@Override
	public void move(int row, int column) {
		setRow(row);
		setColumn(column);
	}

	//unused
	@Override
	public boolean checkPath(int rowStart, int rowEnd, int colStart, int colEnd) {
		return false;
	}

}
