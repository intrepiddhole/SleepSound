package com.nativemediaplayer;

class BufferedAudioInfo {
  private float dataLeftVolume = 1.0F;
  private float dataRightVolume = 1.0F;
  private int dataSeekTo = 0;
  private long duration = 0L;
  private boolean flagRelease = false;
  private boolean flagSeek = false;
  private boolean flagStop = false;
  private boolean flagUpdateVolume = false;
  private boolean looping = false;
  private boolean prepared = false;
  private boolean released = false;
  private int streamType = 3;

  BufferedAudioInfo() {
  }

  public float dataLeftVolume() {
    synchronized(this){}

    float var2;
    try {
      var2 = this.dataLeftVolume;
    } finally {
      ;
    }

    return var2;
  }

  public float dataLeftVolume(float var1) {
    synchronized(this){}

    try {
      this.dataLeftVolume = var1;
    } finally {
      ;
    }

    return var1;
  }

  public float dataRightVolume() {
    synchronized(this){}

    float var2;
    try {
      var2 = this.dataRightVolume;
    } finally {
      ;
    }

    return var2;
  }

  public float dataRightVolume(float var1) {
    synchronized(this){}

    try {
      this.dataRightVolume = var1;
    } finally {
      ;
    }

    return var1;
  }

  public int dataSeekTo() {
    synchronized(this){}

    int var1;
    try {
      var1 = this.dataSeekTo;
    } finally {
      ;
    }

    return var1;
  }

  public int dataSeekTo(int var1) {
    synchronized(this){}

    try {
      this.dataSeekTo = var1;
    } finally {
      ;
    }

    return var1;
  }

  public long duration() {
    synchronized(this){}

    long var1;
    try {
      var1 = this.duration;
    } finally {
      ;
    }

    return var1;
  }

  public long duration(long var1) {
    synchronized(this){}

    try {
      this.duration = var1;
    } finally {
      ;
    }

    return var1;
  }

  public boolean flagRelease() {
    synchronized(this){}

    boolean var1;
    try {
      var1 = this.flagRelease;
    } finally {
      ;
    }

    return var1;
  }

  public boolean flagRelease(boolean var1) {
    synchronized(this){}

    try {
      this.flagRelease = var1;
    } finally {
      ;
    }

    return var1;
  }

  public boolean flagSeek() {
    synchronized(this){}

    boolean var2;
    try {
      var2 = this.flagSeek;
    } finally {
      ;
    }

    return var2;
  }

  public boolean flagSeek(boolean var1) {
    synchronized(this){}

    try {
      this.flagSeek = var1;
    } finally {
      ;
    }

    return var1;
  }

  public boolean flagStop() {
    synchronized(this){}

    boolean var2;
    try {
      var2 = this.flagStop;
    } finally {
      ;
    }

    return var2;
  }

  public boolean flagStop(boolean var1) {
    synchronized(this){}

    try {
      this.flagStop = var1;
    } finally {
      ;
    }

    return var1;
  }

  public boolean flagUpdateVolume() {
    synchronized(this){}

    boolean var1;
    try {
      var1 = this.flagUpdateVolume;
    } finally {
      ;
    }

    return var1;
  }

  public boolean flagUpdateVolume(boolean var1) {
    synchronized(this){}

    try {
      this.flagUpdateVolume = var1;
    } finally {
      ;
    }

    return var1;
  }

  public boolean looping() {
    synchronized(this){}

    boolean var1;
    try {
      var1 = this.looping;
    } finally {
      ;
    }

    return var1;
  }

  public boolean looping(boolean var1) {
    synchronized(this){}

    try {
      this.looping = var1;
    } finally {
      ;
    }

    return var1;
  }

  public boolean prepared() {
    synchronized(this){}

    boolean var2;
    try {
      var2 = this.prepared;
    } finally {
      ;
    }

    return var2;
  }

  public boolean prepared(boolean var1) {
    synchronized(this){}

    try {
      this.prepared = var1;
    } finally {
      ;
    }

    return var1;
  }

  public boolean released() {
    synchronized(this){}

    boolean var2;
    try {
      var2 = this.released;
    } finally {
      ;
    }

    return var2;
  }

  public boolean released(boolean var1) {
    synchronized(this){}

    try {
      this.released = var1;
    } finally {
      ;
    }

    return var1;
  }

  public int streamType() {
    synchronized(this){}

    int var1;
    try {
      var1 = this.streamType;
    } finally {
      ;
    }

    return var1;
  }

  public int streamType(int var1) {
    synchronized(this){}

    try {
      this.streamType = var1;
    } finally {
      ;
    }

    return var1;
  }
}
