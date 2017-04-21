package com.github.cypher;

import com.airhacks.afterburner.injection.Injector;
import com.github.cypher.model.Client;
import com.github.cypher.root.RootView;
import com.github.cypher.sdk.api.MatrixApiLayer;
import com.github.cypher.sdk.api.MatrixMediaURLStreamHandlerFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Main extends Application {
	public static final String WORKING_DIRECTORY = ""; //The path to the folder where settings, credentials etc are saved.

	private final Client client = new Client(new com.github.cypher.sdk.Client(new MatrixApiLayer()));
	private final Settings settings = new SerializableSettings();

	@Override
	public void start(Stage primaryStage) throws Exception {
    
		Locale.setDefault(settings.getLanguage());

		URL.setURLStreamHandlerFactory(new MatrixMediaURLStreamHandlerFactory());

		// Dependency injection with afterburner.fx
		//
		// key is name of injected variable & value is injected object

		Map<String, Object> customProperties = new HashMap<>();
		customProperties.put("client", client); // This corresponds to the line @Inject Integer n1; in the Presenter
		customProperties.put("settings", settings);
		Injector.setConfigurationSource(customProperties::get);


		RootView rootView = new RootView();

		Scene scene = new Scene(rootView.getView());
		final String uri = getClass().getResource("main.css").toExternalForm();
		scene.getStylesheets().add(uri);
		scene.getStylesheets().add("bootstrapfx.css");

		primaryStage.setTitle("Cypher");
		primaryStage.setScene(scene);
		primaryStage.setMinWidth(25);
		primaryStage.setMinHeight(25);
		primaryStage.setOnCloseRequest(event -> {
			client.exit();
			Platform.exit();
			System.exit(0);
		});
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}