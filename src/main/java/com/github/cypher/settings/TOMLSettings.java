
package com.github.cypher.settings;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class TOMLSettings implements Settings {

	// Application specific constants
	private static final String FILE_NAME = "config.toml";

	// Instance variables
	private final File settingsFile;
	private final SettingsData settingsData;
	private final String userDataDirectory;

	// Class representing all settings
	private static class SettingsData{
		// All variables are initiated to default values
		String languageTag = Locale.getDefault().toLanguageTag();
		boolean saveSession = false;
		boolean useSystemTray = true;
		boolean controlEnterToSendMessage = true;
		int SDKTimeout = 30000; // In ms
		int modelTickInterval = 500; // In ms

		boolean maximized = false;
		int lastWindowPosX = -1;
		int lastWindowPosY = -1;
		int lastWindowWidth = -1;
		int lastWindowHeight = -1;
	}

	public TOMLSettings(String userDataDirectory) {
		this.userDataDirectory = userDataDirectory;
		settingsFile = createOrLoadFile();
		settingsData = load(settingsFile);
		save();
	}

	private File createOrLoadFile() {
		try {
			// Create folder if it doesn't exist
			new File(userDataDirectory).mkdir();

			// Load File
			File file = new File(userDataDirectory + File.separator + FILE_NAME);

			// Create file if it doesn't exist
			file.createNewFile();

			return file;

		} catch (IOException e) {
			System.out.printf("Could not create settings file\n");
			return null;
		}
	}

	private SettingsData load(File settingsFile) {
		// Make sure settingsFile is set before loading settings
		if (settingsFile == null) {
			System.out.printf("Could not access settings file, defaults will be loaded.\n");
			return new SettingsData();
		} else {
			System.out.printf("Reading settings from: %s\n", settingsFile);
			return new Toml().read(settingsFile).to(SettingsData.class);
		}
	}

	private void save() {
		synchronized (this){
			// Make sure settingsFile is set before saving settings
			if (settingsFile == null) {
				System.out.printf("Could not access settings file, settings won't be saved.\n");
			} else {
				try {
					new TomlWriter().write(settingsData, settingsFile);
					System.out.printf("Settings saved to: %s\n", settingsFile);
				} catch (IOException e) {
					System.out.printf("Could not access settings file, settings won't be saved.\n");
				}
			}
		}
	}

	// Language setting
	@Override
	public Locale getLanguage() {
		synchronized (this) {
			return Locale.forLanguageTag(settingsData.languageTag);
		}
	}

	@Override
	public void setLanguage(Locale language) {
		synchronized (this){
			settingsData.languageTag = language.toLanguageTag();
			save();
		}
	}

	// Save session ("keep me logged in") settings
	@Override
	public boolean getSaveSession() {
		synchronized (this){
			return settingsData.saveSession;
		}
	}

	@Override
	public void setSaveSession(boolean saveSession) {
		synchronized (this) {
			settingsData.saveSession = saveSession;
			save();
		}
	}

	@Override
	public boolean getUseSystemTray() {
		synchronized (this) {
			return settingsData.useSystemTray;
		}
	}

	@Override
	public void setUseSystemTray(boolean useSystemTray) {
		synchronized (this) {
			settingsData.useSystemTray = useSystemTray;
			save();
		}
	}

	// If control + enter should be used for sending messages (if false only enter is needed)
	@Override
	public boolean getControlEnterToSendMessage() {
		synchronized (this) {
			return settingsData.controlEnterToSendMessage;
		}
	}

	@Override
	public void setControlEnterToSendMessage(boolean controlEnterToSendMessage) {
		synchronized (this) {
			settingsData.controlEnterToSendMessage = controlEnterToSendMessage;
			save();
		}
	}

	// Timeout is maximum time to poll in milliseconds before returning a request
	@Override
	public int getSDKTimeout() {
		synchronized (this) {
			return settingsData.SDKTimeout;
		}
	}

	@Override
	public void setSDKTimeout(int timeout) {
		synchronized (this) {
			settingsData.SDKTimeout = timeout;
			save();
		}
	}

	@Override
	public boolean getMaximized() {
		return settingsData.maximized;
	}

	@Override
	public void setMaximized(boolean maximized) {
		synchronized (this) {
			settingsData.maximized = maximized;
			save();
		}
	}

	// The time between each tick in the model in ms
	@Override
	public int getModelTickInterval() {
		synchronized (this) {
			return settingsData.modelTickInterval;
		}
	}

	@Override
	public void setModelTickInterval(int interval) {
		synchronized (this) {
			settingsData.modelTickInterval = interval;
			save();
		}
	}

	@Override
	public int getLastWindowPosX() {
		synchronized (this) {
			return settingsData.lastWindowPosX;
		}
	}

	@Override
	public void setLastWindowPosX(int posX) {
		synchronized (this) {
			settingsData.lastWindowPosX = posX;
			save();
		}
	}

	@Override
	public int getLastWindowPosY() {
		synchronized (this) {
			return settingsData.lastWindowPosY;
		}
	}

	@Override
	public void setLastWindowPosY(int posY) {
		synchronized (this) {
			settingsData.lastWindowPosY = posY;
			save();
		}
	}

	@Override
	public int getLastWindowWidth() {
		synchronized (this) {
			return settingsData.lastWindowWidth;
		}
	}

	@Override
	public void setLastWindowWidth(int width) {
		synchronized (this) {
			settingsData.lastWindowWidth = width;
			save();
		}
	}

	@Override
	public int getLastWindowHeight() {
		synchronized (this) {
			return settingsData.lastWindowHeight;
		}
	}

	@Override
	public void setLastWindowHeight(int height) {
		synchronized (this) {
			settingsData.lastWindowHeight = height;
			save();
		}
	}

}