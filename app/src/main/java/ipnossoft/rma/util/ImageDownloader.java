package ipnossoft.rma.util;

import android.content.Context;
import android.graphics.Bitmap;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import java.io.*;

public class ImageDownloader
{
  public static interface ImageDownloadCallback
  {

    public abstract void onError(Exception exception);

    public abstract void onSuccess();
  }


  public ImageDownloader()
  {
  }

  public static void downloadImage(Context context, final String s, String s1, final android.graphics.Bitmap.CompressFormat compressformat, final ImageDownloadCallback imagedownloadcallback)
  {
    ImageRequest s_1 = new ImageRequest(s, new com.android.volley.Response.Listener() {

      public void onResponse(Bitmap bitmap)
      {
        Object obj;
        Object obj1;
        Object obj2;
        obj1 = null;
        obj2 = null;
        obj = obj1;
        Object obj3 = s.substring(0, s.lastIndexOf("/") + 1);
        obj = obj1;
        String s2 = s.substring(s.lastIndexOf("/") + 1, s.length());
        obj = obj1;
        obj3 = new File(((String) (obj3)));
        obj = obj1;
        ((File) (obj3)).mkdirs();
        obj = obj1;
        try {
          FileOutputStream obj1_1 = new FileOutputStream(new File(((File) (obj3)), s2));
          bitmap.compress(compressformat, 100, obj1_1);
          imagedownloadcallback.onSuccess();
          if (obj1_1 != null)
            obj1_1.close();
        }catch (Exception e) {
          imagedownloadcallback.onError(e);
        }
      }

      public void onResponse(Object obj)
      {
        onResponse((Bitmap)obj);
      }
    }, 0, 0, android.widget.ImageView.ScaleType.CENTER, android.graphics.Bitmap.Config.ARGB_8888, new com.android.volley.Response.ErrorListener() {

      public void onErrorResponse(VolleyError volleyerror)
      {
      }

    });
    Volley.newRequestQueue(context).add(s_1);
  }
}
