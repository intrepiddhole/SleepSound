package ipnossoft.rma.ui.soundinfo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.sounds.IsochronicSound;

import ipnossoft.rma.free.R;

public class IsochronicInfoActivity extends SoundsInfoActivity<IsochronicSound> {
  public IsochronicInfoActivity() {
    super(R.string.main_activity_isochronic_tones, SoundLibrary.getInstance().getIsochronicSounds());
  }

  private void addBrainwaveTypeHeader() {
    View var1 = this.getLayoutInflater().inflate(R.layout.sound_info_list_header, this.getListView(), false);
    ((TextView)var1.findViewById(R.id.brainwave_type_title)).setText(this.getString(R.string.main_activity_isochronic_no_headphones_required));
    ((ImageView)var1.findViewById(R.id.warning_sign)).setImageResource(R.drawable.icon_headphones_off);
    ((TextView)var1.findViewById(R.id.brainwave_type_description)).setText(this.getString(R.string.brainwave_info_brainwave_type_description_isochronic));
    this.getListView().addHeaderView(var1, (Object)null, false);
  }

  protected void addHeaderViews() {
    this.addBrainwaveTypeHeader();
  }
}
