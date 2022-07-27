package mines;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

/*
MainForMines: The main class that runs the UI application (game) Minesweeper
Launches the FXML file for the menu layout, and generates the grid (via controller class).
Launches a new game (initially a 10x10 grid version), and allows the user to play multiple games.
 -- supports flags --
 */

public class MainForMines extends Application {

    @Override
    public void start(Stage stage) {
        // read FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlMines.fxml"));
        try {
            // load FXML
            HBox root=(fxmlLoader.load());
            // get controller
            MinesweeperController f = fxmlLoader.getController();
            // prepare stage properties
            stage.setTitle("Minesweeper");
            // credit for thumbnail : free icon by modeLiveSky.com
            stage.getIcons().add(new Image("mines/bomb_128x128.png"));
            // initialize the grid-less scene
            stage.setScene(new Scene(
                    root, Math.max(205 + f.getWidth() * 30,200),
                    Math.max(150,25 + f.getHeight() * 30)));
            // use the reset click generates the initial grid
            f.resetButtonClick(null);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
