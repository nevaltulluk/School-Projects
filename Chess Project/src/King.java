import java.awt.Color;
import java.awt.Graphics;


public class King extends Piece{
	
	public King(boolean isBlack){
		this.isBlack = isBlack;
		this.name = "king";
	}
	

	@Override
	public void drawYourself(Graphics g, int positionX, int positionY,
			int squareWidth) {
		// TODO Auto-generated method stub
		
		
		if(isBlack)
		{
			g.setColor(Color.black);
		}
		else
		{
			g.setColor(Color.white);
		}
		
		g.fillRect(positionX+2+(int)(squareWidth*2.0/8.0), 
				positionY+squareWidth/8, 
				squareWidth*2/5, squareWidth/10);
		g.fillRect(positionX+(int)(squareWidth*2.4/5.0), 
				positionY+squareWidth/8-4, 
				squareWidth/10, squareWidth*2/5);
				
		g.fillRect(positionX+(int)(squareWidth*4.0/10.0), 
				positionY+squareWidth/4, 
				squareWidth/5, squareWidth/2);
		g.fillRect(positionX+(int)(squareWidth*1.0/4.0), 
				positionY+(int)(squareWidth*3.0/5.0), 
				squareWidth/2, squareWidth/5);
	}
	

	@Override
	public boolean canMove(int x, int y) {
		if(Math.abs(x) <= 1 && Math.abs(y) <= 1){
			// TODO Auto-generated method stub
			if(board.pieces[board.selectedSquareX+x][board.selectedSquareY+y] == null){
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
		// TODO Auto-generated method stub
		if(board.pieces[board.selectedSquareX+x]
				[board.selectedSquareY+y].isBlack != isBlack ){
			if(checkTurn()){
				turn++;
				numMoves++;
				return true;
			}
		}
		return false;
	}

}
