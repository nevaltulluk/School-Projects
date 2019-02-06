import java.awt.Color;
import java.awt.Graphics;


public class Queen extends Piece{
	
	public Queen(boolean isBlack)
	{
		this.isBlack = isBlack;
		this.name = "queen";
	}

	@Override
	public void drawYourself(Graphics g, int positionX, int positionY,
			int squareWidth) {
		if(isBlack)
		{
			g.setColor(Color.black);
		}
		else
		{
			g.setColor(Color.white);
		}
		int[] xPoints  = {14+positionX,
				14+positionX + (int)squareWidth/6,
				14+positionX+ (int)squareWidth*3/8};
		int[] yPoints = {2+positionY,
				positionY+(int)squareWidth/2,
				2+positionY};
		g.fillPolygon(xPoints, yPoints, 3);
		g.fillOval(positionX+(int)(squareWidth*2.0/6.0), 
				positionY+squareWidth/8, 
				squareWidth/3, squareWidth/3);
		g.fillRect(positionX+(int)(squareWidth*4.0/10.0), 
				positionY+squareWidth/4, 
				squareWidth/5, squareWidth/2);
		g.fillRect(positionX+(int)(squareWidth*1.0/4.0), 
				positionY+(int)(squareWidth*3.0/5.0), 
				squareWidth/2, squareWidth/5);
		
	}
	public boolean isPathEmptyBishop(int x1 , int y1 , int x2 , int y2){
		int diff = Math.abs(x1-x2);
		int counter = 0;
		for(int i = 1 ; i <= diff ; i++){
			if(x1<x2){
				if(y1<y2){
					if(board.pieces[x1+i][y1+i] == null){
						counter++;
					}
				}
				else{
					if(board.pieces[x1+i][y1-i] == null){
						counter++;
					}
				}
			}
			else{
				if(y1<y2){
					if(board.pieces[x1-i][y1+i] == null){
						counter++;
					}
				}
				else{
					if(board.pieces[x1-i][y1-i] == null){
						counter++;
					}
				}
			}
		}
		
		if(counter == diff){
			return true;
		}
		
		return false;
	}
	
	public boolean isPathEmptyRook(int x1, int y1, int x2 , int y2){
		int counter = 0;
		int diff = 0;
		if(x1==x2){
			diff = Math.abs(y1-y2);
			for(int i = 0 ; i < diff ; i++){
				if(y1>y2){
					if(board.pieces[x1][y2+i] == null){
						counter++;
					}
				}
				else{
					if(board.pieces[x1][y2-i] == null){
						counter++;
					}
				}
			}
		}
		if(y1==y2){
			diff = Math.abs(x1-x2);
			for(int i = 0 ; i < diff ; i++){
				if(x1>x2){
					if(board.pieces[x2+i][y1] == null){
						counter++;
					}
				}
				else{
					if(board.pieces[x2-i][y1] == null){
						counter++;
					}
				}
			}
		}
		
		if(counter == diff){
			return true;
		}
		return false;
		
	}
	
	

	@Override
	public boolean canMove(int x, int y) {
		if(x == 0 || y == 0){
			if(isPathEmptyRook(board.selectedSquareX,
					board.selectedSquareY, 
					board.selectedSquareX+x,
					board.selectedSquareY+y)){
				if(checkTurn()){
					turn++;
				return true;
				}
			}
		}
		if(Math.abs(x)==Math.abs(y)){
			if(isPathEmptyBishop(board.selectedSquareX,
					board.selectedSquareY, 
					board.selectedSquareX+x,
					board.selectedSquareY+y)){
				if(checkTurn()){
					turn++;
				return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean canCapture(int x, int y) {
		if(x == 0 || y == 0){
			if(isPathEmptyRook(board.selectedSquareX,
					board.selectedSquareY, 
					board.selectedSquareX+x-1,
					board.selectedSquareY+y-1)){
				if(isDiffColor(x, y)){
				if(checkTurn()){
					turn++;
				return true;
				}
				}
			}
		}
		if(x==y){
			if(isPathEmptyBishop(board.selectedSquareX,
					board.selectedSquareY, 
					board.selectedSquareX+x-1,
					board.selectedSquareY+y-1)){
				if(isDiffColor(x, y)){
				if(checkTurn()){
					turn++;
					return true;
				}
				}
			}
		}
		return false;
	}

}
