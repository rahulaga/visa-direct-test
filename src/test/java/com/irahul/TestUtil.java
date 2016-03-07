package com.irahul;

import org.junit.Assert;
import org.junit.Test;

public class TestUtil {

	@Test
	public void testRRN(){
		String rrn = Util.createRRN(Util.createSystemsTraceAuditNumber());
		System.err.println(rrn);
		Assert.assertEquals(12, rrn.length());
	}
}
