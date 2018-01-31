package ipnossoft.rma.ui.button;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.ipnossoft.api.soundlibrary.Sound;
import ipnossoft.rma.R.anim;
import ipnossoft.rma.R.dimen;
import ipnossoft.rma.R.id;
import ipnossoft.rma.R.layout;
import ipnossoft.rma.ui.AutoResizeTwoLineTextView;
import ipnossoft.rma.util.Utils;
import ipnossoft.rma.util.font.RelaxFont;
import ipnossoft.rma.util.font.RelaxFontFactory;

public class BrainwaveSoundButton extends SoundButton {
  BrainwaveSoundButton(Context var1, Sound var2, int var3, SoundButtonGestureListener var4) {
    super(var1, var2, var3, anim.rotate_beat_button_1, anim.rotate_beat_button_2, var4);
  }

  protected void calculateImageDimensions() {
    this.imageWidth = (int)this.getResources().getDimension(R.dimen.brainwave_image_width);
    this.imageHeight = (int)this.getResources().getDimension(R.dimen.brainwave_image_height);
  }

  protected LayoutParams correctTextOverlayLayoutParams(LayoutParams var1) {
    var1 = new LayoutParams(var1.width, var1.height);
    var1.addRule(9, 0);
    var1.addRule(11, -1);
    var1.addRule(12, -1);
    var1.setMargins(0, 0, 0, this.getContext().getResources().getDimensionPixelSize(R.dimen.brainwave_badge_margin_bottom));
    return var1;
  }

  protected View createButtonView() {
    RelativeLayout var1 = (RelativeLayout)inflate(this.getContext(), layout.brainwave_sound, (ViewGroup)null);
    var1.setId(Utils.generateUniqueViewId());
    this.calculateImageDimensions();
    String var2 = this.getSound().getName();
    RelaxFont var3 = RelaxFontFactory.INSTANCE.getSmallRegularFont(this.getContext());
    AutoResizeTwoLineTextView var4 = (AutoResizeTwoLineTextView)var1.findViewById(R.id.brainwave_sound_text_view);
    var4.setDefaultStartingTextSize(var3.getSize());
    var4.setText(var2);
    var4.setLineSpacing(0.0F, 0.8F);
    this.doSetupTextViewForXMLLayout(var4);
    RelativeLayout var5 = (RelativeLayout)var1.findViewById(R.id.brainwave_rope_and_sound_layout);
    CustomSelectableButton var6 = (CustomSelectableButton)var1.findViewById(R.id.brainwave_sound_image_view);
    this.setupCustomSelectableButton(var6);
    this.imageButton = var6;
    this.setButtonStateNormal();
    this.addView(var1);
    this.setClipChildren(false);
    this.setClipToPadding(false);
    this.setAnimatedView(var5);
    this.setClickable(true);
    this.disableClipOnParents(var5);
    return this.imageButton;
  }

  public void setPivotX(float var1) {
    ((RelativeLayout)this.findViewById(R.id.brainwave_rope_and_sound_layout)).setPivotX(var1);
  }

  public void setPivotY(float var1) {
    ((RelativeLayout)this.findViewById(R.id.brainwave_rope_and_sound_layout)).setPivotY(var1);
  }
}
