package ipnossoft.rma.upgrade;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.media.browse.MediaBrowser;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.upgrade.Subscription.SubscriptionCallback;
import org.onepf.oms.appstore.googleUtils.SkuDetails;

class SubscriptionUniversalBuilder extends Subscription {
  private TextView bigButtonLine1;
  private TextView bigButtonLine2;
  private TextView bigButtonLine3;
  private TextView bigButtonLine4;
  private ImageButton exitButton;
  private View featuresContainerFree;
  private View featuresContainerPremium;
  private TextView meditationFeature;
  private TextView meditationFeaturePremium;
  private TextView secondButtonDetails;
  private TextView secondButtonDuration;
  private TextView secondButtonPrice;
  private TextView soundsFeature;
  private TextView thirdButtonDetails;
  private TextView thirdButtonDuration;
  private TextView thirdButtonPrice;
  private View tier1Button;
  private View tier2Button;
  private View tier3Button;
  private final boolean useBlueAndYellowButtons;

  SubscriptionUniversalBuilder(View var1, SubscriptionCallback var2, Context var3, boolean var4) {
    super(var1, (MediaBrowser.SubscriptionCallback) var2, var3);
    this.useBlueAndYellowButtons = var4;
  }

  private String getPercentageSaved(SkuDetails var1, SkuDetails var2) {
    return (int)Math.round((1.0D - SubscriptionBuilderUtils.getPriceValue(var1) / SubscriptionBuilderUtils.getPriceValue(var2)) * 100.0D) + "%";
  }

  private boolean moreThanOneMonthSubscription(InAppPurchase var1) {
    return this.getNumberOfMonth(var1) > 1.0D;
  }

