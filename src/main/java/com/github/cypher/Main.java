package com.github.cypher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    public static final String WORKING_DIRECTORY = ""; //The path to the folder where settings, credentials etc are saved.

    @Override
    public void start(Stage primaryStage) throws Exception {

        // TODO: Integer acts as placeholder until a model has been built
        final Integer model = 8;

        FXMLLoader loader = NewFXMLLoader(model);

        loader.setLocation(getClass().getResource("/fxml/root.fxml"));

        loader.setResources(ResourceBundle.getBundle("Cypher", new Locale("sv", "SE")));

        Parent root = loader.load();

        primaryStage.setTitle("Cypher");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.setMinWidth(25);
        primaryStage.setMinHeight(25);
        primaryStage.show();
    }

    private static FXMLLoader NewFXMLLoader(Integer model){
        FXMLLoader loader = new FXMLLoader();

        // Set a custom controller factory to handle dependency injection
        loader.setControllerFactory((Class<?> controllerType) -> {
            try {
                // Look for a constructor that accept the dependency.
                for (Constructor<?> constructor : controllerType.getConstructors()) {
                    if (constructor.getParameterTypes()[0] == model.getClass()) {
                        return constructor.newInstance(model);
                    }
                }

                // Otherwise use standard constructor
                return controllerType.newInstance();
            }catch (Exception e){
                System.err.println("Fatal: Couldn't load controller");
                e.printStackTrace(System.err);
                return null;
            }
        });
        return loader;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
