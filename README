# New Relic Apache monitor plugin

This plugin allows you to monitor the status of an Apache 2.2 or 2.4 server, leveraging `mod_status`.

It is heavily based on the plugin written by MeetMe ([MeetMe New Relic Plugin]), with the exception that this only deals with Apache and is written in Java.

### Version

1.0.0 (released, Oct 24 2015)

### Tech

The code itself required Java 1.7 and up and is dependent on the **New Relic Java Plugin Agent SDK**.

* [MeetMe New Relic Plugin] - MeetMe reference plugin
* [New Relic Java Agent SDK] - New Relic Java Plugin Agent SDK


### Installation

You need a Java runtime, version 1.7 and up.
You can get the JAR file by
1. cloning this repo
2. cloning the New relic Java Agent SDK (and linking them)
3. compiling it into a JAR file
4. Creating the configuration file

### Configuration

Configuration is done through plugin.json and newrelic.json.
The repository contains examples for both JSON files in the /config folder.



Format of the 'plugin.json' file:

```
{
  "agents": [
    {
      "name" : "<your custom name>",
      "protocol" : "<http or https>",
      "host" : "<your hostname>",
      "port" : 443,
      "modStatusUrl" : "/server-status?auto"
    }
  ]
}
```

For easy (default) configuration, you can leave everything but the```host``` out, or combine as needed:
```
{
  "agents": [
    {
      "host" : "<minimal is just a host>"
    },
    {
      "host" : "<you can also combine a host and a port>",
      "port" : 8080
    },
    {
      "name" : "<your custom name>",
      "protocol" : "<http or https>",
      "host" : "<your hostname>",
      "port" : 443,
      "modStatusUrl" : "/server-status?auto"
    },
    ...
  ]
}
```

The default values are:
* name: <host> (or <host>:<port> if port is not 80)
* port: 80
* protocol: http
* modStatusUrl: /server-status?auto

For example, you can easily monitor www.apache.org through this config:

```
{
  "agents": [
    {
      "host" : "www.apache.org"
    }
  ]
}
```
which will start monitoring http://www.apache.org/server-status?auto.

### Development

Want to contribute? Great!

Feel free to fork and send a pull request.

Developer references:
 * https://docs.newrelic.com/docs/plugins/plugin-developer-resources/developer-reference/metric-naming-reference
 * https://docs.newrelic.com/docs/plugins/plugin-developer-resources/developer-reference/metric-value-reference

License
----

The BSD 2-Clause License:
http://opensource.org/licenses/BSD-2-Clause

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does it's job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [New Relic Java Agent SDK]: <https://github.com/newrelic-platform/metrics_publish_java>
   [MeetMe New Relic Plugin]: <https://github.com/MeetMe/newrelic-plugin-agent>