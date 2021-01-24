package com.mooo.nicolak.downloaders;

public abstract class Units {
    public static final int KB_IN_BYTES = 1024;
    public static final int MB_IN_BYTES = 1024* KB_IN_BYTES;

    public static long bytesToMB(long bytes) {
        return (long) ((float)bytes / (float) Units.MB_IN_BYTES);
    }
}
