package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Piece.Color;

public class Chess extends ApplicationAdapter {
	//booleans
	private boolean beginning; //if it is the beginning of the game
	private boolean clicked; //if the user has clicked for the first time
	private boolean clickedAgain; //if the user has clicked for the second time
	private static boolean gameIsOver; //if the game is over
	private static boolean GUIIsOpen; //if the pawn promotion GUI is oepn
	//nums
	private long startTime; //used for click detection
	private long endTime; //used for click detection
	private static int turn; //1 = white, 0 = black
	private static long flipTime; //used for flipping the board
	private static int flipState; //used for flipping the board
	//other
	private static SpriteBatch batch;
	private static Board board;	
	
	//initializes variables
	@Override
	public void create() {		
		//booleans
		beginning = true;
		clicked = false;
		clickedAgain = false;
		gameIsOver = false;
		//ints
		turn = 1;
		flipTime = 0;
		//others
		batch = new SpriteBatch();
		board = new Board(batch);
	}

	//runs every tick - mainly checks for click detection
	@Override
	public void render() {
		//clears screen and draws board
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		board.drawBoard();
		
		//at the beginning of game, set intial pieces
		if (beginning) {
			board.setPieces();
			beginning = false;
		}
		
		//draws every piece
		board.drawPieces();
		//checks if the user clicked
		checkClick();
		
		
	}	
		
	public void checkClick(){
			
		/*
		 * if clicked for first time, make a new red square of row clicked on, column clicked on
		 * if endTime < System.nanoTime() - 300000000 makes it so it won't go straight into this if statement
		 * after you click the second time
		 * 
		 */
		
		
		if (!GUIIsOpen && flipState != 1){
			if ( ( (endTime) < System.nanoTime() - 300000000 && !gameIsOver) ) {
				if( Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !clicked ) {
					board.setFirstPiece();
					clicked = true;
					startTime = System.nanoTime();
				}
			}
		}
		
		//if you just clicked for the first time, it draws a red square around where you clicked
		if (clicked && board.getClickedPiece() != null) {
			int row = board.getClickedPiece().getRow();
			int column = board.getClickedPiece().getColumn();
			
			if (board.getPieceAt(row, column) != null){
				if (turn == 1) {
					if (board.getPieceAt(row, column).getColor() == Piece.Color.WHITE) {					
						if (board.getPieceAt(row, column) != null){
							batch.begin();
							batch.draw(new Texture("Red Square.png"), 560 - board.getPieceAt(row, column).getY(), 560 - board.getPieceAt(row, column).getX());
							batch.end();
						}
					}
				} else {
					if (board.getPieceAt(row, column).getColor() == Piece.Color.BLACK) {
						if (board.getPieceAt(row, column) != null){
							batch.begin();
							batch.draw(new Texture("Red Square.png"), 560 - board.getPieceAt(row, column).getY(), 560 - board.getPieceAt(row, column).getX());
							batch.end();
						}
					}
				}
			}
		}
		
		
		/*
		 * if you clicked once and just clicked again, it sets the redsquare's row and column
		 * to the row and column you just clicked on
		 * 
		 * if startTime < System.nanoTime() - 300000000 is necessary because this waits before going through the if 
		 * statement, so that Gdx.input.isButtonPressed(Input.Buttons.LEFT) isn't the same isButtonPressed as the last
		 * time you pressed the button
		 * 
		 */
		
		if (startTime < System.nanoTime() - 300000000) {
			if (clicked && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
				board.setSecondPiece();
				clickedAgain = true;
			}
		}
		
		//moves if you clicked for the second time
		if (clickedAgain) {
			int firstX = board.getFirstClickX()/80;
			int secondX = board.getSecondClickX()/80;
			int firstY = board.getFirstClickY()/80;
			int secondY = board.getSecondClickY()/80;
			if (board.getPieceAt(firstY, firstX) != null) {
				board.getPieceAt(firstY, firstX).move(secondY, secondX);
				//test if moved: normal
				try {				
					if (board.getPieceAt(secondY, secondX).getHasMoved() && !gameIsOver) {
						board.getPieceAt(secondY, secondX).setHasMoved(false);
						board.getPieceAt(secondY, secondX).setIsMoving(true);
						flipState = 1;
					}
				} catch (NullPointerException e) {}

				//test if moved: castling
				try {
					if (board.getCastleInProgress()){
						if ( ( secondX != 7 && board.getPieceAt(secondY, secondX + 1).getHasMoved()
							&& ( board.getPieceAt(secondY, secondX + 1).getType() == Piece.Type.ROOK ||
							board.getPieceAt(secondY, secondX + 1).getType() == Piece.Type.KING ) ) ||								
							( secondX != 0 && board.getPieceAt(secondY, secondX - 1).getHasMoved() 
							&& ( board.getPieceAt(secondY, secondX - 1).getType() == Piece.Type.ROOK ||
							board.getPieceAt(secondY, secondX - 1).getType() == Piece.Type.KING ) ) ){					
							
							if (secondX != 7){
								board.getPieceAt(secondY, secondX + 1).setHasMoved(false);
							}
							if (secondX != 0){
								board.getPieceAt(secondY, secondX - 1).setHasMoved(false);
							}
		
							board.flip();
							turn = (turn == 1) ? 0 : 1;
							flipState = 1;

							board.setCastleInProgress(false);
						}
					}
				} catch (NullPointerException e) {}
			}
			clickedAgain = false;
			clicked = false;
			endTime = System.nanoTime();			
		}
	}
	
