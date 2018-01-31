package ipnossoft.rma.util;

import com.ipnossoft.api.soundlibrary.Sound;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.media.SoundTrack;
import ipnossoft.rma.util.Analytics.UpgradeReferer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.onepf.oms.appstore.googleUtils.SkuDetails;

public class RelaxAnalytics extends Analytics {
  public static final String PREF_LAST_LOGGED_SUBSCRIPTION = "lastLoggedSubscription";

  public RelaxAnalytics() {
  }

  public static void flagScrolledIntoBinaurals() {
    setUserPropertyOnce("scroll_binaurals", true);
  }

  public static void flagScrolledIntoIsochronics() {
    setUserPropertyOnce("scroll_isochronics", true);
  }

  public static void flagScrolledIntoSounds(int var0) {
    setUserPropertyMax("scroll_sounds_max", var0);
  }

  public static void logActivationCodeDialog() {
    logEvent("settings", "settings_activation_code");
  }

  public static void logActivationCodeResult(String var0, String var1, boolean var2) {
    HashMap var3 = new HashMap();
    if(var1 == null) {
      var1 = "None";
    }

    var3.put("feature_name", var1);
    if(var2) {
      logEvent("settings", "settings_activation_code_succeed", var3, var1, 1);
      addPromoCode(var0);
    } else {
      logEvent("settings", "settings_activation_code_failed", var3, var1, 1);
    }
  }

  public static void logAdClicked() {
    logEvent("default", "ad_click");
  }

  public static void logBinauralInfoShown() {
    logEvent("brainwaves", "show_binaural_dialog");
  }

  public static void logBlogShown() {
    logEvent("more", "navigation_blog");
  }

  public static void logCancelDownload(String var0) {
    logEvent("download", "cancel_feature", "feature_name", var0, var0, 1);
  }

  public static void logClearAllSounds() {
    logEvent("bottom_cloud_menu", "clear_selection");
  }

  public static void logContactUs() {
    logEvent("more", "more_action_contact_us");
  }

  public static void logCreateFavorite(List<SoundTrack> var0) {
    logEventWithSounds("favorites", "create_favorite_result", (Map)null, var0);
    refreshUserDimension();
  }

  public static void logCreateFavoriteDialog() {
    logEvent("favorites", "create_favorite");
  }

  public static void logDeleteFavorite() {
    logEvent("favorites", "delete_favorite");
    refreshUserDimension();
  }

  public static void logDownload(String var0) {
    logEvent("download", "download_feature", "feature_name", var0, var0, 1);
  }

  public static void logFavoritesShown() {
    logEvent("default", "navigation_favorite");
  }

  public static void logFeedbackDialogAnswer(String var0) {
    logEvent("opening_dialog", "feedback_dialog_result", "answer", var0, var0, 1);
  }

  public static void logFeedbackDialogShown() {
    logEvent("opening_dialog", "feedback_dialog");
  }

  public static void logGiftDialogShown() {
    logEvent("opening_dialog", "gifts_dialog");
  }

  public static void logGuidedMeditationCompleted(String var0, int var1) {
    HashMap var2 = new HashMap();
    var2.put("percentage", String.valueOf(var1));
    var2.put("sound_id", var0);
    logEvent("meditations", "meditation_stopped", var2, var0, var1);
  }

  public static void logGuidedMeditationSubVolumeChanged(Sound var0, int var1) {
    HashMap var2 = new HashMap();
    var2.put("sound_id", var0.getId());
    var2.put("sound_volume", String.valueOf(var1));
    var2.put("sound_category", "meditations");
    logEvent("meditations", "sub_volume_changed", var2, var0.getId(), var1);
  }

  public static void logHelpShown() {
    logEvent("more", "navigation_help");
  }

  public static void logIsochronicsInfoShown() {
    logEvent("brainwaves", "show_isochronic_dialog");
  }

  public static void logLegalShown() {
    logEvent("more", "navigation_legal");
  }

  public static void logLoopCorrectionModeChanged(String var0) {
    logEvent("settings", "settings_loop_correct_mode", "loop_value", var0, var0, 1);
    refreshUserDimension();
  }

  public static void logMeditationsShown() {
    logEvent("default", "navigation_guided_meditation");
  }

  public static void logMoreSectionShown() {
    logEvent("default", "navigation_more");
  }

  public static void logNewProductDialogResult(boolean var0) {
    String var1;
    if(var0) {
      var1 = "opened";
    } else {
      var1 = "closed";
    }

    logEvent("opening_dialog", "show_new_product_dialog_result", "answer", var1, var1, 1);
  }

  public static void logNewProductDialogShown() {
    logEvent("opening_dialog", "show_new_product_dialog");
  }

  public static void logNewsShown() {
    logEvent("more", "navigation_news");
  }

