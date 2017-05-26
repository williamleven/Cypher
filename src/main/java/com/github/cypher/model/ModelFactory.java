package com.github.cypher.model;

import com.github.cypher.sdk.SdkFactory;
import com.github.cypher.settings.Settings;
import com.google.common.eventbus.EventBus;

public final class ModelFactory {
	public static Client createClient(Settings settings, EventBus eventBus, String userDataDirectory, String settingsNamespace){
		return new Client(() -> SdkFactory.createClient(settingsNamespace),
		                  settings,
		                  eventBus,
		                  userDataDirectory
		);
	}

	private ModelFactory(){}
}
