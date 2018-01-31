package ipnossoft.rma.web;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import com.ipnossoft.api.newsservice.NewsService;
import com.ipnossoft.api.newsservice.NewsServiceListener;
import com.ipnossoft.api.newsservice.model.News;
import ipnossoft.rma.ActionBarListActivity;
import ipnossoft.rma.DefaultServiceConnection;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.util.Utils;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressLint("ResourceType")
public class NewsActivity extends ActionBarListActivity implements NewsServiceListener {
  private static final String TAG = "NewsActivity";
  private DefaultServiceConnection conn = new DefaultServiceConnection();
  private boolean newsFetched = false;

  public NewsActivity() {
  }

  private List<Map<String, Object>> buildNewsListAdapterData(List<News> var1) {
    ArrayList var2 = new ArrayList();
    Iterator var5 = var1.iterator();

    while(var5.hasNext()) {
      News var3 = (News)var5.next();
      HashMap var4 = new HashMap();
      var4.put("id", Integer.valueOf(var3.getId()));
      var4.put("imageurl", var3.getImage());
      var4.put("date", this.formatDate(var3.getDate()));
      var4.put("title", var3.getTitle());
      var4.put("message", var3.getMessage());
      var2.add(var4);
    }

    return var2;
  }

  private ArrayAdapter<String> buildSingleStringAdapter(String var1) {
    ArrayAdapter var2 = new ArrayAdapter(this, 17367043);
    var2.add(var1);
    return var2;
  }

  private String formatDate(Date var1) {
    return DateFormat.getDateInstance(2).format(var1);
  }

  public void newsServiceDidFailFetchingNews(Exception var1) {
  }

  public void newsServiceDidFinishFetchingNews(List<News> var1) {
    if(!this.newsFetched) {
      Utils.executeTask(new NewsActivity.FetchListTask(), new Void[0]);
    }

  }

  protected void onCreate(Bundle var1) {
    super.onCreate(var1);
    this.setContentView(R.layout.news);
    this.setTitle(R.string.news_activity_title);
    ActionBar var2 = this.getSupportActionBar();
    if(var2 != null) {
      var2.setHomeButtonEnabled(true);
      var2.setDisplayHomeAsUpEnabled(true);
    }

    this.setListAdapter(this.buildSingleStringAdapter(this.getString(R.string.news_activity_loading_message)));
    if(!this.newsFetched) {
      Utils.executeTask(new NewsActivity.FetchListTask(), new Void[0]);
    }

  }

  public boolean onOptionsItemSelected(MenuItem var1) {
    if(var1.getItemId() == 16908332) {
      this.finish();
      return true;
    } else {
      return super.onOptionsItemSelected(var1);
    }
  }

  protected void onResume() {
    super.onResume();
    NewsService var1 = RelaxMelodiesApp.getInstance().getNewsService();
    if(var1 != null) {
      var1.addListener(this);
    }

  }

  protected void onStart() {
    super.onStart();
    this.conn.connect(this);
  }

  protected void onStop() {
    this.conn.disconnect(this);
    NewsService var1 = RelaxMelodiesApp.getInstance().getNewsService();
    if(var1 != null) {
      var1.removeListener(this);
    }

    super.onStop();
  }

  private class FetchListTask extends AsyncTask<Void, Void, BaseAdapter> {
    private NewsService newsService;

    private FetchListTask() {
    }

    protected BaseAdapter doInBackground(Void... var1) {
      this.newsService = ((RelaxMelodiesApp)NewsActivity.this.getApplicationContext()).getNewsService();
      if(this.newsService == null) {
        return NewsActivity.this.buildSingleStringAdapter(NewsActivity.this.getString(R.string.news_activity_error_fetching_newsmanager));
      } else {
        List var2 = this.newsService.getNews();
        if(var2 != null && !var2.isEmpty()) {
          NewsActivity.this.newsFetched = true;
          var2 = NewsActivity.this.buildNewsListAdapterData(var2);
          return new NewsAdapter(NewsActivity.this, var2);
        } else {
          return NewsActivity.this.buildSingleStringAdapter(NewsActivity.this.getString(R.string.news_activity_error_fetching_news));
        }
      }
    }

    protected void onPostExecute(BaseAdapter var1) {
      NewsActivity.this.setListAdapter(var1);
      if(this.newsService != null) {
        this.newsService.flagNewsAsRead();
      }

    }
  }
}
