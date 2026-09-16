package topaz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import topaz.Topaz;

/**
 * Controller for the main Topaz graphical user interface.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Topaz topaz;
    /**
     * Configures automatic scrolling when a dialog box is added.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the Topaz instance that processes user commands.
     *
     * @param topaz the application logic used by this window
     */
    public void setTopaz(Topaz topaz) {
        this.topaz = topaz;
        dialogContainer.getChildren().add(DialogBox.getTopazDialog(
                "Hello! I'm Topaz.\nWhat can I do for you?"));
    }

    /**
     * Adds dialog boxes for the user's input and Topaz's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = topaz.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getTopazDialog(response));
        userInput.clear();
        if (topaz.isExitRequested()) {
            userInput.setDisable(true);
            userInput.setPromptText("Topaz session ended");
            sendButton.setDisable(true);
        }
    }
}
