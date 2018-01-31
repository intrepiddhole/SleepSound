package ipnossoft.rma;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import ipnossoft.rma.ui.tutorial.TutorialActivity;
import ipnossoft.rma.upgrade.Subscription;
import ipnossoft.rma.upgrade.SubscriptionActivity;

public class NavigationHelper {
  public NavigationHelper() {
  }

  public static void showSounds(Activity var0) {
    var0.startActivity(new Intent(var0, RelaxMelodiesApp.getInstance().getMainActivityClass()));
    var0.overridePendingTransition(0, 0);
  }

  public static void showSubscription(Activity var0) {
    if(!Subscription.isPurchaseFlowInProgress()) {
      Intent var1 = new Intent(var0, SubscriptionActivity.class);
      var1.setFlags(67108864);
      var0.startActivityForResult(var1, 0);
    }

  }

  public static void showTutorial(Context var0) {
    var0.startActivity(new Intent(var0, TutorialActivity.class));
  }

  public static void showTutorialForced(Context var0) {
    Intent var1 = new Intent(var0, TutorialActivity.class);
    var1.putExtra(TutorialActivity.TUTORIAL_EXTRA_FORCED, true);
    var0.startActivity(var1);
  }
}