package com.salesforce.newrelic.plugin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.processors.EpochProcessor;
import com.newrelic.metrics.publish.processors.Processor;
import com.newrelic.metrics.publish.util.Logger;
import com.salesforce.newrelic.plugin.apache.ApacheData;

/**
 * Based on: https://github.com/MeetMe/newrelic-plugin-agent/blob/master/
 * newrelic_plugin_agent/plugins/apache_httpd.py
 * 
 * Using test data from: http://www.apache.org/server-status?auto
 * 
 * Base (required) library:
 * https://github.com/newrelic-platform/metrics_publish_java
 * 
 * Developer references:
 * https://docs.newrelic.com/docs/plugins/plugin-developer-resources/developer-
 * reference/metric-naming-reference
 * https://docs.newrelic.com/docs/plugins/plugin-developer-resources/developer-
 * reference/metric-value-reference
 * 
 * @author Jasper Roel - jasperroel@gmail.com
 * @since Oct 21, 2015
 * @version 1.0.0
 *
 */
public class DispatcherAgent extends Agent {

	private final static String GUID = "com.salesforce.newrelic.plugin";
	private final static String version = "1.0.0";

	private final Processor requestProcessor = new EpochProcessor();
	private final Processor bytessentProcessor = new EpochProcessor();

	private final Logger logger = Logger.getLogger(Agent.class);

	private final String agentName;
	private final String protocol;
	private final String host;
	private final int port;
	private String modStatusUrl = "/server-status?auto";

	public DispatcherAgent(String agentName, String protocol, String host, int port) {
		super(GUID, version);
		this.agentName = agentName;
		this.protocol = protocol;
		this.host = host;
		this.port = port;

		logger.info("Agent initialized with: " 
		+ "agentName=" + agentName 
		+ ",protocol=" + protocol
		+ ",host=" + host
		+ ",port=" + port);
	}

	@Override
	public void pollCycle() {
		collectApacheData();
	}

	private void collectApacheData() {
		try {
			URL url = new URL(protocol, host, port, modStatusUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			String result = IOUtils.toString(conn.getInputStream());
			parseApacheModStatusResult(result);
		} catch (IOException e) {
			logger.error("Could not parse server-status, error: " + e.getMessage());
		}
	}

	private void parseApacheModStatusResult(String result) {
		ApacheData data = ApacheData.parse(result);

		// For all absolute metrics
		reportMetric("Totals/Requests", "requests", requestProcessor.process(data.totalAccesses));
		reportMetric("Totals/Bytes Sent", "kb", bytessentProcessor.process(data.totalKbytes));

		// All "regular" Apache 2.2 metrics
		reportMetric("Uptime", "sec", data.uptime);
		reportMetric("Requests/Velocity", "requests/sec", data.reqPerSec);
		reportMetric("Requests/Average Payload Size", "bytes", data.bytesPerReq);
		reportMetric("Bytes/Per Second", "bytes/sec", data.bytesPerSec);
		reportMetric("Workers/Busy", "workers", data.busyWorkers);
		reportMetric("Workers/Idle", "workers", data.idleWorkers);

		// add conns* (Apache 2.4 only)
		reportMetric("Connections/Total", "connections", data.connsTotal);
		reportMetric("Connections/AsyncWriting", "connections", data.connsAsyncWriting);
		reportMetric("Connections/AsyncKeepAlive", "connections", data.connsAsyncKeepAlive);
		reportMetric("Connections/AsyncClosing", "connections", data.connsAsyncClosing);

		// Scoreboard
		reportMetric("Scoreboard/Waiting For Conn", "slots", data.scoreboard.waitinForConn);
		reportMetric("Scoreboard/Starting Up", "slots", data.scoreboard.startingUp);
		reportMetric("Scoreboard/Reading Request", "slots", data.scoreboard.readingRequest);
		reportMetric("Scoreboard/Sending Reply", "slots", data.scoreboard.sendingReply);
		reportMetric("Scoreboard/Keepalive Read", "slots", data.scoreboard.keepaliveRead);
		reportMetric("Scoreboard/DNS Lookup", "slots", data.scoreboard.dnsLookup);
		reportMetric("Scoreboard/Closing Conn", "slots", data.scoreboard.closingConn);
		reportMetric("Scoreboard/Logging", "slots", data.scoreboard.logging);
		reportMetric("Scoreboard/Gracefully Finishing", "slots", data.scoreboard.gracefullyFinishing);
		reportMetric("Scoreboard/Idle Cleanup", "slots", data.scoreboard.idleCleanup);
		reportMetric("Scoreboard/Open Slot", "slots", data.scoreboard.openSlot);
		
		logger.info("Recorded metrics: ", data);
	}

	@Override
	public String getAgentName() {
		return agentName;
	}

	public void setModStatusUrl(String modStatusUrl) {
		this.modStatusUrl = modStatusUrl;
	}
}
