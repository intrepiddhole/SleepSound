package ipnossoft.rma;

import android.os.AsyncTask;
import android.os.Bundle;
import com.ipnossoft.api.soundlibrary.SoundLibrary;

public class RelaxMelodiesActivityFree extends SplashScreenActivity {
  public RelaxMelodiesActivityFree() {
  }

  protected void onCreate(Bundle var1) {
    super.onCreate(var1);
    final boolean[] var2 = new boolean[]{true};
    (new AsyncTask<Void, Void, Void>() {

      protected Void doInBackground(Void... var1) {
        var2[0] = RelaxMelodiesActivityFree.this.processApplicationPreLoading();
        return null;
      }

      protected void onPostExecute(Void var1) {
        super.onPostExecute(var1);
        if(var2[0]) {
          RelaxMelodiesActivityFree.this.splashScreenLoadingFinished();
        }

      }
    }).execute(new Void[0]);
  }

  public boolean processApplicationPreLoading() {
    super.processApplicationPreLoading();
    SoundLibrary.getInstance().loadBuiltInSoundsSynchronously(2131558409, 2131558405, 2131558408, 2131558407);
    if(PersistedDataManager.getBoolean("free_review_sounds_added", false, this).booleanValue()) {
      SoundLibrary.getInstance().loadGiftedSoundsSynchronously(2131558406);
    }

    return true;
  }
}
