package parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class ParserReader extends BufferedReader {

	public ParserReader(Reader in) {
		super(in);
	}
	
	private String line = "";
	private int lineNumber = 1;
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public String getLine() {
		return line;
	}
	
	public int read(boolean inQuotes) throws IOException {
		char c = 0;
		
		do {
			c = (char) super.read();
			
			if (c == '\n') {
				lineNumber++;
				line = "";
			}

		} while (!inQuotes && Character.isWhitespace(c));

		line = line + c;
		count++;
		
		//System.out.println(c);
		
		return c;
	}
	
	int count = 0;
	
	public void mark (int readAheadLimit) throws IOException {
		count = 0;
		super.mark(readAheadLimit);
	}
	
	public void reset () throws IOException {
		line = line.substring(0, line.length() - count);
		count = 0;
		super.reset();
	}

}
