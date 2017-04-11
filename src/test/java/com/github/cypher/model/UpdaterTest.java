package com.github.cypher.model;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class UpdaterTest {

	public class Counter implements Updatable {

		private int counter = 0;

		public void update(){
			counter++;
		}

		public int getCount(){
			return counter;
		}
		public void reset(){
			counter = 0;
		}
	}

	@Test
	public void updaterTest(){

		// Create a set of counter
		Counter c1 = new Counter();
		Counter c2 = new Counter();
		Counter c3 = new Counter();

		// Loop until test can be performed without interrupts
		boolean interrupted = true;
		while (interrupted) {
			interrupted = false;

			Updater u = Updater.getInstance();
			u.setInterval(1);

			// Reset counters and make them listen for updates
			c1.reset();
			c2.reset();
			c3.reset();
			u.add(c1, 1);
			u.add(c2, 2);
			u.add(c3, 1);

			// Start the updater
			u.start();

			// Remove one counter mid running
			try {
				Thread.sleep(10);
				u.remove(c3);
				Thread.sleep(10);
			} catch (InterruptedException err) {
				interrupted = true;
			}
			u.interrupt();
		}

		// Make sure all counters were set to expected values
		assertTrue("Individual speed not working", c2.getCount()*2 <= c1.getCount()+2);
		assertTrue("Individual speed not working", c2.getCount()*2 >= c1.getCount()-2);
		assertTrue("Object was not removed from loop", c3.getCount() < c1.getCount());
	}




}
