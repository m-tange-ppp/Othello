package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class GUIController implements Initializable {
	@FXML
	private AnchorPane pane;
	@FXML
	private Button reset;
	@FXML
	private Button resetboard;
	@FXML
	private Label result;
	@FXML
	private Canvas tbl;
	@FXML
	private Label turn;
	@FXML
	private Label black;
	@FXML
	private Label white;

	Player player;

	@FXML
	void onReset(ActionEvent event) {
		Scene scene = ((Node) event.getSource()).getScene();
		try {
			scene.setRoot(FXMLLoader.load(getClass().getResource("Start.fxml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Player.firstBlack = null;
		Player.mode = null;
		Player.cpuLevel = null;
		Player.running = false;
	}

	@FXML
	void onResetBoard(ActionEvent event) {
		player.resetAll();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.player = new Player(tbl, turn, black, white, result);
	}
}
