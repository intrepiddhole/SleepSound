package ipnossoft.rma.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.ipnossoft.api.soundlibrary.Sound;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.free.R;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


//cavaj
public class Utils
{

  private static final String PREF_KEY_APP_CURRENT_VERSION = "app_current_version";
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final AtomicInteger nextId = new AtomicInteger(1);
  private static List uniqueRelaxDialogMessages = new ArrayList();

  public Utils()
  {
  }

  public static void alert(Context context, String s, String s1)
  {
    ipnossoft.rma.ui.dialog.RelaxDialog.Builder builder = new ipnossoft.rma.ui.dialog.RelaxDialog.Builder(context, ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation.VERTICAL);
    String context_1 = s;
    if(s == null)
    {
      context_1 = "Alert";
    }
    builder.setTitle(context_1);
    builder.setMessage(s1);
    builder.setPositiveButton(R.string.error_dialog_button_ok, null);
    builder.show();
  }

  public static void executeTask(AsyncTask asynctask, Object aobj[])
  {
    if(android.os.Build.VERSION.SDK_INT >= 13)
    {
      asynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, aobj);
      return;
    } else
    {
      asynctask.execute(aobj);
      return;
    }
  }

  public static int generateUniqueViewId()
  {
    if(android.os.Build.VERSION.SDK_INT >= 17)
    {
      return View.generateViewId();
    }
    int i;
    int k;
    do
    {
      k = nextId.get();
      int j = k + 1;
      i = j;
      if(j > 0xffffff)
      {
        i = 1;
      }
    } while(!nextId.compareAndSet(k, i));
    return k;
  }

  public static String getCurrentLanguageLocale(Context context)
  {
    return context.getResources().getString(R.string.app_lang);
  }

  public static PackageInfo getPackageInfo(Context context)
  {
    try
    {
      PackageInfo context_1 = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      return context_1;
    }
    // Misplaced declaration of an exception variable
    catch(Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  public static int getResourceId(Context context, String s, String s1)
  {
    return context.getResources().getIdentifier((new StringBuilder()).append(context.getPackageName()).append(":").append(s1).append("/").append(s).toString(), null, null);
  }

  public static boolean isMainThread()
  {
    return Looper.myLooper() == Looper.getMainLooper();
  }

  public static boolean isNewVersion(Context context)
  {
    return PersistedDataManager.getInteger("app_current_version", -1, context) > getPackageInfo(context).versionCode;
  }

  public static List jsonFileToObjectList(File file, Class class1)
  {
    Object obj = null;
    List file_1;
    try
    {
      file_1 = (List)mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(java.util.List.class, class1));
    }
    catch(Exception e)
    {
      Log.e("Utils", "", e);
      file_1 = null;
    }
    if(file_1 == null)
    {
      file_1 = new ArrayList();
    }
    return file_1;
  }

  public static Object jsonToObject(String s, Class class1)
  {
    try
    {
      try {
        s = ((String) (mapper.readValue(s, class1)));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    catch(Exception e)
    {
      Log.e("Utils", "", e);
      return null;
    }
    return s;
  }

  public static List jsonToObjectList(String s, Class class1)
  {
    List s_1;
    try
    {
      s_1 = (List)mapper.readValue(s, mapper.getTypeFactory().constructCollectionType(List.class, class1));
    }
    catch(Exception e)
    {
      Log.e("Utils", "", e);
      s_1 = null;
    }
    if(s_1 == null)
    {
      s_1 = new ArrayList();
    }
    return s_1;
  }

  public static String objectToJson(Object obj)
  {
    try
    {
      obj = mapper.writeValueAsString(obj);
    }
    catch(Throwable t)
    {
      Log.e("Utils", "", t);
      return null;
    }
    return ((String) (obj));
  }

  public static void setSoundImage(Sound sound, ImageView imageview)
  {
    setSoundImage(sound, imageview, 0);
  }

  private static void setSoundImage(Sound sound, ImageView imageview, int i)
  {
    if(imageview == null)
    {
      Log.e("RMA", "imageView is null in Utils.setSoundImage()");
    } else
    {
      if(sound == null)
      {
        Log.e("RMA", "sound is null in Utils.setSoundImage()");
        return;
      }
      if(sound.getImageResourceId() != 0)
      {
        imageview.setImageResource(sound.getImageResourceId());
        return;
      }
    }
  }

  public static void uniqueAlert(Context context, final String title)
  {
    if(!uniqueRelaxDialogMessages.contains(title))
    {
      uniqueRelaxDialogMessages.add(title);
      ipnossoft.rma.ui.dialog.RelaxDialog.Builder builder = new ipnossoft.rma.ui.dialog.RelaxDialog.Builder(context, ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation.VERTICAL);
      String context_1;
      if(title == null)
      {
        context_1 = "Alert";
      } else
      {
        context_1 = title;
      }
      builder.setTitle(context_1);
      builder.setPositiveButton(R.string.error_dialog_button_ok, new android.view.View.OnClickListener(){
        public void onClick(View view)
        {
          Utils.uniqueRelaxDialogMessages.remove(title);
        }
      });
      builder.setOnDismissListener(new android.content.DialogInterface.OnDismissListener(){
        public void onDismiss(DialogInterface dialoginterface)
        {
          Utils.uniqueRelaxDialogMessages.remove(title);
        }
      });
      builder.show();
    }
  }

  public static void uniqueAlert(Context context, String s, final String message)
  {
    if(!uniqueRelaxDialogMessages.contains(message))
    {
      uniqueRelaxDialogMessages.add(message);
      ipnossoft.rma.ui.dialog.RelaxDialog.Builder builder = new ipnossoft.rma.ui.dialog.RelaxDialog.Builder(context, ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation.VERTICAL);
      String context_1 = s;
      if(s == null)
      {
        context_1 = "Alert";
      }
      builder.setTitle(context_1);
      builder.setMessage(message);
      builder.setPositiveButton(R.string.error_dialog_button_ok, new android.view.View.OnClickListener(){
        public void onClick(View view)
        {
          Utils.uniqueRelaxDialogMessages.remove(message);
        }
      });
      builder.setOnDismissListener(new android.content.DialogInterface.OnDismissListener(){
        public void onDismiss(DialogInterface dialoginterface)
        {
          Utils.uniqueRelaxDialogMessages.remove(message);
        }
      });
      builder.show();
    }
  }

  public static void writeObjectListToJsonFile(File file, Collection collection, Class class1)
  {
    class1 = ((Class) (Array.newInstance(class1, collection.size())));
    Iterator collection_1 = collection.iterator();
    int i = 0;
    while(true){
      if(!collection_1.hasNext())
      {
        break; /* Loop/switch isn't completed */
      }
      Array.set(class1, i, collection_1.next());
      i++;
    }
    try
    {
      mapper.writeValue(file, class1);
      return;
    }
    catch(Exception e)
    {
      Log.e("Utils", "", e);
    }
    return;
  }

}
