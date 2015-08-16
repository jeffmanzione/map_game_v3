package item.structures;

public class NumberFormatter {
	private static String[] factors = {"", "K", "M", "B"};
	public static String format(double num, int numSigFigs) {
		//System.out.println(num + " " + numSigFigs);
		
		boolean isNeg = num < 0;
		if (isNeg) {
			num = -num;
		}
		
		int places = (int) Math.log10(num) - (int) Math.log10(num) % 3;
		
		int stripped = (int) (num - num % (int) Math.pow(10, (int) Math.log10(num) - numSigFigs + 1));
		
		double val = stripped / Math.pow(10, places);
		
		String factor = factors[places / 3];
		
		//System.out.println(places + " " + stripped + " " + val + " " + factor);
		
		//System.out.println(val + " " + (int) val + " " + (val == (int) val));
		String stringed;
		if (val == (int) val) {
			stringed = (int) val + "";
		} else {
			stringed = val + "";
		}
		
		//System.out.println(num + " " + numSigFigs + " " + places + " " + stripped + " " + val + " " + stringed + " " + factor);
		return (isNeg && val != 0 ? "-" : "") + stringed + factor;
	}
	
	public static String formatDecimalPlaces(double num, int places) {
		num = Math.round(num * Math.pow(10, places));
		num /= Math.pow(10, places);
		
		return num + "";
	}
	
	public static void main(String[] args) {
		System.out.println(format(12345.33, 3));
		System.out.println(format(-300.1, 3));
		System.out.println(format(-3.1, 3));
		System.out.println(formatDecimalPlaces(-3.1222222, 2));
	}
}