	//checks for checkmate against the color parameter
	public static int checkWin(Color color) {
		int num = 0;
		int row, col;
		Color oppositeColor;
		if (color == Piece.Color.BLACK){
			row = board.getKingBlackRow();
			col = board.getKingBlackColumn();
		} else {
			row = board.getKingWhiteRow();
			col = board.getKingWhiteColumn();
		}
		if (color == Piece.Color.BLACK){
			oppositeColor = Piece.Color.WHITE;
		} else {
			oppositeColor = Piece.Color.BLACK;
		}
		if (row + 1 <= 7){
			if (col + 1 <= 7){
				if (board.getPieceAt(row + 1, col + 1) != null || board.checkCheck(row + 1, col + 1, oppositeColor)){
					num++;
				}
			}
			if (col - 1 >= 0){
				if (board.getPieceAt(row + 1, col - 1) != null || board.checkCheck(row + 1, col - 1, oppositeColor)){
					num++;
				}
			}
			if (board.getPieceAt(row + 1, col) != null || board.checkCheck(row + 1, col, oppositeColor)){
				num++;
			}
		}
		if (row - 1 >= 0){
			if (col + 1 <= 7){
				if (board.getPieceAt(row - 1, col + 1) != null || board.checkCheck(row - 1, col + 1, oppositeColor)){
					num++;
				}
			}
			if (col - 1 >= 0){
				if (board.getPieceAt(row - 1, col - 1) != null || board.checkCheck(row - 1, col - 1, oppositeColor)){
					num++;
				}
			}
			if (board.getPieceAt(row - 1, col) != null || board.checkCheck(row - 1, col, oppositeColor)){
				num++;
			}
		}
		if (col + 1 <= 7 && (board.getPieceAt(row, col + 1) != null || board.checkCheck(row, col + 1, oppositeColor))){
			num++;
		}
		if (col - 1 >= 0 && (board.getPieceAt(row, col - 1) != null || board.checkCheck(row, col - 1, oppositeColor))){
			num++;
		}
		if ((row == 7 || row == 0) && !(col == 7 || col == 0)) {
			num += 3;
		}	
		if (!(row == 7 || row == 0) && (col == 7 || col == 0)) {
			num += 3;
		}
		if ((col == 7 || col == 0) && (row == 7 || row == 0)){
			num += 2;
		}		
		
		if (board.checkCheck(row, col, oppositeColor)){
			num += 9;
		}		
		if (num == 17){
			//checkmate
			//gameIsOver = true;
		}
		return 0;
	}
	
	//getters and setters
	public static int getTurn(){ return turn; }
	
	public static void setTurn(int turn){ Chess.turn = turn; }
	
	public static boolean getGUIIsOpen(){ return GUIIsOpen; }
	
	public static void setGUIIsOpen(boolean state){ GUIIsOpen = state; }
	
	public static SpriteBatch getBatch(){ return batch; }
	
	public static Board getBoard(){ return board; }
	
	public static void setFlipState(int flip) { flipState = flip; }
	
	public static int getFlipState(){ return flipState; }
	
	public static void addFlipTime(int amt) { flipTime += amt; }

	public static long getFlipTime() { return flipTime; }
	
	public static void setFlipTime(int amt) { flipTime = amt; }
	
	
	
}