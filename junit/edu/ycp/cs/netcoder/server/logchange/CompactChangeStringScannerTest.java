package edu.ycp.cs.netcoder.server.logchange;

import junit.framework.TestCase;
import edu.ycp.cs.netcoder.shared.logchange.Change;
import edu.ycp.cs.netcoder.shared.logchange.ChangeType;

public class CompactChangeStringScannerTest extends TestCase {
	private CompactChangeStringScanner empty;
	private CompactChangeStringScanner oneChange;
	
	@Override
	protected void setUp() throws Exception {
		empty = new CompactChangeStringScanner("");
		
		// IT0,11,0,12,1300476014676;"s"
		oneChange = new CompactChangeStringScanner("IT0,11,0,12,1300476014676;\"s\"");
	}
	
	public void testEmpty() throws Exception {
		assertFalse(empty.hasNext());
	}
	
	public void testOneChange() throws Exception {
		assertTrue(oneChange.hasNext());
		
		Change c = oneChange.next();
		
		assertEquals(ChangeType.INSERT_TEXT, c.getType());
		assertEquals(0, c.getStartRow());
		assertEquals(11, c.getStartColumn());
		assertEquals(0, c.getEndRow());
		assertEquals(12, c.getEndColumn());
		assertEquals(1300476014676L, c.getTimestamp());
		assertEquals(1, c.getNumLines());
		assertEquals("s", c.getText());
		
		assertFalse(oneChange.hasNext());
	}
}
