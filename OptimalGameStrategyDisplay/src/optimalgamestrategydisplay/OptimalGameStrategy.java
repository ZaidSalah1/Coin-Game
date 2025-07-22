/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package optimalgamestrategydisplay;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

public class OptimalGameStrategy extends Application {

    private static int[][] DP;

    // Method to calculate the maximum amount the first player can guarantee
    public static int optimalStrategy(int[] coins) {
        int n = coins.length;
        DP = new int[n][n]; // Initialize DP array

        // Fill the table for all subarray lengths
        for (int length = 1; length <= n; length++) {
            for (int i = 0, j = length - 1; j < n; i++, j++) {
                int pickFirst = i + 2 <= j ? DP[i + 2][j] : 0;
                int pickSecond = i + 1 <= j - 1 ? DP[i + 1][j - 1] : 0;
                int pickLast = i <= j - 2 ? DP[i][j - 2] : 0;
                System.out.println("pick First: " + pickFirst);
                System.out.println("pick second: " + pickSecond);
                System.out.println("pick Last: " + pickLast);
                
                DP[i][j] = Math.max(
                        coins[i] + Math.min(pickFirst, pickSecond),
                        coins[j] + Math.min(pickSecond, pickLast)
                );
                System.out.println("min: " +  Math.min(pickFirst, pickSecond));
                System.out.println("min: " +  Math.min(pickSecond, pickLast));
                System.out.println("Max: " + DP[i][j]);
                System.out.println("=======================");
            }
        }
        return DP[0][n - 1]; // Return the optimal result
    }

    @Override
    public void start(Stage primaryStage) {
        int[] coins = {4, 15, 7, 3, 8, 9};
        int maxWin = optimalStrategy(coins);
        System.out.println("Maximum amount the first player can guarantee to win: " + maxWin);

        GridPane grid = createDPGrid(DP, coins);
        grid.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane(grid);
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Dynamic Programming Table");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createDPGrid(int[][] DP, int[] coins) {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #000000; -fx-padding: 20; -fx-hgap: 5; -fx-vgap: 5;");

        // Add column headers
        for (int j = 0; j <= coins.length; j++) {
            StackPane header = j == 0 ? createHeader("Start") : createHeader("C" + (j - 1));
            grid.add(header, j, 0);
        }

        // Add row headers and DP values
        for (int i = 0; i < DP.length; i++) {
            StackPane rowHeader = createHeader("C" + i);
            grid.add(rowHeader, 0, i + 1); // Row header

            for (int j = i; j < DP[i].length; j++) {
                StackPane cell = createCell(DP[i][j]);
                grid.add(cell, j + 1, i + 1); // Offset by 1 to accommodate headers
            }
        }

        return grid;
    }

    private StackPane createCell(int value) {
        Label label = new Label(String.valueOf(value));
        label.setFont(new Font("Arial", 14));
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight: bold;");

        Rectangle background = new Rectangle(50, 30);
        background.setArcWidth(10);
        background.setArcHeight(10);
        background.setFill(Color.web("#444444"));
        background.setStroke(Color.web("#FFC107"));
        background.setStrokeWidth(2);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(4.0);
        shadow.setOffsetX(2.0);
        shadow.setOffsetY(2.0);
        shadow.setColor(Color.color(0, 0, 0, 0.6));
        background.setEffect(shadow);

        StackPane stack = new StackPane(background, label);
        stack.setAlignment(Pos.CENTER);
        return stack;
    }

    private StackPane createHeader(String text) {
        Label label = new Label(text);
        label.setFont(new Font("Arial", 16));
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight: bold;");

        Rectangle background = new Rectangle(60, 30);
        background.setArcWidth(10);
        background.setArcHeight(10);
        background.setFill(Color.web("#3F51B5")); // Distinct color for headers
        background.setStroke(Color.web("#FFC107"));
        background.setStrokeWidth(2);

        StackPane stack = new StackPane(background, label);
        stack.setAlignment(Pos.CENTER);
        return stack;
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
