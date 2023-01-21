package com.mooo.nicolak.serversconfig;


import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class UrlConfig {
    private final String confUrl;
    private final String servers;

    public enum UrlPaths {
        x1000("http://%s/speedtest/random1000x1000.jpg?x=%d"),
        x2000("http://%s/speedtest/random2000x2000.jpg?x=%d"),
        x2500("http://%s/speedtest/random2500x2500.jpg?x=%d"),
        x3000("http://%s/speedtest/random3000x3000.jpg?x=%d"),
        x3500("http://%s/speedtest/random3500x3500.jpg?x=%d"),
        x4000("http://%s/speedtest/random4000x4000.jpg?x=%d");

        final String pattern;

        UrlPaths(String s) {
            pattern = s;
        }

        public String getUrl(String host) {
            return String.format(pattern, host, System.currentTimeMillis());
        }
    }

    public UrlConfig() {
        confUrl = "https://www.speedtest.net/speedtest-config.php";
        servers = "http://c.speedtest.net/speedtest-servers-static.php";
    }

    public UrlConfig(String confUrl, String serversUrl) {
        this.confUrl = confUrl;
        this.servers = serversUrl;
    }


    public List<TestServer> getServerConfiguration() throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        UrlConfigXmlDef.ConfigXML configXML = xmlMapper.readValue(new URL(confUrl), UrlConfigXmlDef.ConfigXML.class);
        Map<Integer, Boolean> ignoreIdsMap = getIgnoreids(configXML.getServerConfig().getIgnoreids());

        UrlConfigXmlDef.xmlServers serversMap = xmlMapper.readValue(new URL(servers), UrlConfigXmlDef.xmlServers.class);
        return serversMap.getServers().stream().filter(
                server -> !ignoreIdsMap.containsKey(server.getId())).
                collect(Collectors.toList());
    }

    private Map<Integer, Boolean> getIgnoreids (String ignoreidsStr) {
        if (ignoreidsStr == null)
            return null;

        Map<Integer, Boolean> ret = new HashMap<>() {
        };

        String[] aList = ignoreidsStr.split(",");
        Arrays.stream(aList).forEach(str -> ret.put(Integer.parseInt(str), true));

        return ret;
    }
}
