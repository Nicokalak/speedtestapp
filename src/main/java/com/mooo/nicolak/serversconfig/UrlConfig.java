package com.mooo.nicolak.serversconfig;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
//TODO change XML parser to XMLMapper
public class UrlConfig {
    private String confUrl;
    private String servers;

    public enum UrlPaths {
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
        Element docRootConfig = getElement(readXmlConfiguration(confUrl));
        docRootConfig.getElementsByTagName("server-config").item(0).getAttributes().item(1).getTextContent();
        Element serverconfig = getChild(docRootConfig, "server-config");
        Map<Integer, Boolean> ignoreIdsMap = getIgnoreids(serverconfig);
        Element docRootServers = getElement(readXmlConfiguration(servers));
        return getTestServersMap(docRootServers, ignoreIdsMap);
    }


    private List<TestServer> getTestServersMap(Element docRootServers, Map ignoreIdsMap) {
        List<TestServer> retMap = new ArrayList<>();
        if (docRootServers == null || ignoreIdsMap == null)
            return retMap;

        NodeList children = docRootServers.getElementsByTagName("servers").item(0).getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element == false)
                continue;
            NamedNodeMap n = children.item(i).getAttributes();
            String idString = ((Attr) n.getNamedItem("id")).getValue();
            Integer id = Integer.parseInt(idString);
            if (ignoreIdsMap.containsKey(id)) {
                System.out.println("Ignored id " + id);
                continue;
            }
            String url = ((Attr) n.getNamedItem("url")).getValue();
            String host = ((Attr) n.getNamedItem("host")).getValue();
            retMap.add(new TestServer(url, host));
        }
        return retMap;
    }
    /*
        Expects to have server config
     */
    private Map<Integer, Boolean> getIgnoreids (Element element) {
        Map<Integer, Boolean> ret = new HashMap<Integer, Boolean>(){};
        if (element == null)
            return null;
        String ignoreidsStr = element.getAttribute("ignoreids");
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
