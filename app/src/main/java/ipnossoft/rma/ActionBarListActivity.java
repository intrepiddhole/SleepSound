package ipnossoft.rma;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class ActionBarListActivity extends AppCompatActivity implements OnItemClickListener {
  private ListView listView;

  public ActionBarListActivity() {
  }

  private void onListItemClick(ListView var1, View var2, int var3, long var4) {
  }

  protected void attachBaseContext(Context var1) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(var1));
  }

  protected ListAdapter getListAdapter() {
    if(this.getListView() != null) {
      ListAdapter var2 = this.getListView().getAdapter();
      ListAdapter var1 = var2;
      if(var2 instanceof HeaderViewListAdapter) {
        var1 = ((HeaderViewListAdapter)var2).getWrappedAdapter();
      }

      return var1;
    } else {
      return null;
    }
  }

  protected ListView getListView() {
    if(this.listView == null && this.findViewById(16908298) != null) {
      this.listView = (ListView)this.findViewById(16908298);
      this.listView.setOnItemClickListener(this);
    }

    return this.listView;
  }

  public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
    this.onListItemClick(this.getListView(), var2, var3, var4);
  }

  protected void setListAdapter(ListAdapter var1) {
    this.getListView().setAdapter(var1);
  }
}
