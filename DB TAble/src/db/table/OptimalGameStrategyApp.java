
package db.table;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class OptimalGameStrategyApp extends Application {

    // Method to calculate the maximum amount the first player can guarantee
    public static int optimalStrategy(int[] coins, int[][] DP) {
        int n = coins.length;

        // Fill the table for all subarray lengths
        for (int length = 1; length <= n; length++) {
            for (int i = 0, j = length - 1; j < n; i++, j++) {
                // Calculate values for DP[i][j] based on subarray length
                int pickFirst = i + 2 <= j ? DP[i + 2][j] : 0;
                int pickSecond = i + 1 <= j - 1 ? DP[i + 1][j - 1] : 0;
                int pickLast = i <= j - 2 ? DP[i][j - 2] : 0;

                DP[i][j] = Math.max(
                        coins[i] + Math.min(pickFirst, pickSecond),
                        coins[j] + Math.min(pickSecond, pickLast)
                );
            }
        }

        // Return the result for the entire array
        return DP[0][n - 1];
    }

    @Override
    public void start(Stage primaryStage) {
        // Sample coin array for testing
        int[] coins = {4, 15, 7, 3, 8, 9};
        int n = coins.length;

        // Create the DP table
        int[][] DP = new int[n][n];

        // Calculate the optimal strategy
        int maxWin = optimalStrategy(coins, DP);

        // Create the TableView to display the DP table
        TableView<int[]> tableView = new TableView<>();

        // Set table style: remove borders and apply a cleaner design
        tableView.setStyle("-fx-table-cell-border-color: transparent; -fx-border-color: transparent;");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add columns to the table
        for (int i = 0; i < n; i++) {
            final int colIndex = i;
            TableColumn<int[], String> col = new TableColumn<>("Col " + i);

            // Set column style for better appearance (consistent with first row design)
            col.setStyle("-fx-alignment: center; -fx-font-size: 14px; -fx-font-weight: bold;");
            col.setCellValueFactory(cellData -> {
                int[] row = cellData.getValue();
                return new javafx.beans.property.SimpleStringProperty(Integer.toString(row[colIndex]));
            });

            // Customize the cells with a better design using a cell factory
            col.setCellFactory(new Callback<>() {
                @Override
                public TableCell<int[], String> call(TableColumn<int[], String> param) {
                    TableCell<int[], String> cell = new TableCell<>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                                setStyle("-fx-background-color: transparent;");
                            } else {
                                setText(item);
                                // Alternate row colors for better design
                                if (getIndex() % 2 == 0) {
                                    setStyle("-fx-background-color: #f0f0f0; -fx-alignment: center;");
                                } else {
                                    setStyle("-fx-background-color: #ffffff; -fx-alignment: center;");
                                }
                                setFont(Font.font("Arial", 14));

                                // Apply the same design to the first column as the first row
                                if (colIndex == 0) {
                                    setStyle("-fx-background-color: #f0f0f0; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center;");
                                }
                            }
                        }
                    };
                    return cell;
                }
            });

            tableView.getColumns().add(col);
        }

        // Add rows to the table
        for (int i = 0; i < n; i++) {
            tableView.getItems().add(DP[i]);
        }

        // Create a button to refresh and calculate the DP table
        Button calculateButton = new Button("Calculate Optimal Strategy");
        calculateButton.setStyle("-fx-font-size: 16px; -fx-background-color: #20c997; -fx-text-fill: white;");
        calculateButton.setOnAction(event -> {
            optimalStrategy(coins, DP); // Recalculate the DP table
            tableView.refresh(); // Refresh the table to display updated values
            System.out.println("Maximum amount the first player can guarantee to win: " + maxWin);
        });

        // Create a label to show the max win amount
        Label resultLabel = new Label("Maximum Amount: " + maxWin);
        resultLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Display the result and DP table
        VBox vbox = new VBox(20);
        vbox.getChildren().addAll(resultLabel, calculateButton, tableView);

        Scene scene = new Scene(vbox, 700, 500);
        primaryStage.setTitle("Optimal Game Strategy");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
