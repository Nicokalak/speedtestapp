package com.mooo.nicolak;

import java.io.BufferedInputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Downloader implements Runnable{
    private final Set<URL> links;
    final Map<Long, Long> speed;
    private Double totalTimeSec;
    private long totalMB;

    protected Downloader() {
        links = new HashSet<>();
        speed = new ConcurrentHashMap<>();

    }
    public Downloader(String link) throws MalformedURLException {
        this(new URL(link));
    }

    public Downloader(URL link) {
        this();
        this.links.add(link);
    }

    @Override
    public void run() {
        try {
            URL url = links.iterator().next();
            URLConnection httpConnection = url.openConnection();
            long completeFileSize = httpConnection.getContentLength();
            BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream());
            byte[] buff = new byte[Consts.MB_IN_BYTES];
            long downloadedFileSize = 0;
            int x;
            long start = System.currentTimeMillis();
            while ((x = in.read(buff, 0, Consts.MB_IN_BYTES)) >= 0) {
                downloadedFileSize += x;
                countBytes(x);
            }
            totalTimeSec = ((double)(System.currentTimeMillis() - start))/ 1000.00;
            totalMB = bytesToMB(downloadedFileSize);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void countBytes(long downloadedFileSize) {
        long sec = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        if (speed.containsKey(sec)) {
            speed.put(sec, speed.get(sec) + downloadedFileSize );
        } else {
            speed.put(sec, downloadedFileSize);
        }
    }

    public Double getMBPerSec() {
        OptionalDouble average = speed.values().stream().mapToLong(this::bytesToMB).average();
        return average.orElse(0);
    }

    @Override
    public String toString() {
        return String.format("Dowloaded %sMB in %.2f seconds in %.2f MB/s", totalMB, totalTimeSec, getMBPerSec());
    }

    private long bytesToMB(long bytes) {
     return (long) ((float)bytes / (float)Consts.MB_IN_BYTES);
    }
}
