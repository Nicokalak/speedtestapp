package com.mooo.nicolak.downloaders;

import com.mooo.nicolak.serversconfig.UrlConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MultiDownloader implements Downloader{
    private Collection<URL> URLs;
    private final Collection<Downloader> downloaders = new ArrayList<>();
    private final int NumUrlThreads = 4;
    private final int NumThreads = 4;

    @Override
    public void run() {
        URLs.forEach((url) -> {
            IntStream.range(0, NumUrlThreads).forEach(i ->{
                downloaders.add(new DefaultDownloader(url));
            });
        });
        ExecutorService executor = Executors.newFixedThreadPool(NumThreads);
        downloaders.forEach(executor::submit);
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getDownloadStatus() {
        Boolean ret = downloaders.stream().map(downloader -> downloader.getDownloadStatus()).allMatch(status -> status == RUN_OK);
        return ret ? RUN_OK : RUN_INCOMPLETE;
    }

    @Override
    public void setHref(Collection<String> href) throws MalformedURLException {
        URLs = href.stream().map((str) -> {
            try {
                return new URL(str);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.format("Downloaded from %d sources at speed %.2f MB/s, status = %s",
                URLs.size(),
                getMBPerSec(),
                (getDownloadStatus() == RUN_OK ?  "ok" : "incomplete"));
    }

    public Map<Long, Long> getSpeedStat() {
        Map<Long, Long> statmap = new ConcurrentHashMap<>();
        for (Downloader downloader : downloaders) {
            for (Map.Entry<Long, Long> entry : downloader.getSpeedStat().entrySet()) {
                Long key = entry.getKey();
                Long value = entry.getValue();
                if (statmap.containsKey(key)) {
                    statmap.put(key, statmap.get(key) + value);
                } else {
                    statmap.put(key, value);
                }
            }
        }
        return statmap;
    }
}
