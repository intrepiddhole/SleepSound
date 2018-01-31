package ipnossoft.rma.upgrade;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.media.browse.MediaBrowser;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureManager;

import ipnossoft.rma.free.R;
import ipnossoft.rma.upgrade.Subscription.SubscriptionCallback;
import java.text.DecimalFormat;
import org.onepf.oms.appstore.googleUtils.SkuDetails;

public class SubscriptionBuilder extends Subscription {
  private RelativeLayout exitButtonLayout;
  private TextView promoTier3TitleTextView;
  private RelativeLayout tier1Button;
  private TextView tier1PriceForDuration;
  private TextView tier1ThenPerDuration;
  private RelativeLayout tier2Button;
  private TextView tier2DurationTextView;
  private TextView tier2PercentageSaved;
  private TextView tier2PriceForDuration;
  private TextView tier2PricePerMonth;
  private TextView tier2SaveMoneyDurationPrice;
  private TextView tier2TotalPayment;
  private RelativeLayout tier3Button;
  private TextView tier3DurationTextView;
  private TextView tier3PercentageSaved;
  private TextView tier3PricePerMonth;
  private TextView tier3PriceSlashedTextView;
  private TextView tier3RelaxForLifePrice;
  private TextView tier3SlashedDuration;
  private TextView tier3TotalPayment;

  public SubscriptionBuilder(View var1, SubscriptionCallback var2, Context var3) {
    super(var1, (MediaBrowser.SubscriptionCallback) var2, var3);
  }

  private String generateStrokePrice(String var1) {
    try {
      var1 = (new StrokePriceGenerator(var1)).generate();
      return var1;
    } catch (Exception var2) {
      return "";
    }
  }

  private String getEarningValue(SkuDetails var1, InAppPurchase var2) {
    if(var1 == null) {
      return "0$";
    } else {
      int var4 = (int)Math.round(SubscriptionBuilderUtils.getPriceValuePerMonth(this.tier1SkuDetails, this.getTier1Subscription()) * this.getNumberOfMonth(var2) - SubscriptionBuilderUtils.getPriceValue(var1));
      String var5 = var1.getPrice().replaceAll("[\\d,.]", "");
      boolean var3;
      if(var1.getPrice().indexOf(var5) == 0) {
        var3 = true;
      } else {
        var3 = false;
      }

      return var3?var5 + var4:var4 + var5;
    }
  }

  private String getGoIntervalString(InAppPurchase var1) {
    int var2 = (int)this.getNumberOfMonth(var1);
    return var2 == 3?this.context.getString(R.string.subscribe_go_3_months):(var2 == 12?this.context.getString(R.string.subscribe_go_yearly):(var2 == 1?this.context.getString(R.string.subscribe_go_monthly):(var2 < 1?this.context.getString(R.string.subscribe_go_weekly):this.context.getString(R.string.subscribe_go_lifetime))));
  }

  private String getPercentageSaved(SkuDetails var1, InAppPurchase var2) {
    double var3 = SubscriptionBuilderUtils.getPriceValuePerMonth(this.tier1SkuDetails, this.getTier1Subscription());
    double var5 = SubscriptionBuilderUtils.getPriceValuePerMonth(var1, var2);
    return (int)Math.round((1.0D - var5 / var3) * 100.0D) + "%";
  }

  @Nullable
  private String getStrokedPrice() {
    String var1 = "";
    String var2 = SubscriptionOfferResolver.getReplacedSubscriptionIdentifier();
    if(var2 != null) {
      SkuDetails var3 = FeatureManager.getInstance().getPurchaseDetails(var2);
      if(var3 != null) {
        var1 = var3.getPrice();
      }

      return var1;
    } else {
      return this.generateStrokePrice(this.tier3Price);
    }
  }

  private boolean isLifetimeOffer() {
    return this.getTier3Subscription() != null && this.getTier3Subscription().getSubscriptionDuration().intValue() == -1;
  }

