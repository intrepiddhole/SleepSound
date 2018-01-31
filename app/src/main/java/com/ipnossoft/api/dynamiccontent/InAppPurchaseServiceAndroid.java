package com.ipnossoft.api.dynamiccontent;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import com.ipnossoft.api.dynamiccontent.model.DynamicContentTag;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.httputils.*;
import java.net.MalformedURLException;
import java.util.*;

//cavaj
public class InAppPurchaseServiceAndroid extends Observable
        implements InAppPurchaseService
{

  private static String LogTag = "InAppPurchase Service";
  final HttpServiceApi api;
  List availablePurchases;
  InAppPurchaseServiceConfig config;
  private InAppPurchaseAndroidDownloaderService downloader;
  private int retries;

  public InAppPurchaseServiceAndroid(InAppPurchaseServiceConfig inapppurchaseserviceconfig)
  {
    availablePurchases = new ArrayList();
    retries = 0;
    config = inapppurchaseserviceconfig;
    api = new HttpServiceApi(inapppurchaseserviceconfig);
  }

  private void addTagsToDynamicContent(List list_1, List list1_1)
  {
    HashMap hashmap = new HashMap();
    DynamicContentTag dynamiccontenttag;
    for(Iterator list = list_1.iterator(); list.hasNext(); hashmap.put(dynamiccontenttag.getIdentifier(), dynamiccontenttag))
    {
      dynamiccontenttag = (DynamicContentTag)list.next();
    }

    ArrayList arraylist;
    InAppPurchase list1;
    label0:
    for(Iterator list = list1_1.iterator(); list.hasNext(); list1.setTagObjects(arraylist))
    {
      list1 = (InAppPurchase)list.next();
      arraylist = new ArrayList();
      Iterator iterator = list1.getTags().iterator();
      do
      {
        if(!iterator.hasNext())
        {
          continue label0;
        }
        DynamicContentTag dynamiccontenttag1 = (DynamicContentTag)hashmap.get((String)iterator.next());
        if(dynamiccontenttag1 != null)
        {
          arraylist.add(dynamiccontenttag1);
        }
      } while(true);
    }

  }

  private AsyncTask fetchAvailableInAppPurchasesAsyncTask(final Observer observer, final InAppPurchaseServiceAndroid service)
  {
    return new AsyncTask() {
      protected Object doInBackground(Object aobj[])
      {
        return doInBackground((String[])aobj);
      }

      protected List doInBackground(String as_1[])
      {
        String as = as_1[0];
        try
        {
          List<InAppPurchase> as_2 = api.getResourceList(as, InAppPurchase.class);
          List list = api.getResourceList(getTagsURL(), DynamicContentTag.class);
          addTagsToDynamicContent(list, as_2);
          Collections.sort(as_2, new Comparator() {
            public int compare(InAppPurchase inapppurchase, InAppPurchase inapppurchase1)
            {
              if(inapppurchase.getOrder() == inapppurchase1.getOrder())
              {
                return 0;
              }
              return inapppurchase.getOrder() >= inapppurchase1.getOrder() ? 1 : -1;
            }

            public int compare(Object obj, Object obj1)
            {
              return compare((InAppPurchase)obj, (InAppPurchase)obj1);
            }

          });
          return as_2;
        }
        catch(Exception e)
        {
          Log.e(InAppPurchaseServiceAndroid.LogTag, "", e);
          return null;
        }
      }

      protected void onPostExecute(Object obj)
      {
        onPostExecute((List)obj);
      }

      protected void onPostExecute(List list)
      {
        boolean flag = false;
        StringBuilder stringbuilder = (new StringBuilder()).append("Post Executing. Affecting ");
        int i;
        if(list != null)
        {
          i = list.size();
        } else
        {
          i = 0;
        }
        stringbuilder = stringbuilder.append(i).append(" dynamic content. Previous size was ");
        if(availablePurchases != null)
        {
          i = availablePurchases.size();
        } else
        {
          i = 0;
        }
        Log.d("DynamicContent", stringbuilder.append(i).toString());
        if(list != null)
        {
          availablePurchases = list;
        } else
        {
          Log.i("DynamicContent", (new StringBuilder()).append("Retrying fetching dynamic content in 1 second (Retry ").append(retries).append(")").toString());
          retryFetchingAvailableInAppPurchase(observer);
        }
        if(observer != null)
        {
          Observer observer1 = observer;
          InAppPurchaseServiceAndroid inapppurchaseserviceandroid = service;
          if(list != null)
          {
            flag = true;
          }
          observer1.update(inapppurchaseserviceandroid, Boolean.valueOf(flag));
        }
      }
    };
  }

  private String getTagsURL()
          throws MalformedURLException
  {
    return URLUtils.combineParams((new StringBuilder()).append(URLUtils.combine(new String[] {
            api.getConfiguration().getApiBaseUrl(), "inapppurchasetag"
    })).append("/").toString(), new String[] {
            (new StringBuilder()).append("app__code=").append(config.getAppId()).toString()
    });
  }

  private void retryFetchingAvailableInAppPurchase(final Observer observer)
  {
    retries = retries + 1;
    if(retries < 3)
    {
      (new Handler()).postDelayed(new Runnable() {
        public void run()
        {
          fetchAvailableInAppPurchases(observer);
        }
      }, 1L);
    }
  }

  private void startDownload(String s, InAppPurchase inapppurchase, InAppPurchaseDownloadProgressTracker inapppurchasedownloadprogresstracker)
          throws MalformedURLException
  {
    if(downloader == null)
    {
      downloader = new InAppPurchaseAndroidDownloaderService(config.getContext(), config.getAuthorizationHeaderValue(), config.getAppVersion(), config.getPurchasePassword());
    }
    String s1 = config.getDownloadURL(inapppurchase.getIdentifier());
    downloader.setProgressTracker(inapppurchasedownloadprogresstracker);
    downloader.startDownload(s1, inapppurchase, s);
  }

  private void tryFetchAvailableInAppPurchases(Observer observer)
          throws Exception
  {
    fetchAvailableInAppPurchasesAsyncTask(observer, this).execute(new String[] {
            config.getResourceBaseUrl("inapppurchase")
    });
  }

  public List availableInAppPurchases()
  {
    return availablePurchases;
  }

  public void cancelDownload()
  {
    downloader.cancelDownload();
  }

  public boolean didFetchAvailableInAppPurchases()
  {
    return availablePurchases != null && !availablePurchases.isEmpty();
  }

  public void downloadInAppPurchase(String s_1, String s1, InAppPurchaseDownloadProgressTracker inapppurchasedownloadprogresstracker)
  {
    InAppPurchase s = inAppPurchaseWithIdentifier(s_1);
    if(s == null)
    {
      try
      {
        throw new InAppPurchaseNotFoundException();
      }
      catch(Exception e)
      {
        inapppurchasedownloadprogresstracker.downloadFailed(null, e);
      }
      return;
    }
    try {
      startDownload(s1, s, inapppurchasedownloadprogresstracker);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public void fetchAvailableInAppPurchases()
  {
    fetchAvailableInAppPurchases(null);
  }

  public void fetchAvailableInAppPurchases(Observer observer)
  {
    try
    {
      tryFetchAvailableInAppPurchases(observer);
      return;
    }
    catch(Exception e)
    {
      Log.e(LogTag, "Failed fetching available in app purchases", e);
    }
  }

  public InAppPurchase inAppPurchaseWithIdentifier(String s)
  {
    if(!didFetchAvailableInAppPurchases())
    {
      return null;
    }
    for(Iterator iterator = availablePurchases.iterator(); iterator.hasNext();)
    {
      InAppPurchase inapppurchase = (InAppPurchase)iterator.next();
      if(inapppurchase.getIdentifier().equalsIgnoreCase(s))
      {
        return inapppurchase;
      }
    }

    return null;
  }






}
