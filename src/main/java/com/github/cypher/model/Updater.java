package com.github.cypher.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Updater extends Thread {

	// Holds all updatable classes
	// Initiated to size 10 but will resize if necessary
	private Map<Updatable, Integer> watching = new ConcurrentHashMap<>(10);

	// The time between each tic
	private final int interval;

	public Updater(int interval) {
		this.interval = interval;
	}

	// Tic loop
	@Override
	public void run() {

		for (int i = 1; ; i++) {

			// Notify all Updatable classes
			for (Map.Entry<Updatable, Integer> entry : watching.entrySet()) {
				if (i % entry.getValue() == 0) {
					entry.getKey().update();
				}
			}

			// Sleep
			try {
				Thread.sleep(interval);
			} catch (InterruptedException err) {

			}
		}
	}

	// Register a listener. The listener will be notified avery {i}'th tic
	public void add(Updatable u, Integer i) {
		watching.put(u, i);
	}

	// Remove a listener
	public void remove(Updatable u) {
		watching.remove(u);
	}
}
