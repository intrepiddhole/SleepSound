package ipnossoft.rma.upgrade;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ipnossoft.rma.free.R;

public class SubscriptionPageFragment extends Fragment {
  private String subText1;
  private String text1;
  private String text2;

  public SubscriptionPageFragment() {
  }

  public static SubscriptionPageFragment newInstance(String var0, String var1, String var2) {
    SubscriptionPageFragment var3 = new SubscriptionPageFragment();
    Bundle var4 = new Bundle();
    var4.putString("text1", var0);
    var4.putString("subText1", var1);
    var4.putString("text2", var2);
    var3.setArguments(var4);
    return var3;
  }

  public void onCreate(Bundle var1) {
    super.onCreate(var1);
    this.text1 = this.getArguments().getString("text1");
    if(this.getArguments().containsKey("subText1")) {
      this.subText1 = this.getArguments().getString("subText1");
    }

    this.text2 = this.getArguments().getString("text2");
  }

  public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
    View var4 = var1.inflate(R.layout.subscription_view_pager_fragment, var2, false);
    ((TextView)var4.findViewById(R.id.subscription_view_pager_text1)).setText(this.text1);
    TextView var5 = (TextView)var4.findViewById(R.id.subscription_view_pager_subtext1);
    var5.setVisibility(View.GONE);
    if(this.subText1 != null && !this.subText1.isEmpty()) {
      var5.setVisibility(View.VISIBLE);
      var5.setText(this.subText1);
    }

    ((TextView)var4.findViewById(R.id.subscription_view_pager_text2)).setText(this.text2);
    return var4;
  }
}