  private void setupFirstButton() {
    InAppPurchase var8 = this.getTier1Subscription();
    boolean var1;
    if(this.replacedSkuDetails != null) {
      var1 = true;
    } else {
      var1 = false;
    }

    String var4;
    if(var8.getSubscriptionDuration().intValue() == -1) {
      this.bigButtonLine1.setText(this.context.getString(R.string.subscription_no_12_month_access));
      this.bigButtonLine1.setPaintFlags(this.bigButtonLine1.getPaintFlags() | 16);
      this.bigButtonLine2.setText(this.context.getString(R.string.subscription_lifetime_access));
      if(var1) {
        var4 = this.replacedPrice + " " + this.tier1Price;
      } else {
        var4 = this.tier1Price;
      }

      var4 = this.context.getString(R.string.subscription_for, new Object[]{var4});
      this.bigButtonLine3.setText(var4);
      if(var1) {
        SubscriptionBuilderUtils.applyPriceTextWithStrikeThrough(this.bigButtonLine3, var4, this.replacedPrice);
      }

      this.bigButtonLine4.setVisibility(View.GONE);
    } else {
      String var9 = "";
      String var11 = "";
      String var5 = null;
      String var6 = null;
      boolean var2;
      if(var8.getSubscriptionTrialDuration() > 0) {
        var2 = true;
      } else {
        var2 = false;
      }

      String var7;
      String var10;
      if(this.moreThanOneMonthSubscription(var8)) {
        var7 = SubscriptionBuilderUtils.getDurationTimelyText(this.context, var8);
        SkuDetails var13;
        if(var1) {
          var13 = this.replacedSkuDetails;
        } else {
          var13 = this.tier1SkuDetails;
        }

        var10 = SubscriptionBuilderUtils.getFormattedPricePerMonth(var13, var8);
        var9 = this.context.getString(R.string.subscribe_duration_unit_1_month);
        if(var2) {
          var4 = this.context.getString(R.string.subscription_then_per_duration, new Object[]{var10, var9});
        } else {
          var4 = this.context.getString(R.string.subscription_for, new Object[]{var10 + "/" + var9});
        }

        var5 = var4;
        if(var1) {
          var6 = var10 + "/" + var9;
          var5 = String.format("%s/%s", new Object[]{SubscriptionBuilderUtils.getFormattedPricePerMonth(this.tier1SkuDetails, var8), var9});
          var5 = var4 + " " + var5;
        }

        var4 = String.format(this.context.getString(R.string.subscribe_total_payment), new Object[]{this.tier1Price, SubscriptionBuilderUtils.getDurationUnitText(this.context, this.getTier1Subscription())});
        var10 = var6;
        var9 = var5;
        var6 = var7;
      } else {
        String var12 = SubscriptionBuilderUtils.getDurationUnitText(this.context, var8);
        String var15;
        if(var2) {
          if(var1) {
            var4 = this.replacedPrice;
          } else {
            var4 = this.tier1Price;
          }

          var6 = this.context.getString(R.string.subscription_then_per_duration, new Object[]{var4, var12});
          var7 = var9;
          var15 = var6;
          if(var1) {
            var5 = var4 + "/" + var12;
            var15 = var6;
            var7 = var9;
          }
        } else {
          var6 = SubscriptionBuilderUtils.getDurationTimelyText(this.context, var8);
          Context var14 = this.context;
          int var3 = R.string.subscription_for;
          if(var1) {
            var4 = this.replacedPrice;
          } else {
            var4 = this.tier1Price + "/" + var12;
          }

          var4 = var14.getString(var3, new Object[]{var4});
          var7 = var6;
          var15 = var4;
          if(var1) {
            var5 = this.replacedPrice + "/" + var12;
            var7 = var6;
            var15 = var4;
          }
        }

        var6 = var7;
        var9 = var15;
        var4 = var11;
        var10 = var5;
        if(var1) {
          var4 = String.format("%s/%s", new Object[]{this.tier1Price, var12});
          var9 = var15 + " " + var4;
          var6 = var7;
          var4 = var11;
          var10 = var5;
        }
      }

      this.bigButtonLine3.setText(var9);
      if(var10 != null) {
        SubscriptionBuilderUtils.applyPriceTextWithStrikeThrough(this.bigButtonLine3, var9, var10);
      }

      var7 = var6;
      var5 = var4;
      if(var2) {
        var5 = var6;
        if(var6.length() > 0) {
          var5 = var6 + " - ";
        }

        var7 = var5 + this.context.getString(R.string.subscription_7_days_free_trial_button);
        var5 = var4;
        if(var4.length() > 0) {
          var5 = var4 + ". ";
        }

        var5 = var5 + this.context.getString(R.string.no_risk_cancel_anytime);
      }

      this.bigButtonLine2.setText(var7);
      if(var5.length() > 0) {
        this.bigButtonLine4.setText(var5);
      } else {
        this.bigButtonLine4.setVisibility(View.GONE);
      }
    }

    if(var1) {
      TextView var16 = (TextView)this.subscriptionView.findViewById(R.id.subscribe_save_badge_bottom_textview);
      var16.setText(this.getPercentageSaved(this.tier1SkuDetails, this.replacedSkuDetails));
      if(!SubscriptionBuilderUtils.doesCurrentLanguageFitsSaveBadge()) {
        this.subscriptionView.findViewById(R.id.subscribe_save_badge_top_textview).setVisibility(View.GONE);
        var16.setText(String.format("-%s", new Object[]{var16.getText().toString()}));
      }

    } else {
      this.subscriptionView.findViewById(R.id.subscribe_save_badge).setVisibility(View.GONE);
    }
  }

  private void setupSecondButton() {
    this.setupSecondaryButton(this.getTier2Subscription(), this.tier2Price, this.secondButtonPrice, this.secondButtonDuration, this.secondButtonDetails, this.tier2SkuDetails, this.tier2Button);
  }

  private void setupSecondaryButton(InAppPurchase var1, String var2, TextView var3, TextView var4, TextView var5, SkuDetails var6, View var7) {
    if(var4 != null) {
      var4.setText(SubscriptionBuilderUtils.getDurationTimelyText(this.context, var1));
      if(var1.getSubscriptionDuration().intValue() != -1) {
        String var8 = SubscriptionBuilderUtils.getDurationUnitText(this.context, var1);
        if(this.moreThanOneMonthSubscription(var1)) {
          var3.setText(SubscriptionBuilderUtils.getFormattedPricePerMonth(var6, var1) + "/" + this.context.getString(R.string.subscribe_duration_unit_1_month));
          var5.setText(String.format(this.context.getString(R.string.subscribe_total_payment), new Object[]{var2, var8}));
          return;
        }

        var3.setText(var2 + "/" + var8);
        var5.setVisibility(View.GONE);
        return;
      }

      var3.setText(var2);
      var5.setText(this.context.getString(R.string.one_payment_only));
    } else if(var7 instanceof TextView) {
      ((TextView)var7).setText(this.context.getString(R.string.subscribe_price_for_duration, new Object[]{SubscriptionBuilderUtils.getDurationText(this.context, var1), var2}));
      return;
    }

  }

