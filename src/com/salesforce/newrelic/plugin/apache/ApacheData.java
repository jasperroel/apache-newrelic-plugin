package com.salesforce.newrelic.plugin.apache;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author Jasper Roel - jasperroel@gmail.com
 * @since Oct 21, 2015
 * @version 1.0.0
 *
 */
public class ApacheData {

	public long totalAccesses;
	public long totalKbytes;
	public long uptime;
	public double reqPerSec;
	public double bytesPerSec;
	public double bytesPerReq;
	public long busyWorkers;
	public long idleWorkers;

	public long connsTotal;
	public long connsAsyncWriting;
	public long connsAsyncKeepAlive;
	public long connsAsyncClosing;

	public ApacheScoreboard scoreboard;

	// Total Accesses: 17670733
	// Total kBytes: 773199362
	// CPULoad: 4.61315
	// Uptime: 461283
	// ReqPerSec: 38.3078
	// BytesPerSec: 1716420
	// BytesPerReq: 44806.1
	// BusyWorkers: 90
	// IdleWorkers: 660
	// ConnsTotal: 1488
	// ConnsAsyncWriting: 42
	// ConnsAsyncKeepAlive: 1145
	// ConnsAsyncClosing: 214
	// Scoreboard:
	public static ApacheData parse(String data) {
		ApacheData result = new ApacheData();
		String[] splitLines = StringUtils.split(data, "\n");
		boolean parseScoreboard = false;
		for (String line : splitLines) {
			if (line.startsWith("Total Accesses")) {
				result.totalAccesses = getLongValue(line);
			}
			if (line.startsWith("Total kBytes")) {
				result.totalKbytes = getLongValue(line);
			}
			if (line.startsWith("Uptime")) {
				result.uptime = getLongValue(line);
			}
			if (line.startsWith("ReqPerSec")) {
				result.reqPerSec = getDoubleValue(line);
			}
			if (line.startsWith("BytesPerSec")) {
				result.bytesPerSec = getDoubleValue(line);
			}
			if (line.startsWith("BytesPerReq")) {
				result.bytesPerReq = getDoubleValue(line);
			}
			if (line.startsWith("BusyWorkers")) {
				result.busyWorkers = getLongValue(line);
			}
			if (line.startsWith("IdleWorkers")) {
				result.idleWorkers = getLongValue(line);
			}

			if (line.startsWith("ConnsTotal")) {
				result.connsTotal = getLongValue(line);
			}
			if (line.startsWith("ConnsAsyncWriting")) {
				result.connsAsyncWriting = getLongValue(line);
			}
			if (line.startsWith("ConnsAsyncKeepAlive")) {
				result.connsAsyncKeepAlive = getLongValue(line);
			}
			if (line.startsWith("ConnsAsyncClosing")) {
				result.connsAsyncClosing = getLongValue(line);
			}

			if (line.startsWith("Scoreboard")) {
				parseScoreboard = true;
			}
			if (parseScoreboard) {
				result.scoreboard = ApacheScoreboard.parseScoreboard(line);
			}
		}
		return result;
	}

	private static long getLongValue(String line) {
		String sep = ": ";
		return Long.parseLong(line.substring(line.indexOf(sep) + sep.length()));
	}

	private static double getDoubleValue(String line) {
		String sep = ": ";
		return Double.parseDouble(line.substring(line.indexOf(sep) + sep.length()));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
