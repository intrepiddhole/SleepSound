package ipnossoft.rma.util;

public class TimeUtils {
  public TimeUtils() {
  }

  public static String convertMillisecondsToString(long var0) {
    int var3 = (int)(var0 / 1000L);
    int var2 = var3 / 60;
    var3 %= 60;
    return var2 >= 60?String.format("%d:%02d:%02d", new Object[]{Integer.valueOf(var2 / 60), Integer.valueOf(var2 % 60), Integer.valueOf(var3)}):String.format("%d:%02d", new Object[]{Integer.valueOf(var2), Integer.valueOf(var3)});
  }
}
