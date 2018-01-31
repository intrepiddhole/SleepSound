package ipnossoft.rma.util;

import android.util.Log;

public class Stopwatch {
  private String name;
  private long startTime = 0L;
  private long stopTime = 0L;

  private Stopwatch(String var1) {
    this.restart();
    this.name = var1;
  }

  private long lap() {
    long var1;
    if(this.stopTime == 0L) {
      var1 = System.currentTimeMillis();
    } else {
      var1 = this.stopTime;
    }

    return var1 - this.startTime;
  }

  private void logLap(String var1) {
    Log.e("[" + Thread.currentThread().getName() + " #" + Thread.currentThread().getId() + "] STOPWATCH \"" + this.name + "\"", var1 + " (in " + this.lap() + "ms)");
  }

  private void restart() {
    this.startTime = System.currentTimeMillis();
    this.stopTime = 0L;
  }

  public static Stopwatch start(String var0) {
    return new Stopwatch(var0);
  }

  public void logNewLap(String var1) {
    this.logLap(var1);
    this.restart();
  }

  public void stop() {
    this.stopTime = System.currentTimeMillis();
  }
}
