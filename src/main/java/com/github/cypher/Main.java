package com.github.cypher;

import com.airhacks.afterburner.injection.Injector;
import com.github.cypher.model.Client;
import com.github.cypher.root.Executor;
import com.github.cypher.root.RootView;
import com.github.cypher.sdk.api.MatrixApiLayer;
import com.github.cypher.sdk.api.MatrixMediaURLStreamHandlerFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static com.github.cypher.Util.*;

public class Main extends Application {
	public static final String APPLICATION_NAME = "Cypher";
	public static final String USER_DATA_DIRECTORY = getUserDataDirectoryPath(); //The path to the folder where settings, credentials etc are saved.

	private final Settings settings = new TOMLSettings();
	private final Executor executor = new Executor();
	private final Client client = new Client(new com.github.cypher.sdk.Client(new MatrixApiLayer(), "com.github.cypher.settings"), settings);

	@Override
	public void start(Stage primaryStage) throws Exception {

		Locale.setDefault(settings.getLanguage());
		// Starts the Executors thread
		executor.start();
		URL.setURLStreamHandlerFactory(new MatrixMediaURLStreamHandlerFactory());

		// Dependency injection with afterburner.fx
		//
		// key is name of injected variable & value is injected object

		Map<String, Object> customProperties = new HashMap<>();
		customProperties.put("client", client); // This corresponds to the line @Inject Integer n1; in the Presenter
		customProperties.put("settings", settings);
		customProperties.put("executor", executor);
		Injector.setConfigurationSource(customProperties::get);


		RootView rootView = new RootView();

		Scene scene = new Scene(rootView.getView());
		final String uri = getClass().getResource("main.css").toExternalForm();
		scene.getStylesheets().add(uri);
		scene.getStylesheets().add("bootstrapfx.css");

		primaryStage.setTitle("Cypher");
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
		return (SystemTray.isSupported() && settings.getExitToSystemTray());
	}

	private void addTrayIcon(Stage primaryStage) {

		// Load labels from bundle
		ResourceBundle labels = ResourceBundle.getBundle("com.github.cypher.labels", Locale.getDefault());

		// Make sure application doesn't exit when main window is closed
		Platform.setImplicitExit(false);

		// Create the trayIcon
		TrayIcon trayIcon = new TrayIcon(getIconImage(), capitalize(APPLICATION_NAME), new PopupMenu());
		trayIcon.setImageAutoSize(true);

		// Focus and show application on left click
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				// Make sure left click
				if (e.getButton() == 1) {
					Platform.runLater(() -> {
						primaryStage.show();
						primaryStage.requestFocus();
					});
				}
			}
		});

		{ /* The "SHOW" menu item */
			MenuItem item = createMenuItem(labels.getString("show"), e -> {
				Platform.runLater(() -> {
					primaryStage.show();
					primaryStage.requestFocus();
				});
			});
			trayIcon.getPopupMenu().add(item);

		}

		{ /* The "EXIT" menu item */
			MenuItem item = createMenuItem(labels.getString("exit"), e -> {
				exit();
			});
			trayIcon.getPopupMenu().add(item);
		}

		// Add trayIcon to tray
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			DebugLogger.log(e);
		}
	}


	public static void main(String[] args) {
		launch(args);
	}

	// Creates the user data folder path
	private static String getUserDataDirectoryPath() {
		if (System.getenv("APPDATA") != null) { // Windows style
			return System.getenv("APPDATA") + File.separator + capitalize(APPLICATION_NAME);
		} else { //Unix style
			return System.getProperty("user.home") + File.separator + "." + decapitalize(APPLICATION_NAME);
		}
	}

	private void exit() {
		client.exit();
		Platform.exit();
		System.exit(0);
	}
}