  public static void logOpenOtherProduct(String var0) {
    logEvent("more", "navigation_other_product", "product_name", var0, var0, 1);
  }

  public static void logPauseAllSounds() {
    logEvent("bottom_cloud_menu", "pause_all_sounds");
  }

  public static void logPlayFavorite() {
    logEvent("favorites", "play_favorite");
  }

  public static void logPlayFavoriteFromContextMenu() {
    logEvent("favorites", "play_favorite_in_context_menu");
  }

  public static void logPlayStaffPick(String var0) {
    logEvent("favorites", "play_staff_pick", "mix_name", var0, var0, 1);
  }

  public static void logPreRatingDialogShown() {
    logEvent("opening_dialog", "pre_review_dialog");
  }

  public static void logPreReviewDialogAnswer(String var0) {
    logEvent("opening_dialog", "pre_review_dialog_result", "answer", var0, var0, 1);
  }

  public static void logRatingDialogShown() {
    logEvent("opening_dialog", "review_dialog");
  }

  public static void logResetSoundVolumes(boolean var0) {
    String var1;
    if(var0) {
      var1 = "did_reset";
    } else {
      var1 = "did_not_reset";
    }

    logEvent("settings", "settings_reset_sounds_volume", "did_reset", String.valueOf(var0), var1, 1);
  }

  public static void logResumeAllSounds() {
    logEvent("bottom_cloud_menu", "resume_all_sounds");
  }

  public static void logReviewDialogAnswer(String var0) {
    logEvent("opening_dialog", "review_dialog_result", "answer", var0, var0, 1);
  }

  public static void logScreenBinauralInfo() {
    logScreen("screen_binaural_info");
  }

  public static void logScreenBlog() {
    logScreen("screen_blog");
  }

  public static void logScreenFavorites() {
    logScreen("screen_favorites");
  }

  public static void logScreenHelp() {
    logScreen("screen_help");
  }

  public static void logScreenIsochronicsInfo() {
    logScreen("screen_isochronic_info");
  }

  public static void logScreenLegal() {
    logScreen("screen_legal");
  }

  public static void logScreenMeditation() {
    logScreen("screen_meditations");
  }

  public static void logScreenMore() {
    logScreen("screen_more");
  }

  public static void logScreenNews() {
    logScreen("screen_news");
  }

  public static void logScreenSettings() {
    logScreen("screen_settings");
  }

  public static void logScreenSounds() {
    logScreen("screen_sounds");
  }

  public static void logScreenTimer() {
    logScreen("screen_timer");
  }

  public static void logScreenTrialPopup() {
    logScreen("screen_trial");
  }

  public static void logScreenUpgrade() {
    logScreen("screen_upgrade");
  }

  public static void logScreenWalkthroughAtIndex(int var0) {
    logScreen("screen_tutorial_" + var0);
  }

  public static void logScrolledIntoMeditations(int var0) {
    setUserPropertyMax("scroll_medit_max", var0);
  }

  public static void logSettingsShown() {
    logEvent("more", "navigation_settings");
  }

  public static void logShare() {
    logEvent("more", "more_action_share");
  }

  public static void logShowUpgradeSubscriptionFromSettings() {
    logEvent("settings", "settings_upgrade_subscription");
  }

  public static void logShowWalkthroughFromSettings() {
    logEvent("settings", "settings_show_walkthrough");
  }

  public static void logShowedSpecialOfferPopup(String var0, boolean var1) {
    HashMap var2 = new HashMap();
    var2.put("feature_id", var0);
    if(var1) {
      var0 = "yes";
    } else {
      var0 = "no";
    }

    var2.put("answer", var0);
    logEvent("upgrade", "special_offer_popup", var2, var0, 1);
  }

  public static void logShowedSubVolumeWithDoubleTap(Sound var0) {
    logEvent(getSoundCategory(var0), "sub_volume_double_tap");
  }

  public static void logShowedSubVolumeWithLongPress(Sound var0) {
    logEvent(getSoundCategory(var0), "sub_volume_double_tap");
  }

  public static void logSoundPlayed(Sound var0) {
    logEventWithSound(getSoundCategory(var0), "sound_play", var0);
  }

  public static void logSoundStopped(Sound var0) {
    logEventWithSound(getSoundCategory(var0), "sound_stop", var0);
  }

  public static void logSoundSubVolumeChanged(Sound var0, int var1) {
    String var2 = getSoundCategory(var0);
    HashMap var3 = new HashMap();
    var3.put("sound_id", var0.getId());
    var3.put("sound_volume", String.valueOf(var1));
    var3.put("sound_category", var2);
    logEvent(var2, "sub_volume_changed", var3, var0.getId(), var1);
  }

  public static void logSoundsShown() {
    logEvent("default", "navigation_sounds");
  }

  public static void logStopFavorite() {
    logEvent("favorites", "stop_favorite");
  }

