package com.ipnossoft.api.dynamiccontent;

public enum DownloadStages {
    CONNECTING,
    DOWNLOADING_ZIP,
    EXTRACTING_ZIP,
    NONE;

    private DownloadStages() {
    }
}
