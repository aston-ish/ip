package topaz.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Verifies message layouts using real FXML and CSS on the JavaFX thread.
 */
class DialogBoxTest {
    @BeforeAll
    static void startToolkit() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> null);
        Platform.startup(startup);
        startup.get(10, TimeUnit.SECONDS);
        Platform.setImplicitExit(false);
    }

    @Test
    void dialogs_longMessages_wrapWithinConversation() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            String text = "A long task description with several words ".repeat(20);
            DialogBox user = DialogBox.getUserDialog(text);
            DialogBox response = DialogBox.getTopazDialog(text);
            VBox conversation = new VBox(user, response);
            new Scene(conversation, 400, 600);
            conversation.applyCss();
            conversation.layout();

            Label userHeading = (Label) user.lookup(".message-heading");
            Label responseHeading = (Label) response.lookup(".message-heading");
            assertFalse(userHeading.isManaged());
            assertEquals("TOPAZ", responseHeading.getText());
            assertTrue(responseHeading.isVisible());
            assertTrue(user.lookup(".user-dialog").getBoundsInParent().getWidth() <= 320);
            assertTrue(response.lookup(".topaz-dialog").getBoundsInParent().getWidth() > 320);
            for (DialogBox box : new DialogBox[] {user, response}) {
                Label message = (Label) box.lookup(".message-text");
                assertEquals(text, message.getText());
                assertTrue(message.isWrapText());
                assertTrue(message.getHeight() > 50);
                assertTrue(message.getWidth() <= 400);
            }
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }
    @Test
    void errorDialog_failureThenNormalResponse_hasDistinctAppearance() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            DialogBox error = DialogBox.getErrorDialog("Unknown command. Try list.");
            DialogBox normal = DialogBox.getTopazDialog("Here are your tasks.");
            VBox conversation = new VBox(error, normal);
            new Scene(conversation, 400, 600);
            conversation.applyCss();
            conversation.layout();

            Label heading = (Label) error.lookup(".message-heading");
            Label message = (Label) error.lookup(".message-text");
            assertEquals("TOPAZ / ERROR", heading.getText());
            assertEquals("Unknown command. Try list.", message.getText());
            assertEquals(javafx.scene.paint.Color.web("#7a271a"), message.getTextFill());
            assertTrue(error.lookup(".error-dialog") != null);
            assertTrue(normal.lookup(".error-dialog") == null);
            assertEquals("TOPAZ", ((Label) normal.lookup(".message-heading")).getText());
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

}
