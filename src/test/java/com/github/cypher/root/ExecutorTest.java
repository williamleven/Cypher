package com.github.cypher.root;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class ExecutorTest {

	@Test
	public void executorTest(){
		// Create and start executor
		Executor executor = new Executor();
		executor.start();

		// Queue actions
		final BooleanProperty action1 = new SimpleBooleanProperty(false);
		executor.handle(() -> {
			action1.set(true);
		});
		final BooleanProperty isSeparateThread = new SimpleBooleanProperty(false);
		Thread testThread = Thread.currentThread();
		executor.handle(() -> {
			isSeparateThread.setValue(!Thread.currentThread().equals(testThread));
		});

		// Wait for action to be done
		try {
			Thread.sleep(5);
		}catch (InterruptedException e){}
		executor.interrupt();

		// Check if action were done
		assertTrue("Executor didn't execute.", action1.get());
		assertTrue("Executor doesn't seem to be running in its own thread", isSeparateThread.get());
	}


}
