package com.github.cypher.root;

import com.github.cypher.gui.root.Executor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ExecutorTest {

	@Test
	public void executorTest() {
		// Create and start executor
		Executor executor = new Executor();
		executor.start();

		// Queue actions
		final BooleanProperty action1 = new SimpleBooleanProperty(false);
		executor.handle(() -> {
			action1.set(true);
		});
		final BooleanProperty isSeparateThread = new SimpleBooleanProperty(false);
		long testThread = Thread.currentThread().getId();
		executor.handle(() -> {
			isSeparateThread.setValue(Thread.currentThread().getId() != testThread);
		});

		// Wait for action to be done
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
		executor.interrupt();

		// Check if action were done
		assertTrue("Executor didn't execute.", action1.get());
		assertTrue("Executor doesn't seem to be running in its own thread", isSeparateThread.get());
	}
}
