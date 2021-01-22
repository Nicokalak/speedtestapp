package com.mooo.nicolak.serversconfig;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

//TODO change XML parser to XMLMapper
public class UrlConfig {
    private String confUrl;
    private String servers;

    public enum UrlPaths {
        x1000("http://%s/speedtest/random1000x1000.jpg?x=%d"),
        x2000("http://%s/speedtest/random2000x2000.jpg?x=%d"),
        x2500("http://%s/speedtest/random2500x2500.jpg?x=%d"),
        x3000("http://%s/speedtest/random3000x3000.jpg?x=%d"),
        x3500("http://%s/speedtest/random3500x3500.jpg?x=%d"),
        x4000("http://%s/speedtest/random4000x4000.jpg?x=%d");

        String pattern;

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
                server -> ignoreIdsMap.containsKey(server.getId()) == false).
                collect(Collectors.toList());
    }

    private Map<Integer, Boolean> getIgnoreids (String ignoreidsStr) {
        if (ignoreidsStr == null)
            return null;
        Map<Integer, Boolean> ret = new HashMap<Integer, Boolean>(){};

        String[] aList = ignoreidsStr.split(",");
        Arrays.stream(aList).forEach(str -> {
            ret.put(Integer.parseInt(str), true);
        });

        return ret;
    }

    private Element getChild(Element element, String tag) {
        NodeList node = element.getElementsByTagName(tag);
        for (int i=0; i < node.getLength(); i++) {
            Element e = (Element) node.item(i);
            if (e.getAttribute(tag) != null ) {
                return  e;
            }
        }
        return null;
    }

    private Element getElement(Document doc) {
        return doc.getDocumentElement();
    }

    private Document readXmlConfiguration(String confUrl) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(confUrl);

        } catch (SAXException e) {
            e.printStackTrace();
        }
        return doc;
    }
}
