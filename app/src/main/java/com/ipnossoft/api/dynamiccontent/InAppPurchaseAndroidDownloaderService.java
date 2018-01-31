package com.ipnossoft.api.dynamiccontent;

import android.content.Context;
import android.os.*;
import android.util.Log;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.builder.LoadBuilder;
import com.koushikdutta.ion.future.ResponseFuture;
import java.io.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

// cavaj
public class InAppPurchaseAndroidDownloaderService
{

  private static final String TAG = "Downloader";
  private final String appVersion;
  private final String authorizationHeaderValue;
  private final Context context;
  private Future currentDownload;
  private InAppPurchase currentFeature;
  private InAppPurchaseDownloadProgressTracker progressTracker;
  private Handler uiHandler;
  private final String zipPassword;

  public InAppPurchaseAndroidDownloaderService(Context context1, String s, String s1, String s2)
  {
    uiHandler = new Handler(Looper.getMainLooper());
    context = context1;
    authorizationHeaderValue = s;
    appVersion = s1;
    zipPassword = s2;
  }

  private double calculateProgress(int i, int j)
  {
    return ((double)i / (double)j) * 100D;
  }

  private ZipFile configureZipFile(ZipFile zipfile, String s)
          throws ZipException
  {
    if(zipfile.isEncrypted())
    {
      zipfile.setPassword(s);
    }
    return zipfile;
  }

  private void createMissingDirectoriesInPath(String s)
  {
    File s_1 = new File(s);
    if(!s_1.mkdirs())
    {
      Log.v("Downloader", (new StringBuilder()).append("Did not create directory ").append(s_1.getPath()).toString());
    }
  }

  private File createZipFile()
  {
    File file = context.getCacheDir();
    try
    {
      file = File.createTempFile("temp", ".zip", file);
    }
    catch(IOException ioexception)
    {
      notifyDownloadFailed(ioexception);
      return null;
    }
    return file;
  }

  private ZipFile createZipFile(File file)
          throws ZipException
  {
    return new ZipFile(file.getPath());
  }

  private void extractDownloadedZip(final File zipFile, final String extractionPath)
  {
    notifyExtractingZip();
    AsyncTask.execute(new Runnable() {
      public void run()
      {
        try
        {
          String as[] = extractZipFile(zipFile, zipPassword, extractionPath);
          removeZipFile(zipFile);
          notifyDownloadComplete(as);
          return;
        }
        catch(Exception exception)
        {
          notifyDownloadFailed(exception);
        }
      }
    });
  }

  private void extractFileFromZipStream(ZipFile zipfile, FileHeader fileheader, String s)
          throws Exception
  {
    ZipFile zipfile1;
    Object obj;
    byte abyte0[];
    obj = null;
    abyte0 = new byte[4096];
    ZipInputStream zipfile_1 = zipfile.getInputStream(fileheader);
    FileOutputStream fileheader_1 = new FileOutputStream(s);
    try {
      while (true) {
        int i = zipfile_1.read(abyte0);
        if (i <= 0) {
          tryCloseStream(fileheader_1);
          tryCloseStream(zipfile_1);
          return;
        }
        fileheader_1.write(abyte0, 0, i);
      }
    } catch (Exception e) {
      tryCloseStream(fileheader_1);
      tryCloseStream(zipfile_1);
      throw e;
    }
  }

