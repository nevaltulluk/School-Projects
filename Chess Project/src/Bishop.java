import java.awt.Color;
import java.awt.Graphics;


public class Bishop extends Piece{
	
	public Bishop(boolean isBlack)
	{
		this.isBlack = isBlack;
		this.name = "bishop";
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
		int[] yPoints = {positionY+(int)squareWidth/2,
				positionY,
				positionY+(int)squareWidth/2};
		g.fillPolygon(xPoints, yPoints, 3);
		g.fillRect(positionX+(int)(squareWidth*4.0/10.0), 
				positionY+squareWidth/4, 
				squareWidth/5, squareWidth/2);
		g.fillRect(positionX+(int)(squareWidth*1.0/4.0), 
				positionY+(int)(squareWidth*3.0/5.0), 
				squareWidth/2, squareWidth/5);
		
	}
	
	public boolean isPathEmpty(int x1 , int y1 , int x2 , int y2){
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
		
	@Override
	public boolean canMove(int x, int y) {
		// TODO Auto-generated method stub
		if(Math.abs(x) == Math.abs(y)){
			if(isPathEmpty(board.selectedSquareX, 
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
		if(Math.abs(x) == Math.abs(y)){
		if(x>0){
			if(y>0){
				if(isPathEmpty(board.selectedSquareX, 
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
			else{
				if(isPathEmpty(board.selectedSquareX, 
						board.selectedSquareY, 
						board.selectedSquareX+x-1,  
						board.selectedSquareY+y+1)){
					if(isDiffColor(x, y)){
						if(checkTurn()){
						turn++;
						return true;}
					}
				}
			}
		}
		else{
			if(y>0){
				if(isPathEmpty(board.selectedSquareX, 
						board.selectedSquareY, 
						board.selectedSquareX+x+1,  
						board.selectedSquareY+y-1)){
					if(isDiffColor(x, y)){
						if(checkTurn()){
							turn++;
							return true;}
					}		
				}
			}
			else{
				if(isPathEmpty(board.selectedSquareX, 
						board.selectedSquareY, 
						board.selectedSquareX+x+1,  
						board.selectedSquareY+y+1)){
					if(isDiffColor(x, y)){
						if(checkTurn()){
							turn++;
							return true;}
					}				
				}
			}
		}
		}
		return false;
	}
}