  private void setupTier3PromoButton() {
    String var6;
    if(this.isLifetimeOffer()) {
      this.tier3SlashedDuration.setText(this.context.getString(R.string.subscription_no_12_month_access));
      this.promoTier3TitleTextView.setText(this.context.getString(R.string.subscription_lifetime_access));
      this.tier3SlashedDuration.setPaintFlags(this.tier3SlashedDuration.getPaintFlags() | 16);
      this.tier3SlashedDuration.setTextSize(2, 12.0F);
      this.promoTier3TitleTextView.setVisibility(View.VISIBLE);
      SkuDetails var2 = null;
      String var3 = SubscriptionOfferResolver.getReplacedSubscriptionIdentifier();
      if(var3 != null) {
        var2 = FeatureManager.getInstance().getPurchaseDetails(var3);
      }

      TextView var7 = (TextView)this.subscriptionView.findViewById(R.id.subscribe_save_badge_bottom_textview);
      var7.setText(this.getSavedPercentageFromSlashedPrice(this.tier3SkuDetails, var2) + "%");
      if(!SubscriptionBuilderUtils.doesCurrentLanguageFitsSaveBadge()) {
        this.subscriptionView.findViewById(R.id.subscribe_save_badge_top_textview).setVisibility(View.GONE);
        var7.setText("-" + var7.getText().toString());
      }

      var3 = this.getStrokedPrice();
      if(var3.isEmpty()) {
        var6 = this.tier3Price;
      } else {
        var6 = var3 + " " + this.tier3Price;
      }

      var6 = String.format(this.context.getString(R.string.subscription_for), new Object[]{var6});
      if(var3.isEmpty()) {
        this.tier3PriceSlashedTextView.setText(var6);
        return;
      }

      SubscriptionBuilderUtils.applyPriceTextWithStrikeThrough(this.tier3PriceSlashedTextView, var6, var3);
    } else if(this.getTier3Subscription() != null) {
      this.subscriptionView.findViewById(R.id.subscribe_save_badge).setVisibility(View.GONE);
      var6 = this.tier3Price.replaceAll("[^\\d,.]", "");
      var6 = this.tier3Price.replaceAll(var6, "");
      double var4 = Double.parseDouble(SubscriptionBuilderUtils.cleansePrice(this.tier3Price)) / this.getNumberOfMonth(this.getTier3Subscription());
      boolean var1;
      if(this.tier3Price.indexOf(var6) == 0) {
        var1 = true;
      } else {
        var1 = false;
      }

      DecimalFormat var8 = new DecimalFormat("#.00");
      if(var1) {
        this.tier3MonthlyPrice = var6 + var8.format(var4);
      } else {
        this.tier3MonthlyPrice = var8.format(var4) + var6;
      }

      this.promoTier3TitleTextView.setText(String.format(this.promoTier3TitleTextView.getText().toString(), new Object[]{this.tier3MonthlyPrice}));
      this.tier3SlashedDuration.setText(this.context.getText(R.string.subscription_limited_time_offer));
      this.tier3PriceSlashedTextView.setText(String.format(this.tier3PriceSlashedTextView.getText().toString(), new Object[]{this.tier3Price}));
      return;
    }

  }

  int getSavedPercentageFromSlashedPrice(SkuDetails var1, SkuDetails var2) {
    if(var1 != null) {
      double var3 = SubscriptionBuilderUtils.getPriceValue(var1);
      double var5 = SubscriptionBuilderUtils.getPriceValue(var2);
      if(var3 != 0.0D && var5 != 0.0D && var3 < var5) {
        return (int)Math.round((1.0D - var3 / var5) * 100.0D);
      }
    }

    return 66;
  }

