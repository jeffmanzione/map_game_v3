package time;

import gfx.bars.DateBar;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import maps.GameMap;

public class TimeManager extends Thread {

	private Lock lock = new ReentrantLock();

	private boolean dead = false;

	private boolean started = false;

	private static final int[] SPEEDS = { 448, 320, 128, 64, 8 };
	private int index = 0;
	private int speed = 448;

	private GameMap map;

	private DateBar dateBar;

	private Timeline timeline;

	public TimeManager(GameMap map) {
		this.map = map;
		timeline = new Timeline(9, 1, 1, -1200, 1, "B.C.E");

		map.setTimeManager(this);
	}

	public void setDateBar(DateBar dateBar) {
		this.dateBar = dateBar;
		dateBar.setDate(timeline.getDate());
	}

	private long wait = -1;

	public void run() {

		while (!dead) {

			try {
				lock.lock();

				if (++wait % 8 == 0) {
					dateBar.setDate(timeline.addHour());

					map.advance();

					if (timeline.isNewMonth()) {
						map.advanceMonth();
					}

					if (timeline.isNewYear()) {
						map.advanceYear();
					}
				}

			} finally {
				lock.unlock();
			}

			try {

				sleep(speed / 8);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void pause() {
		lock.lock();
	}

	public void unpause() {
		if (!started) {
			started = true;
			start();
		} else {
			lock.unlock();
		}
	}

	public void kill() {
		dead = true;
	}

	public void slow() {
		if (index > 0) {
			index--;
			speed = SPEEDS[index];
		}
	}

	public void fast() {
		if (index < SPEEDS.length - 2) {
			index++;
			speed = SPEEDS[index];
		}
	}

	public long getTime() {
		return wait;
	}
}
