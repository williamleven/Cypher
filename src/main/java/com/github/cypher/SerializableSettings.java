package com.github.cypher;

import java.io.*;
import java.util.Locale;


// Mutable and thread-safe Settings implementation that automatically serializes and saves the settings to the disk after each change.
// The class is not immutable as the same object will be used in many different classes (through dependency injection)
public class SerializableSettings implements Settings {
	public static final String FILE_NAME = "config";

	private SettingsData settingsData;
	private int SDKTimeout; // In ms

	private static class SettingsData implements Serializable{
		Locale language;
	}

	SerializableSettings() {
		SettingsData readSettingsData = loadSettings();
		if (readSettingsData != null) {
			settingsData = readSettingsData;
		} else {
			settingsData = new SettingsData();

			// Set defaults

			settingsData.language = Locale.getDefault();
			SDKTimeout = 500;
		}
	}

	@Override
	public synchronized Locale getLanguage() {
		return settingsData.language; //java.util.Locale is immutable so no defensive copying is needed.
	}

	@Override
	public synchronized void setLanguage(Locale language) {
		settingsData.language = language; //java.util.Locale is immutable so no defensive copying is needed.
		writeSettings();
	}

	@Override
	public synchronized int getSDKTimeout() {
		return SDKTimeout;
	}

	@Override
	public synchronized void setSDKTimeout(int timeout) {
		SDKTimeout = timeout;
	}

	private SettingsData loadSettings() {
		SettingsData settingsData = null;
		FileInputStream fin = null;
		ObjectInputStream ois = null;

		try {
			fin = new FileInputStream(Main.WORKING_DIRECTORY + "/" + FILE_NAME);
			ois = new ObjectInputStream(fin);
			settingsData = (SettingsData) ois.readObject();

			if (DebugLogger.ENABLED) {
				DebugLogger.log("Settings file deserialized!");
			}

		} catch (Exception ex) {
			//ex.printStackTrace();
			if (DebugLogger.ENABLED) {
				DebugLogger.log("Settings file deserialization failed!");
			}
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return settingsData;
	}

	private void writeSettings() {
		FileOutputStream fout = null;
		ObjectOutputStream oos = null;

		try {
			fout = new FileOutputStream(Main.WORKING_DIRECTORY + "/" + FILE_NAME);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(settingsData);

			if (DebugLogger.ENABLED) {
				DebugLogger.log("Settings file serialized!");
			}

		} catch (Exception ex) {
			//ex.printStackTrace();
			if (DebugLogger.ENABLED) {
				DebugLogger.log("Settings file serialization failed!");
			}
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
