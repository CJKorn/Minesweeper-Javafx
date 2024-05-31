//Mine = X
//Empty = " "
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.event.*;
import java.lang.*;
import javafx.geometry.*;

public class Minesweeper extends Application {
    Background DefaultBG;
    HBox hbox = new HBox(10);
    VBox root = new VBox();
    Text title = new Text("Minesweeper");
    int width = 20;
    int height = 15;
    int mines = 35;
    int clickedCells = 0;
    boolean firstButton = true;
    boolean flags = false;
    public static void main(String[] args) { launch(args); }
    GridPane grid = new GridPane();
    Scene scene = new Scene(root);
    Button printBoard = new Button("Print Board");
    Button newGame = new Button("New Game");
    TextField widthField = new TextField(Integer.toString(width));
    TextField heightField = new TextField(Integer.toString(height));
    TextField mineField = new TextField(Integer.toString(mines)); //Ha
    Text flagsText = new Text("Flag:");
    ToggleButton flagsButton = new ToggleButton("⚑");

    @Override
    public void start(Stage stage) throws Exception {
        newGame();
        flagsButton.setMinSize(30, 30);
        flagsText.setTextAlignment(TextAlignment.CENTER);
        hbox.getChildren().addAll(newGame, printBoard, widthField, heightField, mineField, flagsText, flagsButton);
        widthField.setMaxWidth(75);
        heightField.setMaxWidth(75);
        mineField.setMaxWidth(75);
        printBoard.setOnAction(event -> printBoard());
        newGame.setOnAction(event -> newGame());
        flagsButton.setOnKeyPressed(event -> flags = !flags);
        DefaultBG = newGame.getBackground();
        root.getChildren().addAll(title, grid, hbox);
        stage.setScene(scene);
        stage.show();
    }
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) { //https://stackoverflow.com/questions/20655024/javafx-gridpane-retrieve-specific-cell-content
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
    private void buttonClicked(Button button) {
        if (flagsButton.isSelected()) {
            if (button.getText().equals("*")) {
                button.setText("");
            }
            else if (button.getText().equals("")) {
                button.setText("*");
            }
        }
        else {
            if (firstButton == true) {
                initializeBoard(GridPane.getColumnIndex(button), GridPane.getRowIndex(button));
                firstButton = false;
                if (!button.getUserData().equals("")) {
                    unhideEmptyCells(GridPane.getColumnIndex(button), GridPane.getRowIndex(button));
                }
            }
            if (!button.getText().equals("*")) {
                String currentValue = (String) button.getUserData();
                button.setText(currentValue);
                // clickedCells++;
                switch (currentValue) {
                    case "X":
                        System.out.println("Game Over");
                        title.setText("You Lose! - Revealed: " + clickedCells);
                        showMines();
                        break;
                    case "":
                        // button.setUserData(" ");
                        button.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.LIGHTGRAY, null, null)));
                        unhideEmptyCells(GridPane.getColumnIndex(button), GridPane.getRowIndex(button));
                        // button.setDisable(true);
                        break;
                    default:
                        button.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.LIGHTGRAY, null, null)));
                        clickedCells++;
                        // button.setDisable(true);
                        break;
                }
                if (!currentValue.equals("X")) {
                    title.setText("Minesweeper - Revealed: " + clickedCells);
                }
                if (clickedCells >= height*width-mines) {
                    title.setText("You win! - Revealed: " + clickedCells);
                    showMines();
                }
            }
            else {
                button.setText("");
            }
        }
    }
    private void printBoard() {
        String currentItem = "";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Button button = (Button) getNodeFromGridPane(grid, j, i);
                if (button.getUserData().equals("")) {
                    currentItem = " ";
                }
                else {
                    currentItem = (String) button.getUserData();
                }
                System.out.print("[" + currentItem + "]");
            }
            System.out.println();
        }
    }
    private void initializeBoard(int clcx, int clcy) {
        boolean validPos = false;
        for (int i = 0; i < mines; i++) {
            validPos = false;
            while (!validPos) {
                int x = (int) (Math.random() * width);
                int y = (int) (Math.random() * height);
                if ((!getNodeFromGridPane(grid, x, y).getUserData().equals("X")) && !(Math.abs(x - clcx) <= 3 && Math.abs(y - clcy) <= 3)) {
                    validPos = true;
                    Button button = (Button) getNodeFromGridPane(grid, x, y);
                    button.setUserData("X");
                    incrementSurroundingTiles(x, y);
                }
            }
        }
    }
    private void incrementSurroundingTiles(int x, int y) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < width && j >= 0 && j < height) {
                    Button button = (Button) getNodeFromGridPane(grid, i, j);
                    String currentValue = (String) button.getUserData();
                    if (!currentValue.equals("X")) {
                        if (currentValue.equals("")) {
                            button.setUserData("1");
                        }
                        else {
                            int newValue = Integer.parseInt(currentValue) + 1;
                            button.setUserData(Integer.toString(newValue));
                            // button.setText(Integer.toString(newValue));
                        }
                    }
                }
            }
        }
    }
    private void unhideEmptyCells(int x, int y) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < width && j >= 0 && j < height) {
                    Button button = (Button) getNodeFromGridPane(grid, i, j);
                    String currentValue = (String) button.getUserData();
                    String shownValue = (String) button.getText();
                    button.setDisable(true);
                    button.setStyle("-fx-opacity: 0.9");
                    button.setText(currentValue);
                    if (shownValue.equals("")) {
                        clickedCells++;
                    }
                    if ((currentValue.equals(""))) {
                        button.setUserData(" ");
                        button.setText(" ");
                        button.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.LIGHTGRAY, null, null)));
                        unhideEmptyCells(i, j);
                    }
                    else {
                        button.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.LIGHTGRAY, null, null)));
                    }
                }
            }
        }
    }
    public void showMines() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Button button = (Button) getNodeFromGridPane(grid, i, j);
                button.setDisable(true);
                button.setStyle("-fx-opacity: 0.9");
                if (button.getUserData().equals("X")) {
                    button.setText("X");
                }
            }
        }
        // title.setText("You Lose :( - Tiles Cleared: " + clickedCells);
    }
    public void newGame() {
        width = Integer.valueOf(widthField.getText());
        height = Integer.valueOf(heightField.getText());
        mines = Integer.valueOf(mineField.getText());
        grid.getChildren().clear();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Button button = new Button();
                button.setOnAction(event -> buttonClicked(button));
                button.setUserData("");
                button.setMinSize(30, 30);
                grid.add(button, i, j);
            }
        }
        clickedCells = 0;
        firstButton = true;
        title.setText("Minesweeper");
    }
}
