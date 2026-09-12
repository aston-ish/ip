package topaz.ui;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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
    private final Image userImage = new Image(Objects.requireNonNull(
            getClass().getResourceAsStream("/images/user.png")));
    private final Image topazImage = new Image(Objects.requireNonNull(
            getClass().getResourceAsStream("/images/topaz.png")));

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
                "Hello! I'm Topaz.\nWhat can I do for you?", topazImage));
    }

    /**
     * Adds dialog boxes for the user's input and Topaz's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = topaz.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getTopazDialog(response, topazImage));
        userInput.clear();
        if (topaz.isExitRequested()) {
            userInput.setDisable(true);
            userInput.setPromptText("Topaz session ended");
            sendButton.setDisable(true);
        }
    }
}