  protected void loadViewsFromSubscriptionView() {
    this.tier1Button = (RelativeLayout)this.subscriptionView.findViewById(R.id.subscribe_button_tier1);
    this.tier2Button = (RelativeLayout)this.subscriptionView.findViewById(R.id.subscribe_button_tier2);
    this.tier3Button = (RelativeLayout)this.subscriptionView.findViewById(R.id.subscribe_button_tier3);
    this.tier3SlashedDuration = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier3_duration_slashed);
    this.tier2TotalPayment = (TextView)this.subscriptionView.findViewById(R.id.subscribe_total_payment_tier2);
    this.tier3TotalPayment = (TextView)this.subscriptionView.findViewById(R.id.subscribe_total_payment_tier3);
    this.promoTier3TitleTextView = (TextView)this.subscriptionView.findViewById(R.id.subscribe_promo_title);
    this.tier3PriceSlashedTextView = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier3_price_slashed);
    this.tier2PricePerMonth = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier2_price_per_month);
    this.tier3PricePerMonth = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier3_price_per_month);
    this.tier2SaveMoneyDurationPrice = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier2_save_money_duration_price);
    this.tier3RelaxForLifePrice = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier3_textual_price);
    this.tier2DurationTextView = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier2_duration);
    this.tier3DurationTextView = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier3_duration);
    this.tier2PercentageSaved = (TextView)this.subscriptionView.findViewById(R.id.subscribe_save_percentage_tier2);
    this.tier3PercentageSaved = (TextView)this.subscriptionView.findViewById(R.id.subscribe_save_percentage_tier3);
    this.tier1ThenPerDuration = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier1_then_per_duration);
    this.tier2PriceForDuration = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier2_price_duration);
    this.tier1PriceForDuration = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier1_price_duration);
    ((ImageButton)this.subscriptionView.findViewById(R.id.subscribe_exit_button)).setColorFilter(-1, Mode.SRC_ATOP);
    this.exitButtonLayout = (RelativeLayout)this.subscriptionView.findViewById(R.id.subscribe_exit_button_layout);
  }

  protected void setupSubscriptionView() {
    if(!this.getPrices()) {
      this.setErrorMessage(this.context.getString(R.string.error_dialog_message_store_unavailable));
    }

    if(this.promoTier3TitleTextView != null) {
      this.setupTier3PromoButton();
    }

    if(this.tier2TotalPayment != null) {
      if(this.getNumberOfMonth(this.getTier2Subscription()) > 1.0D) {
        this.tier2TotalPayment.setText(String.format(this.context.getString(R.string.subscribe_total_payment), new Object[]{this.tier2Price, SubscriptionBuilderUtils.getDurationUnitText(this.context, this.getTier2Subscription())}));
      } else {
        this.tier2TotalPayment.setVisibility(View.GONE);
      }
    }

    if(this.tier3TotalPayment != null) {
      if(this.getNumberOfMonth(this.getTier3Subscription()) > 1.0D) {
        this.tier3TotalPayment.setText(String.format(this.context.getString(R.string.subscribe_total_payment), new Object[]{this.tier3Price, SubscriptionBuilderUtils.getDurationUnitText(this.context, this.getTier3Subscription())}));
      } else {
        this.tier3TotalPayment.setVisibility(View.GONE);
      }
    }

    if(this.tier2PricePerMonth != null) {
      this.tier2PricePerMonth.setText(SubscriptionBuilderUtils.getFormattedPricePerMonth(this.tier2SkuDetails, this.getTier2Subscription()));
    }

    if(this.tier3PricePerMonth != null) {
      this.tier3PricePerMonth.setText(SubscriptionBuilderUtils.getFormattedPricePerMonth(this.tier3SkuDetails, this.getTier3Subscription()));
    }

    if(this.tier2SaveMoneyDurationPrice != null) {
      String var1 = this.context.getString(R.string.subscribe_save_value, new Object[]{this.getEarningValue(this.tier2SkuDetails, this.getTier2Subscription())});
      this.tier2SaveMoneyDurationPrice.setText(String.format("%s, %s %s", new Object[]{var1, this.getGoIntervalString(this.getTier2Subscription()), this.tier2Price}));
    }

    if(this.tier3RelaxForLifePrice != null) {
      this.tier3RelaxForLifePrice.setText(String.format("%s %s", new Object[]{this.context.getString(R.string.subscribe_relax_for_life), this.tier3Price}));
    }

    if(this.tier2DurationTextView != null) {
      this.tier2DurationTextView.setText(SubscriptionBuilderUtils.getDurationText(this.context, this.getTier2Subscription()));
    }

    if(this.tier3DurationTextView != null) {
      this.tier3DurationTextView.setText(SubscriptionBuilderUtils.getDurationText(this.context, this.getTier3Subscription()));
    }

    if(this.tier2PercentageSaved != null) {
      this.tier2PercentageSaved.setText(String.format(this.context.getString(R.string.subscribe_save_value), new Object[]{this.getPercentageSaved(this.tier2SkuDetails, this.getTier2Subscription())}));
    }

    if(this.tier3PercentageSaved != null) {
      this.tier3PercentageSaved.setText(String.format(this.context.getString(R.string.subscribe_save_value), new Object[]{this.getPercentageSaved(this.tier3SkuDetails, this.getTier3Subscription())}));
    }

    if(this.tier1ThenPerDuration != null) {
      this.tier1ThenPerDuration.setText(String.format(this.context.getString(R.string.subscription_then_per_duration), new Object[]{this.tier1Price, SubscriptionBuilderUtils.getDurationUnitText(this.context, this.getTier1Subscription())}));
    }

    if(this.tier1PriceForDuration != null) {
      this.tier1PriceForDuration.setText(String.format(this.context.getString(R.string.subscribe_price_for_duration), new Object[]{SubscriptionBuilderUtils.getDurationText(this.context, this.getTier1Subscription()), this.tier1Price}));
    }

    if(this.tier2PriceForDuration != null) {
      this.tier2PriceForDuration.setText(String.format(this.context.getString(R.string.subscribe_price_for_duration), new Object[]{SubscriptionBuilderUtils.getDurationText(this.context, this.getTier2Subscription()), this.tier2Price}));
    }

    this.setupClickListeners(this.tier1Button, this.tier2Button, this.tier3Button, this.exitButtonLayout);
  }
}
