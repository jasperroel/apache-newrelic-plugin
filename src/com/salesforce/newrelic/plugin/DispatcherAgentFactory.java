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

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

/**
 * 
 * @author Jasper Roel - jasperroel@gmail.com
 * @since Oct 21, 2015
 * @version 1.0.0
 *
 */
public class DispatcherAgentFactory extends AgentFactory {

	private final String defaultProtocol = "http";
	private final int defaultPort = 80;

	@Override
	public Agent createConfiguredAgent(Map<String, Object> properties) throws ConfigurationException {
		// Collect stats from JSON
		String agentName = (String) properties.get("name");
		String host = (String) properties.get("host");
		String protocol = defaultProtocol;
		int port = defaultPort;
		String modStatusUrl = null;

		if (null == host) {
			throw new ConfigurationException("'host' cannot be null.");
		}

		// Override only if there is an override
		if (properties.containsKey("modStatusUrl")) {
			modStatusUrl = (String) properties.get("modStatusUrl");
		}
		if (properties.containsKey("protocol")) {
			protocol = (String) properties.get("protocol");
		}
		if (properties.containsKey("port")) {
			// Yes, this doesn't look pretty, but it works
			// JSON always is a long and we just directly cast it to an integer
			port = (int) (long) properties.get("port");
		}

		// If we didn't get a name, we make one up
		if (null == agentName) {
			agentName = host;
			if (port != 80) {
				agentName += ":" + port;
			}
		}

		DispatcherAgent agent = new DispatcherAgent(agentName, protocol, host, port);
		if (null != modStatusUrl) {
			agent.setModStatusUrl(modStatusUrl);
		}
		return agent;
	}
}
