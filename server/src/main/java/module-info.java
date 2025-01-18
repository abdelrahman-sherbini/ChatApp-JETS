module gov.iti.jets {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    opens gov.iti.jets to javafx.fxml;
    opens gov.iti.jets.controller to javafx.fxml;
    exports gov.iti.jets.controller;
    exports gov.iti.jets;
}
