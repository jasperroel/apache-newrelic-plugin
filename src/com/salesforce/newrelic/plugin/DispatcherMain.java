package com.salesforce.newrelic.plugin;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.Runner;
import com.newrelic.metrics.publish.configuration.ConfigurationException;
import com.newrelic.metrics.publish.util.Logger;

/**
 * 
 * @author Jasper Roel - jasperroel@gmail.com
 * @since Oct 21, 2015
 * @version 1.0.0
 *
 */
public class DispatcherMain {

	private static final Logger logger = Logger.getLogger(Agent.class);

	public static void main(String[] args) {
		try {
			Runner runner = new Runner();
			runner.add(new DispatcherAgentFactory());
			runner.setupAndRun(); // Never returns
		} catch (ConfigurationException e) {
			logger.error("Configuration error:", e.getMessage());
			System.exit(-1);
		}
	}
}
