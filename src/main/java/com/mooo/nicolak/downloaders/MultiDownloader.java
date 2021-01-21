package com.mooo.nicolak.downloaders;

import com.mooo.nicolak.serversconfig.UrlConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MultiDownloader implements Downloader{
    private Collection<URL> URLs;
    private final Collection<Downloader> downloaders = new ArrayList<>();
    @Override
    public void run() {
        URLs.forEach((url) -> {
            downloaders.add(new DefaultDownloader(url));
        });
        ExecutorService executor = Executors.newFixedThreadPool(UrlConfig.UrlPaths.values().length);
        downloaders.forEach(executor::submit);
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Double getMBPerSec() {
        return downloaders.stream().mapToDouble((downloader) -> downloader.getMBPerSec()).average().orElse(0.0);
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
}
