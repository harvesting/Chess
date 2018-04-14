package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class King extends Piece {
	private Board board;

	//constructor
	public King(Texture texture, Color color, Board board, boolean firstMove) {
		super(texture, color, board);
		setFirstMove(firstMove);
		setType(Type.KING);
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
					if (getPieceToKill() != null){
						board.setPieceAt(getPieceToKill(), rowEnd, colEnd);
					}
					board.getPieceAt(rowStart, colStart).setHasMoved(false);
				}
			}
			if (Chess.getTurn() == 0 && getColor() == Piece.Color.BLACK){
				moveAndDelete(rowEnd, colEnd, rowStart, colStart, false);
				if (board.checkCheck(board.getKingBlackRow(), board.getKingBlackColumn(), Piece.Color.WHITE)){
					moveAndDelete(rowStart, colStart, rowEnd, colEnd, oldFirstMove);
					if (getPieceToKill() != null){
						board.setPieceAt(getPieceToKill(), rowEnd, colEnd);
					}
					board.getPieceAt(rowStart, colStart).setHasMoved(false);
				}
			}
		} else {
			if ((Chess.getTurn() == 1 && getColor() == Piece.Color.WHITE) || (Chess.getTurn() == 0 && getColor() == Piece.Color.BLACK)){
				checkCastle(rowEnd, colEnd);
				if (getColor() == Piece.Color.WHITE){
					if (board.checkCheck(board.getKingWhiteRow(), board.getKingWhiteColumn(), Piece.Color.BLACK)){						
						board.setKingWhiteRow(rowEnd);
						board.setKingWhiteColumn(colEnd - 1);
						
						board.setPieceAt(new Rook(new Texture("Rook White.png"), getColor(), Chess.getBoard(), true), rowStart, colStart + 1);
						board.setPieceAt(new King(getTexture(), getColor(), Chess.getBoard(), true), rowEnd, colEnd - 1);
						
						board.deletePieceAt(rowStart, colStart);
						board.deletePieceAt(rowEnd, colEnd);
						
						board.getPieceAt(rowEnd, colEnd - 1).setHasMoved(false);
					} 				
				} else {
					if (board.checkCheck(board.getKingBlackRow(), board.getKingBlackColumn(), Piece.Color.WHITE)){			
						board.setKingBlackRow(rowEnd);
						board.setKingBlackColumn(colEnd + 1);
						
						board.setPieceAt(new Rook(new Texture("Rook Black.png"), getColor(), Chess.getBoard(), true), rowStart, colStart - 1);
						board.setPieceAt(new King(getTexture(), getColor(), Chess.getBoard(), true), rowEnd, colEnd + 1);

						board.deletePieceAt(rowStart, colStart);
						board.deletePieceAt(rowEnd, colEnd);
						
						//weird exception thrown here
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
		if (board.getPieceAt(rowEnd, colEnd) != null && board.getPieceAt(rowEnd, colEnd).getColor() == getColor())
			return false;
		if (rowStart + 1 == rowEnd){
			if (colStart + 1 == colEnd){
				return true;
			} else 
				if (colStart - 1 == colEnd){
					return true;
				} else {
				if (colStart == colEnd){
					return true;
				}
			}
		} else 
			if (rowStart - 1 == rowEnd){
				if (colStart + 1 == colEnd){
					return true;
				} else 
					if (colStart - 1 == colEnd){
						return true;
					} else {
					if (colStart == colEnd){
						return true;
					}
				}
		} else {
			if (rowStart == rowEnd){
				if (colStart + 1 == colEnd){
					return true;
				} else 
					if (colStart - 1 == colEnd){
						return true;
					} else {
				}
			}
		} 
		return false;
	}
	
	//creates new piece and deletes old one
	public void moveAndDelete(int newRow, int newColumn, int oldRow, int oldColumn, boolean firstMove) {
		if (getColor() == Piece.Color.WHITE){
			board.setKingWhiteRow(newRow);
			board.setKingWhiteColumn(newColumn);
		} else {
			board.setKingBlackRow(newRow);
			board.setKingBlackColumn(newColumn);
		}
		//stores the piece that will be killed
		if (board.getPieceAt(newRow, newColumn) != null && board.getPieceAt(newRow, newColumn).getColor() == getOppositeColor()){
			setPieceToKill(board.getPieceAt(newRow, newColumn));
		}
		
		//draws new piece
		board.setPieceAt(new King(getTexture(), getColor(), Chess.getBoard(), firstMove), newRow, newColumn);
			
		//deleted old piece
		board.deletePieceAt(oldRow, oldColumn);
		
		board.getPieceAt(newRow, newColumn).setHasMoved(true);
	}
	
	//checks and moves castle
	public void checkCastle(int row, int column) {
		if (board.getPieceAt(row, column) != null && board.getPieceAt(row, column).getType() == Piece.Type.ROOK && board.getPieceAt(row, column).getFirstMove() == true
			&& getColor() == Piece.Color.BLACK
			&& board.getPieceAt(row, column + 2) == null && board.getPieceAt(row, column).getColor() == Piece.Color.BLACK
			&& getFirstMove() == true && board.getPieceAt(row, column + 1) == null){
			
			board.setKingBlackRow(row);
			board.setKingBlackColumn(column - 1);
			
			board.setPieceAt(new Rook(new Texture("Rook Black.png"), getColor(), Chess.getBoard(), false), row, column + 2);
			board.setPieceAt(new King(getTexture(), getColor(), Chess.getBoard(), false), row, column + 1);

			board.deletePieceAt(getRow(), getColumn());
			board.deletePieceAt(row, column);
			
			board.getPieceAt(row, column + 1).setHasMoved(true);
		} else {
			if (board.getPieceAt(row, column) != null && board.getPieceAt(row, column).getType() == Piece.Type.ROOK && board.getPieceAt(row, column).getFirstMove() == true
				&& getColor() == Piece.Color.WHITE
				&& board.getPieceAt(row, column - 2) == null && board.getPieceAt(row, column).getColor() == Piece.Color.WHITE
				&& getFirstMove() == true && board.getPieceAt(row, column - 1) == null){
								
				board.setKingWhiteRow(row);
				board.setKingWhiteColumn(column - 1);

				board.setPieceAt(new Rook(new Texture("Rook White.png"), getColor(), Chess.getBoard(), false), row, column - 2);
				board.setPieceAt(new King(getTexture(), getColor(), Chess.getBoard(), false), row, column - 1);
	
				board.deletePieceAt(getRow(), getColumn());
				board.deletePieceAt(row, column);
				
				board.getPieceAt(row, column - 1).setHasMoved(true);
			}
		}
		board.setCastleInProgress(true);
	}
}
