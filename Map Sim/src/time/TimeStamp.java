package time;


public class TimeStamp {
	int hour, day, month, year, era;
	public TimeStamp(int h, int d, int m, int y, int e) {
		hour = h;
		day = d;
		month = m;
		year = y;
		era = e;
	}
	public TimeStamp(String[] date) {
		hour = Integer.valueOf(date[0]);
		day = Integer.valueOf(date[1]);
		month = Integer.valueOf(date[2]);
		year = Integer.valueOf(date[3]);
		era = Integer.valueOf(date[4]);
	}
	
	public int getHour() {
		return hour;
	}
	
	public int getDay() {
		return day;
	}
	public int getMonth() {
		return month;
	}
	public int getYear() {
		return year;
	}
	public int getEra() {
		return era;
	}
	public String toString() {
		return hour + ":00, " + day + "/" + month + "/" + year;
	}
	public boolean equals(TimeStamp d) {
		if(hour == d.hour && day == d.getDay() && month == d.getMonth() && year == d.getYear() && era == d.getEra()) return true;
		else return false;
	}
}
