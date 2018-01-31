package com.ipnossoft.api.featuremanager.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtils {
  private static DateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS'+00:00'");

  static {
    utcDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  public DateUtils() {
  }

  public static Date add(Date var0, int var1, Integer var2) {
    Calendar var3 = Calendar.getInstance();
    var3.setTime(var0);
    var3.add(var1, var2.intValue());
    return var3.getTime();
  }

  public static Date addMonths(Date var0, int var1) {
    return add(var0, 2, Integer.valueOf(var1));
  }

  public static Date createDate(int var0, int var1, int var2) {
    Calendar var3 = Calendar.getInstance();
    var3.set(var0, var1, var2);
    return var3.getTime();
  }

  public static String dateToIsoString(Date var0) {
    return var0 == null?null:utcDateFormat.format(var0);
  }

  public static Date parseDateFromIsoString(String var0) throws ParseException {
    return var0 != null && !var0.isEmpty()?utcDateFormat.parse(var0):null;
  }
}
