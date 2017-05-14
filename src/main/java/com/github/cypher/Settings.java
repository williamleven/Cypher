package com.github.cypher;

import java.util.Locale;

public interface Settings {
	Locale getLanguage();
	void setLanguage(Locale language);
	boolean getSaveSession();
	void setSaveSession(boolean saveSession);
	boolean getControlEnterToSendMessage();
	void setControlEnterToSendMessage(boolean controlEnterToSendMessage);

	// Timeout is maximum time to poll in milliseconds before returning a request
	int getSDKTimeout();
	void setSDKTimeout(int timeout);

	// The time between each tick in the model in ms
	int getModelTickInterval();
	void setModelTickInterval(int interval);
}
