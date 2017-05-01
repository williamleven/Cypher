package com.github.cypher;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.*;
import java.util.Locale;

public class TOMLSettings implements Settings {

	// Application specific constants
	private static final String APPLICATION_NAME = "cypher";
	private static final String FILE_NAME = "config.toml";

	// System specific constants
	private static final String APPDATA_DIRECTORY = System.getenv("APPDATA");
	private static final String HOME_DIRECTORY = System.getProperty("user.home");

	// Instance variables
	private final File settingsFile;
	private final SettingsData settingsData;

	// Class representing all settings
	private static class SettingsData{
		String languageTag = Locale.getDefault().toLanguageTag(); // Default Value
	}

	TOMLSettings() {
		settingsFile = createOrLoadFile();
		settingsData = load(settingsFile);
		save();
	}

	private static File createOrLoadFile(){
		try {
			// Create folder if it doesn't exist
			new File(settingsFolderPath()).mkdir();

			// Load File
			File file = new File(settingsFolderPath() + File.separator + FILE_NAME);

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
			DebugLogger.log("reading settings from: " + settingsFile);
			return new Toml().read(settingsFile).to(SettingsData.class);
		}else{
			DebugLogger.log("Could not access settings file, defaults will be loaded.");
			return new SettingsData();
		}
	}

	private synchronized void save() {
		// Make sure settingsFile is set before saving settings
		if (settingsFile != null){
			try {
				new TomlWriter().write(settingsData, settingsFile);
				DebugLogger.log("Settings saved to: " + settingsFile);
			} catch (IOException e) {
				DebugLogger.log("Could not access settings file, settings won't be saved.");
			}
		}else{
			DebugLogger.log("Could not access settings file, settings won't be saved.");
		}
	}

	// Language setting
	public synchronized Locale getLanguage() {
		return Locale.forLanguageTag(settingsData.languageTag);
	}
	public synchronized void setLanguage(Locale language) {
		settingsData.languageTag = language.toLanguageTag();
		save();
	}

	// Creates the settings folder path
	private static String settingsFolderPath(){
		if ( APPDATA_DIRECTORY != null){ // Windows style
			return APPDATA_DIRECTORY + File.separator + capitalize(APPLICATION_NAME);
		}else{ //Unix style
			return HOME_DIRECTORY + File.separator + "." + decapitalize(APPLICATION_NAME);
		}
	}

	// Utility methods
	private static String capitalize(String name){
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	private static String decapitalize(String name){
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}
}
