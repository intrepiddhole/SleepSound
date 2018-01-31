package ipnossoft.rma.review;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import ipnossoft.rma.MainActivity;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxPropertyHandler;
import ipnossoft.rma.free.R;
import ipnossoft.rma.ui.ReviewDialog;
import ipnossoft.rma.ui.ReviewDialog.RatingDialogType;
import ipnossoft.rma.ui.dialog.RelaxDialog.Builder;
import ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation;
import ipnossoft.rma.ui.scroller.RelaxScrollView;
import ipnossoft.rma.util.ConfigurableString;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.SendMailUtils;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ReviewProcess {
  private static final int DAYS_BEFORE_STARTING_REVIEW_PROCESS = 6;
  private static final int DEFAULT_OPENINGS_UNTIL_GIFT_RATING_PROCESS = 13;
  private static final int DEFAULT_OPENINGS_UNTIL_RATING_PROCESS = 13;
  private static final String FIRST_GIFT_SOUND_ID = "ipnossoft.rma.sounds.sound125";
  private static final int MAX_DAYS_AFTER_GIFT_DIALOG_BEFORE_GIFT_RATING_DIALOG = 7;
  private static final String PREF_KEY_DATE_TO_FORCE_START_REVIEW_PROCESS = "date_to_force_start_rating_process";
  private static final String PREF_KEY_HAS_SHOWN_GIFT_RATING_DIALOG = "has_shown_gift_rating_dialog";
  private static final String PREF_KEY_MAX_DATE_TO_SHOW_GIFT_RATING_DIALOG = "max_date_to_show_gift_sounds_rating_dialog";
  private static final String PREF_KEY_PREFIX_GIFT_SOUND_LISTENED_TO = "review_sound_listened_to_with_id_";
  private static final int SECONDS_BEFORE_SHOWING_GIFT_RATING_DIALOG_AFTER_PLAYING_SOUNDS = 2;
  private static final String SECOND_GIFT_SOUND_ID = "ipnossoft.rma.sounds.sound126";
  private static final String TAG = ReviewProcess.class.getSimpleName();
  private static ReviewProcess instance;
  public static boolean showingGiftDialog = false;
  public static boolean showingReviewDialogs = false;
  private MainActivity activity;
  private boolean hasBeenConfigured = false;
  private boolean hasGiftedSounds = false;
  private boolean hasUserListenedToBothGiftedSounds = false;
  private RelaxScrollView relaxScrollView;
  private int reviewProcessCounter;

  public ReviewProcess() {
  }

  private void addNewSoundsToUserLibrary() {
    SoundLibrary.getInstance().loadGiftedSoundsSynchronously(R.array.sounds_gifted);
    PersistedDataManager.saveBoolean("free_review_sounds_added", true, this.activity);
  }

  private boolean areGiftsEnabled() {
    boolean var2 = false;
    String var1 = RelaxPropertyHandler.getInstance().getProperties().getProperty("gifts.enabled");
    if(var1 != null) {
      var2 = Boolean.parseBoolean(var1);
    }

    return var2;
  }

  private View buildCustomGiftDialogView() {
    View var1 = LayoutInflater.from(this.activity).inflate(R.layout.gift_dialog_content, (ViewGroup)null, false);
    ((TextView)var1.findViewById(R.id.gift_dialog_message)).setText(this.getGiftMessageString());
    return var1;
  }

  private void createAndShowFeedbackDialog(final Context var1) {
    Builder var2 = new Builder(var1, RelaxDialogButtonOrientation.VERTICAL);
    String var3 = this.getFeedbackTitleString();
    if(var3 != null && !var3.isEmpty()) {
      var2.setTitle(var3);
    }

    var3 = this.getFeedbackMessageString();
    if(var3 != null && !var3.isEmpty()) {
      var2.setMessage(var3);
    }

    var2.setCancelable(false);
    var2.setPositiveButton(this.getFeedbackPositiveButtonString(), new OnClickListener() {
      public void onClick(View var1x) {
        RelaxAnalytics.logFeedbackDialogAnswer("yes");
        ReviewDialog.forceSaveReviewChoice(var1, false);
        SendMailUtils.sendSupportMail(ReviewProcess.this.activity);
        ReviewProcess.showingReviewDialogs = false;
      }
    });
    var2.setNegativeButton(this.getFeedbackNegativeButtonString(), new OnClickListener() {
      public void onClick(View var1x) {
        RelaxAnalytics.logFeedbackDialogAnswer("no");
        ReviewDialog.forceSaveReviewChoice(var1, false);
        ReviewProcess.showingReviewDialogs = false;
      }
    });
    var2.show();
  }

  private void createAndShowPreRatingDialog(final Context var1) {
    Builder var2 = new Builder(var1, RelaxDialogButtonOrientation.VERTICAL);
    String var3 = this.getPreRatingTitleString();
    if(var3 != null && !var3.isEmpty()) {
      var2.setTitle(var3);
    }

    var3 = this.getPreRatingMessageString();
    if(var3 != null && !var3.isEmpty()) {
      var2.setMessage(var3);
    }

    var2.setCancelable(false);
    var2.setPositiveButton(this.getPreRatingPostiveString(), new OnClickListener() {
      public void onClick(View var1x) {
        if(ReviewProcess.this.activity.isActivityRunning()) {
          RelaxAnalytics.logPreReviewDialogAnswer("yes");
          ReviewProcess.this.showRatingDialog(var1, RatingDialogType.NORMAL);
        }

      }
    });
    var2.setNegativeButton(this.getPreRatingNegativeString(), new OnClickListener() {
      public void onClick(View var1x) {
        if(ReviewProcess.this.activity.isActivityRunning()) {
          RelaxAnalytics.logPreReviewDialogAnswer("no");
          RelaxAnalytics.logFeedbackDialogShown();
          ReviewProcess.this.createAndShowFeedbackDialog(var1);
        }

      }
    });
    showingReviewDialogs = true;
    var2.show();
  }

  private void flashSpecialEffectOnGiftedSounds() {
    List var1 = Arrays.asList(new String[]{"ipnossoft.rma.sounds.sound125", "ipnossoft.rma.sounds.sound126"});
    this.relaxScrollView.flashNewGiftSpecialEffectsOnSounds(var1);
  }

  private String getFeedbackMessageString() {
    return ConfigurableString.getString(this.activity, "string.feeedback.dialog.message", R.string.empty);
  }

  private String getFeedbackNegativeButtonString() {
    return ConfigurableString.getString(this.activity, "string.feeedback.dialog.negative.button", R.string.dialog_label_no);
  }

  private String getFeedbackPositiveButtonString() {
    return ConfigurableString.getString(this.activity, "string.feeedback.dialog.positive.button", R.string.dialog_label_yes);
  }

  private String getFeedbackTitleString() {
    return ConfigurableString.getString(this.activity, "string.feeedback.dialog.title", R.string.feedback_dialog_title);
  }

  private String getGiftMessageString() {
    return ConfigurableString.getString(this.activity, "string.gift.dialog.message", R.string.gift_dialog_message);
  }

  private String getGiftPositiveButtonString() {
    return ConfigurableString.getString(this.activity, "string.gift.dialog.button.positive", R.string.gift_dialog_positive_button);
  }

  private String getGiftTitleString() {
    return ConfigurableString.getString(this.activity, "string.gift.dialog.title", R.string.gift_dialog_title);
  }

  public static ReviewProcess getInstance() {
    if(instance == null) {
      instance = new ReviewProcess();
    }

    return instance;
  }

  private int getOpeningUntilGiftPrompt() {
    String var1 = RelaxPropertyHandler.getInstance().getProperties().getProperty("gifts.opening.until.prompt", String.valueOf(13));

    try {
      int var2 = Integer.parseInt(var1);
      return var2;
    } catch (NumberFormatException var3) {
      return 13;
    }
  }

  private int getOpeningsUntilPrompt() {
    String var1 = RelaxPropertyHandler.getInstance().getProperties().getProperty("review.openings.until.prompt", String.valueOf(13));

    try {
      int var2 = Integer.parseInt(var1);
      return var2;
    } catch (NumberFormatException var3) {
      return 13;
    }
  }

  private String getPreRatingMessageString() {
    return ConfigurableString.getString(this.activity, "string.prereview.dialog.message", R.string.empty);
  }

  private String getPreRatingNegativeString() {
    return ConfigurableString.getString(this.activity, "string.prereview.dialog.negative.button", R.string.dialog_label_no);
  }

  private String getPreRatingPostiveString() {
    return ConfigurableString.getString(this.activity, "string.prereview.dialog.positive.button", R.string.dialog_label_yes);
  }

  private String getPreRatingTitleString() {
    return ConfigurableString.getString(this.activity, "string.prereview.dialog.title", R.string.pre_rating_dialog_title);
  }

  private void giftSoundsAndShowGiftDialog() {
    this.addNewSoundsToUserLibrary();
    if(this.activity.isActivityRunning()) {
      this.showGiftDialog();
    }

  }

  private boolean hasDateToForceStartReviewProcessPassed() {
    long var1 = PersistedDataManager.getLong("date_to_force_start_rating_process", 0L, this.activity);
    return var1 != 0L && var1 < System.currentTimeMillis();
  }

  private boolean haveBothGiftedSoundsBeenListenedTo() {
    boolean var2 = false;
    boolean var1 = var2;
    if(PersistedDataManager.getBoolean("review_sound_listened_to_with_id_ipnossoft.rma.sounds.sound125", false, this.activity).booleanValue()) {
      var1 = var2;
      if(PersistedDataManager.getBoolean("review_sound_listened_to_with_id_ipnossoft.rma.sounds.sound126", false, this.activity).booleanValue()) {
        var1 = true;
      }
    }

    return var1;
  }

  private void setDateForForcedStartingOfReviewProcess() {
    Calendar var1 = Calendar.getInstance();
    var1.add(Calendar.DAY_OF_YEAR, 6);
    var1.set(Calendar.HOUR_OF_DAY, 0);
    var1.set(Calendar.MINUTE, 0);
    var1.set(Calendar.SECOND, 1);
    PersistedDataManager.saveLong("date_to_force_start_rating_process", var1.getTimeInMillis(), this.activity);
  }

  private void setMaxDateForShowingOfGiftRatingDialog() {
    Calendar var1 = Calendar.getInstance();
    var1.add(Calendar.DAY_OF_YEAR, 7);
    PersistedDataManager.saveLong("max_date_to_show_gift_sounds_rating_dialog", var1.getTimeInMillis(), this.activity);
  }

  private boolean shouldShowGiftDialog() {
    return this.areGiftsEnabled() && (!this.hasGiftedSounds && this.reviewProcessCounter >= this.getOpeningUntilGiftPrompt() || !this.hasGiftedSounds && this.hasDateToForceStartReviewProcessPassed());
  }

  private boolean shouldShowGiftRatingDialog() {
    boolean var4 = false;
    boolean var3 = var4;
    if(this.hasGiftedSounds) {
      long var1 = PersistedDataManager.getLong("max_date_to_show_gift_sounds_rating_dialog", 0L, this.activity);
      boolean var5 = PersistedDataManager.getBoolean("has_shown_gift_rating_dialog", false, this.activity).booleanValue();
      var3 = var4;
      if(var1 != 0L) {
        var3 = var4;
        if(var1 < System.currentTimeMillis()) {
          var3 = var4;
          if(!var5) {
            var3 = var4;
            if(ReviewDialog.isUserNeutral(this.activity)) {
              var3 = true;
            }
          }
        }
      }
    }

    return var3;
  }

  private boolean shouldShowRatingDialog() {
    return this.reviewProcessCounter >= this.getOpeningsUntilPrompt() && ReviewDialog.isUserNeutral(this.activity);
  }

  private void showGiftDialog() {
    Builder var1 = new Builder(this.activity, RelaxDialogButtonOrientation.VERTICAL);
    var1.setTitle(this.getGiftTitleString());
    var1.setCustomContentView(this.buildCustomGiftDialogView());
    var1.setPositiveButton(this.getGiftPositiveButtonString(), new OnClickListener() {
      public void onClick(View var1) {
        ReviewProcess.this.smoothScrollToNewSounds();
        ReviewProcess.this.flashSpecialEffectOnGiftedSounds();
        ReviewProcess.this.setMaxDateForShowingOfGiftRatingDialog();
        ReviewProcess.showingGiftDialog = false;
      }
    });
    var1.setCancelable(false);
    var1.show();
    showingGiftDialog = true;
    RelaxAnalytics.logGiftDialogShown();
  }

  private void showGiftRatingDialogAfterNumberOfSeconds(int var1) {
    final MainActivity var2 = this.activity;
    (new Handler()).postDelayed(new Runnable() {
      public void run() {
        if(ReviewProcess.this.activity.isActivityRunning()) {
          ReviewProcess.this.showRatingDialog(var2, RatingDialogType.GIFT_SOUNDS);
        }

      }
    }, (long)(var1 * 1000));
  }

  private void showPreRatingDialog(Context var1) {
    if(this.activity.isActivityRunning()) {
      RelaxAnalytics.logPreRatingDialogShown();
      this.createAndShowPreRatingDialog(var1);
    }

  }

  private void showRatingDialog(Context var1, RatingDialogType var2) {
    try {
      (new ReviewDialog(var1)).createAndShow(var2);
      RelaxAnalytics.logRatingDialogShown();
      if(var2 == RatingDialogType.GIFT_SOUNDS) {
        PersistedDataManager.saveBoolean("has_shown_gift_rating_dialog", true, this.activity);
      }

    } catch (Exception var3) {
      ;
    }
  }

  private void smoothScrollToNewSounds() {
    this.activity.simulateHomeTabClick();
    this.relaxScrollView.smoothScrollToEnd();
  }

  public void configure(MainActivity var1, RelaxScrollView var2) {
    this.activity = var1;
    this.relaxScrollView = var2;
    this.reviewProcessCounter = PersistedDataManager.getInteger("app_startup_review_counter", 0, this.activity);
    this.hasGiftedSounds = PersistedDataManager.getBoolean("free_review_sounds_added", false, this.activity).booleanValue();
    this.hasUserListenedToBothGiftedSounds = this.haveBothGiftedSoundsBeenListenedTo();
    if(PersistedDataManager.getLong("date_to_force_start_rating_process", 0L, this.activity) == 0L) {
      this.setDateForForcedStartingOfReviewProcess();
    }

    this.hasBeenConfigured = true;
  }

  public boolean isSoundGifted(Sound var1) {
    boolean var3 = false;
    boolean var2;
    if(!"ipnossoft.rma.sounds.sound125".equals(var1.getId())) {
      var2 = var3;
      if(!"ipnossoft.rma.sounds.sound126".equals(var1.getId())) {
        return var2;
      }
    }

    var2 = var3;
    if(!PersistedDataManager.getBoolean("review_sound_listened_to_with_id_" + var1.getId(), false, this.activity).booleanValue()) {
      var2 = true;
    }

    return var2;
  }

  public void listenedToSound(Sound var1) {
    if(!this.hasBeenConfigured) {
      Log.e(TAG, "Cannot call this method before configure has been called.");
    } else if(!this.hasUserListenedToBothGiftedSounds) {
      if("ipnossoft.rma.sounds.sound125".equals(var1.getId()) || "ipnossoft.rma.sounds.sound126".equals(var1.getId())) {
        PersistedDataManager.saveBoolean("review_sound_listened_to_with_id_" + var1.getId(), true, this.activity);
      }

      if(this.haveBothGiftedSoundsBeenListenedTo() && this.shouldShowRatingDialog()) {
        this.showGiftRatingDialogAfterNumberOfSeconds(2);
        this.hasUserListenedToBothGiftedSounds = true;
        return;
      }
    }

  }

  public boolean shouldStart() {
    if(!this.hasBeenConfigured) {
      Log.e(TAG, "Cannot call this method before configure has been called.");
    } else if(!showingReviewDialogs && !showingGiftDialog && (this.shouldShowGiftDialog() || this.shouldShowGiftRatingDialog() || this.shouldShowRatingDialog())) {
      return true;
    }

    return false;
  }

  public void start() {
    if(!this.hasBeenConfigured) {
      Log.e(TAG, "Cannot call this method before configure has been called.");
    } else {
      if(this.areGiftsEnabled()) {
        if(this.shouldShowGiftDialog()) {
          this.giftSoundsAndShowGiftDialog();
          return;
        }

        if(this.shouldShowGiftRatingDialog() && this.activity.isActivityRunning()) {
          this.showRatingDialog(this.activity, RatingDialogType.GIFT_SOUNDS);
          return;
        }
      }

      if(this.shouldShowRatingDialog()) {
        this.showPreRatingDialog(this.activity);
        return;
      }
    }

  }
}
