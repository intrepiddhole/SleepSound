package ipnossoft.rma;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import ipnossoft.rma.free.R;

public class BottomMenuFragment extends Fragment {
  private ImageView background;

  public BottomMenuFragment() {
  }

  public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
    View var4 = var1.inflate(R.layout.bottom_menu, var2, false);
    this.background = (ImageView)var4.findViewById(R.id.bottom_menu_image);
    Glide.with(this).load(Integer.valueOf(R.drawable.bottom_menu)).placeholder(R.drawable.bottom_menu).dontAnimate().into(this.background);
    return var4;
  }
}