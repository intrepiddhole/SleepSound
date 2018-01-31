package ipnossoft.rma.favorites;

import android.util.Log;
import com.ipnossoft.api.soundlibrary.Sound;
import org.json.JSONException;
import org.json.JSONObject;

@Deprecated
class FavoriteSound {
  public static final String FIELD_JSON_ID = "id";
  public static final String FIELD_JSON_VOLUME = "volume";
  private static final String TAG = "FavoriteSound";
  private final String imageResourceName = "";
  private final int soundId;
  private final float volume;

  public FavoriteSound(Sound var1, float var2) {
    this.soundId = var1.getMediaResourceId();
    this.volume = var2;
  }

  public int getSoundId() {
    return this.soundId;
  }

  public float getVolume() {
    return this.volume;
  }

  public JSONObject toJSONObject() {
    try {
      JSONObject var1 = new JSONObject();
      var1.put("id", this.soundId);
      var1.put("volume", (double)this.volume);
      return var1;
    } catch (JSONException var2) {
      Log.e("FavoriteSound", "JSONException while transforming FavoriteSound to json", var2);
      throw new RuntimeException(var2);
    }
  }
}
