package com.github.cypher;

import com.airhacks.afterburner.injection.Injector;
import com.github.cypher.root.RootView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Main extends Application {
    public static final String WORKING_DIRECTORY = ""; //The path to the folder where settings, credentials etc are saved.

    @Override
    public void start(Stage primaryStage) throws Exception {
		Locale.setDefault(new Locale("sv", "SE"));
        Map<Object, Object> customProperties = new HashMap<>();
        customProperties.put("n1", new Integer(8));
        customProperties.put("s1", "hejsan");
		Injector.setConfigurationSource(customProperties::get);
		RootView rootView = new RootView();
		Scene scene = new Scene(rootView.getView());
		final String uri = getClass().getResource("main.css").toExternalForm();
		scene.getStylesheets().add(uri);

        primaryStage.setTitle("Cypher");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(25);
        primaryStage.setMinHeight(25);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}