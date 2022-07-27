package mines;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/*

MinesweeperController: A controller class for te Minesweeper application.
This class bridges the logic layer of the application (Mines class)
and the UI - the javaFX dependent MainForMines class.
This class defines the operations required for the Minesweeper game to run
from start to finish, including the unique messages for win/loss, and
defines the necessary operations for starting a new game.

 */

public class MinesweeperController {

    // extension for button (handle coordinates)
    private static class GridTile extends Button {
        private final int x;
        private final int y;
        private GridTile(int x, int y){
            this.x=x;
            this.y=y;
        }
    }

    // FXML file variables (scene objects)
    @FXML
    private TextField mineCountText;

    @FXML
    private TextField heightText;

    @FXML
    private TextField widthText;

    @FXML
    private Button resetButton;

    @FXML
    private Label modeLabel;

    // maintain a grid variable
    private GridPane grid;
    // maintain width,height, numMines for UI-logic interface
    private Integer width,height, numMines ;
    // maintain the minefield
    private Mines mineField;

    // for user feedback, maintain final tile (if lost, show it differently)
    private GridTile finalClickedTile;
    // keep track on game states
    private boolean gameWon, gameLost, gameRunning;
    // log victories and win ratio
    private static int victories = 0, totalGames = 0;

    // keep first click ->
    private boolean firstClick; // we don't want first click to kill player (implemented)

    // graphics - button font color (fit to neighbor count)
    private static final Color[] labelFontColorArr = new Color[]{
            Color.BLUE, Color.GREEN, Color.RED, Color.DARKBLUE,
            Color.DARKRED, Color.DARKORANGE, Color.BROWN,Color.BLACK};

    // helper variables for end-of-game pop-ups
    private final Background victoryBg = new Background(new BackgroundFill(Color.LIGHTGREEN,null,null));
    private final Background defeatBg = new Background(new BackgroundFill(Color.INDIANRED,null,null));

    // create first grid as a 10x10 grid
    public MinesweeperController(){
        init(10,10,10);
    }

    // getters for minefield width and height
    protected int getWidth(){return width;}
    protected int getHeight(){return height;}

    // initialize grid (called for each new game)
    private void init(int height, int width, int n){
        // set class variables
        this.width=width;
        this.height=height;
        numMines=n;
        gameLost = gameWon = false;
        gameRunning=true;
        firstClick = true;
        grid = new GridPane(); // create a grid
        mineField = new Mines(height,width,numMines); // create the minefield
        // add each cell to the grid
        for(int i=0;i<height;i++){ //
            for(int j=0;j<width;j++){
                grid.add(initGridTile(i,j),j,i);
            }
        }
    }

