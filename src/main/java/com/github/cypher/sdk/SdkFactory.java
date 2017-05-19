package com.github.cypher.sdk;

import com.github.cypher.sdk.api.MatrixApiLayer;

public class SdkFactory {
	public static Client createClient(String settingsNamespace){
		return new Client(new MatrixApiLayer(), settingsNamespace );
	}
}
