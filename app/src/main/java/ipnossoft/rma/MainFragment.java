package ipnossoft.rma;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ipnossoft.rma.free.R;

public class MainFragment extends Fragment {
  private MainFragment.OnMainFragmentReadyListener mListener;

  public MainFragment() {
  }

  public void onActivityCreated(Bundle var1) {
    super.onActivityCreated(var1);
    this.mListener.OnMainFragmentReady();
  }

  public void onAttach(Activity var1) {
    super.onAttach(var1);

    try {
      this.mListener = (MainFragment.OnMainFragmentReadyListener)var1;
    } catch (ClassCastException var3) {
      throw new ClassCastException(var1.toString() + " must implement OnMainFragmentReadyListener");
    }
  }

  public void onCreate(Bundle var1) {
    super.onCreate(var1);
  }

  public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
    return var1.inflate(R.layout.main, var2, false);
  }

  public interface OnMainFragmentReadyListener {
    void OnMainFragmentReady();
  }
}