package com.irahul;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public class Util {

	public static String createSystemsTraceAuditNumber(){
		String curr = String.valueOf(System.currentTimeMillis());
		return curr.substring(curr.length()-6);
	}
	
	public static String createRRN(String systemsTraceAuditNumber){
		//ydddhhnnnnnn
		LocalDateTime now = LocalDateTime.now();
		String y=String.valueOf(now.get(ChronoField.YEAR)).substring(3, 4);
		int ddd = now.get(ChronoField.DAY_OF_YEAR);
		int hh=now.get(ChronoField.HOUR_OF_DAY);
		return y+ddd+hh+systemsTraceAuditNumber;		
	}
}
