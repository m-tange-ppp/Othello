package application;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Stone {
	GraphicsContext g;

	public Stone(Canvas tbl) {
		this.g = tbl.getGraphicsContext2D();
	}
	
	//黒を描画
	public void drawBlack(int i, int j) {
		g.setFill(Color.BLACK);
		g.fillOval(50 * i, 50 * j, 50, 50);
	}

	//白を描画
	public void drawWhite(int i, int j) {
		g.setFill(Color.WHITE);
		g.fillOval(50 * i, 50 * j, 50, 50);
	}
}
