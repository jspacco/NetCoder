package edu.ycp.cs.netcoder.server.logchange;

/**
 * An extremely limited JSON parser that just handles strings.
 */
public class ParseJSON {
	private enum Mode { OUT, IN, ESCAPE };
	
	public String parse(String jsonText, ParseJSONCallback callback) {
		 Mode mode = Mode.OUT;
		 
		 System.out.println("Parsing " + jsonText);
		 
		 boolean isArray = jsonText.startsWith("[");
		 if (!isArray && !jsonText.startsWith("\"")) {
			 throw new IllegalArgumentException("Not string or array: " + jsonText);
		 }
		 
		 StringBuilder buf = new StringBuilder();
		
		for (int i = 0; i < jsonText.length(); i++) {
			char c = jsonText.charAt(i);
			
			switch (mode) {
			case OUT:
				if (isArray && c == ']') {
					// finished array
					return jsonText.substring(i + 1);
				}
				if (c == '"') {
					// beginning of string
					mode = Mode.IN;
					buf.setLength(0);
				}
				break;
			case IN:
				if (c == '"') {
					// done
					String s = buf.toString();
					//System.out.println("Got string!" + s);
					callback.visitString(s);
					
					if (!isArray) {
						// done with single string
						return jsonText.substring(i + 1);
					}
					
					mode = Mode.OUT;
				} else if (c == '\\') {
					// start escape
					mode = Mode.ESCAPE;
				} else {
					// normal character
					buf.append(c);
				}
				break;
			case ESCAPE:
				if (c == '"' || c == '\\' || c == '/') {
					buf.append(c);
				} else if (c == 'b') {
					buf.append('\b');
				} else if (c == 'f') {
					buf.append('\f');
				} else if (c == 'n') {
					buf.append('\n');
				} else if (c == 'r') {
					buf.append('\r');
				} else if (c == 't') {
					buf.append('\t');
				} else if (c == 'u') {
					// unicode escape
					i++;
					if (i + 4 >= jsonText.length()) {
						throw new IllegalArgumentException("Invalid unicode escape");
					}
					String hex = jsonText.substring(i, i + 4).toLowerCase();
					int code = Integer.parseInt(hex, 16);
					buf.append((char) code);
					i += 3;
				} else {
					throw new IllegalArgumentException("Invalid JSON string escape " + c);
				}
				mode = Mode.IN; // back to normal processing
			}
		}
		
		return ""; // should not happen 
	}
}
