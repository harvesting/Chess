package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class Pawn extends Piece {
	private Board board;

	//constructor
	public Pawn(Texture texture, Color color, Board board, boolean firstMove) {
		super(texture, color, board);
		setFirstMove(firstMove);
		setType(Type.PAWN);
		this.board = board;
	}

	//main move method
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
		}
		if (board.getPieceAt(rowEnd, colEnd) != null && board.getPieceAt(rowEnd, colEnd).getHasMoved() == true){
			Chess.checkWin(getColor());		
		}
	}
	
	//checks if move is valid
	public boolean checkPath(int rowStart, int rowEnd, int colStart, int colEnd){
		//1 space forward
		if (rowStart - 1 == rowEnd && colStart == colEnd && board.getPieceAt(rowEnd, colEnd) == null){
			return true;
		}
		//2 spaces forward
		if (rowStart - 2 == rowEnd && colStart == colEnd && getFirstMove() == true && board.getPieceAt(rowEnd, colEnd) == null && board.getPieceAt(rowEnd + 1, colEnd) == null){
			return true;
		}
		//diagnol
		if (rowStart - 1 == rowEnd && (colStart + 1 == colEnd || colStart - 1 == colEnd) && 
			(board.getPieceAt(rowEnd, colEnd) != null && board.getPieceAt(rowEnd, colEnd).getColor() == getOppositeColor())){
			return true;
		}
		return false;
	}
		
	//creates new piece and deletes old one
	public void moveAndDelete(int newRow, int newColumn, int oldRow, int oldColumn, boolean firstMove) {
		
		//drawMovingPiece(oldRow, newRow, oldColumn, newColumn);
		
		//stores the piece that will be killed
		if (board.getPieceAt(newRow, newColumn) != null && board.getPieceAt(newRow, newColumn).getColor() == getOppositeColor()){
			setPieceToKill(board.getPieceAt(newRow, newColumn));
		}
				
		//draws new piece
		board.setPieceAt(new Pawn(getTexture(), getColor(), Chess.getBoard(), firstMove), newRow, newColumn);
		
		
		
		//deletes old piece
		board.deletePieceAt(oldRow, oldColumn);		
				
		board.getPieceAt(newRow, newColumn).setHasMoved(true);
		

		
		if (getColor() == Piece.Color.WHITE && newRow == 0){
			PawnGUI gui = new PawnGUI(new Texture("PawnGUIWhite.png"), getColor(), getColumn());
		}
		if (getColor() == Piece.Color.BLACK && newRow == 7){
			PawnGUI gui = new PawnGUI(new Texture("PawnGUIBlack.png"), getColor(), getColumn());
		}
		
	//	System.out.println(board.getPieceAt(newRow, newColumn).getHasMoved());

	}
	
	
}
