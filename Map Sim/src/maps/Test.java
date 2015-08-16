package maps;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test {
	public void lol() {
		Lock locky = new ReentrantLock();
		if (locky.tryLock()) {
			try {
				
			} finally {
				locky.unlock();
			}
		}
	}
}
