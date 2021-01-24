package com.mooo.nicolak.downloaders;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;
import java.util.OptionalDouble;

public interface Downloader extends Runnable {
    int RUN_OK = 0;
    int RUN_INCOMPLETE = 1;
    int RUN_UNEXPECTED_ERROR = 2;

    @Override
    void run();

    default Double getMBPerSec() {
        OptionalDouble average = getSpeedStat().values().stream().mapToLong(Units::bytesToMB).average();
        return average.orElse(0);
    }

    int getDownloadStatus();

    void setHref(Collection<String> href) throws MalformedURLException;

    Map<Long, Long> getSpeedStat();
}
