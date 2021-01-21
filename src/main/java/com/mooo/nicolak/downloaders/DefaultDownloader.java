package com.mooo.nicolak.downloaders;

import com.mooo.nicolak.Consts;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DefaultDownloader implements Downloader {
    private URL href;
    private final Map<Long, Long> speed;
    private Double totalTimeSec;
    private int downloadStatus;
    private long totalMB;

    public DefaultDownloader() {
        speed = new ConcurrentHashMap<>();
        downloadStatus = RUN_INCOMPLETE;

    }
    public DefaultDownloader(String href) throws MalformedURLException {
        this(new URL(href));
    }

    public DefaultDownloader(URL href) {
        this();
        this.href = href;
    }

    @Override
    public void run() {
        try {
            URL url = this.href;
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
            this.downloadStatus = RUN_OK;
        } catch (IOException e) {
            e.printStackTrace();
            this.downloadStatus = Downloader.RUN_UNEXPECTED_ERROR;
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

    @Override
    public Double getMBPerSec() {
        OptionalDouble average = speed.values().stream().mapToLong(this::bytesToMB).average();
        return average.orElse(0);
    }

    @Override
    public String toString() {
        return String.format("Downloaded %sMB from %s in %.2f seconds in %.2f MB/s, status = %s",
                href,
                totalMB,
                totalTimeSec,
                getMBPerSec(),
                (downloadStatus == RUN_OK ?  "ok" : "incomplete"));
    }

    private long bytesToMB(long bytes) {
     return (long) ((float)bytes / (float)Consts.MB_IN_BYTES);
    }


    @Override
    public int getDownloadStatus() {
        return downloadStatus;
    }

    @Override
    public void setHref(Collection<String> href) throws MalformedURLException {
        this.href = new URL(href.iterator().next());
    }
}
