package ipnossoft.rma.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ProgressBar;
import ipnossoft.rma.DefaultServiceConnection;
import ipnossoft.rma.free.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WebViewActivity extends AppCompatActivity {
  public static final String EXTRA_TITLE = "title";
  public static final String EXTRA_URL = "url";
  private DefaultServiceConnection conn = new DefaultServiceConnection();
  private WebView webView;

  public WebViewActivity() {
  }

  private String getExtraOrDefault(String var1, String var2) {
    var1 = this.getIntent().getStringExtra(var1);
    return var1 != null?var1:var2;
  }

  protected void attachBaseContext(Context var1) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(var1));
  }

  protected void loadContentView() {
    this.setContentView(R.layout.webview);
  }

  public void onBackPressed() {
    if(this.webView != null && this.webView.copyBackForwardList().getCurrentIndex() > 0) {
      this.webView.goBack();
    } else {
      super.onBackPressed();
    }
  }

  @SuppressLint({"SetJavaScriptEnabled"})
  protected void onCreate(Bundle var1) {
    super.onCreate(var1);
    this.loadContentView();
    this.prepareView();
  }

  public boolean onOptionsItemSelected(MenuItem var1) {
    if(var1.getItemId() == 16908332) {
      this.finish();
      return true;
    } else {
      return super.onOptionsItemSelected(var1);
    }
  }

  protected void onStart() {
    super.onStart();
    this.conn.connect(this);
  }

  protected void onStop() {
    this.conn.disconnect(this);
    super.onStop();
  }

  protected void prepareView() {
    ActionBar var1 = this.getSupportActionBar();
    if(var1 != null) {
      var1.setHomeButtonEnabled(true);
      var1.setDisplayHomeAsUpEnabled(true);
    }

    this.setTitle(this.getExtraOrDefault("title", this.getString(R.string.blog_title)));
    ProgressBar var3 = (ProgressBar)this.findViewById(R.id.webViewProgressBar);
    String var5 = this.getString(R.string.app_lang);
    String var4 = this.getExtraOrDefault("url", String.format(this.getString(R.string.web_link_blog), new Object[]{var5}));
    this.webView = (WebView)this.findViewById(R.id.webViewBlog);
    this.webView.getSettings().setJavaScriptEnabled(true);
    String var2 = this.webView.getSettings().getUserAgentString();
    var5 = var2;
    if(!var2.toLowerCase().contains("mobile")) {
      var5 = var2 + " Mobile";
    }

    this.webView.getSettings().setUserAgentString(var5);
    this.webView.setWebViewClient(new WebViewClientCustom(this, var3));
    this.webView.loadUrl(var4);
  }
}