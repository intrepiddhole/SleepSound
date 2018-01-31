package ipnossoft.rma;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import ipnossoft.rma.util.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class RelaxPropertyHandler {
  public static String RELAX_AD_PRODIVDER = "RELAX_AD_PROVIDER";
  public static String RELAX_APP_CODE = "RELAX_APP_CODE";
  public static String RELAX_SERVER_API_KEY = "RELAX_SERVER_API_KEY";
  public static String RELAX_SERVER_URL = "RELAX_SERVER_URL";
  public static String RELAX_SERVER_USERNAME = "RELAX_SERVER_USERNAME";
  public static String ZENDESK_SUPPORT_URL = "ZENDESK_SUPPORT_URL";
  private static RelaxPropertyHandler instance;
  private Context context;
  private RelaxPropertyReader propertyReader;
  private Properties relaxProperties;
  private String remoteFolderURL;

  public RelaxPropertyHandler() {
  }

  public static RelaxPropertyHandler getInstance() {
    if(instance == null) {
      instance = new RelaxPropertyHandler();
    }

    return instance;
  }

  private void loadLocalProperties(String[] var1) {
    int var3 = var1.length;

    for(int var2 = 0; var2 < var3; ++var2) {
      String var4 = var1[var2];
      this.relaxProperties.putAll(AssetsPropertyReader.getPropertiesFromFileName(var4, this.context));
    }

  }

  private void loadRemoteCacheProperties(String[] var1) {
    int var3 = var1.length;

    for(int var2 = 0; var2 < var3; ++var2) {
      String var4 = var1[var2];
      var4 = this.context.getFilesDir() + "/properties/" + var4;
      this.relaxProperties.putAll(AssetsPropertyReader.getPropertiesFromFilePath(var4));
    }

  }

  private void loadRemoteProperties(String[] var1) {
    long var2 = PersistedDataManager.getLong("propertiesLastRemoteFetch", 0L, this.context);
    long var4 = System.currentTimeMillis();
    if(var4 - var2 > (long)43200000) {
      PersistedDataManager.saveLong("propertiesLastRemoteFetch", var4, this.context);
      this.tryReadingRemotePropertyFilesAsynchronously(var1);
    }

  }

  private void saveRemotePropertiesFile(String var1, Properties var2) {
    try {
      String var3 = this.context.getFilesDir() + "/properties/";
      (new File(var3)).mkdirs();
      var2.store(new FileOutputStream(new File(var3, var1)), (String)null);
    } catch (IOException var4) {
      Log.e(this.getClass().getSimpleName(), "Failed saving remote properties file", var4);
    }
  }

  private void tryReadingRemotePropertyFilesAsynchronously(final String[] var1) {
    Utils.executeTask(new AsyncTask<Void, Void, Void>(){
      protected Void doInBackground(Void... v1) {
        String[] var6 = var1;
        int var5 = var6.length;

        for(int var4 = 0; var4 < var5; ++var4) {
          String var2 = var6[var4];
          Properties var3 = AssetsPropertyReader.getPropertiesFromRemoteURL(remoteFolderURL + var2);
          if(var3.size() > 0) {
            saveRemotePropertiesFile(var2, var3);
            relaxProperties.putAll(var3);
          }
        }

        return null;
      }
    }, new Void[0]);
  }

  public void configureRelaxPropertyReader(Context var1, RelaxPropertyReader var2, String var3) {
    this.relaxProperties = new Properties();
    this.remoteFolderURL = var3;
    this.context = var1;
    this.propertyReader = var2;
    this.getProperties();
  }

  public Properties getProperties() {
    if(this.relaxProperties.size() == 0) {
      this.relaxProperties = new Properties();
      String var1 = this.propertyReader.buildConfigBuildType();
      String var2 = this.propertyReader.relaxConfiguration();
      String var3 = this.propertyReader.buildConfigFlavor();
      String[] var4 = new String[]{"relax.properties", "relax-" + var1 + ".properties", "relax-" + var2 + ".properties", "relax-" + var2 + "-" + var1 + ".properties", "relax-" + var2 + "-" + var3 + ".properties", "relax-" + var2 + "-" + var3 + "-" + var1 + ".properties"};
      this.loadLocalProperties(var4);
      this.loadRemoteCacheProperties(var4);
      this.loadRemoteProperties(var4);
    }

    return this.relaxProperties;
  }
}
