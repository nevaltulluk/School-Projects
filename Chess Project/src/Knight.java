import java.awt.Color;
import java.awt.Graphics;


public class Knight extends Piece{
	
	public Knight(boolean isBlack)
	{
		this.isBlack = isBlack;
		this.name = "knight";
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
		
		int[] xPoints  = {2+positionX,
				14+positionX + (int)squareWidth/6,
				14+positionX+ (int)squareWidth*3/8};
		int[] yPoints = {-5+positionY+(int)squareWidth/2,
				1+positionY,
				-5+positionY+(int)squareWidth/2};
		g.fillPolygon(xPoints, yPoints, 3);
		
		g.fillRect(positionX+(int)(squareWidth*4.0/10.0), 
				positionY+squareWidth/4, 
				squareWidth/5, squareWidth/2);
		g.fillRect(positionX+(int)(squareWidth*1.0/4.0), 
				positionY+(int)(squareWidth*3.0/5.0), 
				squareWidth/2, squareWidth/5);
		
	}
	

	@Override
	public boolean canMove(int x, int y) {
		if(Math.abs(x)==1 && Math.abs(y)== 2){
			if(checkTurn()){
				turn++;
				
				
				return true;
			}
		}
		if(Math.abs(x)==2 && Math.abs(y)== 1){
			if(checkTurn()){
				turn++;
				
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canCapture(int x, int y) {
		if(Math.abs(x)==1 && Math.abs(y)== 2){
			if(checkTurn()){
				if(isDiffColor(x, y)){
					turn++;
					return true;
				}				
			}
		}
		if(Math.abs(x)==2 && Math.abs(y)== 1){
			if(checkTurn()){
				if(isDiffColor(x, y)){
					turn++;
					return true;
				}
			}
		}
		return false;
	}

}
