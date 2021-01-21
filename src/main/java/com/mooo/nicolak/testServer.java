package com.mooo.nicolak;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;

public class testServer {
    private String url = "";
    private Double lat = 0.0;
    private Double lon = 0.0;
    private String name = "Name not defined";
    private String country = "Country not defined";
    private String cc = "CC not defined";
    private String sponsor = "No sponsor";
    private Integer id = 0;
    private String host = "";
    private Boolean debug = false;

    testServer(String url, String host) {
        this.url = url;
        this.host = host;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public TestResult testServerTTL() {
        String[] result  = getHost().split(":");
        String server = result[0];
        Integer port = Integer.parseInt(result[1]);
        Long delta = Long.MAX_VALUE;
        try {
            Long start = System.currentTimeMillis();
            Socket s = new Socket();
            try {
                s.connect(new InetSocketAddress(server, port), 100);
            } catch (SocketTimeoutException e) {
                if (debug)
                    System.out.println("Host timeout " + server);
                return new TestResult(Long.MAX_VALUE, this);
            } catch (UnknownHostException e){
                if (debug)
                System.out.println("Host not found " + server);
                return new TestResult(Long.MAX_VALUE, this);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return new TestResult(Long.MAX_VALUE, this);
            }
            Long end = System.currentTimeMillis();
            delta = end - start;
            if (debug)
             System.out.println("Server " + result[0] + " conn time " + delta);
            if (s.isConnected()) {
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TestResult(delta, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        testServer that = (testServer) o;
        return Objects.equals(id, that.id) && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, host);
    }

    class TestResult implements Comparable<TestResult> {
        private long time;
        private testServer server;

        public testServer getServer() {
            return server;
        }

        public TestResult(long time, testServer server) {
            this.time = time;
            this.server = server;
        }

        @Override
        public int compareTo(TestResult o) {
            if (o == null) {
                return Integer.MAX_VALUE;
            }
            return Long.compare(time, o.time);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestResult that = (TestResult) o;
            return Objects.equals(server, that.server);
        }

        @Override
        public int hashCode() {
            return Objects.hash(server);
        }

        @Override
        public String toString() {
            return "TestResult{" +
                    "time=" + time +
                    ", server=" + server.getHost() +
                    '}';
        }
    }
}
