package ipnossoft.rma.upgrade;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.util.Utils;
import java.text.DecimalFormat;
import java.util.Locale;
import org.onepf.oms.appstore.googleUtils.SkuDetails;

public class SubscriptionBuilderUtils {
  public SubscriptionBuilderUtils() {
  }

  public static void applyPriceTextWithStrikeThrough(TextView var0, String var1, String var2) {
    SpannableString var3 = new SpannableString(var1);
    int var4 = var1.indexOf(var2);
    int var5 = var4 + var2.length();
    var3.setSpan(new StrikethroughSpan(), var4, var5, 0);
    var3.setSpan(new ForegroundColorSpan(Color.argb(204, 255, 255, 255)), var4, var5, 0);
    var0.setText(var3, BufferType.SPANNABLE);
  }

  public static String cleansePrice(String var0) {
    var0 = var0.replaceAll("[^\\d,.]", "").replaceAll(",", ".");
    String var2 = var0;
    if(var0.startsWith(".")) {
      var2 = var0.substring(1);
    }

    var0 = var2;
    if(var2.endsWith(".")) {
      var0 = var2.substring(0, var2.length() - 1);
    }

    int var1 = var0.lastIndexOf(".");
    var2 = var0;
    if(var1 > -1) {
      var2 = "00";
      String var3 = var0;
      if(var1 == var0.length() - 3) {
        var2 = var0.substring(var1 + 1);
        var3 = var0.substring(0, var1);
      }

      var0 = var3.replaceAll("\\.", "");
      var2 = var0 + "." + var2;
    }

    return var2;
  }

  public static boolean doesCurrentLanguageFitsSaveBadge() {
    String var0 = Utils.getCurrentLanguageLocale(RelaxMelodiesApp.getInstance().getApplicationContext());
    return var0.equals(Locale.ENGLISH.getLanguage()) || var0.equals(Locale.GERMAN.getLanguage()) || var0.equals((new Locale("es", "ES")).getLanguage()) || var0.equals(Locale.JAPANESE.getLanguage()) || var0.equals(Locale.KOREAN.getLanguage()) || var0.equals(Locale.SIMPLIFIED_CHINESE.getLanguage()) || var0.equals(Locale.TRADITIONAL_CHINESE.getLanguage());
  }

  public static String formatPriceWithCurrency(SkuDetails var0, double var1) {
    String var3 = "$0.00";
    if(var0 != null) {
      var3 = var0.getPrice();
    }

    String var6 = var3.replaceAll("[\\d,.]", "");
    DecimalFormat var4 = new DecimalFormat("#.00");
    boolean var5;
    if(var3.indexOf(var6) == 0) {
      var5 = true;
    } else {
      var5 = false;
    }

    var3 = var4.format(var1);
    return var5?var6 + var3:var3 + var6;
  }

  public static String getDurationText(Context var0, InAppPurchase var1) {
    double var2 = getNumberOfMonth(var1);
    return var2 == 3.0D?var0.getString(R.string.subscribe_duration_3_months):(var2 == 12.0D?var0.getString(R.string.subscribe_duration_1_year):(var2 == 1.0D?var0.getString(R.string.subscribe_duration_1_month):(var2 < 1.0D && var2 > 0.0D?var0.getString(R.string.subscribe_duration_1_week):var0.getString(R.string.subscribe_duration_lifetime_access))));
  }

  public static String getDurationTimelyText(Context var0, InAppPurchase var1) {
    double var2 = getNumberOfMonth(var1);
    return var2 == 3.0D?var0.getString(R.string.subscribe_duration_3_months):(var2 == 12.0D?var0.getString(R.string.subscribe_duration_yearly):(var2 == 1.0D?var0.getString(R.string.subscribe_duration_monthly):(var2 < 1.0D && var2 > 0.0D?var0.getString(R.string.subscribe_duration_weekly):var0.getString(R.string.subscribe_duration_forever))));
  }

  public static String getDurationUnitText(Context var0, InAppPurchase var1) {
    double var2 = getNumberOfMonth(var1);
    return var2 == 3.0D?var0.getString(R.string.subscribe_duration_3_months).toLowerCase():(var2 == 12.0D?var0.getString(R.string.subscribe_duration_unit_1_year).toLowerCase():(var2 == 1.0D?var0.getString(R.string.subscribe_duration_unit_1_month).toLowerCase():(var2 < 1.0D && var2 > 0.0D?var0.getString(R.string.subscribe_duration_unit_1_week).toLowerCase():var0.getString(R.string.subscribe_duration_lifetime_access))));
  }

  public static String getFormattedPricePerMonth(SkuDetails var0, InAppPurchase var1) {
    return formatPriceWithCurrency(var0, getPriceValuePerMonth(var0, var1));
  }

  public static double getNumberOfMonth(InAppPurchase var0) {
    int var1 = var0.getSubscriptionDuration().intValue();
    int var2 = var0.getSubscriptionDurationUnit();
    return var2 == 2?(double)var1:(var2 == 1?(double)var1 * 12.0D:(var2 == 3?(double)var1 * 12.0D / 52.0D:-1.0D));
  }

  public static double getPriceValue(SkuDetails var0) {
    return var0 == null?0.0D:Double.parseDouble(cleansePrice(var0.getPrice()));
  }

  public static double getPriceValuePerMonth(SkuDetails var0, InAppPurchase var1) {
    return getPriceValue(var0) / getNumberOfMonth(var1);
  }
}
