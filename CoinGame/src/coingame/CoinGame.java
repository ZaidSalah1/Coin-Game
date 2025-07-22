package coingame;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class CoinGame extends Application {

    private static final String IMAGE_PATH = "file:///C:/Users/zaid7/Downloads/dice.png";
    private static final Font LABEL_FONT = Font.font("Arial", FontWeight.BOLD, 14);
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final int IMAGE_SIZE = 100;
    private static final int SCENE_WIDTH = 1200;
    private static final int SCENE_HEIGHT = 800;
    private Scene mainScene;
    private StringBuilder arrayIntegerToString;

    private ComboBox<String> comboBoxAraySize;
    private Label lblCoinArray;
    private int[] randomArray;

    private boolean isPlayer1Turn = true;
    private List<Integer> player1Selections = new ArrayList<>();
    private List<Integer> player2Selections = new ArrayList<>();
    private Text resultsText;
    private int left = 0; // Left pointer starts at the first coin
    private int right; // Right pointer will be initialized later

    private int[][] DP;
    private int arr[];

    @Override
    public void start(Stage primaryStage) {
        VBox mainLayout = createMainLayout(primaryStage);

        // Set up the scene
        mainScene = new Scene(mainLayout, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setTitle("Coin Game");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private VBox createMainLayout(Stage primaryStage) {
        VBox vBox1 = createVBox(new Image(IMAGE_PATH), "Random", primaryStage);
        VBox vBox2 = createVBox(new Image("file:///C:/Users/zaid7/Downloads/8635566.png"), "Manual", primaryStage);
        VBox vBox3 = createVBox(new Image("https://cdn-icons-png.flaticon.com/512/1299/1299860.png"), "Read File", primaryStage);

        HBox buttonBox = new HBox(20, vBox1, vBox2, vBox3);
        buttonBox.setAlignment(Pos.CENTER);

        vBox3.setOnMouseClicked(event -> {
            try {
                handleFileSelection(primaryStage);
            } catch (Exception ex) {
                showErrorAlert("Unexpected Error", ex.getMessage());
            }
        });

        VBox mainLayout = new VBox(20, buttonBox);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setBackground(createBackground("file:///C:/Users/zaid7/Downloads/bg.png"));

        return mainLayout;
    }

    public int optimalStrategy(int[] coins) {
        int n = coins.length;
        DP = new int[n][n]; // Initialize DP array

        // Fill the table for all subarray lengths
        for (int length = 1; length <= n; length++) {
            for (int i = 0, j = length - 1; j < n; i++, j++) {
                int pickFirst = i + 2 <= j ? DP[i + 2][j] : 0;
                int pickSecond = i + 1 <= j - 1 ? DP[i + 1][j - 1] : 0;
                int pickLast = i <= j - 2 ? DP[i][j - 2] : 0;

                DP[i][j] = Math.max(
                        coins[i] + Math.min(pickFirst, pickSecond),
                        coins[j] + Math.min(pickSecond, pickLast)
                );
            }
        }
        return DP[0][n - 1]; // Return the optimal result
    }

    private void showPlayWithComputerScene(Stage primaryStage, int[] coin) {
        int gameResult = optimalStrategy(coin);

        // Title Text
        Text msgResultText = createStyledText("The Optimal Solution", 18, FontWeight.BOLD, Color.DARKBLUE);
        Text result = createStyledText("" + gameResult, 16, FontWeight.NORMAL, Color.BLACK);

        // Optimal Coins Text
        Text msgOptimalResultText = createStyledText("The Coins that give the result", 18, FontWeight.BOLD, Color.DARKBLUE);
        Text coinsResult = createStyledText("1, 12, 2, 23", 16, FontWeight.NORMAL, Color.BLACK);

        // VBox for results
        VBox v1 = new VBox(5, msgResultText, result);
        v1.setAlignment(Pos.CENTER);
        v1.setStyle("-fx-background-color: #E8F6F3; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");

        VBox v2 = new VBox(5, msgOptimalResultText, coinsResult);
        v2.setAlignment(Pos.CENTER);
        v2.setStyle("-fx-background-color: #E8F6F3; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Right Container for results
        VBox rightContainer = new VBox(20, v1);
        rightContainer.setAlignment(Pos.CENTER);
        rightContainer.setStyle("-fx-padding: 40;");

        // Grid Pane (Placeholder for DP Grid)
        GridPane grid = createDPGrid(DP, coin);
        grid.setAlignment(Pos.CENTER);

        // Create Back Button
        Button backButton = createBackButton(primaryStage);
        HBox backButtonBox = new HBox(backButton);
        backButtonBox.setAlignment(Pos.BOTTOM_LEFT); // Align the button to the bottom-left
        backButtonBox.setPadding(new Insets(10));    // Add padding around the button

        // BorderPane for layout
        BorderPane container = new BorderPane();
        container.setRight(rightContainer);
        container.setLeft(grid);
        container.setBottom(backButtonBox); // Add Back Button to the bottom
        container.setBackground(createBackground("file:///C:/Users/zaid7/Downloads/bg.png"));
        container.setPadding(new Insets(80));
        // Scene
        Scene gameScene = new Scene(container, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    private void handleFileSelection(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Coin Game Data File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                int[] coins = readAndValidateFile(selectedFile);
                int result = optimalStrategy(coins);
                createFileScreen(stage, coins.length, coins);
                showInfoAlert("Optimal Strategy Result", "Maximum coins the first player can guarantee: " + result);
            } catch (Exception e) {
                showErrorAlert("File Error", e.getMessage());
            }
        }
    }

    private int[] readAndValidateFile(File file) throws IOException {
        // Read all lines from the file
        String[] lines = Files.readAllLines(file.toPath()).toArray(new String[0]);

        // Validate file structure
        if (lines.length < 2) {
            throw new IllegalArgumentException("Invalid file format: File must contain at least two lines.");
        }

        // Parse array size
        int arraySize;
        try {
            arraySize = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid array size in the first line.");
        }

        // Ensure the array size is even
        if (arraySize % 2 != 0) {
            throw new IllegalArgumentException("Invalid array size: Array size must be an even number.");
        }

        // Parse coin array
        String[] coinStrings = lines[1].split(",");
        if (coinStrings.length != arraySize) {
            throw new IllegalArgumentException("Array size mismatch: Expected " + arraySize + " elements, found " + coinStrings.length + ".");
        }

        int[] coins = new int[arraySize];
        try {
            for (int i = 0; i < arraySize; i++) {
                coins[i] = Integer.parseInt(coinStrings[i].trim());
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid coin value: Ensure all elements are integers.");
        }

        return coins;
    }

    private void createFileScreen(Stage primaryStage, int arraySize, int[] coins) {
        comboBoxAraySize = createComboBox(arraySize);
        comboBoxAraySize.setValue(arraySize + "");
        StringBuilder array1 = new StringBuilder();
        for (int i = 0; i < arraySize; i++) {
            array1.append(coins[i]);
            array1.append(", ");
        }

        lblCoinArray = createStyledLabel(array1.toString());
        lblCoinArray.setStyle(
                "-fx-background-color: #12569D; "
                + "-fx-text-fill: white; "
                + "-fx-background-radius: "
                + "15; -fx-font-size: 30;");

        VBox inputs = new VBox(5, comboBoxAraySize, lblCoinArray);
        inputs.setAlignment(Pos.CENTER);

        // Create a spacer with a fixed height to act as a top margin
        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);  // Allow it to grow in available space
        topSpacer.setPrefHeight(150);  // This sets the top margin height

        VBox playerVsPlayerBox = createVBox(new Image("file:///G:/Coin%20Game%20Project/CoinGame/1v1.png"), "Player VS Player", primaryStage);
        VBox playWithComputerBox = createVBox(new Image("file:///C:/Users/zaid7/Downloads/8635566.png"), "Play with Computer", primaryStage);

        HBox optionsBox = new HBox(50, playerVsPlayerBox, playWithComputerBox);
        optionsBox.setAlignment(Pos.CENTER);

        VBox mainContent = new VBox(20, topSpacer, optionsBox, inputs);

        comboBoxAraySize.setOnAction(value -> {
            int range = Integer.parseInt(comboBoxAraySize.getValue());
            System.out.println("Selected size: " + range);

            // Create a new array with the selected size
            arr = new int[range];
            for (int i = 0; i < range; i++) {
                arr[i] = coins[i]; // Populate the array from coins
            }

            // Build the coin array as a comma-separated string
            StringBuilder array = new StringBuilder();
            for (int i = 0; i < arr.length; i++) {
                array.append(arr[i]);
                if (i < arr.length - 1) {
                    array.append(", ");
                }
            }

            // Update the label text
            lblCoinArray.setText(array.toString());
        });

        playWithComputerBox.setOnMouseClicked(event -> {

            showPlayWithComputerScene(primaryStage, arr);
        });

        playerVsPlayerBox.setOnMouseClicked(event -> {
            showPlayerVsPlayerScene(primaryStage, coins);
        });

        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(0, 150, 0, 0));  // 150px margin on the right side

        Button backButton = createBackButton(primaryStage);
        HBox backButtonBox = new HBox(backButton);
        backButtonBox.setAlignment(Pos.BOTTOM_LEFT);
        backButtonBox.setPadding(new Insets(20));

        BorderPane borderPane = new BorderPane();
        borderPane.setRight(mainContent);
        borderPane.setBottom(backButtonBox);
        borderPane.setBackground(createBackground("file:///C:/Users/zaid7/Downloads/bg2.png"));
        Scene nextScene = new Scene(borderPane, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(nextScene);
    }

    //-----------Player VS Player--------------\\
    private void showPlayerVsPlayerScene(Stage primaryStage, int[] coin) {
        HBox coinsRow = createCoinsRow(coin);
        VBox leftPlayer = createPlayerBox("Player 1", "file:///C:/Users/zaid7/Downloads/p1.png");
        VBox rightPlayer = createPlayerBox("Player 2", "file:///C:/Users/zaid7/Downloads/p2.png");

        resultsText = new Text();
        resultsText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        resultsText.setText("Results will be displayed here...");

        VBox resultBox = new VBox(resultsText);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setPadding(new Insets(10));

        // Create the back button and add it to the bottom-left
        Button backButton = createBackButton(primaryStage);
        HBox backButtonBox = new HBox(backButton);
        backButtonBox.setAlignment(Pos.BOTTOM_LEFT); // Align the button to the bottom-left
        backButtonBox.setPadding(new Insets(10));    // Add some padding around the button

        // Combine the resultBox and backButton in the bottom section
        VBox bottomSection = new VBox(resultBox, backButtonBox);
        bottomSection.setAlignment(Pos.CENTER);

        BorderPane gameLayout = new BorderPane();
        gameLayout.setLeft(leftPlayer);
        gameLayout.setRight(rightPlayer);
        gameLayout.setCenter(coinsRow);
        gameLayout.setBottom(bottomSection); // Set the bottom section

        gameLayout.setPadding(new Insets(20));
        gameLayout.setBackground(createBackground("file:///C:/Users/zaid7/Downloads/bg.png"));

        Scene gameScene = new Scene(gameLayout, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    private VBox createVBox(Image image, String labelText, Stage primaryStage) {
        ImageView imageView = createImageView(image, IMAGE_SIZE);
        Label label = createStyledLabel(labelText);

        VBox vBox = new VBox(10, imageView, label);
        vBox.setPadding(new Insets(10));
        vBox.setAlignment(Pos.CENTER);
        vBox.setPrefSize(IMAGE_SIZE + 80, IMAGE_SIZE + 80);
        vBox.setCursor(Cursor.HAND);
        vBox.setStyle(getVBoxStyle());
        addHoverEffect(vBox);

        if ("Random".equals(labelText)) {
            vBox.setOnMouseClicked(event -> createNextScene(primaryStage));
        } else if ("Player VS Player".equals(labelText)) {
            vBox.setOnMouseClicked(event -> showPlayerVsPlayerScene(primaryStage, randomArray));
        } else if ("Play with Computer".equals(labelText)) {
            vBox.setOnMouseClicked(event -> showPlayWithComputerScene(primaryStage, randomArray));
        }

        return vBox;
    }

    private HBox createCoinsRow(int[] coins) {
        right = coins.length - 1; // Initialize right pointer to the last coin
        HBox coinsRow = new HBox(10);
        coinsRow.setAlignment(Pos.CENTER);

        System.out.println("coinsRow.getHeight();" + coinsRow.getHeight());
        for (int i = 0; i < coins.length; i++) {
            VBox coinBox = new VBox(5);
            coinBox.setCursor(Cursor.HAND);
            coinBox.setAlignment(Pos.CENTER);
            // Set min, pref, and max heights to ensure consistent sizing
            coinBox.setMinHeight(120);
            coinBox.setPrefHeight(120);
            coinBox.setMaxHeight(120);

            ImageView coinImage = new ImageView(new Image("file:///G:/Coin%20Game%20Project/CoinGame/coin.png"));
            coinImage.setFitWidth(100);
            coinImage.setFitHeight(60);

            Text coinValue = new Text(String.valueOf(coins[i]));
            coinValue.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            coinBox.setStyle("-fx-border-color: transparent; -fx-background-color: white; -fx-background-radius: 15; "
                    + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);");
            coinBox.getChildren().addAll(coinImage, coinValue);
            final int index = i;
            // Add event handler for clicking on coins
            coinBox.setOnMouseClicked(event -> {
                if (index == left || index == right) { // Ensure selection is from edges only
                    playerVsPlayer(coins, coins[index], index == left ? "left" : "right", coinBox);
                }
            });

            coinsRow.getChildren().add(coinBox);
        }

        return coinsRow;
    }

    private void playerVsPlayer(int[] coins, int coin, String postion, VBox coinBox) {
        if (isPlayer1Turn) {
            player1Selections.add(coin);
            System.out.println("Player 1 selected: " + coin);
        } else {
            player2Selections.add(coin);
            System.out.println("Player 2 selected: " + coin);
        }

        // Update UI to indicate selection
        coinBox.setOpacity(0.5); // Make the selected coin semi-transparent
        coinBox.setDisable(true);

        // Update the pointers based on the selected position
        if (postion.equals("right")) {
            right--; // Move left pointer to the next coin

        } else if (postion.equals("left")) {
            left++; // Move right pointer to the previous coin
        }

        // Check if all coins have been selected
        if (player1Selections.size() + player2Selections.size() == coins.length) {
            displayResults();
        } else {
            // Switch turns
            isPlayer1Turn = !isPlayer1Turn;
        }
    }

    private void displayResults() {
        int player1Sum = player1Selections.stream().mapToInt(Integer::intValue).sum();
        int player2Sum = player2Selections.stream().mapToInt(Integer::intValue).sum();

        String resultText = String.format(
                "Player 1 selections: %s\nPlayer 1 Total: %d\n\n"
                + "Player 2 selections: %s\nPlayer 2 Total: %d\n\n"
                + "Winner: %s",
                player1Selections, player1Sum,
                player2Selections, player2Sum,
                (player1Sum > player2Sum ? "Player 1" : "Player 2")
        );

        resultsText.setText(resultText);
    }
    //---------------- End --------------------\\\

    private void createNextScene(Stage primaryStage) {
        generateRandomIntArray(6);

        comboBoxAraySize = new ComboBox<>();
        comboBoxAraySize.setValue("6");
        comboBoxAraySize.getItems().addAll("2", "4", "6", "8");
        comboBoxAraySize.setPrefSize(70, 30);
        comboBoxAraySize.setStyle("-fx-background-color: #d3d3d3; -fx-font-size: 14px; -fx-background-radius: 10;");
// Add an event handler to process the value only after the user makes a selection
        comboBoxAraySize.setOnAction(event -> {
            String selectedValue = comboBoxAraySize.getValue(); // Get the selected value
            if (selectedValue != null) {
                try {
                    int value = Integer.parseInt(selectedValue); // Parse the value
                    generateRandomIntArray(value); // Call your array generation method
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + selectedValue);
                }
            } else {
                System.out.println("No value selected.");
            }
        });
        lblCoinArray = createStyledLabel(arrayIntegerToString.toString());
        lblCoinArray.setStyle(
                "-fx-background-color: #12569D; "
                + "-fx-text-fill: white; "
                + "-fx-background-radius: "
                + "15; -fx-font-size: 30;");

        Button generateBtn = createImageButton(new Image(IMAGE_PATH));

        VBox inputs = new VBox(5, comboBoxAraySize, lblCoinArray);
        inputs.setAlignment(Pos.CENTER);

        // Create a spacer with a fixed height to act as a top margin
        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);  // Allow it to grow in available space
        topSpacer.setPrefHeight(150);  // This sets the top margin height

        VBox mainContent = new VBox(20, topSpacer, createOptionsBox(primaryStage), inputs, generateBtn);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(0, 150, 0, 0));  // 150px margin on the right side

        Button backButton = createBackButton(primaryStage);
        HBox backButtonBox = new HBox(backButton);
        backButtonBox.setAlignment(Pos.BOTTOM_LEFT);
        backButtonBox.setPadding(new Insets(20));

        BorderPane borderPane = new BorderPane();
        borderPane.setRight(mainContent);
        borderPane.setBottom(backButtonBox);
        borderPane.setBackground(createBackground("file:///C:/Users/zaid7/Downloads/bg2.png"));

        Scene nextScene = new Scene(borderPane, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(nextScene);
    }

    private HBox createOptionsBox(Stage primaryStage) {
        VBox playerVsPlayerBox = createVBox(new Image("file:///G:/Coin%20Game%20Project/CoinGame/1v1.png"), "Player VS Player", primaryStage);
        VBox playWithComputerBox = createVBox(new Image("file:///C:/Users/zaid7/Downloads/8635566.png"), "Play with Computer", primaryStage);

        HBox optionsBox = new HBox(50, playerVsPlayerBox, playWithComputerBox);
        optionsBox.setAlignment(Pos.CENTER);

        return optionsBox;
    }

    private Button createBackButton(Stage primaryStage) {
        ImageView backIconView = createImageView(new Image("file:///C:/Users/zaid7/Downloads/back.png"), 60);
        Button backButton = new Button("Main", backIconView);
        backButton.setStyle(getButtonStyle("#1a73e8"));
        backButton.setCursor(Cursor.HAND);

        backButton.setOnMouseEntered(event -> backButton.setStyle(getButtonStyle("#135ba1")));
        backButton.setOnMouseExited(event -> backButton.setStyle(getButtonStyle("#1a73e8")));
        backButton.setOnAction(event -> primaryStage.setScene(mainScene));
        return backButton;
    }

    private ComboBox<String> createComboBox(int size) {
        ComboBox<String> comboBox = new ComboBox<>();

        for (int i = 2; i <= size; i += 2) { // Start from 2 and increment by 2
            comboBox.getItems().add("" + i); // Add only even numbers
        }

//        comboBox.getItems().addAll("2", "4", "6", "8", "10", "12", "14");
        comboBox.setPrefSize(70, 30);
        comboBox.setStyle("-fx-background-color: #d3d3d3; -fx-font-size: 14px; -fx-background-radius: 10;");
        return comboBox;
    }

    private ImageView createImageView(Image image, int size) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private Button createImageButton(Image image) {
        ImageView iconView = createImageView(image, 100);
        Button button = new Button("", iconView);
        button.setStyle("-fx-background-color: transparent;");
        button.setCursor(Cursor.HAND);
        button.setOnAction(e -> {
            int size = Integer.parseInt(comboBoxAraySize.getValue());
            generateRandomIntArray(size);
            lblCoinArray.setText(arrayIntegerToString.toString());

        });
        return button;
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setFont(LABEL_FONT);
        label.setTextFill(LABEL_COLOR);

        label.setPadding(new Insets(10));

        return label;
    }

    private void addHoverEffect(VBox vBox) {
        ScaleTransition hoverEffect = new ScaleTransition(Duration.millis(200), vBox);
        hoverEffect.setToX(1.05);
        hoverEffect.setToY(1.05);

        ScaleTransition resetEffect = new ScaleTransition(Duration.millis(200), vBox);
        resetEffect.setToX(1.0);
        resetEffect.setToY(1.0);

        vBox.setOnMouseEntered(event -> hoverEffect.play());
        vBox.setOnMouseExited(event -> resetEffect.play());
    }

    private Background createBackground(String imagePath) {
        Image bgImage = new Image(imagePath);
        BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, true, false);
        return new Background(new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize));
    }

    private String getVBoxStyle() {
        return "-fx-border-color: transparent; -fx-background-color: white; -fx-background-radius: 15; "
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);";
    }

    private String getButtonStyle(String backgroundColor) {
        return "-fx-background-color: " + backgroundColor + "; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 10; -fx-padding: 8 16 8 16;";
    }

    private int[] generateRandomIntArray(int size) {
        Random random = new Random();
        arrayIntegerToString = new StringBuilder();
        randomArray = new int[size];
        for (int i = 0; i < size; i++) {
            randomArray[i] = random.nextInt(100) + 1; // Generates random numbers between 1 and 100
            arrayIntegerToString.append(randomArray[i]);
            if (i < randomArray.length - 1) {
                arrayIntegerToString.append(", ");
                right = arrayIntegerToString.length() - 1;
            }
        }
        return randomArray;
    }

    private VBox createPlayerBox(String playerName, String avatarPath) {
        ImageView avatar = new ImageView(new Image(avatarPath));
        avatar.setFitWidth(100);
        avatar.setFitHeight(100);

        Text playerLabel = new Text(playerName);
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox playerBox = new VBox(10, avatar, playerLabel);
        playerBox.setAlignment(Pos.CENTER);
        return playerBox;
    }

    private GridPane createDPGrid(int[][] DP, int[] coins) {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-padding: 20; -fx-hgap: 5; -fx-vgap: 5;");

        // Add column headers
        for (int j = 0; j <= coins.length; j++) {
            StackPane header = j == 0 ? createHeader("") : createHeader("C" + (j - 1));
            grid.add(header, j, 0);
        }

        // Add row headers and DP values
        for (int i = 0; i < DP.length; i++) {
            StackPane rowHeader = createHeader("" + i);
            grid.add(rowHeader, 0, i + 1);
            for (int j = i; j < DP[i].length; j++) {
                StackPane cell = createCell(DP[i][j]);
                grid.add(cell, j + 1, i + 1); // Offset by 1 to accommodate headers
            }
        }

        return grid;
    }

    // Helper Method to Create Styled Text
    private Text createStyledText(String content, int fontSize, FontWeight weight, Color color) {
        Text text = new Text(content);
        text.setFont(Font.font("Arial", weight, fontSize));
        text.setFill(color);
        return text;
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

    //Alerts 
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