  private void setupThirdButton() {
    this.setupSecondaryButton(this.getTier3Subscription(), this.tier3Price, this.thirdButtonPrice, this.thirdButtonDuration, this.thirdButtonDetails, this.tier3SkuDetails, this.tier3Button);
  }

  protected void loadViewsFromSubscriptionView() {
    this.tier1Button = this.subscriptionView.findViewById(R.id.subscribe_button_tier1);
    this.tier2Button = this.subscriptionView.findViewById(R.id.subscribe_button_tier2);
    this.tier3Button = this.subscriptionView.findViewById(R.id.subscribe_button_tier3);
    this.meditationFeature = (TextView)this.subscriptionView.findViewById(R.id.more_meditations_feature_label);
    this.meditationFeaturePremium = (TextView)this.subscriptionView.findViewById(R.id.more_meditations_feature_label_premium);
    this.soundsFeature = (TextView)this.subscriptionView.findViewById(R.id.more_sounds_feature_label);
    this.bigButtonLine1 = (TextView)this.subscriptionView.findViewById(R.id.subscribe_standard_big_button_line_1);
    this.bigButtonLine2 = (TextView)this.subscriptionView.findViewById(R.id.subscribe_standard_big_button_line_2);
    this.bigButtonLine3 = (TextView)this.subscriptionView.findViewById(R.id.subscribe_standard_big_button_line_3);
    this.bigButtonLine4 = (TextView)this.subscriptionView.findViewById(R.id.subscribe_standard_big_button_line_4);
    this.secondButtonDuration = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier2_duration);
    this.secondButtonPrice = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier2_price);
    this.secondButtonDetails = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier2_details);
    this.thirdButtonDuration = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier3_duration);
    this.thirdButtonPrice = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier3_price);
    this.thirdButtonDetails = (TextView)this.subscriptionView.findViewById(R.id.subscribe_tier3_details);
    this.featuresContainerFree = this.subscriptionView.findViewById(R.id.subscribe_feature_list_free);
    this.featuresContainerPremium = this.subscriptionView.findViewById(R.id.subscribe_feature_list_premium);
    this.exitButton = (ImageButton)this.subscriptionView.findViewById(R.id.subscribe_exit_button);
    this.exitButton.setColorFilter(-1, Mode.SRC_ATOP);
  }

  protected void setupSubscriptionView() {
    if(!this.getPrices()) {
      this.setErrorMessage(this.context.getString(R.string.error_dialog_message_store_unavailable));
    } else {
      if(this.useBlueAndYellowButtons) {
        this.tier2Button.setBackgroundResource(R.drawable.subscribe_yellow_button);
        this.secondButtonDetails.setTextColor(Color.parseColor("#ffee58"));
        this.tier3Button.setBackgroundResource(R.drawable.subscribe_blue_button);
        this.thirdButtonDetails.setTextColor(Color.parseColor("#40c4ff"));
      }

      this.setupFirstButton();
      this.setupSecondButton();
      this.setupThirdButton();
    }

    this.meditationFeature.setText(this.subscriptionView.getResources().getString(R.string.subscription_view_pager_meditation_text1, new Object[]{Integer.valueOf(SoundLibrary.getInstance().getGuidedMeditationSounds().size())}));
    this.meditationFeaturePremium.setText(this.subscriptionView.getResources().getString(R.string.subscription_view_pager_meditation_text1, new Object[]{Integer.valueOf(SoundLibrary.getInstance().getGuidedMeditationSounds().size())}));
    this.soundsFeature.setText(this.subscriptionView.getResources().getString(R.string.subscription_view_pager_sounds_text1, new Object[]{Integer.valueOf(SoundLibrary.getInstance().getBinauralSounds().size() + SoundLibrary.getInstance().getIsochronicSounds().size() + SoundLibrary.getInstance().getAmbientSound().size())}));
    if(RelaxMelodiesApp.isPremium().booleanValue()) {
      this.featuresContainerFree.setVisibility(View.GONE);
      this.featuresContainerPremium.setVisibility(View.VISIBLE);
    } else {
      this.featuresContainerFree.setVisibility(View.VISIBLE);
      this.featuresContainerPremium.setVisibility(View.GONE);
    }

    this.setupClickListeners(this.tier1Button, this.tier2Button, this.tier3Button, this.exitButton);
  }
}
