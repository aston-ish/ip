package topaz.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Represents one message in the Topaz conversation.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private VBox message;

    @FXML
    private Label heading;

    /**
     * Creates a dialog box containing the given message.
     *
     * @param text the message displayed in the dialog box
     */
    private DialogBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialog.setText(text);
    }

    /**
     * Flips a dialog box so that it is aligned on the left for a Topaz response.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        message.getStyleClass().add("topaz-dialog");
        HBox.setHgrow(message, Priority.ALWAYS);
    }

    /**
     * Creates a right-aligned dialog box for a user message.
     *
     * @param text the user's message
     * @return the created dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.heading.setVisible(false);
        dialogBox.heading.setManaged(false);
        dialogBox.message.getStyleClass().add("user-dialog");
        dialogBox.message.maxWidthProperty().bind(dialogBox.widthProperty().multiply(0.8));
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box for a Topaz response.
     *
     * @param text Topaz's response
     * @return the created dialog box
     */
    public static DialogBox getTopazDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates an error card with both a text heading and a distinct color.
     *
     * @param text the error explanation
     * @return the created error card
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getTopazDialog(text);
        dialogBox.heading.setText("TOPAZ / ERROR");
        dialogBox.message.getStyleClass().add("error-dialog");
        return dialogBox;
    }
}
