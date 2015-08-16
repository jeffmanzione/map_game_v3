package time;

public class Timeline {
	public int currentHour, currentDay, currentMonth, currentYear, currentWeekDay;
	public String insig;
	public String[] months = {"PLACEHOLDER", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	public String[] weekdays = {"PLACEHOLDER", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
	public int[] dayNumber= {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	private boolean isLeap;
	public TimeStamp current;
	public Timeline(int h, int d, int m, int y, int wd, String sig) {
		currentDay = d;
		currentMonth = m;
		currentYear = y;
		currentWeekDay = wd;
		currentHour = h;
		insig = sig;
		isLeap = false;
		current = new TimeStamp(currentHour, currentDay, currentMonth, currentYear, 0);
	}
	public String addHour() {

		currentHour++;

		if (currentHour == 24) {
			currentHour = 0;
			
			// for day of the week
			if (currentWeekDay == 7) {
				currentWeekDay = 1;
			} else {
				currentWeekDay++;
			}

			// if the next day is not in the same month as today and if feb 28
			if (dayNumber[currentMonth] < currentDay+1 && !(isLeap && currentDay == 28 && currentMonth == 2)) {
				// if the current month is December
				if (currentMonth == months.length-1) {
					// next year
					currentYear++;
					currentMonth = 1;
					currentDay = 1;

					// checks for leap year
					if (currentYear%4 == 0) {
						isLeap = true;
					} else {
						isLeap = false;
					}
				} else {
					// next month
					currentMonth++;
					currentDay = 1;
				}
			} /* if it is the next month */ else {
				currentDay++;
			}

		}

		current = new TimeStamp(currentHour, currentDay, currentMonth, currentYear, 0);
		return getDate();
	}

	// gets the current date
	public String getDate() {
		if (currentYear < 0) {
			insig ="B.C.E";
		} else {
			insig = "C.E";
		}
		return currentHour + ":00, " + weekdays[currentWeekDay] + ", " + months[currentMonth] + " " + currentDay + ", " + Math.abs(currentYear) + " " + insig;
	}
	// starts a new Kingdom
	public String changeKingdom(String s) {
		currentYear = 1;
		currentDay = 1;
		currentMonth = 1;

		if (currentWeekDay == 7) {
			currentWeekDay = 1;
		} else {
			currentWeekDay++;
		}

		insig = s;

		return getDate();

	}

	// returns whether or not it is the first of the month
	public boolean isNewMonth() {
		return currentDay == 1;
	}
	// returns whether or not it is the first of the year
	public boolean isNewYear() {
		return currentDay == 1 && currentMonth == 1;
	}

	// gets the day
	public TimeStamp getTimeStamp() {
		return current;
	}

	public static void main(String args[]) {
		Timeline t = new Timeline(9, 11, 10, 2011, 2, "E.K.");
		System.out.println(t.getDate());
		for (int i = 0; i < 400; i++) {
			System.out.println(t.addHour());
		}
		System.out.println(t.changeKingdom("M.K."));
		for (int i = 0; i < 800; i++) {
			System.out.println(t.addHour());
		}
	}

}
