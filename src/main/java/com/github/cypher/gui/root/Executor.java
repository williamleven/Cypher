package com.github.cypher.gui.root;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Executor extends Thread {

	// Queue of actions
	private Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

	// Flag for handling interruptions that shouldn't kill the thread
	private volatile boolean wakeUp = false;

	@Override
	public void run() {

		Runnable current;

		boolean isRunning = true;
		while (isRunning) {

			// Get action.
			current = queue.poll();
			if (current != null) {
				// execute action
				current.run();
			} else {
				try {
					// Sleep for a long time
					Thread.sleep(100000);
				} catch (InterruptedException e) {
					if (wakeUp) {            // Resume if interrupt was a wakeUpCall.
						wakeUp = false;
					} else {
						isRunning = false;  // Otherwise, kill thread.
					}
				}
			}
		}
	}

	// Receives code to be handled by this thread.
	public void handle(Runnable e) {
		queue.add(e);
		wakeUp();
	}

	// Make the thread wake up from sleep
	private synchronized void wakeUp() {
		this.wakeUp = true;
		this.interrupt();
	}
}
