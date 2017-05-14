package com.github.cypher;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static com.github.cypher.Main.USER_DATA_DIRECTORY;

public class TOMLSettings implements Settings {

	// Application specific constants
	private static final String FILE_NAME = "config.toml";

	// Instance variables
	private final File settingsFile;
	private final SettingsData settingsData;

	// Class representing all settings
	private static class SettingsData{
		// All variables are initiated to default values
		String languageTag = Locale.getDefault().toLanguageTag();
		int SDKTimeout = 500; // In ms
		int modelTickInterval = 500; // In ms

		String languageTag = Locale.getDefault().toLanguageTag(); // Default Value
		boolean saveSession = false;
		boolean controlEnterToSendMessage = true;
	}

	TOMLSettings() {
		settingsFile = createOrLoadFile();
		settingsData = load(settingsFile);
		save();
	}

	private static File createOrLoadFile(){
		try {
			// Create folder if it doesn't exist
			new File(USER_DATA_DIRECTORY).mkdir();

			// Load File
			File file = new File(USER_DATA_DIRECTORY + File.separator + FILE_NAME);

			// Create file if it doesn't exist
			file.createNewFile();

			return file;

		}catch (IOException e) {
			DebugLogger.log("Could not create settings file");
			return null;
		}
	}

	private static synchronized SettingsData load(File settingsFile) {
		// Make sure settingsFile is set before loading settings
		if (settingsFile != null) {
			if (DebugLogger.ENABLED) {
				DebugLogger.log("Reading settings from: " + settingsFile);
			}
			return new Toml().read(settingsFile).to(SettingsData.class);
		}else{
			if (DebugLogger.ENABLED) {
				DebugLogger.log("Could not access settings file, defaults will be loaded.");
			}
			return new SettingsData();
		}
	}

	private synchronized void save() {
		// Make sure settingsFile is set before saving settings
		if (settingsFile != null){
			try {
				new TomlWriter().write(settingsData, settingsFile);
				if (DebugLogger.ENABLED) {
					DebugLogger.log("Settings saved to: " + settingsFile);
				}
			} catch (IOException e) {
				if (DebugLogger.ENABLED) {
					DebugLogger.log("Could not access settings file, settings won't be saved.");
				}
			}
		}else{
			if (DebugLogger.ENABLED){
				DebugLogger.log("Could not access settings file, settings won't be saved.");
			}
		}
	}

	// Language setting
	@Override
	public synchronized Locale getLanguage() {
		return Locale.forLanguageTag(settingsData.languageTag);
	}

	@Override
	public synchronized void setLanguage(Locale language) {
		settingsData.languageTag = language.toLanguageTag();
		save();
	}

	// Save session ("keep me logged in") settings
	@Override
	public synchronized boolean getSaveSession() {
		return settingsData.saveSession;
	}

	@Override
	public synchronized void setSaveSession(boolean saveSession) {
		settingsData.saveSession = saveSession;
		save();
	}

	// If control + enter should be used for sending messages (if false only enter is needed)
	@Override
	public boolean getControlEnterToSendMessage() {
		return settingsData.controlEnterToSendMessage;
	}

	@Override
	public void setControlEnterToSendMessage(boolean controlEnterToSendMessage) {
		settingsData.controlEnterToSendMessage = controlEnterToSendMessage;
		save();
	}

	@Override
	public int getSDKTimeout() {
		return settingsData.SDKTimeout;
	}

	@Override
	public void setSDKTimeout(int timeout) {
		settingsData.SDKTimeout = timeout;
		save();
	}

	@Override
	public int getModelTickInterval() {
		return settingsData.modelTickInterval;
	}

	@Override
	public void setModelTickInterval(int interval) {
		settingsData.modelTickInterval = interval;
		save();
	}
}
