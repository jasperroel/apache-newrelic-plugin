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
