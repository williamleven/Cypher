package com.github.cypher;

import java.util.Locale;

public interface Settings {
	Locale getLanguage();
	void setLanguage(Locale language);

	// Timeout is maximum time to poll in milliseconds before returning a request
	int getSDKTimeout();
	void setSDKTimeout(int timeout);
}
