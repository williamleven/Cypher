package com.github.cypher;

import java.io.*;

public final class Util {

	// Util class shouldn't be creatable
	private Util(){}

	static String capitalize(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	static String decapitalize(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	// Creates the user data folder path
	static String getUserDataDirectoryPath(String applicationName) {
		if (System.getenv("XDG_CONFIG_HOME") != null){ // XDG Base Directory Specification
			return System.getProperty("XDG_CONFIG_HOME") + File.separator + "." + decapitalize(applicationName);

		} else if (System.getenv("APPDATA") != null) { // Windows default
			return System.getenv("APPDATA") + File.separator + capitalize(applicationName);

		}  else {                                         // Unix default
			return System.getProperty("user.home") + File.separator + ".config" + File.separator + decapitalize(applicationName);
		}
	}
}
