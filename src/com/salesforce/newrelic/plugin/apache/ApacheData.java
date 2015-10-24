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

    private long totalAccesses;
    private long totalKbytes;
    private long uptime;
    private double reqPerSec;
    private double bytesPerSec;
    private double bytesPerReq;
    private long busyWorkers;
    private long idleWorkers;

    private long connsTotal;
    private long connsAsyncWriting;
    private long connsAsyncKeepAlive;
    private long connsAsyncClosing;

    private ApacheScoreboard scoreboard;

    /**
     * <p>This will create an ApacheData object through the raw output from the mod_status module.</p>
     * 
     * <p>The expected output should look like this:<br />
     * <pre> {@code
     * Total Accesses: 17670733
     * Total kBytes: 773199362
     * CPULoad: 4.61315
     * Uptime: 461283
     * ReqPerSec: 38.3078
     * BytesPerSec: 1716420
     * BytesPerReq: 44806.1
     * BusyWorkers: 90
     * IdleWorkers: 660
     * ConnsTotal: 1488
     * ConnsAsyncWriting: 42
     * ConnsAsyncKeepAlive: 1145
     * ConnsAsyncClosing: 214
     * Scoreboard:
     * GGG...
     * } </pre></p>
     * 
     * @param data Raw output from mod_status (/server-status?auto)
     * @return A complete {@link ApacheData} object (including {@link ApacheScoreboard} if found).
     *         If data is empty or null, this will return an empty ApacheData object.
     */
    public static ApacheData parse(String data) {
        ApacheData result = new ApacheData();

        if (StringUtils.isEmpty(data)) {
            return result;
        }

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

    /**
     * @return the totalAccesses
     */
    public long getTotalAccesses() {
        return totalAccesses;
    }

    /**
     * @return the totalKbytes
     */
    public long getTotalKbytes() {
        return totalKbytes;
    }

    /**
     * @return the uptime
     */
    public long getUptime() {
        return uptime;
    }

    /**
     * @return the reqPerSec
     */
    public double getReqPerSec() {
        return reqPerSec;
    }

    /**
     * @return the bytesPerSec
     */
    public double getBytesPerSec() {
        return bytesPerSec;
    }

    /**
     * @return the bytesPerReq
     */
    public double getBytesPerReq() {
        return bytesPerReq;
    }

    /**
     * @return the busyWorkers
     */
    public long getBusyWorkers() {
        return busyWorkers;
    }

    /**
     * @return the idleWorkers
     */
    public long getIdleWorkers() {
        return idleWorkers;
    }

    /**
     * @return the connsTotal
     */
    public long getConnsTotal() {
        return connsTotal;
    }

    /**
     * @return the connsAsyncWriting
     */
    public long getConnsAsyncWriting() {
        return connsAsyncWriting;
    }

    /**
     * @return the connsAsyncKeepAlive
     */
    public long getConnsAsyncKeepAlive() {
        return connsAsyncKeepAlive;
    }

    /**
     * @return the connsAsyncClosing
     */
    public long getConnsAsyncClosing() {
        return connsAsyncClosing;
    }

    /**
     * @return the scoreboard
     */
    public ApacheScoreboard getScoreboard() {
        return scoreboard;
    }

    /**
     * @return true if this object has a scoreboard
     */
    public boolean hasScoreboard() {
        return null != scoreboard;
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
