package com.mooo.nicolak.downloaders;

import java.net.MalformedURLException;
import java.util.Collection;

public interface Downloader extends Runnable {
    int RUN_OK = 0;
    int RUN_INCOMPLETE = 1;
    int RUN_UNEXPECTED_ERROR = 2;

    @Override
    void run();

    Double getMBPerSec();

    int getDownloadStatus();

    void setHref(Collection<String> href) throws MalformedURLException;
}
