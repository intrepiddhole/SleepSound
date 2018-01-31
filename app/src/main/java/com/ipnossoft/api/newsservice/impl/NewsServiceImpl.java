package com.ipnossoft.api.newsservice.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipnossoft.api.httputils.HttpServiceApi;
import com.ipnossoft.api.httputils.ServiceConfig;
import com.ipnossoft.api.httputils.URLUtils;
import com.ipnossoft.api.newsservice.NewsService;
import com.ipnossoft.api.newsservice.NewsServiceListener;
import com.ipnossoft.api.newsservice.exceptions.NewsFetchFailedException;
import com.ipnossoft.api.newsservice.model.News;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.*;
//cavaj
public class NewsServiceImpl
        implements NewsService
{

  private static final String NEWS_SERVICE_PREFERENCES = "NewsServicePreferences";
  private static final String NEWS_SERVICE_PREFERENCES_LATEST_DATE = "NewsServiceLatestDate";
  private HttpServiceApi api;
  private final Context applicationContext;
  private ServiceConfig config;
  private boolean finishedFetching;
  private ArrayList listeners;
  private ObjectMapper mapper;
  private List newsList;

  public NewsServiceImpl(String s, String s1, String s2, String s3, Context context)
  {
    listeners = new ArrayList();
    mapper = new ObjectMapper();
    config = new ServiceConfig(s, s1, s2, s3);
    api = new HttpServiceApi(config);
    applicationContext = context;
  }

  private News convertJSONObjectToNews(JSONObject jsonobject)
  {
    News news = null;
    News news1;
    try
    {
      news1 = (News)mapper.readValue(jsonobject.toString(), News.class);
      news = news1;
      news1.setTitle(extractLocalizedValue(jsonobject.optJSONObject("title")));
      news = news1;
      news1.setMessage(extractLocalizedValue(jsonobject.optJSONObject("message")));
      news = news1;
      news1.setImage(config.getServiceUrl().concat(jsonobject.optString("image")));
      return news1;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return news;
    }
  }

  private String extractLocalizedValue(JSONObject jsonobject)
  {
    String s = jsonobject.optString(Locale.getDefault().getLanguage());
    if(s != null && !s.isEmpty())
    {
      return s;
    } else
    {
      return jsonobject.optString("en");
    }
  }

  private AsyncTask fetchNewsAsyncTask(final Observer observer, NewsServiceImpl newsserviceimpl)
  {
    return new AsyncTask() {

      protected Object doInBackground(Object aobj[])
      {
        return doInBackground((String[])aobj);
      }

      protected List doInBackground(String as[])
      {
        try
        {
          String as_1 = URLUtils.combine(new String[] {config.getServiceUrl(), "api", "v1", "news", "/", (new StringBuilder()).append("?app__code=").append(config.getAppId()).toString()});
          HttpGet as_2 = api.buildGetRequest(as_1);
          HttpResponse as_3 = api.executeRequest(as_2);
          if(as_3 == null)
          {
            return new ArrayList();
          }
          String s;
          int i;
          s = EntityUtils.toString(as_3.getEntity(), "UTF-8");
          i = as_3.getStatusLine().getStatusCode();
          if(i != 200)
          {
            return new ArrayList();
          }
          List as_4 = parseNewsResponse(as_1);
          return as_4;
        }
        catch(Exception e)
        {
          Log.e("NewsServiceHTTP", "Failed executing request to news service API", e);
        }
        return new ArrayList();
      }

      protected void onPostExecute(Object obj)
      {
        onPostExecute((List)obj);
      }

      protected void onPostExecute(List list)
      {
        newsList = list;
        finishedFetching = true;
        observer.update(null, list);
      }
    };
  }

  private SharedPreferences getNewsServicePreferences()
  {
    return applicationContext.getSharedPreferences("NewsServicePreferences", 0);
  }

  private boolean isNewsOlderThanOneMonth(long l)
  {
    return Calendar.getInstance().getTimeInMillis() - l >= oneMonthInMillis();
  }

  private long oneMonthInMillis()
  {
    return TimeUnit.MILLISECONDS.convert(1L, TimeUnit.DAYS) * 30L;
  }

  private List parseNewsResponse(String s)
          throws JSONException
  {
    JSONArray s_1 = (new JSONObject(s)).optJSONArray("objects");
    ArrayList arraylist = new ArrayList();
    if(s_1 != null)
    {
      int i = 0;
      while(i < s_1.length())
      {
        News news = convertJSONObjectToNews(s_1.getJSONObject(i));
        if(news != null)
        {
          arraylist.add(news);
        } else
        {
          Log.e("NewsServiceJSON", "Failed converting news JSON object to news Object");
        }
        i++;
      }
    }
    return arraylist;
  }

  public void addListener(NewsServiceListener newsservicelistener)
  {
    listeners.add(newsservicelistener);
  }

  public void fetchNews()
  {
    fetchNewsAsyncTask(new Observer() {
      @Override
      public void update(Observable observable, Object obj)
      {
        if(!listeners.isEmpty())
        {
          List observable_1 = (List)obj;
          if(observable == null)
          {
            for(Iterator observable_2 = listeners.iterator(); observable_2.hasNext(); ((NewsServiceListener)observable_2.next()).newsServiceDidFailFetchingNews(new NewsFetchFailedException("Could not fetch news from API"))) { }
          } else
          {
            for(obj = listeners.iterator(); ((Iterator) (obj)).hasNext(); ((NewsServiceListener)((Iterator) (obj)).next()).newsServiceDidFinishFetchingNews(observable_1)) { }
          }
        }
      }
    }, this).execute(new String[0]);
  }

  public void flagNewsAsRead()
  {
    if(isFinishedFetchingNews())
    {
      long l = 0L;
      Object obj = newsList.iterator();
      do
      {
        if(!((Iterator) (obj)).hasNext())
        {
          break;
        }
        long l1 = ((News)((Iterator) (obj)).next()).getDate().getTime();
        if(l < l1)
        {
          l = l1;
        }
      } while(true);
      obj = getNewsServicePreferences().edit();
      ((android.content.SharedPreferences.Editor) (obj)).putLong("NewsServiceLatestDate", l);
      ((android.content.SharedPreferences.Editor) (obj)).apply();
      return;
    } else
    {
      Log.e("NewsServiceRead", "Tried to flag news as read before news finish fetching");
      return;
    }
  }

  public List getNews()
  {
    return newsList;
  }

  public boolean isFinishedFetchingNews()
  {
    return finishedFetching;
  }

  public void removeListener(NewsServiceListener newsservicelistener)
  {
    listeners.remove(newsservicelistener);
  }

  public int unreadCount()
  {
    int i = 0;
    if(isFinishedFetchingNews())
    {
      long l = getNewsServicePreferences().getLong("NewsServiceLatestDate", 0L);
      Iterator iterator = newsList.iterator();
      do
      {
        if(!iterator.hasNext())
        {
          break;
        }
        long l1 = ((News)iterator.next()).getDate().getTime();
        if(!isNewsOlderThanOneMonth(l1) && l1 > l)
        {
          i++;
        }
      } while(true);
    } else
    {
      Log.e("NewsServiceUnread", "Tried to get unreadCount before news finish fetching");
      return -1;
    }
    return i;
  }






/*
    static List access$402(NewsServiceImpl newsserviceimpl, List list)
    {
        newsserviceimpl.newsList = list;
        return list;
    }

*/


/*
    static boolean access$502(NewsServiceImpl newsserviceimpl, boolean flag)
    {
        newsserviceimpl.finishedFetching = flag;
        return flag;
    }

*/
}
