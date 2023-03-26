package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class Player {
	GraphicsContext g;
	Board board;
	StringBuffer[] sb;
	Label turn;
	Label black;
	Label white;
	Label result;
	boolean isBlack = true; //現在のターン
	boolean isHuman = false; //人のターン
	final Pattern PATTERN_BLACK = Pattern.compile("^2+1"); //白が続いて黒で終わるパターン
	final Pattern PATTERN_WHITE = Pattern.compile("^1+2"); //黒が続いて白で終わるパターン
	final List<int[]> COORDINATE_LIST = new ArrayList<>(); //座標のリスト
	{
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				COORDINATE_LIST.add(new int[] { i, j });
			}
		}
	}
	//CPUができるだけ置きたくない座標
	final int[][] NEAR_CORNER = { { 1, 0 }, { 0, 1 }, { 1, 1 }, { 0, 6 }, { 1, 7 }, { 1, 6 }, { 6, 0 }, { 7, 1 },
			{ 6, 1 }, { 7, 6 }, { 6, 7 }, { 6, 6 } };
	static Integer mode = null; //0CPU1自分2爆速CPUバトル
	static Boolean firstBlack = null; //最初が黒
	static Integer cpuLevel = null; //1～4はvsCPUで5は爆速CPUバトル
	static boolean running = true; //CPUの停止に使う

	public Player(Canvas tbl, Label turn, Label black, Label white, Label result) {
		this.g = tbl.getGraphicsContext2D();
		this.board = new Board(tbl);
		this.turn = turn;
		this.black = black;
		this.white = white;
		this.result = result;
		disp();
		tbl.setOnMouseClicked(this::click);
		if (!firstBlack && (mode == 0)) {
			aiMove();
		} else if (mode == 2) {
			aiMove();
		}
	}

	//ルールはそのままで初期状態に戻す
	public void resetAll() {
		isBlack = true;
		isHuman = false;
		running = false;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board.board[i][j] = 0;
			}
		}
		board.setStone();
		board.countStone();
		result.setText("");
		disp();
		if (!firstBlack && (mode != 1)) {
			aiMove();
		} else if (mode == 2) {
			aiMove();
		}
	}

	//指定位置が空か判定して返す
	public boolean hitCheck(int i, int j) {
		if (board.board[i][j] == 0) {
			return true;
		} else {
			return false;
		}
	}

	//指定位置に石を置く
	public void hit(int i, int j, int[][] board, boolean isBlack) {
		if (isBlack) {
			board[i][j] = 1;
		} else {
			board[i][j] = 2;
		}
	}

	//ある方向に対してひっくり返す情報をつくり枚数を数えて返す
	public int flipNum(StringBuffer sb, boolean isBlack) {
		int l = 0;
		if (isBlack) {
			Matcher m = PATTERN_BLACK.matcher(sb);
			if (m.find()) {
				l = m.group().length();
				for (int k = 0; k < l; k++) {
					sb.setCharAt(k, '1');
				}
			}
		} else {
			Matcher m = PATTERN_WHITE.matcher(sb);
			if (m.find()) {
				l = m.group().length();
				for (int k = 0; k < l; k++) {
					sb.setCharAt(k, '2');
				}
			}
		}
		return l;
	}

	// ある方向に対してひっくり返せるか確認して返す
	public boolean canFlip(StringBuffer sb, boolean isBlack) {
		if (isBlack) {
			Matcher m = PATTERN_BLACK.matcher(sb);
			if (m.find()) {
				return true;
			}
		} else {
			Matcher m = PATTERN_WHITE.matcher(sb);
			if (m.find()) {
				return true;
			}
		}
		return false;
	}

	// flipNumを8方向に適用してひっくり返す情報を返す
	public boolean flipCheck(int i, int j, StringBuffer[][][] fliplist, boolean isBlack) {
		int sum = 0;
		for (int k = 0; k < 8; k++) {
			sum += flipNum(fliplist[i][j][k], isBlack);
		}
		if (sum == 0) {
			return false;
		} else {
			return true;
		}
	}

	//canFlipを全空マス全方向に適用してひっくり返せるマスがあるか判定して返す
	public boolean flipAllCheck(boolean flagBlack) {
		int sum = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board.board[i][j] == 0) {
					for (int k = 0; k < 8; k++) {
						if (canFlip(board.flipList[i][j][k], flagBlack))
							sum++;
					}
				}
			}
		}
		if (sum == 0) {
			return false;
		} else {
			return true;
		}
	}

	//8方向それぞれをひっくり返す
	public void flip(int i, int j, int[][] board, StringBuffer[][][] fliplist) {
		if (i != 7) { //右
			for (int x = i + 1; x != 8; x++) {
				board[x][j] = fliplist[i][j][0].charAt(x - (i + 1)) - 48;
			}
		}
		if (i != 0) { //左
			for (int x = i - 1; x != -1; x--) {
				board[x][j] = fliplist[i][j][1].charAt((i - 1) - x) - 48;
			}
		}
		if (j != 7) { //下
			for (int y = j + 1; y != 8; y++) {
				board[i][y] = fliplist[i][j][2].charAt(y - (j + 1)) - 48;
			}
		}
		if (j != 0) { //上
			for (int y = j - 1; y != -1; y--) {
				board[i][y] = fliplist[i][j][3].charAt((j - 1) - y) - 48;
			}
		}
		if (i != 7 && j != 7) { //右下
			for (int x = i + 1, y = j + 1; x != 8 && y != 8; x++, y++) {
				board[x][y] = fliplist[i][j][4].charAt(x - (i + 1)) - 48;
			}
		}
		if (i != 0 && j != 0) { //左上
			for (int x = i - 1, y = j - 1; x != -1 && y != -1; x--, y--) {
				board[x][y] = fliplist[i][j][5].charAt((i - 1) - x) - 48;
			}
		}
		if (i != 7 && j != 0) { //右上
			for (int x = i + 1, y = j - 1; x != 8 && y != -1; x++, y--) {
				board[x][y] = fliplist[i][j][6].charAt(x - (i + 1)) - 48;
			}
		}
		if (i != 0 && j != 7) { //左下
			for (int x = i - 1, y = j + 1; x != -1 && y != 8; x--, y++) {
				board[x][y] = fliplist[i][j][7].charAt((i - 1) - x) - 48;
			}
		}
	}

	// aiのfilterに使う。座標を引数にとり、ひっくり返すマスがあるか判定して返す
	public boolean flipFilter(int[] a) {
		boolean b = false;
		if (isBlack) {
			for (int k = 0; k < 8; k++) {
				if (PATTERN_BLACK.matcher(board.flipList[a[0]][a[1]][k]).find()) {
					b = true;
				}
			}
		} else {
			for (int k = 0; k < 8; k++) {
				if (PATTERN_WHITE.matcher(board.flipList[a[0]][a[1]][k]).find()) {
					b = true;
				}
			}
		}
		return b;
	}

	//ターンと個数を表示する
	public void disp() {
		if (isBlack) {
			turn.setStyle("-fx-text-fill:white;-fx-background-color:black");
			turn.setText("黒の番");
		} else {
			turn.setStyle("-fx-text-fill:black;-fx-background-color:white;-fx-border-color:black;-fx-border-width:1");
			turn.setText("白の番");
		}
		black.setText(String.format("黒%d個", board.b));
		white.setText(String.format("白%d個", board.w));
	}

	//試合終了の判定をして画面表示する
	public boolean finish() {
		if ((board.b == 0 || board.w == 0 || board.e == 0) || (!flipAllCheck(isBlack) && !flipAllCheck(!isBlack))) {
			if (board.b > board.w) {
				result.setText("黒の勝ち");
			} else if (board.b < board.w) {
				result.setText("白の勝ち");
			} else {
				result.setText("引き分け");
			}
			turn.setText("");
			turn.setStyle("-fx-border-width:0");
			return true;
		} else {
			return false;
		}
	}

	//人とCPUが共通して行う処理
	public void routine(int i, int j) {
		if (hitCheck(i, j) && flipCheck(i, j, board.flipList, isBlack)) {
			hit(i, j, board.board, isBlack);
			flip(i, j, board.board, board.flipList);
			isBlack = !isBlack;
			board.flipPattern(board.flipList, board.board);
			board.drawBoard();
			board.countStone();
			disp();
			if (finish()) {
				return;
			}
			if (!flipAllCheck(isBlack)) {
				isBlack = !isBlack;
				disp();
				if (!isHuman && (mode != 1)) {
					aiMove();
				}
				return;
			}
			if (isHuman && (mode == 0)) {
				aiMove();
			} else if (mode == 2) {
				aiMove();
			}
		}
	}

	//クリック時に行う、人の処理
	public void click(MouseEvent e) {
		isHuman = true;
		running = true;
		board.flipPattern(board.flipList, board.board);
		double x = e.getX();
		double y = e.getY();
		LOOP_I: for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((50 * i <= x) && (x < 50 * (i + 1)) && (50 * j <= y) && (y < 50 * (j + 1))) {
					routine(i, j);
					break LOOP_I;
				}
			}
		}
	}

	//AIが打つマスを決める。完全ランダム
	public int[] aiRandom() {
		List<int[]> choices = new ArrayList<>(); //選択肢のリスト
		choices = COORDINATE_LIST.stream()
				.filter(a -> board.board[a[0]][a[1]] == 0)
				.filter(a -> flipFilter(a)).collect(Collectors.toList());
		Random rand = new Random();
		return choices.get(rand.nextInt(choices.size()));
	}

	//MaxMinの比較に使う
	public int aiMaxMinCompareRoutine(int[] a, Pattern PATTERN) {
		int sum = 0;
		Matcher m;
		for (int k = 0; k < 8; k++) {
			m = PATTERN.matcher(board.flipList[a[0]][a[1]][k]);
			if (m.find()) {
				sum += m.group().length() - 1;
			}
		}
		return sum;
	}

	//AIが打つマスを決める。最小個数をとる
	public int[] aiMin() {
		List<int[]> choices = new ArrayList<>(); //選択肢のリスト
		choices = COORDINATE_LIST.stream()
				.filter(a -> board.board[a[0]][a[1]] == 0)
				.filter(a -> flipFilter(a))
				.sorted(new Comparator<int[]>() {

					@Override
					public int compare(int[] a, int[] b) { //並び替えのルール
						int sumA = 0;
						int sumB = 0;
						if (isBlack) {
							sumA = aiMaxMinCompareRoutine(a, PATTERN_BLACK);
							sumB = aiMaxMinCompareRoutine(b, PATTERN_BLACK);
						} else {
							sumA = aiMaxMinCompareRoutine(a, PATTERN_WHITE);
							sumB = aiMaxMinCompareRoutine(b, PATTERN_WHITE);
						}
						return sumA - sumB; //昇順
					}
				}).collect(Collectors.toList());
		return choices.get(0);
	}

	//AIが打つマスを決める。最大個数をとり、危険なマスをできるだけ回避
	public int[] aiMax() {
		List<int[]> choices = new ArrayList<>(); //選択肢のリスト
		choices = COORDINATE_LIST.stream()
				.filter(a -> board.board[a[0]][a[1]] == 0)
				.filter(a -> flipFilter(a))
				.sorted(new Comparator<int[]>() {

					@Override
					public int compare(int[] a, int[] b) { //並び替えのルール
						int sumA = 0;
						int sumB = 0;
						if (isBlack) {
							sumA = aiMaxMinCompareRoutine(a, PATTERN_BLACK);
							sumB = aiMaxMinCompareRoutine(b, PATTERN_BLACK);
						} else {
							sumA = aiMaxMinCompareRoutine(a, PATTERN_WHITE);
							sumB = aiMaxMinCompareRoutine(b, PATTERN_WHITE);
						}
						return sumB - sumA; //降順
					}
				}).collect(Collectors.toList());
		//危険なマスをできるだけ回避
		LABEL_I: for (int i = 0; i < choices.size() - 1; i++) {
			int[] choice = choices.get(i);
			for (int[] danger : NEAR_CORNER) {
				if (Arrays.equals(danger, choice)) {
					continue LABEL_I;
				}
			}
			return choice;
		}
		return choices.get(choices.size() - 1);
	}

	//StrongWeakの比較に使う
	public void aiStrongWeakCompareRoutine(int[] a, int[][] imageBoard, StringBuffer[][][] flipList, boolean isBlack,
			int sum) {
		board.flipPattern(flipList, imageBoard);
		flipCheck(a[0], a[1], flipList, isBlack);
		hit(a[0], a[1], imageBoard, isBlack);
		flip(a[0], a[1], imageBoard, flipList);
		isBlack = !isBlack;
		board.flipPattern(flipList, imageBoard);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					if (canFlip(flipList[i][j][k], isBlack)) {
						sum++;
						break;
					}
				}
			}
		}
	}

	//AIが打つマスを決める。相手が置ける場所が少ないマスをとり、危険なマスをできるだけ回避
	public int[] aiStrong() {
		List<int[]> choices = new ArrayList<>(); //選択肢のリスト
		choices = COORDINATE_LIST.stream()
				.filter(a -> board.board[a[0]][a[1]] == 0)
				.filter(a -> flipFilter(a))
				.sorted(new Comparator<int[]>() {

					@Override
					public int compare(int[] a, int[] b) { //並び替えのルール
						int[][] imageBoardA = new int[8][8];
						for (int i = 0; i < 8; i++) {
							imageBoardA[i] = board.board[i].clone();
						}
						int[][] imageBoardB = new int[8][8];
						for (int i = 0; i < 8; i++) {
							imageBoardB[i] = board.board[i].clone();
						}
						StringBuffer[][][] flipListA = new StringBuffer[8][8][8];
						{
							for (int i = 0; i < 8; i++) {
								for (int j = 0; j < 8; j++) {
									for (int k = 0; k < 8; k++) {
										flipListA[i][j][k] = new StringBuffer("0");
									}
								}
							}
						}
						StringBuffer[][][] flipListB = new StringBuffer[8][8][8];
						{
							for (int i = 0; i < 8; i++) {
								for (int j = 0; j < 8; j++) {
									for (int k = 0; k < 8; k++) {
										flipListB[i][j][k] = new StringBuffer("0");
									}
								}
							}
						}
						boolean isBlackA = isBlack;
						boolean isBlackB = isBlack;
						int sumA = 0;
						int sumB = 0;
						//
						aiStrongWeakCompareRoutine(a, imageBoardA, flipListA, isBlackA, sumA);
						aiStrongWeakCompareRoutine(a, imageBoardB, flipListB, isBlackB, sumB);
						return sumA - sumB; //昇順
					}
				}).collect(Collectors.toList());
		//危険なマスをできるだけ回避
		LABEL_I: for (int i = 0; i < choices.size() - 1; i++) {
			int[] choice = choices.get(i);
			for (int[] danger : NEAR_CORNER) {
				if (Arrays.equals(danger, choice)) {
					continue LABEL_I;
				}
			}
			return choice;
		}
		return choices.get(choices.size() - 1);
	}

	//AIが打つマスを決める。相手が置ける場所が多いマスをとる
	public int[] aiWeak() {
		List<int[]> choices = new ArrayList<>(); //選択肢のリスト
		choices = COORDINATE_LIST.stream()
				.filter(a -> board.board[a[0]][a[1]] == 0)
				.filter(a -> flipFilter(a))
				.sorted(new Comparator<int[]>() {

					@Override
					public int compare(int[] a, int[] b) { //並び替えのルール
						int[][] imageBoardA = new int[8][8];
						for (int i = 0; i < 8; i++) {
							imageBoardA[i] = board.board[i].clone();
						}
						int[][] imageBoardB = new int[8][8];
						for (int i = 0; i < 8; i++) {
							imageBoardB[i] = board.board[i].clone();
						}
						StringBuffer[][][] flipListA = new StringBuffer[8][8][8];
						{
							for (int i = 0; i < 8; i++) {
								for (int j = 0; j < 8; j++) {
									for (int k = 0; k < 8; k++) {
										flipListA[i][j][k] = new StringBuffer("0");
									}
								}
							}
						}
						StringBuffer[][][] flipListB = new StringBuffer[8][8][8];
						{
							for (int i = 0; i < 8; i++) {
								for (int j = 0; j < 8; j++) {
									for (int k = 0; k < 8; k++) {
										flipListB[i][j][k] = new StringBuffer("0");
									}
								}
							}
						}
						boolean isBlackA = isBlack;
						boolean isBlackB = isBlack;
						int sumA = 0;
						int sumB = 0;
						//
						aiStrongWeakCompareRoutine(a, imageBoardA, flipListA, isBlackA, sumA);
						aiStrongWeakCompareRoutine(a, imageBoardB, flipListB, isBlackB, sumB);
						return sumB - sumA; //降順
					}
				}).collect(Collectors.toList());
		return choices.get(0);
	}

	//CPUを動かす
	public void aiMove() {
		Task<Boolean> task = new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				if (cpuLevel != 5) {
					Thread.sleep(500);
				} else {
					Thread.sleep(100);
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (running) {
							isHuman = false;
							board.flipPattern(board.flipList, board.board);
							int[] hitai;
							if (cpuLevel == 1) {
								hitai = aiWeak();
							} else if (cpuLevel == 2) {
								hitai = aiMin();
							} else if (cpuLevel == 3) {
								hitai = aiMax();
							} else if (cpuLevel == 4) {
								hitai = aiStrong();
							} else {
								hitai = aiRandom();
							}
							routine(hitai[0], hitai[1]);
						}
					}
				});
				return null;
			}
		};
		Thread thread = new Thread(task);
		thread.start();
	}
}