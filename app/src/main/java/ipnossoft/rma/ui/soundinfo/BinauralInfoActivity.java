package ipnossoft.rma.ui.soundinfo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.sounds.BinauralSound;

import ipnossoft.rma.free.R;

public class BinauralInfoActivity extends SoundsInfoActivity<BinauralSound> {
  public BinauralInfoActivity() {
    super(R.string.main_activity_binaural_beats, SoundLibrary.getInstance().getBinauralSounds());
  }

  private void addBrainwaveTypeHeader() {
    View var1 = this.getLayoutInflater().inflate(R.layout.sound_info_list_header, this.getListView(), false);
    ((TextView)var1.findViewById(R.id.brainwave_type_title)).setText(this.getString(R.string.main_activity_binaural_requires_headphones));
    ((ImageView)var1.findViewById(R.id.warning_sign)).setImageResource(R.drawable.icon_headphones);
    ((TextView)var1.findViewById(R.id.brainwave_type_description)).setText(this.getString(R.string.brainwave_info_brainwave_type_description_binaural));
    this.getListView().addHeaderView(var1, (Object)null, false);
  }

  protected void addHeaderViews() {
    this.addBrainwaveTypeHeader();
  }
}
