package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Piece.Color;
import com.mygdx.game.Piece.Type;

public class Rook extends Piece{
	private Board board;
	
	//constructor
	public Rook(Texture texture, Color color, Board board, boolean firstMove) {
		super(texture, color, board);
		setFirstMove(firstMove);
		setType(Type.ROOK);
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
		boolean oldFirstMove = getFirstMove();
		//if nothing blocking piece and didn't double-click
		if (checkPath(rowStart, rowEnd, colStart, colEnd) && (rowStart != rowEnd || colEnd != colStart)) {
			if (Chess.getTurn() == 1 && getColor() == Piece.Color.WHITE){
				moveAndDelete(rowEnd, colEnd, rowStart, colStart, false);
				if (board.checkCheck(board.getKingWhiteRow(), board.getKingWhiteColumn(), Piece.Color.BLACK)){
					moveAndDelete(rowStart, colStart, rowEnd, colEnd, oldFirstMove);
					board.getPieceAt(rowStart, colStart).setHasMoved(false);
				}
			}
			if (Chess.getTurn() == 0 && getColor() == Piece.Color.BLACK){
				moveAndDelete(rowEnd, colEnd, rowStart, colStart, false);
				if (board.checkCheck(board.getKingBlackRow(), board.getKingBlackColumn(), Piece.Color.WHITE)){
					moveAndDelete(rowStart, colStart, rowEnd, colEnd, oldFirstMove);
					if (getPieceToKill() != null){
						board.setPieceAt(getPieceToKill(), rowEnd, column);
					}
					board.getPieceAt(rowStart, colStart).setHasMoved(false);
				}
			}
		} else {
			if ((Chess.getTurn() == 1 && getColor() == Piece.Color.WHITE) || (Chess.getTurn() == 0 && getColor() == Piece.Color.BLACK)){
				checkCastle(rowEnd, colEnd);
				if (getColor() == Piece.Color.WHITE){
					if (board.checkCheck(board.getKingWhiteRow(), board.getKingWhiteColumn(), Piece.Color.BLACK)){									
						board.setKingWhiteRow(rowStart);
						board.setKingWhiteColumn(colStart - 1);
						
						board.setPieceAt(new Rook(getTexture(), getColor(), Chess.getBoard(), true), rowEnd, colEnd + 1);
						board.setPieceAt(new King(new Texture("King White.png"), getColor(), Chess.getBoard(), true), rowStart, colStart - 1);
					
						board.deletePieceAt(rowStart, colStart);
						board.deletePieceAt(rowEnd, colEnd);
						
						board.getPieceAt(rowEnd, colEnd - 1).setHasMoved(false);	
					}
				} else {
					if (board.checkCheck(board.getKingBlackRow(), board.getKingBlackColumn(), Piece.Color.WHITE)){				
						board.setKingBlackRow(rowStart);
						board.setKingBlackColumn(colStart + 1);
						
						board.setPieceAt(new Rook(getTexture(), getColor(), Chess.getBoard(), true), rowEnd, colEnd - 1);
						board.setPieceAt(new King(new Texture("King Black.png"), getColor(), Chess.getBoard(), true), rowStart, colEnd + 1);

						board.deletePieceAt(rowStart, colStart);
						board.deletePieceAt(rowEnd, colEnd);
						
						board.getPieceAt(rowEnd, colEnd - 1).setHasMoved(false);	
					}		
				}
			}
		}
		if (board.getPieceAt(rowEnd, colEnd) != null && board.getPieceAt(rowEnd, colEnd).getHasMoved() == true){
			Chess.checkWin(getColor());		
		}
	}
	
	//checks if move is valid
	public boolean checkPath(int rowStart, int rowEnd, int colStart, int colEnd) {
		if (rowStart != rowEnd && colStart != colEnd){
			return false;
		} else if (rowStart == rowEnd){
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
		if (board.getPieceAt(rowEnd, colEnd) != null && board.getPieceAt(rowEnd, colEnd).getColor() == getColor())
			return false;
		return true;
	}
	
	//creates new piece and deletes old one
	public void moveAndDelete(int newRow, int newColumn, int oldRow, int oldColumn, boolean firstMove) {
		//stores the piece that will be killed
		if (board.getPieceAt(newRow, newColumn) != null && board.getPieceAt(newRow, newColumn).getColor() == getOppositeColor()){
			setPieceToKill(board.getPieceAt(newRow, newColumn));
		}
				
		//draws new piece
		board.setPieceAt(new Rook(getTexture(), getColor(), Chess.getBoard(), firstMove), newRow, newColumn);	
						
		//deleted old piece
		board.deletePieceAt(oldRow, oldColumn);
		
		board.getPieceAt(newRow, newColumn).setHasMoved(true);
	}
	
	//checks and moves castle
	public void checkCastle(int row, int column) {

		if (board.getPieceAt(row, column) != null && board.getPieceAt(row, column).getType() == Piece.Type.KING && board.getPieceAt(row, column).getFirstMove() == true
			&& getColor() == Piece.Color.WHITE
			&& getFirstMove() == true && board.getPieceAt(row, column + 1) == null
			&& board.getPieceAt(row, column + 2) == null && board.getPieceAt(row, column).getColor() == Piece.Color.WHITE){

			board.setKingWhiteRow(row);
			board.setKingWhiteColumn(column + 2);
			
			board.setPieceAt(new Rook(getTexture(), getColor(), Chess.getBoard(), false), row, column + 1);
			board.setPieceAt(new King(new Texture("King White.png"), getColor(), Chess.getBoard(), false), row, column + 2);

			board.deletePieceAt(getRow(), getColumn());
			board.deletePieceAt(row, column);
			
			board.getPieceAt(row, column + 1).setHasMoved(true);
		} else {
			if (board.getPieceAt(row, column) != null && board.getPieceAt(row, column).getType() == Piece.Type.KING && board.getPieceAt(row, column).getFirstMove() == true
				&& getColor() == Piece.Color.BLACK
				&& getFirstMove() == true && board.getPieceAt(row, column - 1) == null
				&& board.getPieceAt(row, column - 2) == null && board.getPieceAt(row, column).getColor() == Piece.Color.BLACK){
				
				board.setKingBlackRow(row);
				board.setKingBlackColumn(column - 2);
				
				board.setPieceAt(new Rook(getTexture(), getColor(), Chess.getBoard(), false), row, column - 1);
				board.setPieceAt(new King(new Texture("King Black.png"), getColor(), Chess.getBoard(), false), row, column - 2);

				board.deletePieceAt(getRow(), getColumn());
				board.deletePieceAt(row, column);
				
				board.getPieceAt(row, column - 1).setHasMoved(true);
			}
		}
		board.setCastleInProgress(true);
	}
}
