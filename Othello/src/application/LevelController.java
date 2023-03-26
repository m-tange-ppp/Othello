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

public class LevelController {
	@FXML
	private AnchorPane pane;
	@FXML
	private ToggleGroup first;
	@FXML
	private RadioButton black;
	@FXML
	private RadioButton white;
	@FXML
	private ToggleGroup level;
	@FXML
	private RadioButton level1;
	@FXML
	private RadioButton level2;
	@FXML
	private RadioButton level3;
	@FXML
	private RadioButton level4;
	@FXML
	private Button start;

	@FXML
	void onBlack(ActionEvent event) {
		Player.firstBlack = true;
	}

	@FXML
	void onWhite(ActionEvent event) {
		Player.firstBlack = false;
	}

	@FXML
	void onLevel1(ActionEvent event) {
		Player.cpuLevel = 1;
	}

	@FXML
	void onLevel2(ActionEvent event) {
		Player.cpuLevel = 2;
	}

	@FXML
	void onLevel3(ActionEvent event) {
		Player.cpuLevel = 3;
	}

	@FXML
	void onLevel4(ActionEvent event) {
		Player.cpuLevel = 4;
	}

	@FXML
	void onStart(ActionEvent event) {
		if (Player.cpuLevel != null && Player.firstBlack != null) {
			Scene scene = ((Node) event.getSource()).getScene();
			try {
				scene.setRoot(FXMLLoader.load(getClass().getResource("GUI.fxml")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