  public static void logSubscriptionActivated(String var0) {
    String var1 = PersistedDataManager.getString("lastLoggedSubscription", "", applicationContext);
    if(var0 != null && !var0.equals(var1)) {
      PersistedDataManager.saveString("lastLoggedSubscription", var0, applicationContext);
      logEvent("upgrade", "subscription_activated", "feature_id", var0, var0, 1);
      refreshUserDimension();
    }

  }

  public static void logSubscriptionDeactivated(String var0) {
    PersistedDataManager.saveString("lastLoggedSubscription", (String)null, applicationContext);
    logEvent("upgrade", "subscription_deactivated", "feature_id", var0, var0, 1);
    refreshUserDimension();
  }

  public static void logSubscriptionProcessFailed(String var0, int var1) {
    HashMap var2 = new HashMap();
    var2.put("feature_id", String.valueOf(var0));
    var2.put("tier", String.valueOf(var1));
    logEvent("upgrade", "subscription_process_failed", var2, "tier" + var1, 1);
  }

  public static void logSubscriptionProcessSucceed(String var0, int var1, SkuDetails var2) {
    HashMap var3 = new HashMap();
    var3.put("feature_id", var0);
    var3.put("tier", String.valueOf(var1));
    logEvent("upgrade", "subscription_process_succeed", var3, "tier" + var1, 1);
    logPurchase(var2);
  }

  public static void logSubscriptionProcessTriggered(String var0, int var1) {
    HashMap var2 = new HashMap();
    var2.put("feature_id", var0);
    var2.put("tier", String.valueOf(var1));
    logEvent("upgrade", "subscription_process", var2, "tier" + var1, 1);
  }

  public static void logSubscriptionUpgradeProcessFailed(String var0, int var1) {
    HashMap var2 = new HashMap();
    var2.put("feature_id", String.valueOf(var0));
    var2.put("tier", String.valueOf(var1));
    logEvent("upgrade", "subscription_upgrade_process_failed", var2, "tier" + var1, 1);
  }

  public static void logSubscriptionUpgradeProcessSucceed(String var0, int var1, SkuDetails var2) {
    HashMap var3 = new HashMap();
    var3.put("feature_id", var0);
    var3.put("tier", String.valueOf(var1));
    logEvent("upgrade", "subscription_upgrade_process_succeed", var3, "tier" + var1, 1);
    logPurchase(var2);
  }

  public static void logSubscriptionUpgradeProcessTriggered(String var0, int var1) {
    HashMap var2 = new HashMap();
    var2.put("feature_id", var0);
    var2.put("tier", String.valueOf(var1));
    logEvent("upgrade", "subscription_upgrade_process", var2, "tier" + var1, 1);
  }

  public static void logTimerActivated(int var0, boolean var1) {
    HashMap var3 = new HashMap();
    var3.put("timer_value", String.valueOf(var0));
    var3.put("will_close_app", String.valueOf(var1));
    String var2;
    if(var1) {
      var2 = "closing_app";
    } else {
      var2 = "normal";
    }

    logEvent("timer", "active_timer", var3, var2, var0);
  }

  public static void logTimerComplete() {
    logEvent("timer", "timer_completed");
  }

  public static void logTimerShown() {
    logEvent("default", "navigation_timer");
  }

  public static void logTimerStopApp(boolean var0) {
    HashMap var2 = new HashMap();
    var2.put("will_close_app", String.valueOf(var0));
    String var1;
    if(var0) {
      var1 = "closing_app";
    } else {
      var1 = "normal";
    }

    logEvent("timer", "changed_timer_closing_app", var2, var1, 1);
  }

  public static void logUpdateFavorite() {
    logEvent("favorites", "update_favorite");
  }

  public static void logUpgradeFromBinaurals() {
    logEventUpgradeWithUpgradeReferer(UpgradeReferer.binaural);
  }

  public static void logUpgradeFromCloudMenu() {
    logEventUpgradeWithUpgradeReferer(UpgradeReferer.cloud_menu);
  }

  public static void logUpgradeFromFavorites() {
    logEventUpgradeWithUpgradeReferer(UpgradeReferer.favorites);
  }

  public static void logUpgradeFromIsochronics() {
    logEventUpgradeWithUpgradeReferer(UpgradeReferer.isochronic);
  }

  public static void logUpgradeFromMeditations() {
    logEventUpgradeWithUpgradeReferer(UpgradeReferer.guided_meditation);
  }

  public static void logUpgradeFromNotification() {
    logEventUpgradeWithUpgradeReferer(UpgradeReferer.notification);
  }

  public static void logUpgradeFromSounds() {
    logEventUpgradeWithUpgradeReferer(UpgradeReferer.sounds);
  }

  public static void logWalkthroughPageShownAtIndex(int var0) {
    logEvent("walkthrough", "screen_tutorial_" + var0);
  }
}
