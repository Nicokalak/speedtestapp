package com.mooo.nicolak;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Downloader implements Runnable{
    private final String link;
    final static int KB = 1024;
    final static int MB = 1024*KB;
    final Map<Long, Long> speed;
    private long totalTimeSec;
    private long totalMB;


    public Downloader(String link) {
        this.link = link;
        speed = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        try {
            URL url = new URL(link);
            HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
            long completeFileSize = httpConnection.getContentLength();
            BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream());
            byte[] buff = new byte[MB];
            long downloadedFileSize = 0;
            int x = 0;
            while ((x = in.read(buff, 0, MB)) >= 0) {
                downloadedFileSize += x;
                countBytes(x);
            }
            totalMB = bytesToMB(downloadedFileSize);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        totalTimeSec = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start);
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
        return String.format("Dowloaded %sMB in %s seconds in %.2f MB/s", totalMB, totalTimeSec, getMBPerSec());
    }

    private long bytesToMB(long bytes) {
     return (long) ((float)bytes / (float)MB);
    }
}
