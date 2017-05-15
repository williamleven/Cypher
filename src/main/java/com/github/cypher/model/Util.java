package com.github.cypher.model;

public class Util {

	// Check if sting could be a roomcollection
	static boolean isHomeserver(String s) {
		//Todo
		return true;
	}

	// Check if sting could be a room label
	static boolean isRoomLabel(String s) {
		//Todo
		return true;
	}

	// Check if sting could be a user
	static boolean isUser(String s) {
		//Todo
		return true;
	}

	static String extractServer(String input) {
		return input.split(":", 2)[1];
	}
}
