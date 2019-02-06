import java.awt.Color;
import java.awt.Graphics;

public class Pawn extends Piece{
	
	public Pawn(boolean isBlack)
	{
		this.isBlack = isBlack;
		this.name = "pawn";
	}

	@Override
	public void drawYourself(Graphics g, int positionX, int positionY, int squareWidth) {
		// TODO Auto-generated method stub
		
		
		if(isBlack)
		{
			g.setColor(Color.black);
		}
		else
		{
			g.setColor(Color.white);
		}
		
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

	@Override
	public boolean canMove(int x, int y) {
		// TODO Auto-generated method stub
		
		if(turn % 2 == 0 && !isBlack){if(isBlack)
		{
			if(numMoves == 0){
				if(y == 2 && x == 0)
				{
					turn++;
					return true;
				}
			}
			if(y == 1 && x == 0)
			{
				turn++;
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if(numMoves == 0){
				if(y == -2 && x == 0)
				{
					numMoves++;
					turn++;
					return true;
				}
			}
			
			if(y == -1 && x == 0)
			{
				numMoves++;
				turn++;
				return true;
			}
			else
			{
				return false;
			}
		}}
		else if(turn % 2 == 1 && isBlack){if(isBlack)
		{
			if(numMoves == 0){
				if(y == 2 && x == 0)
				{
					Integer[] i = {board.selectedSquareX , board.selectedSquareY
							,board.selectedSquareX+x, board.selectedSquareY+y};
					undoStc.push(i);
					numMoves++;
					turn++;
					return true;
				}
			}
			if(y == 1 && x == 0)
			{
				Integer[] i = {board.selectedSquareX , board.selectedSquareY
						,board.selectedSquareX+x, board.selectedSquareY+y};
				undoStc.push(i);
				numMoves++;
				turn++;
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if(numMoves == 0){
				if(y == -2 && x == 0)
				{
					Integer[] i = {board.selectedSquareX , board.selectedSquareY
							,board.selectedSquareX+x, board.selectedSquareY+y};
					undoStc.push(i);
					numMoves++;
					turn++;
					return true;
				}
			}
			
			if(y == -1 && x == 0)
			{
				Integer[] i = {board.selectedSquareX , board.selectedSquareY
						,board.selectedSquareX+x, board.selectedSquareY+y};
				undoStc.push(i);
				numMoves++;
				turn++;
				return true;
			}
			else
			{
				return false;
			}
		}}
		else{return false;
		}
		
		
		
	}

	@Override
	public boolean canCapture(int x, int y) {
		// TODO Auto-generated method stub
		if(isBlack)
		{
			if((x == -1 || x == 1) && y == 1)
			{
				if(!board.pieces[board.selectedSquareX+x][board.selectedSquareY+y].isBlack){
					if(checkTurn()){
					numMoves++;
					turn++;
					return true;}
				}
				else{ 
					return false;
					}
			}
			else
			{
				return false;
			}
		}
		else
		{
			if((x == -1 || x == 1) && y == -1)
			{
				if(board.pieces[board.selectedSquareX+x][board.selectedSquareY+y].isBlack){
					if(checkTurn()){
				numMoves++;
				turn++;
				return true;}
				}
				else{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		return false;
	}
	

}
