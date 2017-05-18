package com.github.cypher.model;

import com.github.cypher.Main;
import com.github.cypher.sdk.api.Session;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Handles the loading and saving of the "last session" to enable auto-login / "keep me logged in"
class SessionManager {
	private static final String SESSION_FILE_NAME = "lastSession";

	public boolean savedSessionExists() {
		Path lastSessionFilePath = Paths.get(Main.USER_DATA_DIRECTORY + File.separator + SESSION_FILE_NAME);
		return Files.exists(lastSessionFilePath) && Files.isRegularFile(lastSessionFilePath);
	}

	// Returns null if loading failed
	// Session is loaded from USER_DATA_DIRECTORY + File.separator + SESSION_FILE_NAME
	public Session loadSessionFromDisk() {
		Session lastSession = null;
		FileInputStream fin = null;
		ObjectInputStream ois = null;

		try {
			fin = new FileInputStream(Main.USER_DATA_DIRECTORY + File.separator + SESSION_FILE_NAME);
			ois = new ObjectInputStream(fin);
			lastSession = (Session) ois.readObject();

			System.out.printf("last session file deserialized!\n");

		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.printf("last session file deserialization failed!\n");
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
	public void saveSessionToDisk(Session session) {
		if (session == null) {
			System.out.printf("Session not saved! Session parameter was null.\n");
			return;
		}

		FileOutputStream fout = null;
		ObjectOutputStream oos = null;

		try {
			fout = new FileOutputStream(Main.USER_DATA_DIRECTORY + File.separator + SESSION_FILE_NAME);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(session);

			System.out.printf("Session file serialized & saved!\n");

		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.printf("Session file serialization/saving failed!\n");
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

	// Deletes the saved session (if it exists) from the disk. The path USER_DATA_DIRECTORY + File.separator + SESSION_FILE_NAME is used
	public void deleteSessionFromDisk() {
		try {
			Files.deleteIfExists(Paths.get(Main.USER_DATA_DIRECTORY + File.separator + SESSION_FILE_NAME));
		} catch (IOException e) {
			System.out.printf("Session file exists but deleting it failed! %s \n", e.getMessage());
		}
	}
}
