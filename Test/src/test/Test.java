/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author zaid7
 */
public class Test extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Load the image
        Image image = new Image("img.png"); // Update with your image path

        // Create an ImageView to display the image
        ImageView imageView = new ImageView(image);

        // You can adjust image size if necessary
        imageView.setFitWidth(400);  // Set the width
        imageView.setFitHeight(300); // Set the height
        imageView.setPreserveRatio(true); // Maintain the aspect ratio

        // Create a layout container
        StackPane root = new StackPane();
        root.getChildren().add(imageView); // Add the image to the layout

        // Create a scene and set it on the stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("JavaFX Image Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
