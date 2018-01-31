package ipnossoft.rma.ui.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ipnossoft.rma.free.R;

public class TutorialFragment extends Fragment {
  public TutorialFragment() {
  }

  public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
    return var1.inflate(R.layout.tutorial_view_pager, (ViewGroup)null);
  }
}
