package ipnossoft.rma.upgrade;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.media.browse.MediaBrowser;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import ipnossoft.rma.free.R;
import ipnossoft.rma.upgrade.Subscription.SubscriptionCallback;

public class SubscriptionTrialBuilder extends Subscription {
  private TextView price;
  private TextView subTitle;

  SubscriptionTrialBuilder(View var1, SubscriptionCallback var2, Context var3) {
    //super(var1, var2, var3);
    super(var1, (MediaBrowser.SubscriptionCallback) var2, var3);
  }

  protected void loadViewsFromSubscriptionView() {
    this.subTitle = (TextView)this.subscriptionView.findViewById(R.id.subscribe_trial_sub_title);
    this.price = (TextView)this.subscriptionView.findViewById(R.id.subscribe_trial_price);
    ((ImageButton)this.subscriptionView.findViewById(R.id.subscribe_exit_button)).setColorFilter(-1, Mode.SRC_ATOP);
  }

  protected void setupSubscriptionView() {
    if(!this.getPrices()) {
      this.setErrorMessage(this.context.getString(R.string.error_dialog_message_store_unavailable));
    }

    if(this.getTier1Subscription().getSubscriptionTrialDuration() > 0) {
      if(this.subTitle != null) {
        this.subTitle.setText(String.format(this.context.getString(R.string.subscribe_trial_sub_title), new Object[]{this.tier1Price, SubscriptionBuilderUtils.getDurationUnitText(this.context, this.getTier1Subscription())}));
      }

      if(this.price != null) {
        this.price.setText(String.format(this.context.getString(R.string.subscribe_trial_price), new Object[]{this.tier1Price, SubscriptionBuilderUtils.getDurationUnitText(this.context, this.getTier1Subscription())}));
      }
    }

  }
}
