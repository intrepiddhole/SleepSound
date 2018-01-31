package ipnossoft.rma.media;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import com.nativemediaplayer.IMediaPlayer;
import com.nativemediaplayer.SdkMediaPlayer;
import com.nativemediaplayer.IMediaPlayer.OnCompletionListener;
import ipnossoft.rma.exceptions.UnavailableSoundException;
import ipnossoft.rma.free.R;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

class MediaPlayerFactory {
  MediaPlayerFactory() {
  }

  private static IMediaPlayer createPlayer(Context var0, int var1, float var2) throws Exception {
    return createPlayer(var0, getResourceUri(var0.getPackageName(), var1), var2);
  }

  private static IMediaPlayer createPlayer(Context var0, Uri var1, float var2) throws Exception {
    IMediaPlayer var3 = NativeMediaPlayerMonitor.buildInstance(var0);
    var3.setDataSource(var0, var1);
    prepareMediaPlayer(var3, var2, true);
    return var3;
  }

  public static IMediaPlayer createPlayer(Context var0, Sound var1, float var2) throws Exception {
    if(var1 == null) {
      throw new Exception("Sound is null, unable to read media file information.");
    } else if(!var1.isPlayable()) {
      throw new UnavailableSoundException(String.format(var0.getResources().getString(R.string.sound_not_playable), new Object[]{var1.getName()}), var1);
    } else if(var1.getClass().equals(GuidedMeditationSound.class)) {
      return getGuidedMeditationPlayer(var1, var2, var0, SoundManager.getInstance());
    } else if(var1.isBuiltIn() && var1.getMediaResourceId() != 0) {
      return createPlayer(var0, var1.getMediaResourceId(), var2);
    } else if(var1.isDownloaded()) {
      return createPlayer(var0, Uri.parse("file://" + var1.getFilePath()), var2);
    } else if(var1.isPreviewDownloaded()) {
      return createPlayer(var0, (new FileInputStream(var1.getPreviewFilePath())).getFD(), var2);
    } else {
      throw new UnavailableSoundException("Sound " + var1.getId() + " is not available on this device.", var1);
    }
  }

  private static IMediaPlayer createPlayer(Context var0, FileDescriptor var1, float var2) throws Exception {
    IMediaPlayer var3 = NativeMediaPlayerMonitor.buildInstance(var0);
    var3.setDataSource(var1);
    prepareMediaPlayer(var3, var2, true);
    return var3;
  }

  @NonNull
  private static SdkMediaPlayer getGuidedMeditationPlayer(Sound var0, float var1, Context var2, OnCompletionListener var3) throws IOException {
    SdkMediaPlayer var4 = new SdkMediaPlayer(var2);
    var4.setLooping(false);
    var4.setOnCompletionListener(var3);
    if(var0.isBuiltIn()) {
      var4.setDataSource(var2, getResourceUri(var2.getPackageName(), var0.getMediaResourceId()));
    } else {
      var4.setDataSource((new FileInputStream(var0.getFilePath())).getFD());
    }

    prepareMediaPlayer(var4, var1, false);
    return var4;
  }

  private static Uri getResourceUri(String var0, int var1) {
    return Uri.parse(String.format("android.resource://%s/%s", new Object[]{var0, Integer.valueOf(var1)}));
  }

  private static void prepareMediaPlayer(IMediaPlayer var0, float var1, boolean var2) throws IOException {
    var0.setLooping(var2);
    var0.setVolume(var1, var1);
    var0.prepare();
  }
}
