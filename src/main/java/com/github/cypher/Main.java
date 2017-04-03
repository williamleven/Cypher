package com.github.cypher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    public static final String WORKING_DIRECTORY = ""; //The path to the folder where settings, credentials etc are saved.

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/root.fxml"),
				ResourceBundle.getBundle("Cypher", new Locale("sv", "SE")));
        primaryStage.setTitle("Cypher");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.setMinWidth(25);
        primaryStage.setMinHeight(25);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
