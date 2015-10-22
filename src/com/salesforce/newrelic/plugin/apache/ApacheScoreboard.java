package com.salesforce.newrelic.plugin.apache;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author Jasper Roel - jasperroel@gmail.com
 * @since Oct 21, 2015
 * @version 1.0.0
 *
 */
public class ApacheScoreboard {
//	  '_': {'type': 'gauge', 'label': 'Scoreboard/Waiting For Conn', 'suffix': 'slots'},
//    'S': {'type': 'gauge', 'label': 'Scoreboard/Starting Up', 'suffix': 'slots'},
//    'R': {'type': 'gauge', 'label': 'Scoreboard/Reading Request', 'suffix': 'slots'},
//    'W': {'type': 'gauge', 'label': 'Scoreboard/Sending Reply', 'suffix': 'slots'},
//    'K': {'type': 'gauge', 'label': 'Scoreboard/Keepalive Read', 'suffix': 'slots'},
//    'D': {'type': 'gauge', 'label': 'Scoreboard/DNS Lookup', 'suffix': 'slots'},
//    'C': {'type': 'gauge', 'label': 'Scoreboard/Closing Conn', 'suffix': 'slots'},
//    'L': {'type': 'gauge', 'label': 'Scoreboard/Logging', 'suffix': 'slots'},
//    'G': {'type': 'gauge', 'label': 'Scoreboard/Gracefully Finishing', 'suffix': 'slots'},
//    'I': {'type': 'gauge', 'label': 'Scoreboard/Idle Cleanup', 'suffix': 'slots'},
//    '.': {'type': 'gauge', 'label': 'Scoreboard/Open Slot', 'suffix': 'slots'}}
	
	public long waitinForConn; // _
	public long startingUp; // S
	public long readingRequest; // R
	public long sendingReply; // W
	public long keepaliveRead; // K
	public long dnsLookup; // D
	public long closingConn; // C
	public long logging; // L
	public long gracefullyFinishing; // G
	public long idleCleanup; // I
	public long openSlot; // .
	
	public static ApacheScoreboard parseScoreboard(String scoreboard) {
		ApacheScoreboard result = new ApacheScoreboard();
		
		char[] chars =scoreboard.toCharArray();
		for (char c :chars) {
			String s = Character.toString(c);
			if ("_".equals(s)) {
				result.waitinForConn++;
			}
			if ("S".equals(s)) {
				result.startingUp++;
			}
			if ("R".equals(s)) {
				result.readingRequest++;
			}
			if ("W".equals(s)) {
				result.sendingReply++;
			}
			if ("K".equals(s)) {
				result.keepaliveRead++;
			}
			if ("D".equals(s)) {
				result.dnsLookup++;
			}
			if ("C".equals(s)) {
				result.closingConn++;
			}
			if ("L".equals(s)) {
				result.logging++;
			}
			if ("G".equals(s)) {
				result.gracefullyFinishing++;
			}
			if ("I".equals(s)) {
				result.idleCleanup++;
			}
			if (".".equals(s)) {
				result.openSlot++;
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
