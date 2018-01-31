package com.ipnossoft.api.soundlibrary;

import java.util.List;

public abstract interface SoundLibraryObserver
{
  public abstract void onNewSound(Sound paramSound);
  
  public abstract void onNewSounds(List<Sound> paramList);
  
  public abstract void onSoundUpdated(Sound paramSound);
  
  public abstract void onSoundsUpdated(List<Sound> paramList);
}


/* Location:           D:\ProcessingWork\2018.01.15\SleepSound\classes2-dex2jar.jar
 * Qualified Name:     com.ipnossoft.api.soundlibrary.SoundLibraryObserver
 * JD-Core Version:    0.7.0.1
 */