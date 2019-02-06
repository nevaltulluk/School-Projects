import java.awt.Graphics;
import java.util.Stack;

public abstract class Piece {
	public static Stack<Integer[]> undoStc = new Stack<>();
	String name;// at metodunda dönmesi için taþlara isim tanýmla
	public static ChessFrame board;
	public boolean isBlack;
	public int numMoves=0;
	public static int turn = 0;
	public abstract void drawYourself(Graphics g, int positionX, int positionY, int squareWidth);
	public abstract boolean canMove(int x, int y);
	public abstract boolean canCapture(int x, int y);
	public int getMoves(){		
		return numMoves;}
	public boolean isDiffColor(int x, int y){
		if(isBlack && !board.pieces[board.selectedSquareX+x]
				[board.selectedSquareY+y].isBlack){		
			return true;
		}
		if(!isBlack && board.pieces[board.selectedSquareX+x]
				[board.selectedSquareY+y].isBlack){
			return true;
		}
		return false;
	}
	public boolean checkTurn(){
		if(turn % 2 == 0 && !isBlack){
			return true;
		}
		else if(turn % 2 == 1 && isBlack){
			return true;
		}
		return false;
	}
	
	
}

