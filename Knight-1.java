package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Piece.Color;
import com.mygdx.game.Piece.Type;

public class Knight extends Piece{
	private Board board;
	
	//constructor
	public Knight(Texture texture, Color color, Board board) {
		super(texture, color, board);
		setType(Type.KNIGHT);
		this.board = board;
	}

	//main move method
	@Override
	public void move(int row, int column) {
		Chess.checkWin(getOppositeColor());		
		int rowStart = getRow();
		int rowEnd = row;
		int colStart = getColumn();
		int colEnd = column;
		//if nothing blocking piece and didn't double-click
		if (checkPath(rowStart, rowEnd, colStart, colEnd) && (rowStart != rowEnd || colEnd != colStart)) {
			if (Chess.getTurn() == 1 && getColor() == Piece.Color.WHITE){
				moveAndDelete(rowEnd, colEnd, rowStart, colStart);
				if (board.checkCheck(board.getKingWhiteRow(), board.getKingWhiteColumn(), Piece.Color.BLACK)){
					moveAndDelete(rowStart, colStart, rowEnd, colEnd);
					if (getPieceToKill() != null){
						board.setPieceAt(getPieceToKill(), rowEnd, colEnd);
					}
					board.getPieceAt(rowStart, colStart).setHasMoved(false);
				}
			}
			if (Chess.getTurn() == 0 && getColor() == Piece.Color.BLACK){
				moveAndDelete(rowEnd, colEnd, rowStart, colStart);
				if (board.checkCheck(board.getKingBlackRow(), board.getKingBlackColumn(), Piece.Color.WHITE)){
					moveAndDelete(rowStart, colStart, rowEnd, colEnd);
					if (getPieceToKill() != null){
						board.setPieceAt(getPieceToKill(), rowEnd, colEnd);
					}
					board.getPieceAt(rowStart, colStart).setHasMoved(false);
				}
			}
		}
		if (board.getPieceAt(rowEnd, colEnd) != null && board.getPieceAt(rowEnd, colEnd).getHasMoved() == true){
			Chess.checkWin(getColor());		
		}
	}
		
	//checks if move is valid
	public boolean checkPath(int rowStart, int rowEnd, int colStart, int colEnd) {
		if (board.getPieceAt(rowEnd, colEnd) != null && board.getPieceAt(rowEnd, colEnd).getColor() == getColor())
			return false;
		if (rowEnd == rowStart + 1 || rowEnd == rowStart - 1){
			if (colEnd == colStart + 2 || colEnd == colStart - 2){
				return true;
			}
		} else {
			if (rowEnd == rowStart + 2 || rowEnd == rowStart - 2){
				if (colEnd == colStart + 1 || colEnd == colStart - 1){
					return true;
				}
			}
		}
		return false;
	}
	
	//creates new piece and deletes old one
	public void moveAndDelete(int newRow, int newColumn, int oldRow, int oldColumn) {
		//stores the piece that will be killed
		if (board.getPieceAt(newRow, newColumn) != null && board.getPieceAt(newRow, newColumn).getColor() == getOppositeColor()){
			setPieceToKill(board.getPieceAt(newRow, newColumn));
		}
			
		//draws new piece
		board.setPieceAt(new Knight(getTexture(), getColor(), Chess.getBoard()), newRow, newColumn);
			
		//deleted old piece
		board.deletePieceAt(oldRow, oldColumn);
		
						
		board.getPieceAt(newRow, newColumn).setHasMoved(true);
	}	
}
