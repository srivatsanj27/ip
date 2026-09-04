package ace.gui;

import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A single message bubble in the chat history: a text label paired with an
 * avatar image, laid out side by side. Use {@link #getUserDialog(String, Image)}
 * or {@link #getAceDialog(String, Image)} to create one, rather than the
 * constructor directly, so user and Ace messages are styled consistently.
 */
public class DialogBox extends HBox {
    private Label text;
    private ImageView displayPicture;

    private DialogBox(String s, Image i) {
        text = new Label(s);
        displayPicture = new ImageView(i);

        text.setWrapText(true);
        displayPicture.setFitWidth(100.0);
        displayPicture.setFitHeight(100.0);
        this.setAlignment(Pos.TOP_RIGHT);

        this.getChildren().addAll(text, displayPicture);
    }

    /**
     * Reverses the order of this dialog box's children and left-aligns it,
     * so the avatar appears on the left instead of the right — used to
     * visually distinguish Ace's replies from the user's own messages.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for a message the user sent.
     *
     * @param s the message text.
     * @param i the user's avatar image.
     * @return a right-aligned dialog box.
     */
    public static DialogBox getUserDialog(String s, Image i) {
        return new DialogBox(s, i);
    }

    /**
     * Creates a dialog box for one of Ace's responses.
     *
     * @param s the message text.
     * @param i Ace's avatar image.
     * @return a left-aligned dialog box, visually distinct from the user's own messages.
     */
    public static DialogBox getAceDialog(String s, Image i) {
        var db = new DialogBox(s, i);
        db.flip();
        return db;
    }
}
