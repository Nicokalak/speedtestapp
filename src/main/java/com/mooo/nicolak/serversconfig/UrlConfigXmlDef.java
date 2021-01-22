package com.mooo.nicolak.serversconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UrlConfigXmlDef {
    public static class xmlServers {
        @JsonProperty("servers")
        List<TestServer> servers = new ArrayList<>();

        public List<TestServer> getServers() {
            return servers;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConfigXML {
        @JsonProperty("server-config")
        ServerConfigXML serverConfig;

        public ServerConfigXML getServerConfig() {
            return serverConfig;
        }

        @Override
        public String toString() {
            return "ConfigXML{" +
                    "serverConfig=" + serverConfig +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServerConfigXML {
        @JsonProperty("ignoreids")
        String ignoreids;

        public String getIgnoreids() {
            return ignoreids;
        }
    }
}
