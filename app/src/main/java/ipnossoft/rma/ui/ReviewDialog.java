package ipnossoft.rma.ui;

import android.content.Context;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.review.ReviewProcess;
import ipnossoft.rma.ui.dialog.RelaxDialog.Builder;
import ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation;
import ipnossoft.rma.util.ConfigurableString;

public class ReviewDialog {
  private static final String PREF_KEY_RATING = "rating";
  private static final int PREF_VALUE_RATING_NEUTRAL = 0;
  private static final int PREF_VALUE_RATING_NO = 1;
  private static final int PREF_VALUE_RATING_YES = 2;
  private RelaxMelodiesApp app;
  private Context context;

  public ReviewDialog(Context var1) {
    this.context = var1;
    this.app = (RelaxMelodiesApp)var1.getApplicationContext();
  }

  public static void forceSaveReviewChoice(Context var0, boolean var1) {
    byte var2;
    if(var1) {
      var2 = 2;
    } else {
      var2 = 1;
    }

    PersistedDataManager.saveInteger("rating", var2, var0);
  }

  private String getMessageString(ReviewDialog.RatingDialogType var1) {
    return var1 == ReviewDialog.RatingDialogType.GIFT_SOUNDS?ConfigurableString.getString(this.context, "string.review.dialog.gifted.message", R.string.gift_dialog_review_message):ConfigurableString.getString(this.context, "string.review.dialog.standard.message", string.rating_dialog_text);
  }

  private String getNegativeButtonString(ReviewDialog.RatingDialogType var1) {
    return var1 == ReviewDialog.RatingDialogType.GIFT_SOUNDS?ConfigurableString.getString(this.context, "string.review.dialog.gifted.button.negative", R.string.rating_dialog_no):ConfigurableString.getString(this.context, "string.review.dialog.standard.button.negative", string.rating_dialog_no);
  }

  private String getNeutralButtonString(ReviewDialog.RatingDialogType var1) {
    return var1 == ReviewDialog.RatingDialogType.GIFT_SOUNDS?ConfigurableString.getString(this.context, "string.review.dialog.gifted.button.neutral", R.string.rating_dialog_neutral):ConfigurableString.getString(this.context, "string.review.dialog.standard.button.neutral", string.rating_dialog_neutral);
  }

  private String getPositiveButtonString(ReviewDialog.RatingDialogType var1) {
    return var1 == ReviewDialog.RatingDialogType.GIFT_SOUNDS?ConfigurableString.getString(this.context, "string.review.dialog.gifted.button.positive", R.string.rating_dialog_yes):ConfigurableString.getString(this.context, "string.review.dialog.standard.button.positive", string.rating_dialog_yes);
  }

  private String getTitleString(ReviewDialog.RatingDialogType var1) {
    return var1 == ReviewDialog.RatingDialogType.GIFT_SOUNDS?ConfigurableString.getString(this.context, "string.review.dialog.gifted.title", R.string.rating_dialog_title):ConfigurableString.getString(this.context, "string.review.dialog.standard.title", string.rating_dialog_title);
  }

  public static boolean isUserNeutral(Context var0) {
    boolean var1 = false;
    if(PersistedDataManager.getInteger("rating", 0, var0) == 0) {
      var1 = true;
    }

    return var1;
  }

  public static void resetRatingPreference(Context var0) {
    PersistedDataManager.saveInteger("rating", 0, var0);
  }

  public void createAndShow(ReviewDialog.RatingDialogType var1) {
    Builder var2 = new Builder(this.context, RelaxDialogButtonOrientation.VERTICAL);
    var2.setTitle(this.getTitleString(var1));
    var2.setMessage(this.getMessageString(var1));
    var2.setCancelable(false);
    var2.setPositiveButton(this.getPositiveButtonString(var1), new ReviewDialog$1(this));
    var2.setNeutralButton(this.getNeutralButtonString(var1), new ReviewDialog$2(this));
    var2.setNegativeButton(this.getNegativeButtonString(var1), new ReviewDialog$3(this));
    var2.setOnDismissListener(new ReviewDialog$4(this));
    ReviewProcess.showingReviewDialogs = true;
    var2.show();
  }

  public static enum RatingDialogType {
    GIFT_SOUNDS,
    NORMAL;

    private RatingDialogType() {
    }
  }
}
