package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Piece.Color;
import java.util.ArrayList;

public class Board {
	//primitives
	private boolean castleInProgress; //if a castle is being performed
	private int firstClickX, firstClickY; //the x and y coordinates of the first click
	private int secondClickX, secondClickY; //the x and y coordinates of the second click
	private int kingWhiteRow; //white king's row
	private int kingWhiteColumn; //white king's column
	private int kingBlackRow; //black king's row
	private int kingBlackColumn; //black king's column
	//other
	private Texture redSquare;
	private RedSquare clickedPiece;
	private SpriteBatch batch;
	private Piece[][] pieces; //the array which stores all the pieces
	private Texture board;
	
	//constructor for board
	public Board(SpriteBatch batch) {
		redSquare = new Texture("Red Square.png");
		board = new Texture("Chess Board.png");
		this.batch = batch;
		pieces = new Piece[8][8];
		castleInProgress = false;
	}
	
	//creates all pieces at inital positions
	public void setPieces() {
		//pawns
		for (int i = 0; i < 8; i++) {
			setPieceAt(new Pawn(new Texture("Pawn White.png"), Piece.Color.WHITE, Chess.getBoard(), true), 6, i);
		}
		for (int i = 0; i < 8; i++) {
			setPieceAt(new Pawn(new Texture("Pawn Black.png"), Piece.Color.BLACK, Chess.getBoard(), true), 1, i);
		}
		
		//rooks
		setPieceAt(new Rook(new Texture("Rook White.png"), Piece.Color.WHITE, Chess.getBoard(), true), 7, 0);
		setPieceAt(new Rook(new Texture("Rook White.png"), Piece.Color.WHITE, Chess.getBoard(), true), 7, 7);
		setPieceAt(new Rook(new Texture("Rook Black.png"), Piece.Color.BLACK, Chess.getBoard(), true), 0, 0);
		setPieceAt(new Rook(new Texture("Rook Black.png"), Piece.Color.BLACK, Chess.getBoard(), true), 0, 7);

		//knights
		setPieceAt(new Knight(new Texture("Knight White.png"), Piece.Color.WHITE, Chess.getBoard()), 7, 1);
		setPieceAt(new Knight(new Texture("Knight White.png"), Piece.Color.WHITE, Chess.getBoard()), 7, 6);
		setPieceAt(new Knight(new Texture("Knight Black.png"), Piece.Color.BLACK, Chess.getBoard()), 0, 1);
		setPieceAt(new Knight(new Texture("Knight Black.png"), Piece.Color.BLACK, Chess.getBoard()), 0, 6);

		//bishops
		setPieceAt(new Bishop(new Texture("Bishop White.png"), Piece.Color.WHITE, Chess.getBoard()), 7, 2);
		setPieceAt(new Bishop(new Texture("Bishop White.png"), Piece.Color.WHITE, Chess.getBoard()), 7, 5);		
		setPieceAt(new Bishop(new Texture("Bishop Black.png"), Piece.Color.BLACK, Chess.getBoard()), 0, 2);
		setPieceAt(new Bishop(new Texture("Bishop Black.png"), Piece.Color.BLACK, Chess.getBoard()), 0, 5);
		
		//queens
		setPieceAt(new Queen(new Texture("Queen White.png"), Piece.Color.WHITE, Chess.getBoard()), 7, 3);
		setPieceAt(new Queen(new Texture("Queen Black.png"), Piece.Color.BLACK, Chess.getBoard()), 0, 3);
		
		//kings
		setPieceAt(new King(new Texture("King White.png"), Piece.Color.WHITE, Chess.getBoard(), true), 7, 4);
		setPieceAt(new King(new Texture("King Black.png"), Piece.Color.BLACK, Chess.getBoard(), true), 0, 4);
			
		kingWhiteRow = 7;
		kingWhiteColumn = 4;
		kingBlackRow = 7;
		kingBlackColumn = 3;
	
	}
	
	//sets redsquare at first click
	public void setFirstPiece() {
		clickedPiece = new RedSquare(redSquare, Piece.Color.RED, Chess.getBoard());
		firstClickX = Gdx.input.getX();
		firstClickY = Gdx.input.getY();
		clickedPiece.setRow(firstClickY/80);
		clickedPiece.setColumn(firstClickX/80);
	}
	
	//sets redsquare at second click
	public void setSecondPiece() {
		secondClickX = Gdx.input.getX();
		secondClickY = Gdx.input.getY();
		clickedPiece.setRow(secondClickY/80);
		clickedPiece.setColumn(secondClickX/80);
	}
	
	//draws every piece every tick
	public void drawPieces() {
		for (int row = 0; row < pieces.length; row++) {
			for (int col = 0; col < pieces.length; col++) {
				if (pieces[row][col] != null) {
					pieces[row][col].draw();
				}
			}
		}
	}
	
