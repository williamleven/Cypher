package com.github.cypher;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.*;
import java.util.Locale;

public class TOMLSettings implements Settings {

	private static final String HOME_DIRECTORY = System.getProperty("user.home");

	private static final String FILE_NAME = "config.toml";

	private final File settingsFile;

	private SettingsData settingsData;

	private static class SettingsData{
		String languageTag = Locale.getDefault().toLanguageTag(); // Default Value
	}

	TOMLSettings() {
		settingsFile = createOrLoadFile();
		load();
		save();
	}

	private static File createOrLoadFile(){
		try {

			// Create folder if it doesn't exist
			new File(HOME_DIRECTORY + File.separator + ".cypher").mkdir();

			// Load File
			File file = new File(HOME_DIRECTORY + File.separator + ".cypher" + File.separator + FILE_NAME);

			// Create file if it doesn't exist
			file.createNewFile();

			return file;
		}catch (IOException e) {
			DebugLogger.log("Could not create settings file");
			return null;
		}
	}


	public synchronized Locale getLanguage() {
		return Locale.forLanguageTag(settingsData.languageTag);
	}

	public synchronized void setLanguage(Locale language) {
		settingsData.languageTag = language.toLanguageTag();
		save();
	}

	public synchronized void load() {
		// Make sure settingsFile is set before loading settings
		if (settingsFile != null) {
			DebugLogger.log("reading settings from: " + settingsFile);
			settingsData =  new Toml().read(settingsFile).to(SettingsData.class);
		}else{
			DebugLogger.log("Could not access settings file, defaults will be loaded.");
			settingsData = new SettingsData();
		}
	}

	public synchronized void save() {
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

}
