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
}
