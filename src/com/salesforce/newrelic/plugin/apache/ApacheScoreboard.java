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
 * <p>This class deals with parsing and representing the Scoreboard overview from an Apache /server-status request.</p>
 * 
 * <p>The Scoreboard is an overview of the current status of the various Workers in an Apache instance.</p>
 * 
 * @author Jasper Roel - jasperroel@gmail.com
 * @since Oct 21, 2015
 * @version 1.0.0
 * 
 */
public class ApacheScoreboard {
    private long waitinForConn; // _
    private long startingUp; // S
    private long readingRequest; // R
    private long sendingReply; // W
    private long keepaliveRead; // K
    private long dnsLookup; // D
    private long closingConn; // C
    private long logging; // L
    private long gracefullyFinishing; // G
    private long idleCleanup; // I
    private long openSlot; // .

    /**
     * <p>Parses a Scoreboard string and returns an {@link ApacheScoreboard}.<br />
     * 
     * @param scoreboard This should match the Scoreboard only (no "Scoreboard: "), so something like <code>GGW..__</code>
     * @return {@link ApacheScoreboard}, if the string is empty, this will return an empty {@link ApacheScoreboard}.
     */
    public static ApacheScoreboard parseScoreboard(String scoreboard) {
        ApacheScoreboard result = new ApacheScoreboard();

        if (StringUtils.isEmpty(scoreboard)) {
            return result;
        }

        char[] chars = scoreboard.toCharArray();
        for (char c : chars) {
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

    /**
     * @return the waitinForConn
     */
    public long getWaitinForConn() {
        return waitinForConn;
    }

    /**
     * @return the startingUp
     */
    public long getStartingUp() {
        return startingUp;
    }

    /**
     * @return the readingRequest
     */
    public long getReadingRequest() {
        return readingRequest;
    }

    /**
     * @return the sendingReply
     */
    public long getSendingReply() {
        return sendingReply;
    }

    /**
     * @return the keepaliveRead
     */
    public long getKeepaliveRead() {
        return keepaliveRead;
    }

    /**
     * @return the dnsLookup
     */
    public long getDnsLookup() {
        return dnsLookup;
    }

    /**
     * @return the closingConn
     */
    public long getClosingConn() {
        return closingConn;
    }

    /**
     * @return the logging
     */
    public long getLogging() {
        return logging;
    }

    /**
     * @return the gracefullyFinishing
     */
    public long getGracefullyFinishing() {
        return gracefullyFinishing;
    }

    /**
     * @return the idleCleanup
     */
    public long getIdleCleanup() {
        return idleCleanup;
    }

    /**
     * @return the openSlot
     */
    public long getOpenSlot() {
        return openSlot;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
