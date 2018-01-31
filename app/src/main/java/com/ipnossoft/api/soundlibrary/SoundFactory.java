package com.ipnossoft.api.soundlibrary;

import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import java.util.*;

// Referenced classes of package com.ipnossoft.api.soundlibrary:
//            Sound
//cavaj
public class SoundFactory
{

  public static final String SOUND_TYPE_AMBIENT_SOUNDS = "AmbientSounds";
  private static final String SOUND_TYPE_GUIDED_MEDITATIONS = "GuidedMeditations";
  private static ObjectMapper mapper = new ObjectMapper();
  private static LinkedHashMap typeNameToTypeMap;

  public SoundFactory()
  {
  }

  public static Sound createFromInAppPurchase(InAppPurchase inapppurchase)
  {
    Object obj;
    try
    {
      obj = getClassName(inapppurchase);
    }
    // Misplaced declaration of an exception variable
    catch(Exception e)
    {
      Log.e(SoundFactory.class.getSimpleName(), "", e);
      return null;
    }
    if(obj == null)
    {
      return null;
    }
    try {
      obj = (Sound)((Class) (obj)).newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    ((Sound) (obj)).updateByInAppPurchase(inapppurchase);
    return ((Sound) (obj));
  }

  public static Sound createFromJson(String s)
  {
    try
    {
      Sound s_1 = (Sound)mapper.readValue(s, Sound.class);
      return s_1;
    }
    // Misplaced declaration of an exception variable
    catch(Exception e)
    {
      Log.e(SoundFactory.class.getSimpleName(), "", e);
      return null;
    }
  }

  public static List createListFromJson(String s)
  {
    try
    {
      List s_1 = (List)mapper.readValue(s, mapper.getTypeFactory().constructCollectionType(List.class, Sound.class));
      return s_1;
    }
    // Misplaced declaration of an exception variable
    catch(Exception e)
    {
      Log.e(SoundFactory.class.getSimpleName(), "", e);
      return null;
    }
  }

  private static Class getClassName(InAppPurchase inapppurchase)
  {
    label0:
    {
      if(inapppurchase == null || inapppurchase.getType() == null || inapppurchase.getType().isEmpty())
      {
        break label0;
      }
      Iterator iterator = typeNameToTypeMap.keySet().iterator();
      String s;
      do
      {
        if(!iterator.hasNext())
        {
          break label0;
        }
        s = (String)iterator.next();
      } while(!inapppurchase.getType().equals(s));
      return (Class)typeNameToTypeMap.get(s);
    }
    return null;
  }

  static
  {
    typeNameToTypeMap = new LinkedHashMap();
    typeNameToTypeMap.put("GuidedMeditations", GuidedMeditationSound.class);
    typeNameToTypeMap.put("AmbientSounds", null);
  }
}
