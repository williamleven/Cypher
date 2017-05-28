package com.github.cypher.settings;

import java.util.Locale;

public interface Settings {
	// Language setting
	Locale getLanguage();
	void setLanguage(Locale language);

	// Save session ("keep me logged in") settings
	boolean getSaveSession();
	void setSaveSession(boolean saveSession);

	boolean getUseSystemTray();
	void setUseSystemTray(boolean exitToSystemTray);

	// If control + enter should be used for sending messages (if false only enter is needed)
	boolean getControlEnterToSendMessage();
	void setControlEnterToSendMessage(boolean controlEnterToSendMessage);

	// Timeout is maximum time to poll in milliseconds before returning a request
	int getSDKTimeout();
	void setSDKTimeout(int timeout);

	// The time between each tick in the model in ms
	int getModelTickInterval();
	void setModelTickInterval(int interval);

	// Used to remember the windows X position
	int getLastWindowPosX();
	void setLastWindowPosX(int posX);

	// Used to remember the windows Y position
	int getLastWindowPosY();
	void setLastWindowPosY(int posY);

	// Used to remember the windows width
	int getLastWindowWidth();
	void setLastWindowWidth(int width);

	// Used to remember the windows height
	int getLastWindowHeight();
	void setLastWindowHeight(int height);
}
