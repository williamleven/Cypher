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
		loadSettings();
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
			System.out.println("Could not create settings file");
			return null;
		}
	}


	public synchronized Locale getLanguage() {
		return Locale.forLanguageTag(settingsData.languageTag); //java.util.Locale is immutable so no defensive copying is needed.
	}

	public synchronized void setLanguage(Locale language) {
		settingsData.languageTag = language.toLanguageTag(); //java.util.Locale is immutable so no defensive copying is needed.
		saveSettings();
	}

	public synchronized void loadSettings() {
		// Make sure settingsFile is set before loading settings
		if (settingsFile != null) {
			System.out.println("reading settings from: " + settingsFile);
			settingsData =  new Toml().read(settingsFile).to(SettingsData.class);
		}else{
			System.out.println("Could not access settings file, defaults will be loaded.");
			settingsData = new SettingsData();
		}
	}

	public synchronized void saveSettings() {
		// Make sure settingsFile is set before saving settings
		if (settingsFile != null){
			try {
				new TomlWriter().write(settingsData, settingsFile);
				System.out.println("Settings saved to: " + settingsFile);
			} catch (IOException e) {
				System.out.println("Could not access settings file, settings won't be saved.");
			}
		}else{
			System.out.println("Could not access settings file, settings won't be saved.");
		}
	}

}
