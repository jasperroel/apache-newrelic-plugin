/*******************************************************************************
 * Copyright (c) 2015, Jasper Roel
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 * 
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 * 
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
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
