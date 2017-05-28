package com.github.cypher.sdk;

import com.github.cypher.sdk.api.MatrixApiLayer;
import com.github.cypher.sdk.api.Util;
import java.net.URL;

public final class SdkFactory {
	private SdkFactory(){}

	public static Client createClient(String settingsNamespace){
		setupApiLayer();
		return new Client(new MatrixApiLayer(), settingsNamespace);
	}

	private static void setupApiLayer() {
		try {
			URL.setURLStreamHandlerFactory(new Util.MatrixMediaURLStreamHandlerFactory());
		} catch (Error e) {
			return; // Operation only permitted once; catching for harness.
		}
	}
}
