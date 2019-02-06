import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;

import javax.swing.JFrame;

public class ChessFrame extends JFrame implements MouseListener {
	public static final int SQUARE_WIDTH = 45;
	public static final int BOARD_MARGIN = 80;
	int selectedSquareX = -1;
	int selectedSquareY = -1;
	Piece pieces[][] = new Piece[8][8];

	public ChessFrame() {
		initializeChessBoard();
		setTitle("Chess Game");
		// let the screen size fit the board size
		setSize(SQUARE_WIDTH * 8 + BOARD_MARGIN * 2, SQUARE_WIDTH * 8
				+ BOARD_MARGIN * 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addMouseListener(this);

	}

	public boolean move(String from, String to) {
		selectedSquareX = translateCoor(from)[0];
		selectedSquareY = translateCoor(from)[1];
		int targetSquareX = translateCoor(to)[0];
		int targetSquareY = translateCoor(to)[1];

		if (pieces[selectedSquareX][selectedSquareY] != null) {
			System.out.println("selected");
			int diffX = targetSquareX - selectedSquareX;
			int diffY = targetSquareY - selectedSquareY;
			if (pieces[targetSquareX][targetSquareY] != null) {
				System.out.println("a target");
				if (pieces[selectedSquareX][selectedSquareY].canCapture(diffX,
						diffY)) {
					System.out.println("can capture");
					// promotion
					if (pieces[selectedSquareX][selectedSquareY] instanceof Pawn
							&& targetSquareX == 0) {
						pieces[targetSquareX][targetSquareY] = new Queen(false);
						Integer[] i = { selectedSquareX, selectedSquareY,
								targetSquareX, targetSquareY, 0 };
						Piece.undoStc.push(i);
					} else if (pieces[selectedSquareX][selectedSquareY] instanceof Pawn
							&& targetSquareX == 7) {
						pieces[targetSquareX][targetSquareY] = new Queen(true);
						Integer[] i = { selectedSquareX, selectedSquareY,
								targetSquareX, targetSquareY, -1 };
						Piece.undoStc.push(i);
					} else {
						// parçayý stack e burada at

						Integer[] i = { selectedSquareX, selectedSquareY,
								targetSquareX, targetSquareY, 0, 0 };
						if (pieces[targetSquareX][targetSquareY] instanceof Pawn) {
							i[4] = 1;
						}
						if (pieces[targetSquareX][targetSquareY] instanceof Rook) {
							i[4] = 2;
						}
						if (pieces[targetSquareX][targetSquareY] instanceof Knight) {
							i[4] = 3;
						}
						if (pieces[targetSquareX][targetSquareY] instanceof Bishop) {
							i[4] = 4;
						}
						if (pieces[targetSquareX][targetSquareY] instanceof Queen) {
							i[4] = 5;
						}
						if (pieces[targetSquareX][targetSquareY] instanceof King) {
							i[4] = 6;
						}
						if (pieces[targetSquareX][targetSquareY].isBlack) {
							i[5] = -1;
						} else if (!pieces[targetSquareX][targetSquareY].isBlack) {
							i[5] = 0;
						}
						Piece.undoStc.push(i);
						// ?*//
						pieces[targetSquareX][targetSquareY] = pieces[selectedSquareX][selectedSquareY];
						pieces[selectedSquareX][selectedSquareY] = null;
						return true;
					}
				}
			} else {
				System.out.println("no target");
				if (pieces[selectedSquareX][selectedSquareY].canMove(diffX,
						diffY)) {
					// boardda hareket ettirmek için kullandýðý kod parçasý
					System.out.println("can move");

					Integer[] i = { selectedSquareX, selectedSquareY,
							targetSquareX, targetSquareY };
					Piece.undoStc.push(i);

					pieces[targetSquareX][targetSquareY] = pieces[selectedSquareX][selectedSquareY];
					pieces[selectedSquareX][selectedSquareY] = null;
					return true;
				}
			}

		}
		repaint();
		return false;
	}

	public String at(String pos) {
		int coorX = translateCoor(pos)[0];
		int coorY = translateCoor(pos)[1];
		String color = "white";
		if (pieces[coorX][coorY].isBlack) {
			color = "black";
		}
		return color + "-" + pieces[coorX][coorY].name;
	}

	public boolean Castling(boolean isKingSide) {
		// 8 -> önce king sonra rook
		if (isCastling(isKingSide) == 0) {
			pieces[6][7] = pieces[4][7];
			pieces[5][7] = pieces[7][7];
			pieces[4][7] = null;
			pieces[7][7] = null;
			Piece.turn++;
			Integer[] i = { 4, 7, 6, 7, 7, 7, 5, 7 };
			Piece.undoStc.push(i);
			return true;
		} else if (isCastling(isKingSide) == 1) {
			pieces[5][0] = pieces[7][0];
			pieces[6][0] = pieces[4][0];
			pieces[7][0] = null;
			pieces[4][0] = null;
			Integer[] i = { 4, 0, 6, 0, 7, 0, 5, 0 };
			Piece.undoStc.push(i);
			Piece.turn++;
			return true;
		} else if (isCastling(isKingSide) == 2) {
			pieces[3][7] = pieces[0][7];
			pieces[2][7] = pieces[4][7];
			pieces[0][7] = null;
			pieces[4][7] = null;
			Integer[] i = { 4, 7, 2, 7, 0, 7, 3, 7 };
			Piece.undoStc.push(i);
			Piece.turn++;
		} else if (isCastling(isKingSide) == 3) {
			pieces[3][0] = pieces[0][0];
			pieces[2][0] = pieces[4][0];
			pieces[0][0] = null;
			pieces[4][0] = null;
			Integer[] i = { 4, 0, 2, 0, 0, 0, 3, 0 };
			Piece.undoStc.push(i);
			Piece.turn++;
		}
		return false;
	}

	public int isCastling(boolean isKingSide) {
		// turn is inside the piece class
		if (isKingSide) {
			// Piece temp;
			if (Piece.turn % 2 == 0 && !pieces[7][7].isBlack
					&& !pieces[4][7].isBlack) {
				if (pieces[5][7] == null && pieces[6][7] == null
						&& pieces[7][7] instanceof Rook
						&& pieces[4][7] instanceof King
				/*
				 * && pieces[7][7].numMoves == 0 && pieces[4][7].numMoves == 0
				 */) {
					// temp = pieces[7][7];
					// pieces[7][7] = pieces[4][7];
					// pieces[4][7] = temp;
					// Piece.turn++;
					return 0;
				}
			} else if (Piece.turn % 2 == 1 && pieces[7][0].isBlack
					&& pieces[4][0].isBlack) {
				if (pieces[5][0] == null && pieces[6][0] == null
						&& pieces[7][0] instanceof Rook
						&& pieces[4][0] instanceof King
				/*
				 * && pieces[4][0].numMoves == 0 && pieces[7][0].numMoves == 0
				 */) {
					// temp = pieces[7][0];
					// pieces[7][0] = pieces[4][0];
					// pieces[4][0] = temp;
					// Piece.turn++;
					return 1;
				}
			}
		} else if (!isKingSide) {

			// Piece temp;
			if (Piece.turn % 2 == 0 && !pieces[0][7].isBlack
					&& !pieces[4][7].isBlack) {
				if (pieces[1][7] == null && pieces[2][7] == null
						&& pieces[3][7] == null && pieces[0][7] instanceof Rook
						&& pieces[4][7] instanceof King
				/*
				 * && pieces[0][7].numMoves == 0 && pieces[4][7].numMoves == 0
				 */) {
					// temp = pieces[0][7];
					// pieces[0][7] = pieces[4][7];
					// pieces[4][7] = temp;
					// Piece.turn++;
					return 2;
				}
			} else if (Piece.turn % 2 == 1 && pieces[0][0].isBlack
					&& pieces[4][0].isBlack) {
				if (pieces[1][0] == null && pieces[2][0] == null
						&& pieces[3][0] == null && pieces[0][0] instanceof Rook
						&& pieces[4][0] instanceof King
				/*
				 * && pieces[4][0].numMoves == 0 && pieces[0][0].numMoves == 0
				 */) {
					// temp = pieces[0][0];
					// pieces[0][0] = pieces[4][0];
					// pieces[4][0] = temp;
					// Piece.turn++;
					return 3;
				}
			}
		}
		return -1;
	}

	public boolean isInDanger(int x, int y) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				selectedSquareX = i;
				selectedSquareY = j;
				if (pieces[x][y] != null && pieces[i][j] != null) {
					if (Piece.turn % 2 == 0 && !pieces[i][j].isBlack) {
						if (pieces[i][j].canCapture(x - i, y - j)) {
							return true;
						}
					}
					if (Piece.turn % 2 == 1 && pieces[i][j].isBlack) {
						if (pieces[i][j].canCapture(x - i, y - j)) {
							return true;
						}
					}
				}
				if (pieces[x][y] == null) {
					if (Piece.turn % 2 == 0 && !pieces[i][j].isBlack) {
						if (pieces[i][j].canMove(x - i, y - j)) {
							return true;
						}
					}
					if (Piece.turn % 2 == 1 && pieces[i][j].isBlack) {
						if (pieces[i][j].canMove(x - i, y - j)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isInCheck() {
		int kingCoorX = -1, kingCoorY = -1;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (pieces[i][j] instanceof King) {
					if (Piece.turn % 2 == 0 && pieces[i][j].isBlack) {
						kingCoorX = i;
						kingCoorY = j;
					} else if (Piece.turn % 2 == 1 && !pieces[i][j].isBlack) {
						kingCoorX = i;
						kingCoorY = j;
					}
				}
			}
		}
		if (kingCoorX == -1 || kingCoorY == -1) {
			return false;
		} else if (isInDanger(kingCoorX, kingCoorY)) {
			return true;
		}
		return false;
	}

	public boolean isCheckmate() {
		int kingCoorX = -1, kingCoorY = -1;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (pieces[i][j] instanceof King) {
					if (Piece.turn % 2 == 0 && pieces[i][j].isBlack) {
						kingCoorX = i;
						kingCoorY = j;
					} else if (Piece.turn % 2 == 1 && !pieces[i][j].isBlack) {
						kingCoorX = i;
						kingCoorY = j;
					}
				}
			}
		}
		if (kingCoorX == -1 || kingCoorY == -1) {
			return false;
		} else {
			for (int i = kingCoorX - 1; i <= kingCoorX + 1; i++) {
				for (int j = kingCoorX - 1; j <= kingCoorX + 1; j++) {
					if (i < 0)
						i = 0;
					if (i > 7)
						i = 7;
					if (j < 0)
						i = 0;
					if (j > 7)
						i = 7;
					if (!isInDanger(i, j)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public Piece getPiece(int i, boolean j) {
		boolean f = j;

		if (i == 1) {
			return new Pawn(f);
		}
		if (i == 2) {
			return new Rook(f);
		}
		if (i == 3) {
			return new Knight(f);
		}
		if (i == 4) {
			return new Bishop(f);
		}
		if (i == 5) {
			return new Queen(f);
		}
		if (i == 6) {
			return new King(f);
		}

		return null;
	}

	public int pieceToNumber(int targetSquareX, int targetSquareY) {
		if (pieces[targetSquareX][targetSquareY] instanceof Pawn) {
			return 1;
		}
		if (pieces[targetSquareX][targetSquareY] instanceof Rook) {
			return 2;
		}
		if (pieces[targetSquareX][targetSquareY] instanceof Knight) {
			return 3;
		}
		if (pieces[targetSquareX][targetSquareY] instanceof Bishop) {
			return 4;
		}
		if (pieces[targetSquareX][targetSquareY] instanceof Queen) {
			return 5;
		}
		if (pieces[targetSquareX][targetSquareY] instanceof King) {
			return 6;
		}
		return -10;
	}

	/*
	 * undostk.size = 4 -> move
	 *  undostck.size = 5 -> promotion 0->white -1 b cancapturedaysa 5 capturelanan taþ 
	 * undostck.size = 6 -> capture 4->taþ türü 5-> taþ rengi 0 beyaz
	 */

	public void undo() {
		if(Piece.undoStc.isEmpty())return;
		if (Piece.undoStc.peek().length == 4)// undo move
		{
			System.out.println("Hey");
			if (pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]] instanceof Pawn) {
				pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]].numMoves--;
			}
			Piece a=pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]];
			pieces[Piece.undoStc.peek()[0]][Piece.undoStc.peek()[1]] =a;
			pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]] = null;
			if(pieces[Piece.undoStc.peek()[0]][Piece.undoStc.peek()[1]] instanceof Pawn &&
					pieces[Piece.undoStc.peek()[0]][Piece.undoStc.peek()[1]].isBlack){Piece.undoStc.pop();}
			Piece.undoStc.pop();
			
			
		
		}
		else if (Piece.undoStc.peek().length == 5)// undo prop
		{
			if(Piece.undoStc.peek()[4] == 0 || Piece.undoStc.peek()[4] == -1){//capture yok
				if(Piece.undoStc.peek()[4] == 0){
					pieces[Piece.undoStc.peek()[0]][Piece.undoStc.peek()[1]] = 
							new Pawn(false);
				}
				else{
					pieces[Piece.undoStc.peek()[0]][Piece.undoStc.peek()[1]] =
							new Pawn(true);
				}
				pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]] = null;
				Piece.undoStc.pop();
			}
			else{//capture var
				boolean color = pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]].isBlack;
				pieces[Piece.undoStc.peek()[0]][Piece.undoStc.peek()[1]] = new Pawn(color);
				pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]] = 
						getPiece(Piece.undoStc.peek()[4], color); 
				Piece.undoStc.pop();
			}
		}
		else if (Piece.undoStc.peek().length == 6)// undo capture
		{
			boolean color = pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]].isBlack;
			if (pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]] instanceof Pawn) {
				pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]].numMoves--;
			}
			pieces[Piece.undoStc.peek()[0]][Piece.undoStc.peek()[1]]=
					pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]];
			pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]]=
					getPiece(Piece.undoStc.peek()[4], !color);
			Piece.undoStc.pop();
		}
		else if (Piece.undoStc.peek().length == 8)// undo castling önce king sonra rook
		{
			pieces[Piece.undoStc.peek()[0]][Piece.undoStc.peek()[1]]=
						pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]];
			pieces[Piece.undoStc.peek()[4]][Piece.undoStc.peek()[5]]=
						pieces[Piece.undoStc.peek()[6]][Piece.undoStc.peek()[7]];
			pieces[Piece.undoStc.peek()[2]][Piece.undoStc.peek()[3]] = null;
			pieces[Piece.undoStc.peek()[6]][Piece.undoStc.peek()[7]]= null;
			Piece.undoStc.pop();
		}
		
		Piece.turn++;
		repaint();
	}

	public void save(String fileName) {
		// File file = new File(fileName);
		try {
			PrintWriter writer = new PrintWriter(fileName);
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (pieces[i][j] != null) {
						if (pieces[i][j].isBlack) {
							writer.write("black-");
						} else {
							writer.write("white-");
						}
						writer.write(pieces[i][j].name + "-");
						if (i == 0)
							writer.write("a");
						else if (i == 1)
							writer.write("b");
						else if (i == 2)
							writer.write("c");
						else if (i == 3)
							writer.write("d");
						else if (i == 4)
							writer.write("e");
						else if (i == 5)
							writer.write("f");
						else if (i == 6)
							writer.write("g");
						else if (i == 7)
							writer.write("h");
						if (j == 0)
							writer.write("8");
						else if (j == 1)
							writer.write("7");
						else if (j == 2)
							writer.write("6");
						else if (j == 3)
							writer.write("5");
						else if (j == 4)
							writer.write("4");
						else if (j == 5)
							writer.write("3");
						else if (j == 6)
							writer.write("2");
						else if (j == 7)
							writer.write("1");

						writer.println();
					}
				}
			}
			writer.close();
		} catch (IOException e) {
		}

	}
	public int[] translateCoor(String coor) {
		int[] cArr = new int[2];

		if (coor.charAt(0) == 'a') {
			cArr[0] = 0;
		}
		if (coor.charAt(0) == 'b') {
			cArr[0] = 1;
		}
		if (coor.charAt(0) == 'c') {
			cArr[0] = 2;
		}
		if (coor.charAt(0) == 'd') {
			cArr[0] = 3;
		}
		if (coor.charAt(0) == 'e') {
			cArr[0] = 4;
		}
		if (coor.charAt(0) == 'f') {
			cArr[0] = 5;
		}
		if (coor.charAt(0) == 'g') {
			cArr[0] = 6;
		}
		if (coor.charAt(0) == 'h') {
			cArr[0] = 7;
		}

		if (coor.charAt(1) == '1') {
			cArr[1] = 7;
		}
		if (coor.charAt(1) == '2') {
			cArr[1] = 6;
		}
		if (coor.charAt(1) == '3') {
			cArr[1] = 5;
		}
		if (coor.charAt(1) == '4') {
			cArr[1] = 4;
		}
		if (coor.charAt(1) == '5') {
			cArr[1] = 3;
		}
		if (coor.charAt(1) == '6') {
			cArr[1] = 2;
		}
		if (coor.charAt(1) == '7') {
			cArr[1] = 1;
		}
		if (coor.charAt(1) == '8') {
			cArr[1] = 0;
		}

		return cArr;

	}

	public static ChessFrame load(String fileName) {
		ChessFrame frame = new ChessFrame();
		for(int i = 0 ; i < 8 ; i++){
			for(int j = 0 ; j < 8 ; j++){
				frame.pieces[i][j]=null;
			}
		}
		Scanner scanner;
		try {
			scanner = new Scanner(new File(fileName));
		
		while(scanner.hasNext()){
			boolean color;
			Piece piece = null;
			String str=scanner.nextLine();
			String[] parts = str.split("-");
			if(parts[0].equalsIgnoreCase("black")){
				color = true;
			}else{
				color=false;
			}
			
			if(parts[1].equalsIgnoreCase("pawn")){
				piece = new Pawn(color);		
			}
			else if(parts[1].equalsIgnoreCase("rook")){
				piece = new Rook(color);		
			}
			else if(parts[1].equalsIgnoreCase("knight")){
				piece = new Knight(color);		
			}
			else if(parts[1].equalsIgnoreCase("bishop")){
				piece = new Bishop(color);		
			}
			else if(parts[1].equalsIgnoreCase("queen")){
				piece = new Queen(color);		
			}
			else if(parts[1].equalsIgnoreCase("king")){
				piece = new King(color);		
			}
			String coor = parts[2];
			int[] cArr = new int[2];

			if (coor.charAt(0) == 'a') {
				cArr[0] = 0;
			}
			if (coor.charAt(0) == 'b') {
				cArr[0] = 1;
			}
			if (coor.charAt(0) == 'c') {
				cArr[0] = 2;
			}
			if (coor.charAt(0) == 'd') {
				cArr[0] = 3;
			}
			if (coor.charAt(0) == 'e') {
				cArr[0] = 4;
			}
			if (coor.charAt(0) == 'f') {
				cArr[0] = 5;
			}
			if (coor.charAt(0) == 'g') {
				cArr[0] = 6;
			}
			if (coor.charAt(0) == 'h') {
				cArr[0] = 7;
			}

			if (coor.charAt(1) == '1') {
				cArr[1] = 7;
			}
			if (coor.charAt(1) == '2') {
				cArr[1] = 6;
			}
			if (coor.charAt(1) == '3') {
				cArr[1] = 5;
			}
			if (coor.charAt(1) == '4') {
				cArr[1] = 4;
			}
			if (coor.charAt(1) == '5') {
				cArr[1] = 3;
			}
			if (coor.charAt(1) == '6') {
				cArr[1] = 2;
			}
			if (coor.charAt(1) == '7') {
				cArr[1] = 1;
			}
			if (coor.charAt(1) == '8') {
				cArr[1] = 0;
			}
			
			frame.pieces[cArr[0]][cArr[1]] = piece;
			
		}
		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	
		
		}
		return frame;
	}

	

	public void initializeChessBoard() {
		Piece.board = this;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (j == 1) {
					pieces[i][j] = new Pawn(true);
				} else if (j == 6) {
					pieces[i][j] = new Pawn(false);
				} else {
					pieces[i][j] = null;
				}
			}
		}

		pieces[4][0] = new King(true);
		pieces[4][7] = new King(false);

		pieces[3][0] = new Queen(true);
		pieces[3][7] = new Queen(false);

		pieces[0][0] = new Rook(true);
		pieces[0][7] = new Rook(false);
		pieces[7][0] = new Rook(true);
		pieces[7][7] = new Rook(false);

		pieces[1][0] = new Knight(true);
		pieces[1][7] = new Knight(false);
		pieces[6][0] = new Knight(true);
		pieces[6][7] = new Knight(false);

		pieces[2][0] = new Bishop(true);
		pieces[2][7] = new Bishop(false);
		pieces[5][0] = new Bishop(true);
		pieces[5][7] = new Bishop(false);
	}

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		// numbers and letters
		g.setFont(g.getFont().deriveFont(g.getFont().getSize() * 1.8F));
		g.drawString("   a      b      c     d      e     f      g      h",
				BOARD_MARGIN, BOARD_MARGIN - 10);
		g.drawString("   a      b      c     d      e     f      g      h",
				BOARD_MARGIN, BOARD_MARGIN + 8 * SQUARE_WIDTH + 25);
		// save and undo buttons
		g.setColor(Color.PINK);
		g.fillRect(15, 40, 60, 25);
		g.drawRect(15, 40, 60, 25);
		g.setColor(Color.BLUE);
		g.fillRect(15, 65, 60, 25);
		g.drawRect(15, 65, 60, 25);
		g.drawString("save", 20, 60);
		g.setColor(Color.PINK);
		g.drawString("undo", 20, 85);
		g.setColor(Color.BLACK);
		// print the board 's lines to show squares
		for (int i = 0; i <= 8; i++) {
			if (i != 8) {
				g.drawString(String.valueOf(8 - i), BOARD_MARGIN - 20, 30
						+ BOARD_MARGIN + (i) * SQUARE_WIDTH);
				g.drawString(String.valueOf(8 - i), BOARD_MARGIN + 8
						* SQUARE_WIDTH + 11, 30 + BOARD_MARGIN + (i)
						* SQUARE_WIDTH);
			}
			g.drawLine(BOARD_MARGIN, BOARD_MARGIN + (i) * SQUARE_WIDTH,
					BOARD_MARGIN + 8 * SQUARE_WIDTH, BOARD_MARGIN + (i)
							* SQUARE_WIDTH);
			g.drawLine(BOARD_MARGIN + (i) * SQUARE_WIDTH, BOARD_MARGIN,
					BOARD_MARGIN + (i) * SQUARE_WIDTH, BOARD_MARGIN + 8
							* SQUARE_WIDTH);
		}
		// print the pieces
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (pieces[i][j] != null) {
					pieces[i][j].drawYourself(g, i * SQUARE_WIDTH
							+ BOARD_MARGIN, j * SQUARE_WIDTH + BOARD_MARGIN,
							SQUARE_WIDTH);
				}
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		// System.out.println("Clicked");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		// TODO Auto-generated method stub
		// System.out.println("Pressed");
		// calculate which square is selected
		selectedSquareX = (e.getX() - BOARD_MARGIN) / SQUARE_WIDTH;
		selectedSquareY = (e.getY() - BOARD_MARGIN) / SQUARE_WIDTH;
		System.out.println(selectedSquareX + "," + selectedSquareY);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		// System.out.println("Released");
		// calculate which square is targeted
		

		int targetSquareX = (e.getX() - BOARD_MARGIN) / SQUARE_WIDTH;
		int targetSquareY = (e.getY() - BOARD_MARGIN) / SQUARE_WIDTH;
		System.out.println(targetSquareX + "," + targetSquareY + "\n");

		// if these are inside the board
		if (selectedSquareX >= 0 && selectedSquareY >= 0 && selectedSquareX < 8
				&& selectedSquareY < 8 && targetSquareX >= 0
				&& targetSquareY >= 0 && targetSquareX < 8 && targetSquareY < 8) {
			System.out.println("inside");
			if (pieces[selectedSquareX][selectedSquareY] != null) {
				System.out.println("selected");
				int diffX = targetSquareX - selectedSquareX;
				int diffY = targetSquareY - selectedSquareY;
				// **//
				// TODO

				if (selectedSquareX == 4 && selectedSquareY == 0
						&& targetSquareX == 7 && targetSquareY == 0
						&& isCastling(true) == 1) {
					pieces[5][0] = pieces[7][0];
					pieces[6][0] = pieces[4][0];
					pieces[7][0] = null;
					pieces[4][0] = null;
					Piece.turn++;
					Integer[] i = { 4, 0, 6, 0, 7, 0, 5, 0 };
					Piece.undoStc.push(i);
				}
				if (selectedSquareX == 4 && selectedSquareY == 7
						&& targetSquareX == 7 && targetSquareY == 7
						&& isCastling(true) == 0) {
					pieces[6][7] = pieces[4][7];
					pieces[5][7] = pieces[7][7];
					pieces[4][7] = null;
					pieces[7][7] = null;
					Integer[] i = { 4, 7, 6, 7, 7, 7, 5, 7 };
					Piece.undoStc.push(i);
					Piece.turn++;
				}

				if (selectedSquareX == 4 && selectedSquareY == 7
						&& targetSquareX == 0 && targetSquareY == 7
						&& isCastling(false) == 2) {
					pieces[2][7] = pieces[0][7];
					pieces[3][7] = pieces[4][7];
					pieces[0][7] = null;
					pieces[4][7] = null;
					Integer[] i = { 4, 7, 2, 7, 0, 7, 3, 7 };
					Piece.undoStc.push(i);
					Piece.turn++;

				}
				if (selectedSquareX == 4 && selectedSquareY == 0
						&& targetSquareX == 0 && targetSquareY == 0
						&& isCastling(false) == 3) {
					pieces[3][0] = pieces[0][0];
					pieces[2][0] = pieces[4][0];
					pieces[0][0] = null;
					pieces[4][0] = null;
					Integer[] i = { 4, 0, 2, 0, 0, 0, 3, 0 };
					Piece.undoStc.push(i);
					Piece.turn++;
				}
				repaint();
				// **//
				if (pieces[targetSquareX][targetSquareY] != null) {
					System.out.println("a target");
					if (pieces[selectedSquareX][selectedSquareY].canCapture(
							diffX, diffY)) {
						// promotion
						if (pieces[selectedSquareX][selectedSquareY] instanceof Pawn
								&& targetSquareY == 0 ) {

							Integer[] i = { selectedSquareX, selectedSquareY,
									targetSquareX, targetSquareY, -1 };
							i[4] = pieceToNumber(targetSquareX, targetSquareY);
							Piece.undoStc.push(i);
							pieces[targetSquareX][targetSquareY] = new Queen(
									false);
							pieces[selectedSquareX][selectedSquareY] = null;
						} else if (pieces[selectedSquareX][selectedSquareY] instanceof Pawn
								&& targetSquareY == 7) {

							Integer[] i = { selectedSquareX, selectedSquareY,
									targetSquareX, targetSquareY, -1 };
							i[4] = pieceToNumber(targetSquareX, targetSquareY);
							Piece.undoStc.push(i);
							pieces[targetSquareX][targetSquareY] = new Queen(
									true);
							pieces[selectedSquareX][selectedSquareY] = null;
						} else {
							// end of p
							// **//
							Integer[] i = { selectedSquareX, selectedSquareY,
									targetSquareX, targetSquareY, 0, 0 };
							i[4] = pieceToNumber(targetSquareX, targetSquareY);
							if (pieces[targetSquareX][targetSquareY].isBlack) {
								i[5] = 0;
							} else if (!pieces[targetSquareX][targetSquareY].isBlack) {
								i[5] = 1;
							}
							Piece.undoStc.push(i);
							// **//
							System.out.println("can capture");
							pieces[targetSquareX][targetSquareY] = pieces[selectedSquareX][selectedSquareY];
							pieces[selectedSquareX][selectedSquareY] = null;
						}
					}
				} else {
					System.out.println("no target");
					try {
						if (pieces[selectedSquareX][selectedSquareY].canMove(
								diffX, diffY)) {
							// boardda hareket ettirmek için kullandýðý kod
							// parçasý
							System.out.println("can move");
							// promotion
							if (pieces[selectedSquareX][selectedSquareY] instanceof Pawn
									&& targetSquareY == 0) {
								pieces[targetSquareX][targetSquareY] = new Queen(
										false);
								pieces[selectedSquareX][selectedSquareY] = null;
								Integer[] i = { selectedSquareX,
										selectedSquareY, targetSquareX,
										targetSquareY, 0 };
								Piece.undoStc.push(i);
							} else if (pieces[selectedSquareX][selectedSquareY] instanceof Pawn
									&& targetSquareX == 7) {
								pieces[targetSquareX][targetSquareY] = new Queen(
										true);
								pieces[selectedSquareX][selectedSquareY] = null;
								Integer[] i = { selectedSquareX,
										selectedSquareY, targetSquareX,
										targetSquareY, -1 };
								Piece.undoStc.push(i);
							}
							// end of p
							
							///
							else {
								Integer[] i = { selectedSquareX,
										selectedSquareY, targetSquareX,
										targetSquareY };
								Piece.undoStc.push(i);
								// **//
								pieces[targetSquareX][targetSquareY] = pieces[selectedSquareX][selectedSquareY];
								pieces[selectedSquareX][selectedSquareY] = null;
							}
						}
					} catch (NullPointerException e1) {
					}
				}
			}
		}
		if (15 < e.getX() && e.getX() < 75 && 40 < e.getY() && e.getY() < 65) {
			// save
			save("game.txt");
		}
		if (15 < e.getX() && e.getX() < 75 && 65 < e.getY() && e.getY() < 90) {
			undo();
		}
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		// System.out.println("Entered");

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		// System.out.println("Exited");

	}

}
