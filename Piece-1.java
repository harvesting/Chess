package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Piece {
	//primitives
	private int row, column;
	private double x, y;
	private boolean isMoving; //is the piece being animated
	private boolean hasMoved; //has the piece successfully moved
	private boolean firstMove; //is this the piece's first move (used for pawn, rook, bishop)
	//other
	private Texture texture;
	private Color color;
	private Type type;
	private SpriteBatch batch;
	private Board board;
	private Piece pieceToKill; //the piece that exists at the second click
	
	//main constructor for piece
	public Piece(Texture texture, Color color, Board board) {
		batch = Chess.getBatch();
		this.board = board;
		this.texture = texture;
		this.color = color;
		hasMoved = false;
		isMoving = false;
	}
	
	//constructor for pawn, rook, king
	public Piece(Texture texture, Color color, Board board, boolean firstMove) {
		batch = Chess.getBatch();
		this.texture = texture;
		this.color = color;
		hasMoved = false;
		isMoving = false;
		this.firstMove = firstMove;
	}

	//the three colors - white, black, red
	public static enum Color {
		WHITE,
		BLACK,
		RED
	}
	
	//all types of pieces
	public static enum Type {
		PAWN,
		ROOK,
		KNIGHT,
		BISHOP,
		KING,
		QUEEN,
		RED_SQUARE
	}

	/*
	 * 
	 * The move method for all pieces:
	 * 
	 * 1. calls checkPath to see if it's a valid move, and if there 
	 *    are no other pieces obstructing the path the piece will take.
	 *    If both cases are true, moves the piece to second click
	 *    (if a piece is killed, store in pieceToKill)
	 *    
	 * 2. if the king is in check, move the piece back and restore
	 * 	  pieceToKill
	 * 
	 * 3. otherwise, start the moving animation
	 * 
	 */
	public abstract void move(int row, int column);
	
	/*
	 * 
	 * The checkPath method for all pieces (varies depending on type):
	 * 
	 * returns true if it's a valid move, and if there 
	 * are no other pieces obstructing the path
	 * 
	 * otherwise, return false
	 * 
	 */
	public abstract boolean checkPath(int rowStart, int rowEnd, int colStart, int colEnd);
	
	//draws a piece
	public void draw() {
		
		/*
		 * For normal drawing:
		 * 
		 * Multiply column and row by the scalar (80) so that each piece can only be drawn right on top of the 80x80 
		 * squares in the GUI grid, therefore appearing as if the pieces move only on the tiles.
		 * 
		 * Subtract 560 so that a) a piece doesn't get drawn out of bounds and
		 * b) doing so draws the pieces on the GUI grid exactly how they would look in a 2d array.
		 * 
		 */
		
		if (!isMoving || Chess.getFlipState() > 1){
			batch.begin();
			batch.draw(texture, column * 80, 560 - row * 80);
			batch.end();	
		}
		
		/*
		 * For animated drawing:
		 * 
		 * Draws piece at the first click + a fraction of a square (represented by x and y)
		 * Each tick, x and y are incremented by an amount based on the distance between first and second click
		 * This makes it so moving far distances is fast, and short distances is slow
		 * (same amount of time regardless of distance)
		 * 
		 * vx and vy are adjusted depending on which direction the piece is being moved
		 * 
		 * flipState 0 -> piece is stationary
		 * flipState 1 -> piece is moving
		 * flipState 2 -> piece just finished moving, waits a second, then flips
		 * 
		 */
		
		if (isMoving && Chess.getFlipState() == 1){	
			int firstY = board.getFirstClickY()/80*80;
			int secondY = board.getSecondClickY()/80*80;
			int firstX = board.getFirstClickX()/80*80;
			int secondX = board.getSecondClickX()/80*80;
			int vx = 1, vy = 1;
			boolean switchX = false, switchY = false;
			boolean shouldCheck = true;
			//switches first and second clicks (so first - second = a positive distance)
			if (firstY < secondY){
				int temp = firstY;
				firstY = secondY;
				secondY = temp;
				switchY = true;
			}
			if (firstX < secondX){
				int temp = firstX;
				firstX = secondX;
				secondX = temp;
				switchX = true;
			}
			//increments x and y
			if ((y < firstY - secondY || firstY == secondY) && shouldCheck){
				y += (firstY - secondY) / 25;
				if (y > firstY - secondY){
					y = firstY - secondY;
				}
			} else {
				Chess.setFlipState(2);
				shouldCheck = false;
			}
			if ((x < firstX - secondX || firstX == secondX) && shouldCheck){
				x += (firstX - secondX) / 25;
				if (x > firstX - secondX){
					x = firstX - secondX;
				}
			} else {
				Chess.setFlipState(2);
				shouldCheck = false;
			}
			//switches first and second clicks back and adjusts vx and vy for direction
			if (switchY){
				int temp = firstY;
				firstY = secondY;
				secondY = temp;
				vy = -1;
			}
			if (switchX){
				int temp = firstX;
				firstX = secondX;
				secondX = temp;
				vx = - 1;
			}		
			batch.begin();
			batch.draw(texture, firstX - vx * (int)(x), 560 - (firstY - vy * (int)(y)));
			batch.end();
		}
		//waits 1250 ticks
		if (Chess.getFlipState() == 2){
			y = 0;
			x = 0;
			Chess.addFlipTime(1);
		}
		//resets variables and flips board
		if (Chess.getFlipTime() >= 1250){
			Chess.setFlipTime(0);
			board.flip();
			Chess.setTurn((Chess.getTurn() == 1) ? 0 : 1);
			Chess.setFlipState(0);
		}	
	}
	
	//returns the opposite color of the piece
	public Color getOppositeColor(){
		if (getColor() == Piece.Color.WHITE){
			return Piece.Color.BLACK;
		} else {
			return Piece.Color.WHITE;
		}
	}

	//getters and setters	
	public int getRow() { return row; }
	
	public int getColumn() { return column; }
	
	public void setRow(int row) { this.row = row; }
	
	public void setColumn(int column) { this.column = column; }
	
	public int getX() { return row * 80; }

	public int getY() { return 560 - (column * 80); }
	
	public void setX(int x) { this.x = x; }
	
	public void setY(int y) { this.y = y; }
	
	public Type getType() { return type; }
	
	public void setType(Type type) { this.type = type; }
	
	public boolean getHasMoved() { return hasMoved; }
	
	public void setHasMoved(boolean hasMoved) { this.hasMoved = hasMoved; }
	
	public boolean getFirstMove() { return firstMove; }
	
	public void setFirstMove(boolean firstMove) { this.firstMove = firstMove; }
	
	public boolean getIsMoving() { return isMoving; }
	
	public void setIsMoving(boolean isMoving) { this.isMoving = isMoving; }
	
	public Texture getTexture() { return texture; }
	
	public Color getColor() { return color; }
	
	public void setPieceToKill(Piece piece) { pieceToKill = piece; }
	
	public Piece getPieceToKill() { return pieceToKill; }
}
