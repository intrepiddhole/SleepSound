package com.ipnossoft.api.httputils;

import android.text.TextUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class URLUtils
{

  public URLUtils()
  {
  }

  public static String combine(String as[])
  {
    Object obj = "";
    int i = 0;
    do
    {
      label0:
      {
        String s = ((String) (obj));
        if(i < as.length)
        {
          if(!as[i].equals("/") || i != as.length - 1)
          {
            break label0;
          }
          s = (new StringBuilder()).append(((String) (obj))).append('/').toString();
        }
        return s.replace(":/", "://");
      }
      String s1 = validateUrlPart(as[i]);
      Object obj1 = obj;
      if(!s1.isEmpty())
      {
        obj1 = (new StringBuilder()).append(((String) (obj)));
        obj = s1;
        if(i > 0)
        {
          obj = prependSlashIfNeeded(s1);
        }
        obj1 = ((StringBuilder) (obj1)).append(((String) (obj))).toString();
      }
      i++;
      obj = obj1;
    } while(true);
  }

  public static URL combine(URL aurl[])
          throws MalformedURLException
  {
    String as[] = new String[aurl.length];
    for(int i = 0; i < aurl.length; i++)
    {
      as[i] = aurl[i].toString();
    }

    return new URL(combine(as));
  }

  public static String combineParams(String s, String as[])
  {
    String as1[] = s.split("\\?");
    String s1;
    ArrayList arraylist;
    if(as1.length == 2)
    {
      s1 = as1[1];
    } else
    {
      s1 = "";
    }
    arraylist = new ArrayList();
    if(!TextUtils.isEmpty(s1))
    {
      Collections.addAll(arraylist, s1.split("&"));
    }
    Collections.addAll(arraylist, as);
    String as_1 = TextUtils.join("&", arraylist);
    if(TextUtils.isEmpty(as_1))
    {
      return s;
    } else
    {
      return (new StringBuilder()).append(as1[0]).append("?").append(as).toString();
    }
  }

  public static URL parse(String as[])
          throws MalformedURLException
  {
    return new URL(combine(as));
  }

  private static String prependSlashIfNeeded(String s)
  {
    String s1 = s;
    if(s != null)
    {
      s1 = s;
      if(!s.isEmpty())
      {
        s1 = s;
        if(s.charAt(0) != '/')
        {
          s1 = s;
          if(s.charAt(0) != '?')
          {
            s1 = s;
            if(s.charAt(0) != '&')
            {
              s1 = s;
              if(s.charAt(0) != '#')
              {
                s1 = (new StringBuilder()).append("/").append(s).toString();
              }
            }
          }
        }
      }
    }
    return s1;
  }

  private static String validateUrlPart(String s)
  {
    String s1 = "";
    String as[] = s.replace("\\", "/").split("/");
    if(as.length == 0)
    {
      return "";
    }
    int i = 0;
    s = s1;
    while(i < as.length)
    {
      Object obj = s;
      if(as[i] != null)
      {
        obj = s;
        if(!as[i].isEmpty())
        {
          obj = (new StringBuilder()).append(s);
          if(i > 0)
          {
            s = prependSlashIfNeeded(as[i]);
          } else
          {
            s = as[i];
          }
          obj = ((StringBuilder) (obj)).append(s).toString();
        }
      }
      i++;
      s = ((String) (obj));
    }
    return s;
  }
}
