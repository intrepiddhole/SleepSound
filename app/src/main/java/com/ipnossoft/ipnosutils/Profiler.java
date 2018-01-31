package com.ipnossoft.ipnosutils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Profiler {
  static String TAG = "Profiler";
  private static Profiler instance;
  private Map<String, Integer> methodNames = new HashMap();
  private Date startDate = new Date(System.currentTimeMillis());

  private Profiler() {
  }

  private long differenceBetweenStartDateAndNow() {
    return (new Date(System.currentTimeMillis())).getTime() - this.startDate.getTime();
  }

  public static Profiler getInstance() {
    if(instance == null) {
      instance = new Profiler();
    }

    return instance;
  }

  public void logTimeDifference(String var1) {
    Integer var2 = Integer.valueOf(1);
    if(!this.methodNames.containsKey(var1)) {
      this.methodNames.put(var1, var2);
    } else {
      var2 = Integer.valueOf(((Integer)this.methodNames.get(var1)).intValue() + 1);
    }

    this.methodNames.put(var1, var2);
  }
}
