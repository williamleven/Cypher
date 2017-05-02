package com.github.cypher.model;

import com.github.cypher.DebugLogger;
import com.github.cypher.Main;
import com.github.cypher.sdk.api.Session;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Handles the loading and saving of the "last session" to enable auto-login / "keep me logged in"
class SessionManager {
	private static final String SESSION_FILE_NAME = "lastSession";

	private final com.github.cypher.sdk.Client sdkClient;

	public SessionManager(com.github.cypher.sdk.Client sdkClient) {
		this.sdkClient = sdkClient;
	}

	public boolean savedSessionExists() {
		Path lastSessionFilePath = Paths.get(Main.USER_DATA_DIRECTORY + File.separator + SESSION_FILE_NAME);
		return Files.exists(lastSessionFilePath) && Files.isRegularFile(lastSessionFilePath);
	}

	// Returns null if load failed
	// Session is loaded from USER_DATA_DIRECTORY + File.separator + SESSION_FILE_NAME
	public Session loadSession() {
		Session lastSession = null;
		FileInputStream fin = null;
		ObjectInputStream ois = null;

		try {
			fin = new FileInputStream(Main.USER_DATA_DIRECTORY + File.separator + SESSION_FILE_NAME);
			ois = new ObjectInputStream(fin);
			lastSession = (Session) ois.readObject();

			if (DebugLogger.ENABLED) {
				DebugLogger.log("last session file deserialized!");
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			if (DebugLogger.ENABLED) {
				DebugLogger.log("last session file deserialization failed!");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
		return lastSession;
	}


	// Session is saved to USER_DATA_DIRECTORY + File.separator + SESSION_FILE_NAME
	public void saveSession() {
		Session session = sdkClient.getSession();

		if (session == null) {
			if (DebugLogger.ENABLED) {
				DebugLogger.log("Session not saved! SDK returned a null session.");
			}
			return;
		}

		FileOutputStream fout = null;
		ObjectOutputStream oos = null;

		try {
			fout = new FileOutputStream(Main.USER_DATA_DIRECTORY + File.separator + SESSION_FILE_NAME);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(session);

			if (DebugLogger.ENABLED) {
				DebugLogger.log("Session file serialized & saved!");
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			if (DebugLogger.ENABLED) {
				DebugLogger.log("Session file serialization/saving failed!");
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
