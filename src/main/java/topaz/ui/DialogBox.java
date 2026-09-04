package topaz.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents one message in the Topaz conversation.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

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
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("topaz-dialog");
    }

    /**
     * Creates a right-aligned dialog box for a user message.
     *
     * @param text the user's message
     * @return the created dialog box
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text);
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
}
