package application;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Board {
	GraphicsContext g;
	Stone stone;
	
	//ボードの情報。空0黒1白2
	int[][] board = new int[8][8];
	{
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = 0;
			}
		}
	}
	
	//それぞれのマスの8方向の情報の入れ物
	StringBuffer[][][] flipList = new StringBuffer[8][8][8];
	{
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					flipList[i][j][k] = new StringBuffer("0");
				}
			}
		}
	}
	
	int e = 0; //空合計
	int b = 0; //黒合計
	int w = 0; //白合計

	public Board(Canvas tbl) {
		this.g = tbl.getGraphicsContext2D();
		this.stone = new Stone(tbl);
		setStone();
		countStone();
	}

	//初期石をセット
	public void setStone() {
		board[3][4] = 1;
		board[4][3] = 1;
		board[3][3] = 2;
		board[4][4] = 2;
		drawBoard();
	}

	//石なしのボード描画
	public void resetBoard() {
		g.clearRect(0, 0, 400, 400);
		g.setFill(Color.GREEN);
		g.fillRect(0, 0, 400, 400);
		for (int i = 0; i < 9; i++) {
			g.strokeLine(0, i * 50, 400, i * 50);
			g.strokeLine(i * 50, 0, i * 50, 400);
		}
	}

	//石ありのボード描画
	public void drawBoard() {
		resetBoard();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 1) {
					stone.drawBlack(i, j);
				} else if (board[i][j] == 2) {
					stone.drawWhite(i, j);
				}
			}
		}
	}

	//空マスすべてについて8方向の情報を定義
	public void flipPattern(StringBuffer[][][] fliplist, int[][] board) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 0) {
					for (int k = 0; k < 8; k++) {
						fliplist[i][j][k].setLength(0);
					}
					//右
					if (i != 7) {
						for (int x = i + 1; x != 8; x++) {
							fliplist[i][j][0].append(String.valueOf(board[x][j]));
						}
					}
					//左
					if (i != 0) {
						for (int x = i - 1; x != -1; x--) {
							fliplist[i][j][1].append(String.valueOf(board[x][j]));
						}
					}
					//下
					if (j != 7) {
						for (int y = j + 1; y != 8; y++) {
							fliplist[i][j][2].append(String.valueOf(board[i][y]));
						}
					}
					//上
					if (j != 0) {
						for (int y = j - 1; y != -1; y--) {
							fliplist[i][j][3].append(String.valueOf(board[i][y]));
						}
					}
					//右下
					if (i != 7 && j != 7) {
						for (int x = i + 1, y = j + 1; x != 8 && y != 8; x++, y++) {
							fliplist[i][j][4].append(String.valueOf(board[x][y]));
						}
					}
					//左上
					if (i != 0 && j != 0) {
						for (int x = i - 1, y = j - 1; x != -1 && y != -1; x--, y--) {
							fliplist[i][j][5].append(String.valueOf(board[x][y]));
						}
					}
					//右上
					if (i != 7 && j != 0) {
						for (int x = i + 1, y = j - 1; x != 8 && y != -1; x++, y--) {
							fliplist[i][j][6].append(String.valueOf(board[x][y]));
						}
					}
					//左下
					if (i != 0 && j != 7) {
						for (int x = i - 1, y = j + 1; x != -1 && y != 8; x--, y++) {
							fliplist[i][j][7].append(String.valueOf(board[x][y]));
						}
					}
				}
			}
		}
	}

	//空黒白をカウント
	public void countStone() {
		e = 0;
		b = 0;
		w = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 1) {
					b++;
				} else if (board[i][j] == 2) {
					w++;
				} else {
					e++;
				}
			}
		}
	}
}
