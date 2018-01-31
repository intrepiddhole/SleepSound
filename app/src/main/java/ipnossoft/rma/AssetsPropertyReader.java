package ipnossoft.rma;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.*;
import java.net.URL;
import java.util.Properties;


//cavaj
class AssetsPropertyReader
{

  AssetsPropertyReader()
  {
  }

  public static Properties getPropertiesFromFileName(String s, Context context)
  {
    Properties properties = new Properties();
    try
    {
      properties.load(context.getAssets().open(s));
      Log.d(AssetsPropertyReader.class.getSimpleName(), (new StringBuilder()).append("Properties file loaded: ").append(s).toString());
    }
    catch(Exception e)
    {
      Log.d(AssetsPropertyReader.class.getSimpleName(), (new StringBuilder()).append("No properties file founds matching filename: ").append(s).toString());
      return properties;
    }
    return properties;
  }

  public static Properties getPropertiesFromFilePath(String s)
  {
    Properties properties = new Properties();
    try
    {
      properties.load(new FileInputStream(s));
      Log.d(AssetsPropertyReader.class.getSimpleName(), (new StringBuilder()).append("Properties file loaded: ").append(s).toString());
    }
    catch(IOException ioexception)
    {
      Log.d(AssetsPropertyReader.class.getSimpleName(), (new StringBuilder()).append("No properties file founds matching file path: ").append(s).toString());
      return properties;
    }
    return properties;
  }

  public static Properties getPropertiesFromRemoteURL(String s)
  {
    Properties properties = new Properties();
    try {
      InputStreamReader inputstreamreader = new InputStreamReader((new URL(s)).openStream(), "UTF-8");
      properties.load(inputstreamreader);
      Log.d(AssetsPropertyReader.class.getSimpleName(), (new StringBuilder()).append("Properties file loaded from URL: ").append(s).toString());
      inputstreamreader.close();
      return properties;
    }catch (Exception exception){
      try
      {
        Log.d(AssetsPropertyReader.class.getSimpleName(), (new StringBuilder()).append("Properties file loaded from URL: ").append(s).toString());
        throw exception;
      }
      catch(IOException ioexception)
      {
        Log.d(AssetsPropertyReader.class.getSimpleName(), (new StringBuilder()).append("No properties file founds at URL: ").append(s).toString());
      }
    }

    return properties;
  }
}
