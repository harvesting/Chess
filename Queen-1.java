package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Piece.Color;
import com.mygdx.game.Piece.Type;

public class Queen extends Piece{
	private Board board;
	
	//constructor
	public Queen(Texture texture, Color color, Board board) {
		super(texture, color, board);
		setType(Type.QUEEN);
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
		boolean isDiangol = false;
		for (int i = 0; i < 8; i++){
			if (rowStart + i == rowEnd){
				if (colStart + i == colEnd){
					isDiangol = true;
				} else if (colStart - i == colEnd){
					isDiangol = true;				}
			} else if (rowStart - i == rowEnd){
				if (colStart + i == colEnd){
					isDiangol = true;
					} else if (colStart - i == colEnd){
					isDiangol = true;
				}	
			}
		}		
		//horizontal/vertical movement
		if (rowStart == rowEnd || colStart == colEnd){			
			if (rowStart == rowEnd){
				if (colStart < colEnd){
					for (int i = colStart + 1; i < colEnd; i++){
						if (board.getPieceAt(rowStart, i) != null) 
							return false;				
					}
				} else {
					if (colStart > colEnd){
						for (int i = colEnd + 1; i < colStart; i++){
							if (board.getPieceAt(rowStart, i) != null) 
								return false;
						}
					} 
				}		
			} else if (colStart == colEnd){
				if (rowStart < rowEnd){
					for (int i = rowStart + 1; i < rowEnd; i++){
						if (board.getPieceAt(i, colStart) != null) 
							return false;				
					}
					
				} else {
					if (rowStart > rowEnd){
						for (int i = rowEnd + 1; i < rowStart; i++){
							if (board.getPieceAt(i, colStart) != null) 
								return false;
						}				
					} 
				}	
			}
			//diangol movement
		} else if (isDiangol){
			if (rowStart < rowEnd){
				for (int i = 1; i < rowEnd - rowStart - 1; i++){
					if (colStart < colEnd){
						if (board.getPieceAt(rowStart + i, colStart + i) != null)
							return false;
					} else if (colStart > colEnd){
						if (board.getPieceAt(rowStart + i, colStart - i) != null)
							return false;
					}
				}
			} else if (rowStart > rowEnd){
				for (int i = 1; i < rowStart - rowEnd - 1; i++){
					if (colStart < colEnd){
						if (board.getPieceAt(rowStart - i, colStart + i) != null)
							return false;
					} else if (colStart > colEnd){
						if (board.getPieceAt(rowStart - i, colStart - i) != null)
							return false;
					}
				}
			}
		} else 
			return false;
		if (board.getPieceAt(rowEnd, colEnd) != null && board.getPieceAt(rowEnd, colEnd).getColor() == getColor())
			return false;
		return true;
	}
	
	//creates new piece and deletes old one
	public void moveAndDelete(int newRow, int newColumn, int oldRow, int oldColumn) {
		//stores the piece that will be killed
		if (board.getPieceAt(newRow, newColumn) != null && board.getPieceAt(newRow, newColumn).getColor() == getOppositeColor()){
			setPieceToKill(board.getPieceAt(newRow, newColumn));
		}
						
		//draws new piece
		board.setPieceAt(new Queen(getTexture(), getColor(), Chess.getBoard()), newRow, newColumn);
		
		//deleted old piece
		board.deletePieceAt(oldRow, oldColumn);
		
		board.getPieceAt(newRow, newColumn).setHasMoved(true);
	}
}
