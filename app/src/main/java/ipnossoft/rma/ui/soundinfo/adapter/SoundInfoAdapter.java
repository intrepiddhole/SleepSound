package ipnossoft.rma.ui.soundinfo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.sounds.BinauralSound;
import com.ipnossoft.api.soundlibrary.sounds.IsochronicSound;
import ipnossoft.rma.NavigationHelper;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.Utils;
import java.util.List;

public class SoundInfoAdapter<T extends Sound> extends ArrayAdapter<T> {
  private final Activity activity;
  private final List<T> sounds;

  public SoundInfoAdapter(Activity var1, List<T> var2) {
    super(var1, R.layout.sound_info_list_item, var2);
    this.activity = var1;
    this.sounds = var2;
  }

  private void onItemClick(Sound var1) {
    if(var1.isLockedWithPremiumEnabled(RelaxMelodiesApp.isPremium().booleanValue())) {
      if(var1 instanceof BinauralSound) {
        RelaxAnalytics.logUpgradeFromBinaurals();
      } else {
        RelaxAnalytics.logUpgradeFromIsochronics();
      }

      NavigationHelper.showSubscription(this.activity);
    } else {
      SoundManager var2 = SoundManager.getInstance();
      if(var1 instanceof BinauralSound) {
        var2.handleSoundPressed(var1, this.activity);
      } else {
        var2.handleSoundPressed(var1, this.activity);
      }
    }
  }

  public View getView(int var1, View var2, ViewGroup var3) {
    final Sound var4 = (Sound)this.sounds.get(var1);
    SoundInfoAdapter.SoundInfoViewHolder var5;
    if(var2 == null) {
      var2 = ((LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.sound_info_list_item, var3, false);
      var5 = new SoundInfoAdapter.SoundInfoViewHolder(var2);
      var2.setTag(var5);
    } else {
      var5 = (SoundInfoAdapter.SoundInfoViewHolder)var2.getTag();
    }

    var5.update(var4);
    var2.setOnClickListener(new OnClickListener() {
      public void onClick(View var1) {
        SoundInfoAdapter.this.onItemClick(var4);
      }
    });
    return var2;
  }

  public boolean isEnabled(int var1) {
    return false;
  }

  private class SoundInfoViewHolder {
    View backgroundOverlay;
    TextView descriptionText;
    ImageView imageOverlay;
    ImageView imageView;
    TextView subtitleText;
    TextView textOverlay;
    TextView titleText;

    public SoundInfoViewHolder(View var2) {
      this.backgroundOverlay = var2.findViewById(R.id.sound_info_list_item_background_overlay);
      this.imageView = (ImageView)var2.findViewById(R.id.sound_info_list_item_image);
      this.imageOverlay = (ImageView)var2.findViewById(R.id.sound_info_list_item_image_overlay);
      this.textOverlay = (TextView)var2.findViewById(R.id.sound_info_list_item_text_overlay);
      this.titleText = (TextView)var2.findViewById(R.id.sound_info_list_item_title);
      this.subtitleText = (TextView)var2.findViewById(R.id.sound_info_list_item_subtitle);
      this.descriptionText = (TextView)var2.findViewById(R.id.sound_info_list_item_description);
    }

    public void update(T var1) {
      this.imageOverlay.setVisibility(View.INVISIBLE);
      this.textOverlay.setVisibility(View.INVISIBLE);
      if(var1.isLockedWithPremiumEnabled(RelaxMelodiesApp.isPremium().booleanValue())) {
        this.textOverlay.setVisibility(View.VISIBLE);
      } else if(!var1.isPlayable()) {
        this.imageOverlay.setVisibility(View.VISIBLE);
      }

      boolean var2 = SoundManager.getInstance().isSelected(var1.getId());
      ImageView var4 = this.imageView;
      int var3;
      if(var2) {
        var3 = var1.getInfoSelectedImageResourceId();
      } else {
        var3 = var1.getInfoNormalImageResourceId();
      }

      var4.setImageResource(var3);
      View var5 = this.backgroundOverlay;
      if(var2) {
        var3 = Color.parseColor("#20FFFFFF");
      } else {
        var3 = 0;
      }

      var5.setBackgroundColor(var3);
      this.titleText.setText(var1.getName());
      if(var1 instanceof BinauralSound) {
        BinauralSound var6 = (BinauralSound)var1;
        this.subtitleText.setText(var6.getFrequency());
        this.imageView.setVisibility(View.VISIBLE);
      } else if(var1 instanceof IsochronicSound) {
        if(this.imageOverlay.getVisibility() != View.VISIBLE) {
          Utils.setSoundImage(var1, this.imageView);
        }

        IsochronicSound var7 = (IsochronicSound)var1;
        this.subtitleText.setText(var7.getFrequency());
        this.imageView.setVisibility(View.VISIBLE);
      }

      this.descriptionText.setText(var1.getDescription());
    }
  }
}
