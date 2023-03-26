package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class StartController {
	@FXML
	private AnchorPane pane;
	@FXML
	private ToggleGroup vs;
	@FXML
	private RadioButton cpu;
	@FXML
	private RadioButton cpuVsCpu;
	@FXML
	private RadioButton human;
	@FXML
	private Button next;

	@FXML
	void onCpu(ActionEvent event) {
		Player.mode = 0;
		Player.firstBlack = null;
		Player.cpuLevel = null;
	}

	@FXML
	void onHuman(ActionEvent event) {
		Player.mode = 1;
		Player.firstBlack = true;
		Player.cpuLevel = null;
	}

	@FXML
	void onCpuVsCpu(ActionEvent event) {
		Player.mode = 2;
		Player.firstBlack = true;
		Player.cpuLevel = 5;
	}

	//モードごとに異なる画面に遷移
	@FXML
	void onNext(ActionEvent event) {
		if (Player.mode != null) {
			Player.running = true;
			if (Player.mode == 0) {
				Scene scene = ((Node) event.getSource()).getScene();
				try {
					scene.setRoot(FXMLLoader.load(getClass().getResource("Level.fxml")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (Player.mode == 1) {
				Scene scene = ((Node) event.getSource()).getScene();
				try {
					scene.setRoot(FXMLLoader.load(getClass().getResource("GUI.fxml")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (Player.mode == 2) {
				Scene scene = ((Node) event.getSource()).getScene();
				try {
					scene.setRoot(FXMLLoader.load(getClass().getResource("GUI.fxml")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
