package com.github.cypher;

import java.util.Locale;

public interface Settings {
	Locale getLanguage();

	void setLanguage(Locale language);

	boolean getSaveSession();

	void setSaveSession(boolean saveSession);

	boolean getExitToSystemTray();

	void setExitToSystemTray(boolean exitToSystemTray);

	boolean getControlEnterToSendMessage();

	void setControlEnterToSendMessage(boolean controlEnterToSendMessage);
}
