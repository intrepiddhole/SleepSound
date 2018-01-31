package ipnossoft.rma;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ipnossoft.rma.free.R;

public class NavigationFragment extends Fragment {
  public NavigationFragment() {
  }

  public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
    return var1.inflate(R.layout.navigation, var2, false);
  }
}
