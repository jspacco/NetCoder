package edu.ycp.cs.netcoder.server.logchange;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompactChangeStringScanner {
	private static final Pattern META_PATTERN =
		Pattern.compile("^(IT|RT|IL|RL)(\\d+),(\\d+),(\\d+),(\\d+),(\\d+)$");

	private String s;
	private Change next;
	
	public CompactChangeStringScanner(String s) {
		this.s = s;
	}

	public boolean hasNext() {
		peek();
		return next != null;
	}

	public Change next() {
		peek();
		if (next == null) {
			throw new NoSuchElementException();
		}
		Change val = next;
		next = null;
		return val;
	}
	
	private void peek() {
		if (next != null || s.length() == 0) {
			// nothing to do
			return;
		}
		
		// look for semicolon which ends change metadata
		int semi = s.indexOf(';');
		if (semi < 0) {
			throw new IllegalArgumentException("Cannot find end of metadata");
		}
		
		String meta = s.substring(0, semi);
		Matcher m = META_PATTERN.matcher(meta);
		s = s.substring(semi + 1);

		if (!m.matches()) {
			throw new IllegalArgumentException("Invalid change string: " + s);
		}
		
		ChangeType type = ChangeType.fromCompactString(m.group(1));
		int startRow = Integer.parseInt(m.group(2));
		int startColumn = Integer.parseInt(m.group(3));
		int endRow = Integer.parseInt(m.group(4));
		int endColumn = Integer.parseInt(m.group(5));
		long timestamp = Long.parseLong(m.group(6));
		
		final List<String> text = new ArrayList<String>();
		
		ParseJSONCallback callback = new ParseJSONCallback() {
			@Override
			public void visitString(String s) {
				text.add(s);
			}
		};
		ParseJSON parser = new ParseJSON();
		s = parser.parse(s, callback);

		if (type == ChangeType.INSERT_TEXT || type == ChangeType.REMOVE_TEXT) {
			next = new Change(type, startRow, startColumn, endRow, endColumn, timestamp, text.get(0));
		} else {
			next = new Change(type, startRow, startColumn, endRow, endColumn, timestamp, text);
		}
	}
}
