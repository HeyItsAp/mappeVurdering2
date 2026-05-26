package ntnu.gruppe21;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ntnu.gruppe21.view.menus.StartMenu;

public class Main extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  public void start(Stage stage) {
    Scene scene = new Scene(new StartMenu(stage), 1000, 700);
    stage.setScene(scene);
    stage.setTitle("Lets go gambling!");
    stage.setMaximized(true);
    stage.show();
  }
}
