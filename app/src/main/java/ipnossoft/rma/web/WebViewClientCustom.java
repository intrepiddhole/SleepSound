package ipnossoft.rma.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

class WebViewClientCustom extends WebViewClient {
  private final Context context;
  private final ProgressBar progress;

  public WebViewClientCustom(Context var1, ProgressBar var2) {
    this.context = var1;
    this.progress = var2;
  }

  public void onPageFinished(WebView var1, String var2) {
    super.onPageFinished(var1, var2);
    this.progress.setVisibility(View.GONE);
  }

  public void onPageStarted(WebView var1, String var2, Bitmap var3) {
    super.onPageStarted(var1, var2, var3);
    this.progress.setVisibility(View.VISIBLE);
  }

  public boolean shouldOverrideUrlLoading(WebView var1, String var2) {
    if(Uri.parse(var2).getHost() != null && !Uri.parse(var2).getHost().equals("market.android.com") && !var2.contains("facebook.com") && !Uri.parse(var2).getHost().contains("twitter.com") && !Uri.parse(var2).getHost().equals("play.google.com") && !Uri.parse(var2).getHost().contains("bit.ly") && !Uri.parse(var2).getHost().contains("plus.google.com") && !Uri.parse(var2).getHost().contains("youtube.com") && !Uri.parse(var2).getHost().contains("samsungapps") && !Uri.parse(var2).getHost().contains("amazon") && !Uri.parse(var2).getHost().contains("tsto") && !Uri.parse(var2).getScheme().contains("amzn") && !Uri.parse(var2).getScheme().contains("market") && !Uri.parse(var2).getScheme().contains("samsungapps")) {
      return false;
    } else {
      Intent var3 = new Intent("android.intent.action.VIEW", Uri.parse(var2));
      this.context.startActivity(var3);
      return true;
    }
  }
}