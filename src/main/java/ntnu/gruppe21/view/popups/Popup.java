package ntnu.gruppe21.view.popups;

import java.util.List;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Base class for all modal popups in the game. Renders a centred card over a semi-transparent
 * background.
 *
 * <p>Subclasses implement {@link #buildContent()} to supply the nodes that go inside the card,
 * which is built once during construction.
 */
public abstract class Popup extends StackPane {
  private final VBox container;

  /** Creates the popup and calls {@link #buildContent()} to fill the card. */
  public Popup() {
    setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
    setAlignment(Pos.CENTER);

    container = new VBox(16);
    container.setMaxWidth(420);
    container.setMaxHeight(Region.USE_PREF_SIZE);
    container.setStyle(
        """
        -fx-background-color: white;
        -fx-background-radius: 15;
        -fx-border-color: #000000;
        -fx-border-radius: 12;
        -fx-border-width: 2;
        -fx-padding: 24;
        """);
    container.getChildren().addAll(buildContent());
    getChildren().add(container);

    container.setOnMouseClicked(Event::consume);
    setOnMouseClicked(e -> close());
  }

  /**
   * Builds and returns the nodes that form the popup's content card, top to bottom. Called once
   * during construction.
   *
   * @return ordered list of nodes to add to the card
   */
  protected abstract List<Node> buildContent();

  /** Sets the alignment of items inside the card container to be centered. */
  protected void setMiddleAlignment() {
    container.setAlignment(Pos.CENTER);
  }

  /**
   * Overlays this popup on the given pane, stretching it to fill the pane's bounds.
   *
   * @param parent the pane to display the popup over
   */
  public void show(Pane parent) {
    prefWidthProperty().bind(parent.widthProperty());
    prefHeightProperty().bind(parent.heightProperty());
    parent.getChildren().add(this);
  }

  /** Removes this popup from its parent and unbinds size properties. */
  public void close() {
    prefWidthProperty().unbind();
    prefHeightProperty().unbind();
    if (getParent() instanceof Pane parent) {
      parent.getChildren().remove(this);
    }
  }
}
