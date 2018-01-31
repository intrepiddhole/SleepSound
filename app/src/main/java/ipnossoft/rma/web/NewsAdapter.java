package ipnossoft.rma.web;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Map;

import ipnossoft.rma.free.R;

class NewsAdapter extends SimpleAdapter {
  private static final String TAG = "NewsAdapter";
  private static final String[] from = new String[]{"title", "message", "date", "imageurl"};
  private static final int[] to;
  private final Context context;

  static {
    to = new int[]{R.id.web_news_item_title, R.id.web_news_item_message, R.id.web_news_item_date, R.id.web_news_item_image};
  }

  public NewsAdapter(Context var1, List<? extends Map<String, ?>> var2) {
    super(var1, var2, R.layout.web_news_item, from, to);
    this.context = var1;
  }

  public void setViewImage(ImageView var1, String var2) {
    Glide.with(this.context).load(var2).into(var1);
  }

  public void setViewText(TextView var1, String var2) {
    if(var1.getId() == R.id.web_news_item_message) {
      var2 = var2.replaceAll("\n", "<br/>");
      var1.setMovementMethod(LinkMovementMethod.getInstance());
      var1.setText(Html.fromHtml(var2));
    } else {
      super.setViewText(var1, var2);
    }
  }
}