  private String[] extractZipFile(File file_1, String s_1, String s1)
          throws Exception
  {
    int i = 0;
    ArrayList arraylist = new ArrayList();
    ZipFile file = configureZipFile(createZipFile(file_1), s_1);
    List s = file.getFileHeaders();
    Iterator iterator = s.iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      FileHeader fileheader = (FileHeader)iterator.next();
      if(fileheader != null)
      {
        String s2 = (new StringBuilder()).append(context.getFilesDir().getPath()).append("/").append(s1).append("/").append(fileheader.getFileName()).toString();
        Log.d("dynamic-content-service", (new StringBuilder()).append("Extracting: ").append(s2).toString());
        if(fileheader.isDirectory())
        {
          createMissingDirectoriesInPath(s2);
        } else
        {
          createMissingDirectoriesInPath((new StringBuilder()).append(context.getFilesDir().getPath()).append("/").append(s1).toString());
          extractFileFromZipStream(file, fileheader, s2);
          arraylist.add(s2);
          i = (int)((double)i + 1.0D);
          notifyExtractingProgressChanged(calculateProgress(i, s.size()));
        }
      }
    } while(true);
    return (String[])arraylist.toArray(new String[arraylist.size()]);
  }

  private void notifyDownloadCanceled()
  {
    uiHandler.post(new Runnable() {
      public void run()
      {
        progressTracker.downloadCancelled(currentFeature);
      }
    });
  }

  private void notifyDownloadComplete(final String files[])
  {
    uiHandler.post(new Runnable() {
      public void run()
      {
        progressTracker.downloadDone(currentFeature, files);
      }
    });
  }

  private void notifyDownloadFailed(final Exception e)
  {
    uiHandler.post(new Runnable() {
      public void run()
      {
        progressTracker.downloadFailed(currentFeature, e);
      }
    });
  }

  private void notifyDownloadStarted()
  {
    uiHandler.post(new Runnable() {
      public void run()
      {
        progressTracker.downloadProgressChanged(currentFeature, 0.0D, DownloadStages.CONNECTING);
      }
    });
  }

  private void notifyDownloadingProgressChanged(final double progress)
  {
    uiHandler.post(new Runnable() {
      public void run()
      {
        progressTracker.downloadProgressChanged(currentFeature, progress, DownloadStages.DOWNLOADING_ZIP);
      }
    });
  }

  private void notifyExtractingProgressChanged(final double progress)
  {
    uiHandler.post(new Runnable() {
      public void run()
      {
        progressTracker.downloadProgressChanged(currentFeature, progress, DownloadStages.EXTRACTING_ZIP);
      }
    });
  }

  private void notifyExtractingZip()
  {
    uiHandler.post(new Runnable() {
      public void run()
      {
        progressTracker.downloadProgressChanged(currentFeature, 0.0D, DownloadStages.EXTRACTING_ZIP);
      }
    });
  }

  private void removeZipFile(File file)
  {
    try
    {
      if(!file.delete())
      {
        Log.d("Downloader", "Failed to delete temporary Zip file.");
      }
      return;
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void tryCloseStream(Closeable closeable)
  {
    try {
      if (closeable != null)
        closeable.close();
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public void cancelDownload()
  {
    if(currentDownload != null)
    {
      currentDownload.cancel(true);
    }
    notifyDownloadCanceled();
  }

  public void setProgressTracker(InAppPurchaseDownloadProgressTracker inapppurchasedownloadprogresstracker)
  {
    progressTracker = inapppurchasedownloadprogresstracker;
  }

  public void startDownload(String s, final InAppPurchase zipFile_1, final String extractionPath)
  {
    notifyDownloadStarted();
    currentFeature = zipFile_1;
    s = (new StringBuilder()).append(s).append("?app_version=").append(appVersion).toString();
    final File zipFile = createZipFile();
    if(zipFile != null)
    {
      currentDownload = ((com.koushikdutta.ion.builder.Builders.Any.B)((com.koushikdutta.ion.builder.Builders.Any.B)((com.koushikdutta.ion.builder.Builders.Any.B)Ion.with(context).load(s)).addHeader("Authorization", authorizationHeaderValue)).progress(new ProgressCallback() {
        public void onProgress(long l, long l1)
        {
          notifyDownloadingProgressChanged(((float)l / (float)l1) * 100F);
        }
      })).write(zipFile).setCallback(new FutureCallback() {
        public void onCompleted(Exception exception, File file)
        {
          if(exception != null)
          {
            if(exception instanceof CancellationException)
            {
              return;
            } else
            {
              notifyDownloadFailed(new Exception(exception));
              return;
            }
          } else
          {
            extractDownloadedZip(zipFile, extractionPath);
            return;
          }
        }

        public void onCompleted(Exception exception, Object obj)
        {
          onCompleted(exception, (File)obj);
        }
      });
    }
  }
}
