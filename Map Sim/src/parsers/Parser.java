package parsers;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Parser {
	protected ParserReader reader;
	protected Parser (File file) throws FileNotFoundException {
		reader = new ParserReader (new FileReader (file));
	}

	protected String parseChars (boolean inQuotes) throws IOException {
		String id = "";
		while (lookahead(CHAR)) {
			id += (char) reader.read(inQuotes);
		}

		return id;
	}

	protected Module parseID () throws IOException {
		//System.out.println("Parse ID");
		return new Module (parseChars(false), null, null);
	}

	protected Module parseAssignment () throws IOException {
		Module mod = parseID();

		if (consume("=")) {
			mod = parseValue(mod);
		} else {
			System.err.println ("PROBLEM1 " + reader.getLineNumber() + "\n\"" + reader.getLine() + "\"");
		}

		return mod;

	}

	protected Module parseValue (Module mod) throws IOException {
		//System.out.println("Parse Value");
		if (lookahead (LB)) {
			mod.type = Module.Type.MOD;
			mod.ref = parseBrackets();
		} else if (lookahead (DIG)) {
			mod.type = Module.Type.INT;
			mod.ref = parseNumber(mod);
		} else if (lookahead (QUOTE)) {
			mod.type = Module.Type.STRING;
			mod.ref = parseString();
		} else if (lookahead (CHAR)) {
			mod.type = Module.Type.VP;
			mod.ref = parseVP();
		} else {
			System.err.println("PROBLEM2");
		}

		return mod;
	}

	protected String parseVP() throws IOException {
		//System.out.println("Parse VP");
		return parseChars(false);
	}

	protected String parseString() throws IOException {
		//System.out.println("Parse String");
		if (consume ("\"")) {
			String str = parseChars(true);
			if (consume ("\"")) {
				return str;
			} else {
				System.err.println("PROBLEM3");
			}
		} else {
			System.err.println("PROBLEM4");
		}
		return null;
	}

	protected int parseInt() throws IOException {
		int val = 0;
		while (lookahead (DIG)) {
			val = val * 10 + Integer.parseInt(((char) reader.read(false)) + "");
		}

		return val;
	}

	protected Object parseNumber(Module mod) throws IOException {
		if (lookahead (DIG)) {
			int val = parseInt();

			if (consume (".")) {
				Integer dec = parseInt();

				double res = ((double) val) + 
						((double) dec) / Math.pow(10, dec.toString().length());
				mod.type = Module.Type.DOUBLE;
				return res;
			} else if (consume (",")) {
				int g = parseInt();

				if (consume (",")) {
					int b = parseInt();
					mod.type = Module.Type.RGB;
					return new Color (val, g, b);
				} else {
					System.err.println("PROBLEM5");
				}

			} else {
				return val;
			}
		} else {
			System.err.println("PROBLEM6");
		}

		return null;
	}

	protected Object parseBrackets() throws IOException {
		//System.out.println("Parse Brackets");
		Map<String, Object> hash = new HashMap<String, Object>();

		if (consume ("{")) {
			do {
				Module mod = parseAssignment();
				hash.put(mod.id, mod.ref);
			} while (consume (";") && !lookahead (RB));

			if (consume ("}")) {
				return hash;
			} else {
				System.err.println("PROBLEM7");
			}
		} else {
			System.err.println("PROBLEM8");
		}

		return null;
	}

	protected boolean consume (String str) throws IOException {
		reader.mark(str.length());
		for (char c : str.toCharArray()) {
			if (c != reader.read(false)) {
				reader.reset();
				return false;
			}
		}
		return true;
	}

	protected static final int CHAR = 0, DIG = 1, PERIOD = 2, EQUALS = 3, LB = 4, 
			RB = 5, QUOTE = 6;
	protected boolean lookahead (int type) throws IOException {
		reader.mark(1);
		char c = (char) reader.read(false);
		boolean res = false;
		switch (type) {
		case CHAR:
			if (Character.isAlphabetic(c) || c == '_') {
				res = true;
			}
			break;
		case DIG:
			if (Character.isDigit(c)) {
				res = true;
			}
			break;
		case PERIOD:
			if (c == '.') {
				res = true;
			}
			break;
		case EQUALS:
			if (c == '=') {
				res = true;
			}
			break;
		case LB:
			if (c == '{') {
				res = true;
			}
			break;
		case RB:
			if (c == '}') {
				res = true;
			}
			break;
		case QUOTE:
			if (c == '\"') {
				res = true;
			}
			break;
		}

		try {
			reader.reset();
		} catch (IOException e) {
			System.err.println();
		}
		return res;
	}
}
