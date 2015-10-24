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
 * <p>The agent is responsible for connecting to the Apache instance and retrieving the raw data from mod_status.</p>
 * 
 * <p>It then parses the data (using {@link ApacheData}) and reports it to New Relic.</p>
 * 
 * @author Jasper Roel - jasperroel@gmail.com
 * @since Oct 21, 2015
 * @version 1.0.0
 * 
 */
public class ApacheMonitorAgent extends Agent {

    private final static String GUID = "com.salesforce.newrelic.plugin";
    private final static String version = "1.0.0";

    private final Logger logger = Logger.getLogger(ApacheMonitorAgent.class);

    private final Processor requestProcessor = new EpochProcessor();
    private final Processor bytessentProcessor = new EpochProcessor();

    private final String agentName;
    private final String protocol;
    private final String host;
    private final int port;
    private final String modStatusUrl;

    /**
     * <p>Creates an ApacheMonitorAgent. All parameter are required.</p>
     * 
     * @param agentName The name to report in New Relic (usually the domainname or something similar)
     * @param protocol The protocol to connect to Apache with (usually "http" or "https")
     * @param host The hostname ("www.apache.org")
     * @param port The port (usually 80 or 443)
     * @param modStatusUrl The URL where to retrieve the data (usually "/server-status?auto")
     */
    public ApacheMonitorAgent(String agentName, String protocol, String host, int port, String modStatusUrl) {
        super(GUID, version);
        this.agentName = agentName;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.modStatusUrl = modStatusUrl;

        logger.info("Agent initialized with: "
            + "agentName=" + agentName
            + ",protocol=" + protocol
            + ",host=" + host
            + ",port=" + port
            + ",modStatusUrl=" + modStatusUrl);
    }

    @Override
    public void pollCycle() {
        String rawApacheData = collectApacheData();
        ApacheData apacheData = parseApacheModStatusResult(rawApacheData);
        reportApacheDataToNewRelic(apacheData);
    }

    /**
     * <p>Connects to Apache and retrieves the raw data.</p>
     * 
     * @return The raw data from the URL provided. Returns null is an IOException occurs
     */
    private String collectApacheData() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(protocol, host, port, modStatusUrl);
            conn = (HttpURLConnection) url.openConnection();
            return IOUtils.toString(conn.getInputStream());
        } catch (IOException e) {
            logger.error("[", agentName, "] Could not parse server-status, error: " + e.getMessage());
            return null;
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
    }

    /**
     * <p>Turns the raw data into a fully completed {@link ApacheData}.
     * 
     * @param result The raw data from an Apache mod_status call
     * @return a completed {@link ApacheData}
     */
    private ApacheData parseApacheModStatusResult(String result) {
        return ApacheData.parse(result);
    }

    /**
     * <p>Reports the available data from {@link ApacheData} to New Relic.
     * 
     * @param apacheData If the object isn't null, it will report the data to New Relic.
     *            If the scoreboard is present, that will be reported as well.
     */
    private void reportApacheDataToNewRelic(ApacheData apacheData) {
        if (null == apacheData) {
            return;
        }
        // For all absolute metrics
        reportMetric("Totals/Requests", "requests", requestProcessor.process(apacheData.getTotalAccesses()));
        reportMetric("Totals/Bytes Sent", "kb", bytessentProcessor.process(apacheData.getTotalKbytes()));

        // All "regular" Apache 2.2 metrics
        reportMetric("Uptime", "sec", apacheData.getUptime());
        reportMetric("Requests/Velocity", "requests/sec", apacheData.getReqPerSec());
        reportMetric("Requests/Average Payload Size", "bytes", apacheData.getBytesPerReq());
        reportMetric("Bytes/Per Second", "bytes/sec", apacheData.getBytesPerSec());
        reportMetric("Workers/Busy", "workers", apacheData.getBusyWorkers());
        reportMetric("Workers/Idle", "workers", apacheData.getIdleWorkers());

        // add conns* (Apache 2.4 only)
        reportMetric("Connections/Total", "connections", apacheData.getConnsTotal());
        reportMetric("Connections/AsyncWriting", "connections", apacheData.getConnsAsyncWriting());
        reportMetric("Connections/AsyncKeepAlive", "connections", apacheData.getConnsAsyncKeepAlive());
        reportMetric("Connections/AsyncClosing", "connections", apacheData.getConnsAsyncClosing());

        // Scoreboard
        if (apacheData.hasScoreboard()) {
            reportMetric("Scoreboard/Waiting For Conn", "slots", apacheData.getScoreboard().getWaitinForConn());
            reportMetric("Scoreboard/Starting Up", "slots", apacheData.getScoreboard().getStartingUp());
            reportMetric("Scoreboard/Reading Request", "slots", apacheData.getScoreboard().getReadingRequest());
            reportMetric("Scoreboard/Sending Reply", "slots", apacheData.getScoreboard().getSendingReply());
            reportMetric("Scoreboard/Keepalive Read", "slots", apacheData.getScoreboard().getKeepaliveRead());
            reportMetric("Scoreboard/DNS Lookup", "slots", apacheData.getScoreboard().getDnsLookup());
            reportMetric("Scoreboard/Closing Conn", "slots", apacheData.getScoreboard().getClosingConn());
            reportMetric("Scoreboard/Logging", "slots", apacheData.getScoreboard().getLogging());
            reportMetric("Scoreboard/Gracefully Finishing", "slots", apacheData.getScoreboard().getGracefullyFinishing());
            reportMetric("Scoreboard/Idle Cleanup", "slots", apacheData.getScoreboard().getIdleCleanup());
            reportMetric("Scoreboard/Open Slot", "slots", apacheData.getScoreboard().getOpenSlot());
        }

        logger.debug("[", agentName, "] Recorded metrics: ", apacheData);
    }

    @Override
    public String getAgentName() {
        return agentName;
    }
}