	//used only for debugging
	public void printPieces() {
		for (int y = 0; y < pieces.length; y++) {
			for (int i = 0; i < pieces.length; i++) {
				try {
					//print coordinates
					//System.out.print("[" + pieces[y][i].getRow() + ", " + pieces[y][i].getColumn() + "] ");
					//print color
					//System.out.print("[" + pieces[y][i].getColor() + "] ");
				} catch (NullPointerException e) {
					System.out.print("[-----] ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	//creates a new piece at the specified row and column
	public void setPieceAt(Piece piece, int row, int column) {
		piece.setRow(row);
		piece.setColumn(column);
		pieces[row][column] = piece;
	}
	
	//deletes the piece at the specified row and column
	public void deletePieceAt(int row, int column) {
		pieces[row][column] = null;
	}
	
	//draws the board every tick
	public void drawBoard() {
		batch.begin();
		batch.draw(board, 0, 0);
		batch.end();
	}
	
	//flips the board
	public void flip() {
		Piece[][] copy = new Piece[8][8];
		//flips the array
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				copy[row][col] = pieces[row][col];
				
			}
		}
		//flips every piece's row and column
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				pieces[row][col] = null;
				pieces[row][col] = copy[7 - row][7 - col];
				if (pieces[row][col] != null) {
					pieces[row][col].setRow(row);
					pieces[row][col].setColumn(col);
					pieces[row][col].setIsMoving(false);
				}
			}
		}
	}	
	
	public boolean checkCheck(int row, int col, Color color){
		//down
		for (int i = 1; i < 8 - row; i++){
			if (getPieceAt(row + i, col) != null && getPieceAt(row + i, col).getColor() == color &&
				(getPieceAt(row + i, col).getType() == Piece.Type.ROOK || getPieceAt(row + i, col).getType() == Piece.Type.QUEEN)){
				return true;
			} else {
				if (getPieceAt(row + i, col) != null){
					break;
				}
			}
		}
		//up
		for (int i = 1; i < row + 1; i++){
			if (getPieceAt(row - i, col) != null && getPieceAt(row - i, col).getColor() == color &&
				(getPieceAt(row - i, col).getType() == Piece.Type.ROOK || getPieceAt(row - i, col).getType() == Piece.Type.QUEEN)){
				return true;
			} else {
				if (getPieceAt(row - i, col) != null){
					break;
				}
			}
		}
		//right
		for (int i = 1; i < 8 - col; i++){
			if (getPieceAt(row, col + i) != null && getPieceAt(row, col + i).getColor() == color &&
				(getPieceAt(row, col + i).getType() == Piece.Type.ROOK || getPieceAt(row, col + i).getType() == Piece.Type.QUEEN)){
				return true;
			} else {
				if (getPieceAt(row, col + i) != null){
					break;
				}
			}
		}
		//left
		for (int i = 1; i < col + 1; i++){
			if (getPieceAt(row, col - i) != null && getPieceAt(row, col - i).getColor() == color &&
				(getPieceAt(row, col - i).getType() == Piece.Type.ROOK || getPieceAt(row, col - i).getType() == Piece.Type.QUEEN)){
				return true;
			} else {
				if (getPieceAt(row, col - i) != null){
					break;
				}
			}
		}
		//right-down
		int i = 1;
		while (true){
			if (i >= 8 - row || i >= 8 - col){
				break;
			} else {
				if (getPieceAt(row + i, col + i) != null && getPieceAt(row + i, col + i).getColor() == color &&
					(getPieceAt(row + i, col + i).getType() == Piece.Type.BISHOP || getPieceAt(row + i, col + i).getType() == Piece.Type.QUEEN)){				
					return true;
				} else {
					if (getPieceAt(row + i, col + i) != null){
						break;
					}
				}
			}					
			i++;
		}		
		//left-down
		i = 1;
		while (true){
			if (i >= 8 - row || i >= col + 1){
				break;
			} else {
				if (getPieceAt(row + i, col - i) != null && getPieceAt(row + i, col - i).getColor() == color &&
					(getPieceAt(row + i, col - i).getType() == Piece.Type.BISHOP || getPieceAt(row + i, col - i).getType() == Piece.Type.QUEEN)){
					return true;
				} else {
					if (getPieceAt(row + i, col - i) != null){
						break;
					}
				}
			}					
			i++;
		}		
		//right-up
		i = 1;
		while (true){
			if (i >= row + 1 || i >= 8 - col) {
				break;			
			} else {
				if (getPieceAt(row - i, col + i) != null && getPieceAt(row - i, col + i).getColor() == color &&
					(getPieceAt(row - i, col + i).getType() == Piece.Type.BISHOP || getPieceAt(row - i, col + i).getType() == Piece.Type.QUEEN)){
					return true;
				} else {
					if (getPieceAt(row - i, col + i) != null){
						break;
					}
				}
			}
			i++;
		}		
		//left-up
		i = 1;
		while (true){
			if (i >= row + 1 || i >= col + 1){
				break;
			} else {
				if (getPieceAt(row - i, col - i) != null && getPieceAt(row - i, col - i).getColor() == color &&
					(getPieceAt(row - i, col - i).getType() == Piece.Type.BISHOP || getPieceAt(row - i, col - i).getType() == Piece.Type.QUEEN)){
					return true;
				} else {
					if (getPieceAt(row - i, col - i) != null){
						break;
					}
				}
			}					
			i++;
		}
		//knights	
		if ((row + 1 <= 7 &&  col + 2 <= 7 && getPieceAt(row + 1, col + 2) != null && getPieceAt(row + 1, col + 2).getType() == Piece.Type.KNIGHT && getPieceAt(row + 1, col + 2).getColor() == color) ||
			(row - 1 >= 0 &&  col - 2 >= 0 && getPieceAt(row - 1, col - 2) != null && getPieceAt(row - 1, col - 2).getType() == Piece.Type.KNIGHT && getPieceAt(row - 1, col - 2).getColor() == color) ||
			(row + 1 <= 7 &&  col - 2 >= 0 && getPieceAt(row + 1, col - 2) != null && getPieceAt(row + 1, col - 2).getType() == Piece.Type.KNIGHT && getPieceAt(row + 1, col - 2).getColor() == color) ||
			(row - 1 >= 0 &&  col + 2 <= 7 && getPieceAt(row - 1, col + 2) != null && getPieceAt(row - 1, col + 2).getType() == Piece.Type.KNIGHT && getPieceAt(row - 1, col + 2).getColor() == color) ||
			(row + 2 <= 7 &&  col + 1 <= 7 && getPieceAt(row + 2, col + 1) != null && getPieceAt(row + 2, col + 1).getType() == Piece.Type.KNIGHT && getPieceAt(row + 2, col + 1).getColor() == color) ||
			(row - 2 >= 0 &&  col - 1 >= 0 && getPieceAt(row - 2, col - 1) != null && getPieceAt(row - 2, col - 1).getType() == Piece.Type.KNIGHT && getPieceAt(row - 2, col - 1).getColor() == color) ||
			(row + 2 <= 7 &&  col - 1 >= 0 && getPieceAt(row + 2, col - 1) != null && getPieceAt(row + 2, col - 1).getType() == Piece.Type.KNIGHT && getPieceAt(row + 2, col - 1).getColor() == color) ||
			(row - 2 >= 0 &&  col + 1 <= 7 && getPieceAt(row - 2, col + 1) != null && getPieceAt(row - 2, col + 1).getType() == Piece.Type.KNIGHT && getPieceAt(row - 2, col + 1).getColor() == color)){		
			return true;
		}
		//pawns
		if ((row + 1 <= 7 &&  col + 1 <= 7 && getPieceAt(row + 1, col + 1) != null && getPieceAt(row + 1, col + 1).getType() == Piece.Type.PAWN && getPieceAt(row + 1, col + 1).getColor() == color) ||
			(row + 1 <= 7 &&  col - 1 <= 7 && getPieceAt(row + 1, col - 1) != null && getPieceAt(row + 1, col - 1).getType() == Piece.Type.PAWN && getPieceAt(row + 1, col - 1).getColor() == color)){
			return true;
		}
		return false;
	}
	
	//returns the piece at the specified row and column
	public Piece getPieceAt(int row, int column) { return pieces[row][column]; }
	
	//returns the redsquare
	public Piece getClickedPiece() { return clickedPiece; }
	
	//returns the board's texture
	public Texture getTexture() { return board; }
	
	//clicks
	public int getFirstClickX() { return firstClickX; }
	
	public int getFirstClickY() { return firstClickY; }
	
	public int getSecondClickX() { return secondClickX; }
	
	public int getSecondClickY() { return secondClickY; }
	
	//castling and check
	public boolean getCastleInProgress() { return castleInProgress; }
	
	public void setCastleInProgress(boolean castle) { castleInProgress = castle; }
	
	//king coordinates
	public int getKingWhiteRow() { return kingWhiteRow; }
	
	public int getKingWhiteColumn() { return kingWhiteColumn; }
	
	public int getKingBlackRow() { return kingBlackRow; }
	
	public int getKingBlackColumn() { return kingBlackColumn; }
	
	public void setKingWhiteRow(int row) { kingWhiteRow = row; }
	
	public void setKingWhiteColumn(int col) { kingWhiteColumn = col; }
	
	public void setKingBlackRow(int row) { kingBlackRow = row; }
	
	public void setKingBlackColumn(int col) { kingBlackColumn = col; }
	
}
