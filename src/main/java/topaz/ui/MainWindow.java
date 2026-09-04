package topaz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controller for the main Topaz graphical user interface.
 */
public class MainWindow {
    private static final String RESPONSE_PREFIX = "Topaz heard: ";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    /**
     * Configures automatic scrolling when a dialog box is added.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Adds dialog boxes for the user's input and Topaz's temporary echo response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getTopazDialog(RESPONSE_PREFIX + input));
        userInput.clear();
    }
}
