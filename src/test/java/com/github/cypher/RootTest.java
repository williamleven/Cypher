package com.github.cypher;

import com.airhacks.afterburner.injection.Injector;
import com.github.cypher.root.RootView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.HashMap;
import java.util.Map;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class RootTest extends ApplicationTest {
	@Override
	public void start(Stage stage) throws Exception {
		Map<Object, Object> customProperties = new HashMap<>();
		customProperties.put("n1", 8);
		customProperties.put("s1", "test");
		Injector.setConfigurationSource(customProperties::get);
		RootView rootView = new RootView();

		Scene scene = new Scene(rootView.getView());

		stage.setScene(scene);
		stage.show();
	}

	@Test
	public void injection_test_label() {
		// expect:
		verifyThat(".label", hasText("test"));
	}
}
