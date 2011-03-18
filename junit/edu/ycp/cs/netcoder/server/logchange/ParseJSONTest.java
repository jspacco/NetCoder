package edu.ycp.cs.netcoder.server.logchange;

import junit.framework.TestCase;

public class ParseJSONTest extends TestCase {
	private ParseJSON p;
	
	@Override
	protected void setUp() throws Exception {
		p = new ParseJSON();
	}
	
	static class ExpectStrings implements ParseJSONCallback {
		String[] expected;
		int count;
		
		public ExpectStrings(String[] expected) {
			this.expected= expected;
			count = 0;
		}
		
		@Override
		public void visitString(String s) {
			assertTrue(count < expected.length);
			assertEquals(expected[count], s);
			count++;
		}
		
		public void checkDone() {
			assertEquals(expected.length, count);
		}
	}
	
	public void testOneString() throws Exception {
		String s = "\"s\"";
		ExpectStrings callback = new ExpectStrings(new String[]{"s"});
		p.parse(s, callback);
		callback.checkDone();
	}
}
