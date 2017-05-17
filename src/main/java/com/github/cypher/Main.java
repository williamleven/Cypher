package com.github.cypher;

import com.airhacks.afterburner.injection.Injector;
import com.github.cypher.gui.Executor;
import com.github.cypher.gui.root.RootView;
import com.github.cypher.model.Client;
import com.github.cypher.sdk.api.MatrixApiLayer;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static com.github.cypher.Util.getUserDataDirectoryPath;


public class Main extends Application {
	public static final String APPLICATION_NAME = "Cypher";
	public static final String USER_DATA_DIRECTORY = getUserDataDirectoryPath(APPLICATION_NAME); //The path to the folder where settings, credentials etc are saved.

	private final Settings settings = new TOMLSettings();
	private final Executor executor = new Executor();
	private final Client client = new Client((() -> new com.github.cypher.sdk.Client(new MatrixApiLayer(), "com.github.cypher.settings")), settings);

	@Override
	public void start(Stage primaryStage) throws Exception {
		Locale.setDefault(settings.getLanguage());
		// Starts the Executors thread
		executor.start();
		URL.setURLStreamHandlerFactory(new com.github.cypher.sdk.api.Util.MatrixMediaURLStreamHandlerFactory());

		// Dependency injection with afterburner.fx
		//
		// key is name of injected variable & value is injected object

		Map<String, Object> customProperties = new HashMap<>();
		customProperties.put("client", client); // This corresponds to the line @Inject Client client; in the Presenters
		customProperties.put("settings", settings);
		customProperties.put("executor", executor);
		Injector.setConfigurationSource(customProperties::get);


		RootView rootView = new RootView();

		Scene scene = new Scene(rootView.getView());
		final String uri = getClass().getResource("main.css").toExternalForm();
		scene.getStylesheets().add(uri);
		scene.getStylesheets().add("bootstrapfx.css");

		primaryStage.setTitle("Cypher");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		primaryStage.setScene(scene);
		primaryStage.setMinWidth(1100);
		primaryStage.setMinHeight(500);

		// Only hide close the main window if system tray is enabled and supported.
		primaryStage.setOnCloseRequest(event -> {
			if (useSyetemTray()) {
				primaryStage.close();
			} else {
				exit();
			}
		});
		if (useSyetemTray()) {
			addTrayIcon(primaryStage);
		}

		primaryStage.show();
	}

	private boolean useSyetemTray() {
		return ( SystemTray.get() != null && settings.getExitToSystemTray());
	}

	private void addTrayIcon(Stage primaryStage) {

		// Load systemtray
		SystemTray systemTray = SystemTray.get();
		if (systemTray == null) {
			throw new RuntimeException("Unable to load SystemTray!");
		}

		// Load labels from bundle
		ResourceBundle labels = ResourceBundle.getBundle("com.github.cypher.labels", Locale.getDefault());

		// Make sure application doesn't exit when main window is closed
		Platform.setImplicitExit(false);

		// Set image and status
		systemTray.setImage(getClass().getResourceAsStream("/icon.png"));
		systemTray.setStatus("Cypher");

		{ /* The "SHOW" menu item */
			MenuItem item = new MenuItem(labels.getString("show"), e -> {
				Platform.runLater(() -> {
					primaryStage.show();
					primaryStage.requestFocus();
				});
			});
			item.setShortcut('o');
			systemTray.getMenu().add(item);
		}

		{ /* The "EXIT" menu item */
			MenuItem item = new MenuItem(labels.getString("exit"), e -> {
				exit();
			});
			item.setShortcut('q');
			systemTray.getMenu().add(item);
		}
	}


	public static void main(String[] args) {
		launch(args);
	}

	private void exit() {
		client.exit();
		Platform.exit();
		System.exit(0);
	}
}