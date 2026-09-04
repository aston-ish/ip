package topaz.ui;

import javafx.fxml.FXML;
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
    }

    /**
     * Adds dialog boxes for the user's input and Topaz's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getTopazDialog(topaz.getResponse(input)));
        userInput.clear();
    }
}
