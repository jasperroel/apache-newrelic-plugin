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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;
import com.newrelic.metrics.publish.util.Logger;

/**
 * <p>This is a helper Factory provided by the New Relic SDK.
 * It parses the JSON config files and returns it as a single {@link Map} per agent.</p>
 * 
 * <p>This factory then creates the {@link Agent} based on that configuration and add its to the Runner queue.</p>
 * 
 * @author Jasper Roel - jasperroel@gmail.com
 * @since Oct 21, 2015
 * @version 1.0.0
 * 
 */
public class ApacheMonitorAgentFactory extends AgentFactory {

    private final Logger logger = Logger.getLogger(ApacheMonitorAgentFactory.class);

    private final String defaultProtocol = "http";
    private final int defaultPort = 80;
    private final String defaultModStatusUrl = "/server-status?auto";
    private final int defaultConnectionTimeout  = (int) TimeUnit.SECONDS.toMillis(15);

    private final String agentNameProperty = "name";
    private final String hostProperty = "host";
    private final String portProperty = "port";
    private final String modStatusProperty = "modStatusUrl";
    private final String protocolProperty = "protocol";

    /**
     * <p>Creates a {@link ApacheMonitorAgent} for each (valid) agent configured in <code>plugin.json<code>.
     */
    @Override
    public Agent createConfiguredAgent(Map<String, Object> properties) throws ConfigurationException {
        // Collect stats from JSON
        String agentName = (String) properties.get(agentNameProperty);
        String host = (String) properties.get(hostProperty);
        String protocol = defaultProtocol;
        int port = defaultPort;
        String modStatusUrl = defaultModStatusUrl;
        int connectionTimeout = defaultConnectionTimeout;

        if (null == host) {
            throw new ConfigurationException("'" + hostProperty + "' property cannot be null.");
        }

        // Override only if there is an override
        if (properties.containsKey(modStatusProperty)) {
            modStatusUrl = (String) properties.get(modStatusProperty);
        }
        if (properties.containsKey(protocolProperty)) {
            protocol = (String) properties.get(protocolProperty);
        }
        if (properties.containsKey(portProperty)) {
            // Agreed, this doesn't look pretty, but it works.
            // JSON parses the value as a "Long", so we cast it down to an int "gracefully"
            Object portObject = properties.get(portProperty);
            if (null != portObject && portObject.getClass().isAssignableFrom(Long.class)) {
                long portL = (Long) portObject;
                // Only assign port if it's in a valid range
                if (portL > 0 && portL <= 65535) {
                    port = (int) portL;
                } else {
                    logger.warn(portL, " is not a valid port for host ", host, ", defaulting to ", port);
                }
            }
        }

        // If we didn't get a name, we make one up
        if (null == agentName) {
            agentName = host;
            if (port != defaultPort) {
                agentName += ":" + port;
            }
        }

        return new ApacheMonitorAgent(agentName, protocol, host, port, modStatusUrl, connectionTimeout);
    }
}