    // check for click conditions
    private void gridButtonManagement(MouseEvent mouseEvent,GridTile b){
        // check for right click (for flagging)
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            // if cell is closed - set flag
            if(!mineField.isOpen(b.x,b.y)) {
                mineField.toggleFlag(b.x, b.y);
            }
        }
        // click is left click
        else if(!mineField.isFlagged(b.x,b.y)){
            // if first click hit a mine, swap it to another location
            // (do not allow instant-loss situations)
            if(firstClick && mineField.hasMine(b.x,b.y)){
                    // move mine to another cell
                    mineField.moveMine(b.x,b.y);
                    firstClick=false;
            }
            // if location has mine
            if(mineField.hasMine(b.x,b.y)){
                // loss
                endGame(b, false);
            }
            // else if location is not flagged (do not open flagged tiles)
            else{
                // open cell
                mineField.open(b.x,b.y);
                // set first click to false (used only for first-hit-mine state)
                firstClick=false;
                // check victory
                if(mineField.isDone()){
                    // victory
                    endGame(b,true);

                }
            }
        }
    }

    // game over routine
    private void endGame(GridTile b, boolean win) {
        // set variables
        mineField.setShowAll(true);
        totalGames++;
        gameLost =!win;
        gameWon = win;
        gameRunning=false;
        finalClickedTile = b;
        // refresh grid (show win/loss situation)
        gridRefresh();
        // generate a unique pop-up for each condition
        if(win){
            victories++;
            // a new pop-up for win
            showNewWindow("Victory",
                    "Congratulations!\nWant to try again?\n(click reset for a new game)",
                    true);
        }
        else{
            // a new pop-up for loss
            showNewWindow("Defeat",
                    "You lost!\nTough luck, Try again?\n(click reset for a new game)",
                    false);
        }
        // show (cumulative) win to loss ratio
        String lowerText = "Classic mode (first click=no mine)";
        modeLabel.setText(lowerText + "\nWin/Loss: " +
                String.format("%.2f",(double)victories/totalGames));
    }

    // generate win/loss pop up
    private void showNewWindow(String title,String msg, boolean victory){
        Stage popUpWindow=new Stage();
        // keep new pop up on top until it's closed
        popUpWindow.getIcons().add(new Image(victory?"mines/happy.png":"mines/saddy.png"));
        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle(title);
        // set label for pop-up window (with background according to victory)
        Label label= new Label(msg);
        label.setAlignment(Pos.CENTER);
        label.setBackground(victory ? victoryBg : defeatBg);
        // alignment inside layout
        VBox layout= new VBox(10);
        layout.getChildren().add(label);
        layout.setAlignment(Pos.CENTER);
        // fit layout background to label background
        layout.setBackground(label.getBackground());
        // create the scene
        popUpWindow.setScene(new Scene(layout,225,120));
        popUpWindow.showAndWait(); // show the pop-up window
    }


    // method to check if a string is a number (int)
    private static boolean isNumeric(String strNum) {
        if (strNum == null)
            return false;
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    // assign a color value to each number
    private static Color countToFill(int n){
        assert(n>=1 && n<=8);
        return labelFontColorArr[n - 1];
    }

    // generate a tile for a grid coordinate
    private GridTile initGridTile(int x, int y){
        Insets twoSpaces = new Insets(2,2,2,2);
        GridTile g = new GridTile(x, y);
        String st = mineField.get(x,y); // get string for tile
        g.setText(st); // set string of tile
        // check for mines
        if(mineField.hasMine(x,y)){
            // in victory, show mines as non-dangerous
            if(gameWon){
                g.setText("M");
                g.setBackground(new Background(new BackgroundFill(Color.WHEAT,null,
                        twoSpaces)));
            }
            // in defeat, show mines as red X-es
            else if(gameLost){
                // and loss mine as extra-red
                if(finalClickedTile.x == x && finalClickedTile.y == y)
                    g.setBackground(new Background(new BackgroundFill(Color.RED,null,
                            twoSpaces)));
                else
                    g.setBackground(new Background(new BackgroundFill(Color.MEDIUMVIOLETRED,null,
                            twoSpaces)));
            }
        }
        // show open tiles as light blue (with a number indicating neighbor count)
        else if(gameLost || mineField.isOpen(x,y)) {
            g.setBackground(new Background(new BackgroundFill(
                    Color.LIGHTBLUE, null, twoSpaces)));
        }
        // if a cell has neighbors, set font for
        // neighbor count according to the number
        if(isNumeric(st)){
            g.setTextFill(countToFill(Integer.parseInt(st)));
        }
        // set size of each tile - cell
        g.setMinHeight(30); g.setMaxHeight(30);
        g.setMinWidth(30); g.setMaxWidth(30);
        // define the mouse click action for a tile button
        g.setOnMouseClicked(mouseEvent -> {
            // do not allow clicks for finished game
            if(gameRunning) {
                gridButtonManagement(mouseEvent, g);
                // do not refresh if game ended during last move (pop-up is on)
                if(gameRunning) gridRefresh();
            }
        });
        return g;
    }

    // refresh grid after each user interaction
    private void gridRefresh(){
        // remove current tiles
        grid.getChildren().removeAll(grid.getChildren());
        // add tiles in their current minefield state
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                grid.add(initGridTile(i,j),j,i);
            }
        }
    }

    // reset grid - this is called when user clicks the reset button
    private void resetGrid(){
        // cap number of mines to fit size
        if(numMines > width*height) {
            numMines = width*height-1;
        }
        // grab the stage
        Stage s = (Stage) resetButton.getScene().getWindow();
        // set stage parameters
        HBox root = new HBox();
        widthText.clear();
        heightText.clear();
        mineCountText.clear();

        widthText.setPromptText("width = " + width);
        heightText.setPromptText("height = " + height);
        mineCountText.setPromptText("mines = " + numMines);

        // insert everything except the grid to the new layout (HBox)
        root.getChildren().addAll(s.getScene().getRoot().getChildrenUnmodifiable().filtered(
                c -> !(c instanceof GridPane)));

        // initialize a new grid and mine object
        init(height, width, numMines);
        // assign new grid to layout
        root.getChildren().add(grid);
        // set the new scene for out stage
        Scene newScene = new Scene(root, Math.max(205 + width * 30,200),
                Math.max(150,25 + height * 30));
        s.setScene(newScene);
        s.show(); // and show the new stage
    }

    // reset button click operation
    @FXML
    void  resetButtonClick(ActionEvent ignoredEvent) {
        // get text from labels
        // if user replaced either size or mine count (or all), get new ones
        try {
            width = Integer.parseInt(widthText.getText());
        }catch(NumberFormatException ignored){}
        try {
            height = Integer.parseInt(heightText.getText());
        }catch(NumberFormatException ignored){}
        try {
            numMines = Integer.parseInt(mineCountText.getText());
        }catch(NumberFormatException ignored){}

        // reset with current parameters
        resetGrid();
        }

}
