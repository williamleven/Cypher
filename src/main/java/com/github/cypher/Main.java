package com.github.cypher;

import com.airhacks.afterburner.injection.Injector;
import com.github.cypher.gui.Executor;
import com.github.cypher.gui.root.RootView;
import com.github.cypher.model.Client;
import com.github.cypher.model.ModelFactory;
import com.github.cypher.settings.Settings;
import com.github.cypher.settings.TOMLSettings;
import com.google.common.eventbus.EventBus;
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
	private static final String APPLICATION_NAME = "Cypher";
	private static final String USER_DATA_DIRECTORY = getUserDataDirectoryPath(APPLICATION_NAME); //The path to the folder where settings, credentials etc are saved.
	private static final String SETTINGS_NAMESPACE = "com.github.cypher.settings";
	private static final int MIN_WINDOW_WIDTH = 672;
	private static final int MIN_WINDOW_HEIGHT = 438;

	private final Settings settings = new TOMLSettings(USER_DATA_DIRECTORY);
	private final Executor executor = new Executor();
	private final EventBus eventBus = new EventBus();
	private final Client client = ModelFactory.createClient(settings, eventBus, USER_DATA_DIRECTORY, SETTINGS_NAMESPACE);

	private boolean systemTrayInUse;

	@Override
	public void start(Stage primaryStage) {
		Locale.setDefault(settings.getLanguage());
		systemTrayInUse = settings.getUseSystemTray();
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
		customProperties.put("eventBus", eventBus);
		Injector.setConfigurationSource(customProperties::get);


		RootView rootView = new RootView();

		Scene scene = new Scene(rootView.getView());
		final String cssMain = getClass().getResource("main.css").toExternalForm();
		final String cssScroll = getClass().getResource("scrollbars.css").toExternalForm();
		scene.getStylesheets().add(cssMain);
		scene.getStylesheets().add(cssScroll);
		scene.getStylesheets().add("bootstrapfx.css");

		primaryStage.setTitle("Cypher");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		primaryStage.setScene(scene);
		if (settings.getLastWindowPosX() != -1 && settings.getLastWindowPosY() != -1) {
			primaryStage.setX(settings.getLastWindowPosX());
			primaryStage.setY(settings.getLastWindowPosY());
		}
		if (settings.getLastWindowWidth() != -1 && settings.getLastWindowHeight() != -1) {
			primaryStage.setWidth(settings.getLastWindowWidth());
			primaryStage.setHeight(settings.getLastWindowHeight());
		}
		primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
		primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);

		// addSystemTray sets its own "onCloseRequest" on the primaryStage
		if (useSystemTray()) {
			addSystemTray(primaryStage);
		} else {
			primaryStage.setOnCloseRequest(event -> {
				exit(primaryStage);
			});
		}

		primaryStage.show();
	}

	private boolean useSystemTray() {
		return systemTrayInUse && SystemTray.get() != null;
	}

	private void addSystemTray(Stage primaryStage) {

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

		/* The "SHOW" menu item */
		MenuItem showMenuItem = new MenuItem(labels.getString("show"));
		showMenuItem.setCallback(e -> {
			Platform.runLater(() -> {
				primaryStage.show();
				primaryStage.requestFocus();
				showMenuItem.setEnabled(false);
			});
		});
		showMenuItem.setShortcut('o');
		showMenuItem.setEnabled(false);
		systemTray.getMenu().add(showMenuItem);

		/* The "EXIT" menu item */
		MenuItem exitMenuItem = new MenuItem(labels.getString("exit"), e -> {
			exit(primaryStage);
		});
		exitMenuItem.setShortcut('q');
		systemTray.getMenu().add(exitMenuItem);

		// Only hide close the main window if system tray is enabled and supported.
		primaryStage.setOnCloseRequest(event -> {
			if (systemTrayInUse) {
				primaryStage.close();
				showMenuItem.setEnabled(true);
			} else {
				exit(primaryStage);
			}
		});
	}


	public static void main(String[] args) {
		launch(args);
	}

	private void exit(Stage primaryStage) {
		settings.setLastWindowPosX((int) primaryStage.getX());
		settings.setLastWindowPosY((int) primaryStage.getY());
		settings.setLastWindowWidth((int) primaryStage.getWidth());
		settings.setLastWindowHeight((int) primaryStage.getHeight());
		client.exit();
		Platform.exit();
		System.exit(0);
	}
}