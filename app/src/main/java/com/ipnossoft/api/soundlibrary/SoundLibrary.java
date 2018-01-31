package com.ipnossoft.api.soundlibrary;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.FeatureManagerObserver;
import com.ipnossoft.api.featuremanager.data.Subscription;
import com.ipnossoft.api.soundlibrary.data.PermanentDataStore;
import com.ipnossoft.api.soundlibrary.data.Query;
import com.ipnossoft.api.soundlibrary.loaders.BuiltInContentLoader;
import com.ipnossoft.api.soundlibrary.loaders.DownloadableContentLoader;
import com.ipnossoft.api.soundlibrary.loaders.GiftedContentLoader;
import com.ipnossoft.api.soundlibrary.loaders.LoaderCallback;
import com.ipnossoft.api.soundlibrary.sounds.BinauralSound;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import com.ipnossoft.api.soundlibrary.sounds.IsochronicSound;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import org.onepf.oms.appstore.googleUtils.Purchase;

// Referenced classes of package com.ipnossoft.api.soundlibrary:
//            SoundFactory, SoundLibraryObserver, Sound
//cavaj
public class SoundLibrary extends PermanentDataStore
        implements FeatureManagerObserver
{

  private static final String DEPRECATED_SOUNDS_KEY = "sounds";
  private static final String DOWNLOADABLE_MEDITATIONS = "downloadable_meditations";
  private static final String STORE_NAME = "ipnossoft.sounds.store";
  private static SoundLibrary instance;
  private Context context;
  private ObjectMapper jsonMapper;
  private ConcurrentSkipListSet observers;
  private String savedJson;
  private ConcurrentSkipListMap sounds;

  public SoundLibrary()
  {
    savedJson = null;
  }

  private LoaderCallback buildContentLoaderCallback(final LoaderCallback callback)
  {
    return new LoaderCallback() {

      public void callback(List list){
        Iterator iterator;
        boolean flag;
        flag = false;
        iterator = list.iterator();
//        _L7:
//        if(!iterator.hasNext()) goto _L2; else goto _L1
//        _L1:
//        Sound sound;
//        Sound sound1;
//        sound = (Sound)iterator.next();
//        if(!sounds.containsKey(sound.getId()))
//        {
//          if(addSound(sound, false) && !sound.isBuiltIn())
//          {
//            flag = true;
//          }
//          continue; /* Loop/switch isn't completed */
//        }
//        if(sound.getLabelResourceId() == 0)
//        {
//          continue; /* Loop/switch isn't completed */
//        }
//        sound1 = (Sound)sounds.get(sound.getId());
//        String s = context.getResources().getString(sound.getLabelResourceId());
//        String s1 = sound.getDescription();
//        if(sound1.getName().equals(s) && (sound1.getDescription() == null || s1 == null || sound1.getDescription().equals(s1)))
//        {
//          continue; /* Loop/switch isn't completed */
//        }
//        sound1.setName(s);
//        sound1.setDescription(s1);
//        if(!(sound instanceof BinauralSound)) goto _L4; else goto _L3
//        _L3:
//        ((BinauralSound)sound1).setFrequency(context.getResources().getString(((BinauralSound)sound).getFrequencyId()));
//        _L5:
//        if(!sound.isBuiltIn())
//        {
//          flag = true;
//        }
//        continue; /* Loop/switch isn't completed */
//        _L4:
//        if(sound instanceof IsochronicSound)
//        {
//          ((IsochronicSound)sound1).setFrequency(context.getResources().getString(((IsochronicSound)sound).getFrequencyId()));
//        }
//        if(true) goto _L5; else goto _L2
//        _L2:
//        if(flag)
//        {
//          commit();
//        }
//        if(callback != null)
//        {
//          callback.callback(list);
//        }
//        return;
//        if(true) goto _L7; else goto _L6
//        _L6:
        while(true){
          if(!iterator.hasNext()){
            if(flag)
            {
              commit();
            }
            if(callback != null)
            {
              callback.callback(list);
            }
            return;
          }
          Sound sound;
          Sound sound1;
          sound = (Sound)iterator.next();
          if(!sounds.containsKey(sound.getId()))
          {
            if(addSound(sound, false) && !sound.isBuiltIn())
            {
              flag = true;
            }
            continue; /* Loop/switch isn't completed */
          }
          if(sound.getLabelResourceId() == 0)
          {
            continue; /* Loop/switch isn't completed */
          }
          sound1 = (Sound)sounds.get(sound.getId());
          String s = context.getResources().getString(sound.getLabelResourceId());
          String s1 = sound.getDescription();
          if(sound1.getName().equals(s) && (sound1.getDescription() == null || s1 == null || sound1.getDescription().equals(s1)))
          {
            continue; /* Loop/switch isn't completed */
          }
          sound1.setName(s);
          sound1.setDescription(s1);
          if(!(sound instanceof BinauralSound)){
            if(sound instanceof IsochronicSound)
            {
              ((IsochronicSound)sound1).setFrequency(context.getResources().getString(((IsochronicSound)sound).getFrequencyId()));
            }
          }
          ((BinauralSound)sound1).setFrequency(context.getResources().getString(((BinauralSound)sound).getFrequencyId()));
          if(!sound.isBuiltIn())
          {
            flag = true;
          }
        }
      }
    };
  }

  private LoaderCallback buildGiftedContentLoaderCallback(final LoaderCallback callback)
  {
    return new LoaderCallback() {

      public void callback(List list)
      {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = list.iterator();
        do
        {
          if(!iterator.hasNext())
          {
            break;
          }
          Sound sound = (Sound)iterator.next();
          if(!sounds.containsKey(sound.getId()))
          {
            arraylist.add(sound);
            sounds.put(sound.getId(), sound);
          }
        } while(true);
        if(!arraylist.isEmpty())
        {
          notifyNewSounds(arraylist);
        }
        if(callback != null)
        {
          callback.callback(list);
        }
      }
    };
  }

  private void cleanUpDeprecatedData()
  {
    if(contains("sounds"))
    {
      remove("sounds");
    }
  }

  private List getDownloadableGuidedMeditations()
  {
    return sortGuidedMeditationsByOrder(getSoundsOfType(GuidedMeditationSound.class, Boolean.valueOf(false)));
  }

  public static SoundLibrary getInstance()
  {
    if(instance == null)
    {
      instance = new SoundLibrary();
      instance.initialize();
    }
    return instance;
  }

  private List getSortedIsochronicSounds()
  {
    List list = getSoundsOfType(IsochronicSound.class);
    Collections.sort(list, new Comparator() {
      public int compare(IsochronicSound isochronicsound, IsochronicSound isochronicsound1)
      {
        if(isochronicsound.getOrder() > isochronicsound1.getOrder())
        {
          return 1;
        }
        return isochronicsound.getOrder() >= isochronicsound1.getOrder() ? 0 : -1;
      }

      public int compare(Object obj, Object obj1)
      {
        return compare((IsochronicSound)obj, (IsochronicSound)obj1);
      }
    });
    return list;
  }

  private void initialize()
  {
    sounds = new ConcurrentSkipListMap();
    jsonMapper = new ObjectMapper();
    observers = new ConcurrentSkipListSet(new Comparator() {

      public int compare(SoundLibraryObserver soundlibraryobserver, SoundLibraryObserver soundlibraryobserver1)
      {
        return soundlibraryobserver != soundlibraryobserver1 ? -1 : 0;
      }

      public int compare(Object obj, Object obj1)
      {
        return compare((SoundLibraryObserver)obj, (SoundLibraryObserver)obj1);
      }

    });
    configureFeatureManagerObserver();
  }

  private List jsonToSoundList(String s)
  {
    return SoundFactory.createListFromJson(s);
  }

  private void loadStore()
  {
    Object obj = getString("downloadable_meditations");
    if(obj != null && !((String) (obj)).isEmpty())
    {
      savedJson = ((String) (obj));
      obj = jsonToSoundList(((String) (obj)));
      if(obj != null && ((List) (obj)).size() > 0 && addSounds(((Collection) (obj)), true))
      {
        notifyNewSounds(((List) (obj)));
      }
    }
  }

  private void notifyNewSound(Sound sound)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((SoundLibraryObserver)iterator.next()).onNewSound(sound)) { }
  }

  private void notifyNewSounds(List list)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((SoundLibraryObserver)iterator.next()).onNewSounds(list)) { }
  }

  private void notifySoundUpdated(Sound sound)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((SoundLibraryObserver)iterator.next()).onSoundUpdated(sound)) { }
  }

  private void notifySoundsUpdated(ArrayList arraylist)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((SoundLibraryObserver)iterator.next()).onSoundsUpdated(arraylist)) { }
  }

  private void saveSounds()
          throws Exception
  {
    String s = soundListToJson(getDownloadableGuidedMeditations());
    if(s != null && !s.equals(savedJson))
    {
      cleanUpDeprecatedData();
      putString("downloadable_meditations", s);
      savedJson = s;
    }
  }

  private List sortGuidedMeditationsByOrder(List list)
  {
    Collections.sort(list, new Comparator() {

      public int compare(GuidedMeditationSound guidedmeditationsound, GuidedMeditationSound guidedmeditationsound1)
      {
        int k = 0x7fffffff;
        int i;
        int j;
        if(guidedmeditationsound.getTagOrder() == -1)
        {
          i = 0x7fffffff;
        } else
        {
          i = guidedmeditationsound.getTagOrder();
        }
        if(guidedmeditationsound1.getTagOrder() == -1)
        {
          j = 0x7fffffff;
        } else
        {
          j = guidedmeditationsound1.getTagOrder();
        }
        i = Integer.valueOf(i).compareTo(Integer.valueOf(j));
        if(i != 0)
        {
          return i;
        }
        if(guidedmeditationsound.getOrder() == -1)
        {
          i = 0x7fffffff;
        } else
        {
          i = guidedmeditationsound.getOrder();
        }
        if(guidedmeditationsound1.getOrder() == -1)
        {
          j = k;
        } else
        {
          j = guidedmeditationsound1.getOrder();
        }
        i = Integer.valueOf(i).compareTo(Integer.valueOf(j));
        if(i == 0)
        {
          return Boolean.valueOf(guidedmeditationsound1.isBuiltIn()).compareTo(Boolean.valueOf(guidedmeditationsound.isBuiltIn()));
        } else
        {
          return i;
        }
      }

      public int compare(Object obj, Object obj1)
      {
        return compare((GuidedMeditationSound)obj, (GuidedMeditationSound)obj1);
      }


    });
    return list;
  }

  private String soundListToJson(Collection collection)
          throws JsonProcessingException
  {
    Sound[] collection_1 = (Sound[])(Sound[])collection.toArray(new Sound[collection.size()]);
    return jsonMapper.writeValueAsString(collection_1);
  }

  private void tryLoadStore()
  {
    (new AsyncTask() {
      protected Object doInBackground(Object aobj[])
      {
        try
        {
          loadStore();
        }
        // Misplaced declaration of an exception variable
        catch(Exception e)
        {
          Log.e(getClass().getSimpleName(), "Sound store not loaded!", e);
        }
        return null;
      }
    }).execute(new Object[0]);
  }

  private void updateTagInformationOnBuiltInContent()
  {
    List list = getSoundsOfType(GuidedMeditationSound.class, Boolean.valueOf(false));
    Iterator iterator = getSoundsOfType(GuidedMeditationSound.class, Boolean.valueOf(true)).iterator();
    label0:
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      GuidedMeditationSound guidedmeditationsound = (GuidedMeditationSound)iterator.next();
      Iterator iterator1 = list.iterator();
      GuidedMeditationSound guidedmeditationsound1;
      do
      {
        if(!iterator1.hasNext())
        {
          continue label0;
        }
        guidedmeditationsound1 = (GuidedMeditationSound)iterator1.next();
      } while(guidedmeditationsound1.getTagId() == null || !guidedmeditationsound1.getTagId().equals(guidedmeditationsound.getTagId()));
      guidedmeditationsound.setTagOrder(guidedmeditationsound1.getTagOrder());
      guidedmeditationsound.setTagColor(guidedmeditationsound1.getTagColor());
      guidedmeditationsound.setTag(guidedmeditationsound1.getTag());
      guidedmeditationsound.setTagImageUrl(guidedmeditationsound1.getTagImageUrl());
    } while(true);
  }

  public boolean addSound(Sound sound)
  {
    synchronized (this){
      boolean flag = addSound(sound, true);
      return flag;
    }
  }

  public boolean addSound(Sound sound, boolean flag)
  {
    synchronized (this){
      boolean flag1 = false;
      Sound sound1 = (Sound)sounds.get(sound.getId());
      if(sound1 != null) {
        if(sound1.isNotEqual(sound)) {
          if(flag)
          {
            sounds.put(sound.getId(), sound);
          } else {
            sound1.updateBySound(sound);
            sounds.put(sound1.getId(), sound1);
          }
          flag1 = true;
          notifySoundUpdated(sound);
        }
      } else {
        sounds.put(sound.getId(), sound);
        flag1 = true;
        notifyNewSound(sound);
      }
      return flag1;
    }
  }

  public boolean addSounds(Collection collection, boolean flag)
  {
    synchronized (this){
      boolean flag1 = false;
      Iterator collection_1 = collection.iterator();
      while (true){
        boolean flag2;
        if(!collection_1.hasNext())
        {
          return flag1;
        }
        flag2 = addSound((Sound)collection_1.next(), flag);
        if(flag2)
        {
          flag1 = true;
        }
      }
    }
  }

  public void clear()
  {
    super.clear();
    sounds.clear();
  }

  public void commit()
  {
    synchronized (this){
      try {
        saveSounds();
      }catch (Throwable e) {
        Log.e(getClass().getSimpleName(), "", e);
      }
    }
  }

  public void configureFeatureManagerObserver()
  {
    if(FeatureManager.getInstance() != null)
    {
      FeatureManager.getInstance().registerObserver(this);
    }
  }

  public void configureSoundLibrary(Context context1)
  {
    context = context1;
    configureDataStore(context1, "ipnossoft.sounds.store");
    loadStore();
  }

  public String filePathToSoundId(String s)
  {
    try
    {
      s = Uri.parse(s).getLastPathSegment();
      s = (new StringBuilder()).append("ipnossoft.rma.sounds.").append(s.replace(".ogg", "")).toString();
    }
    // Misplaced declaration of an exception variable
    catch(Exception e)
    {
      return null;
    }
    return s;
  }

  public List getAllSounds()
  {
    ArrayList arraylist = new ArrayList();
    arraylist.addAll(sounds.values());
    return arraylist;
  }

  public List getAmbientSound()
  {
    return getSoundsOfType(Sound.class);
  }

  public List getBinauralSounds()
  {
    return getSortedBinauralSounds();
  }

  public List getBuiltinSounds()
  {
    return querySounds(new Query() {
      public boolean where(Sound sound)
      {
        return sound.isBuiltIn();
      }
    });
  }

  public List getDownloadableSounds()
  {
    return querySounds(new Query() {
      public boolean where(Sound sound)
      {
        return !sound.isBuiltIn();
      }

    });
  }

  public List getGuidedMeditationSounds()
  {
    return sortGuidedMeditationsByOrder(getSoundsOfType(GuidedMeditationSound.class));
  }

  public List getIsochronicSounds()
  {
    return getSortedIsochronicSounds();
  }

  public List getSortedAmbientSounds()
  {
    List list = getAmbientSound();
    Collections.sort(list, new Comparator() {

      public int compare(Sound sound, Sound sound1)
      {
        if(sound.getSoundId() > sound1.getSoundId())
        {
          return 1;
        }
        return sound.getSoundId() >= sound1.getSoundId() ? 0 : -1;
      }

      public int compare(Object obj, Object obj1)
      {
        return compare((Sound)obj, (Sound)obj1);
      }
    });
    return list;
  }

  public List getSortedBinauralSounds()
  {
    List list = getSoundsOfType(BinauralSound.class);
    Collections.sort(list, new Comparator() {

      public int compare(BinauralSound binauralsound, BinauralSound binauralsound1)
      {
        if(binauralsound.getOrder() > binauralsound1.getOrder())
        {
          return 1;
        }
        return binauralsound.getOrder() >= binauralsound1.getOrder() ? 0 : -1;
      }

      public int compare(Object obj, Object obj1)
      {
        return compare((BinauralSound)obj, (BinauralSound)obj1);
      }
    });
    return list;
  }

  public Object getSound(String s)
  {
    return sounds.get(s);
  }

  public List getSoundsOfType(Class class1)
  {
    return getSoundsOfType(class1, null);
  }

  public List getSoundsOfType(Class class1, Boolean boolean1)
  {
    ArrayList arraylist = new ArrayList();
    Iterator iterator = sounds.values().iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      Sound sound = (Sound)iterator.next();
      if(class1.equals(sound.getClass()) && (boolean1 == null || sound.isBuiltIn() == boolean1.booleanValue()))
      {
        arraylist.add(sound);
      }
    } while(true);
    return arraylist;
  }

  public boolean isEmpty()
  {
    return sounds == null || sounds.isEmpty() || getAmbientSound().isEmpty();
  }

  public void loadBuiltInSoundsSynchronously(int i, int j, int k)
  {
    (new BuiltInContentLoader(this, i, j, k)).doLoad(buildContentLoaderCallback(null));
  }

  public void loadBuiltInSoundsSynchronously(int i, int j, int k, int l)
  {
    (new BuiltInContentLoader(this, i, j, k, l)).doLoad(buildContentLoaderCallback(null));
  }

  public void loadDownloadableSounds(final LoaderCallback callback)
  {
    (new DownloadableContentLoader(this)).loadContentWithCallback(new LoaderCallback() {
      public void callback(List list)
      {
        if(addSounds(list, false))
        {
          commit();
        }
        if(callback != null)
        {
          callback.callback(list);
        }
      }

    });
  }

  public void loadGiftedSoundsSynchronously(int i)
  {
    (new GiftedContentLoader(this, i)).doLoad(buildGiftedContentLoaderCallback(null));
  }

  public void notifyLockedSounds()
  {
    Iterator iterator = getAllSounds().iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      Sound sound = (Sound)iterator.next();
      if(sound.isLocked() && !sound.getClass().equals(GuidedMeditationSound.class))
      {
        notifySoundUpdated(sound);
      }
    } while(true);
  }

  public void onFeatureDownloaded(InAppPurchase inapppurchase, String as[])
  {
    if(as != null && as.length > 0)
    {
      if(inapppurchase == null || inapppurchase.getType().equals("AmbientSounds"))
      {
        ArrayList inapppurchase_1 = new ArrayList();
        int j = as.length;
        for(int i = 0; i < j; i++)
        {
          String s = as[i];
          Object obj = filePathToSoundId(s);
          if(obj == null)
          {
            continue;
          }
          obj = (Sound)getSound(((String) (obj)));
          if(obj != null)
          {
            ((Sound) (obj)).setFilePath(s);
            addSound(((Sound) (obj)), true);
            inapppurchase_1.add(obj);
          }
        }

        commit();
        for(Iterator inapppurchase_2 = inapppurchase_1.iterator(); inapppurchase_2.hasNext(); notifySoundUpdated((Sound)inapppurchase_2.next())) { }
      } else
      {
        Sound as_1 = (Sound)getSound(inapppurchase.getIdentifier());
        if(as_1 != null)
        {
          as_1.updateByInAppPurchase(inapppurchase);
          addSound(as_1, true);
          commit();
          notifySoundUpdated(as_1);
        }
      }
    }
  }

  public void onFeatureManagerSetupFinished()
  {
  }

  public void onFeatureUnlocked(InAppPurchase inapppurchase, String s)
  {
    Sound inapppurchase_1 = (Sound)getSound(inapppurchase.getIdentifier());
    if(inapppurchase_1 != null)
    {
      inapppurchase_1.setPurchaseId(s);
      addSound(inapppurchase_1, true);
      commit();
    }
  }

  public void onPurchaseCompleted(InAppPurchase inapppurchase, Purchase purchase, Date date)
  {
    if(!inapppurchase.isSubscription())
    {
      Sound inapppurchase_1 = (Sound)getSound(purchase.getSku());
      if(inapppurchase_1 != null && inapppurchase_1.updateByPurchase(purchase))
      {
        addSound(inapppurchase_1, true);
        commit();
        notifySoundUpdated(inapppurchase_1);
      }
    }
  }

  public void onPurchasesAvailable(List list)
  {
    ArrayList arraylist = new ArrayList();
    ArrayList arraylist1 = new ArrayList();
    Iterator list_1 = list.iterator();
    do
    {
      if(!list_1.hasNext())
      {
        break;
      }
      Object obj = (InAppPurchase)list_1.next();
      Sound sound = (Sound)getSound(((InAppPurchase) (obj)).getIdentifier());
      if(sound != null && sound.updateByInAppPurchase(((InAppPurchase) (obj))))
      {
        sounds.put(sound.getId(), sound);
        arraylist1.add(sound);
      } else
      if(sound == null)
      {
        obj = SoundFactory.createFromInAppPurchase(((InAppPurchase) (obj)));
        if(obj != null)
        {
          sounds.put(((Sound) (obj)).getId(), obj);
          arraylist.add(obj);
        }
      }
    } while(true);
    updateTagInformationOnBuiltInContent();
    if(arraylist.size() > 0)
    {
      commit();
      notifyNewSounds(arraylist);
    }
    if(arraylist1.size() > 0)
    {
      commit();
      notifySoundsUpdated(arraylist1);
    }
  }

  public void onSubscriptionChanged(Subscription subscription, boolean flag)
  {
    boolean flag2 = false;
    boolean flag1 = false;
    if(subscription != null)
    {
      Iterator iterator = sounds.values().iterator();
      do
      {
        flag2 = flag1;
        if(!iterator.hasNext())
        {
          break;
        }
        Sound sound = (Sound)iterator.next();
        if(!sound.isBuiltIn() && flag)
        {
          sound.setPurchaseId(subscription.getIdentifier());
          addSound(sound, true);
          flag1 = true;
          notifySoundUpdated(sound);
        } else
        if(!sound.isBuiltIn() && !flag && sound.getPurchaseId() != null && sound.getPurchaseId().equals(subscription.getIdentifier()))
        {
          sound.setPurchaseId(null);
          addSound(sound, true);
          flag1 = true;
          notifySoundUpdated(sound);
        }
      } while(true);
    }
    if(flag2)
    {
      commit();
    }
  }

  public void onUnresolvedPurchases(List list)
  {
    Iterator list_1 = list.iterator();
    String s;
    byte byte0;
    while(true) {
      if (!list_1.hasNext()) {
        break; /* Loop/switch isn't completed */
      }
      s = (String) list_1.next();
      byte0 = -1;
      switch (s.hashCode()) {
        default:

          break;
        case 1275056211:
          if (s.equals("ipnossoft.rma.free.premiumfeatures")) {
            byte0 = 0;
          }
          break;
      }
      switch (byte0) {
        case 0: // '\0'
          notifyLockedSounds();
          break;
      }
    }
  }

  public Sound querySound(Query query)
  {
    List query_1 = querySounds(query);
    if(query_1.size() == 1)
    {
      return (Sound)query_1.get(0);
    } else
    {
      return null;
    }
  }

  public List querySounds(Query query)
  {
    ArrayList arraylist = new ArrayList();
    Iterator iterator = sounds.values().iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      Sound sound = (Sound)iterator.next();
      if(query.where(sound))
      {
        arraylist.add(sound);
      }
    } while(true);
    return arraylist;
  }

  public List querySounds(Query query, Class class1)
  {
    ArrayList arraylist = new ArrayList();
    Iterator iterator = sounds.values().iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      Sound sound = (Sound)iterator.next();
      if(class1.isAssignableFrom(sound.getClass()) && query.where(sound))
      {
        arraylist.add(sound);
      }
    } while(true);
    return arraylist;
  }

  public void registerObserver(SoundLibraryObserver soundlibraryobserver)
  {
    observers.add(soundlibraryobserver);
  }

  public void removeSound(String s)
  {
    sounds.remove(s);
    commit();
  }

  public void unregisterObserver(SoundLibraryObserver soundlibraryobserver)
  {
    observers.remove(soundlibraryobserver);
  }




}
