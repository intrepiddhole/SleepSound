package ipnossoft.rma.web;

import android.content.Intent;
import ipnossoft.rma.AdWebViewActivity;
import ipnossoft.rma.util.RelaxAnalytics;

public class RelaxFreeMoreFragment extends MoreFragment {
  public RelaxFreeMoreFragment() {
  }

  public void displayAdsBlog() {
    RelaxAnalytics.logBlogShown();
    RelaxAnalytics.logScreenBlog();
    Intent var1 = new Intent(this.getActivity(), AdWebViewActivity.class);
    var1.putExtra("title", this.getString(2131231222));
    this.startActivity(var1);
  }
}
