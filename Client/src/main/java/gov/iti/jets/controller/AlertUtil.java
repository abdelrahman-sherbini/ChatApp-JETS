package gov.iti.jets.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;

public class AlertUtil {
    public static void showErrorAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText("Connection Timeout");
        alert.setHeaderText("Connection disconnected, Please try again later.");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
}
