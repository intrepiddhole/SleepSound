package ipnossoft.rma.media;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import com.nativemediaplayer.BufferedAudioPlayer;
import com.nativemediaplayer.ChainedMediaPlayer;
import com.nativemediaplayer.IMediaPlayer;
import com.nativemediaplayer.NativeMediaPlayer;
import com.nativemediaplayer.OsUtil;
import com.nativemediaplayer.SdkMediaPlayer;

public class NativeMediaPlayerMonitor implements OnSharedPreferenceChangeListener {
    public static final String PREF_USE_NATIVE_PLAYER = "useNativePlayer";
    private final Context context;
    private final boolean debug;
    private NativeMediaPlayerMonitor.Mode mode;
    private boolean useNativeMediaPlayer;

    public NativeMediaPlayerMonitor(Context var1) {
        this.context = var1;
        this.debug = false;
    }

    public NativeMediaPlayerMonitor(Context var1, boolean var2) {
        this.context = var1;
        this.debug = var2;
    }

    public static IMediaPlayer buildInstance(Context var0) {
        NativeMediaPlayerMonitor.Mode var1 = getCurrentMode(var0);
        return (IMediaPlayer)(var1 == NativeMediaPlayerMonitor.Mode.OFF?new SdkMediaPlayer(var0):(shouldUseNativeMediaPlayer(var1)?new NativeMediaPlayer(var0):(var1 == NativeMediaPlayerMonitor.Mode.MODE5?new BufferedAudioPlayer(var0):new ChainedMediaPlayer(var0))));
    }

    private static NativeMediaPlayerMonitor.Mode getCurrentMode(Context var0) {
        return NativeMediaPlayerMonitor.Mode.fromValue(PreferenceManager.getDefaultSharedPreferences(var0).getString("useNativePlayer", NativeMediaPlayerMonitor.Mode.OFF.value));
    }

    private static boolean isChainedAudioAvailable() {
        return OsUtil.apiLevel >= 16;
    }

    private boolean isNativeAudioAvailable() {
        return OsUtil.apiLevel >= 9;
    }

    private void saveDefaultPreferenceValue(SharedPreferences var1) {
        boolean var2;
        if(OsUtil.apiLevel >= 14) {
            var2 = true;
        } else {
            var2 = false;
        }

        Editor var3 = var1.edit();
        if(var2) {
            if(OsUtil.apiLevel >= 21) {
                var3.putString("useNativePlayer", NativeMediaPlayerMonitor.Mode.MODE4.value);
            } else {
                var3.putString("useNativePlayer", NativeMediaPlayerMonitor.Mode.MODE2.value);
            }
        } else {
            var3.putString("useNativePlayer", NativeMediaPlayerMonitor.Mode.OFF.value);
        }

        var3.apply();
    }

    private static boolean shouldUseNativeMediaPlayer(NativeMediaPlayerMonitor.Mode var0) {
        return var0 == NativeMediaPlayerMonitor.Mode.MODE1 || var0 == NativeMediaPlayerMonitor.Mode.MODE2 && !isChainedAudioAvailable() || var0 == NativeMediaPlayerMonitor.Mode.MODE3 || var0 == NativeMediaPlayerMonitor.Mode.MODE4;
    }

    private void shutdownNativeMediaPlayer() {
        if(this.useNativeMediaPlayer) {
            NativeMediaPlayer.shutdown();
            this.useNativeMediaPlayer = false;
        }

    }

    private void startupNativeMediaPlayer() {
        if(!this.useNativeMediaPlayer) {
            NativeMediaPlayer.startup(this.context, this.debug, this.useBufferThread(), this.useNativeVorbisDecoder());
            this.useNativeMediaPlayer = true;
        }

    }

    private boolean useBufferThread() {
        return this.mode == NativeMediaPlayerMonitor.Mode.MODE2 || this.mode == NativeMediaPlayerMonitor.Mode.MODE3;
    }

    private boolean useNativeVorbisDecoder() {
        return OsUtil.apiLevel >= 14;
    }

    public void onSharedPreferenceChanged(SharedPreferences var1, String var2) {
        if(var2.equals("useNativePlayer")) {
            NativeMediaPlayerMonitor.Mode var3 = NativeMediaPlayerMonitor.Mode.fromValue(var1.getString("useNativePlayer", NativeMediaPlayerMonitor.Mode.OFF.value));
            if(var3 != this.mode) {
                this.mode = var3;
                SoundManager.getInstance().pauseAll(true);
                SoundManager.getInstance().flushAllPlayers();
                this.shutdownNativeMediaPlayer();
                if(shouldUseNativeMediaPlayer(this.mode)) {
                    this.startupNativeMediaPlayer();
                    return;
                }
            }
        }

    }

    public void start() {
        if(this.isNativeAudioAvailable()) {
            SharedPreferences var1 = PreferenceManager.getDefaultSharedPreferences(this.context);
            if(!var1.contains("useNativePlayer")) {
                this.saveDefaultPreferenceValue(var1);
            }

            this.mode = NativeMediaPlayerMonitor.Mode.fromValue(var1.getString("useNativePlayer", NativeMediaPlayerMonitor.Mode.OFF.value));
            if(shouldUseNativeMediaPlayer(this.mode)) {
                this.startupNativeMediaPlayer();
            }

            var1.registerOnSharedPreferenceChangeListener(this);
        }
    }

    public void stop() {
        if(this.isNativeAudioAvailable()) {
            PreferenceManager.getDefaultSharedPreferences(this.context).unregisterOnSharedPreferenceChangeListener(this);
            this.shutdownNativeMediaPlayer();
        }
    }

    public static enum Mode {
        MODE1("1"),
        MODE2("2"),
        MODE3("3"),
        MODE4("4"),
        MODE5("5"),
        OFF("0");

        public final String value;

        private Mode(String var3) {
            this.value = var3;
        }

        public static NativeMediaPlayerMonitor.Mode fromValue(String var0) {
            NativeMediaPlayerMonitor.Mode var3;
            if(var0 == null) {
                var3 = OFF;
            } else {
                NativeMediaPlayerMonitor.Mode[] var5 = values();
                int var2 = var5.length;
                int var1 = 0;

                while(true) {
                    if(var1 >= var2) {
                        throw new IllegalArgumentException("No constant with value " + var0 + " found");
                    }

                    NativeMediaPlayerMonitor.Mode var4 = var5[var1];
                    var3 = var4;
                    if(var0.equals(var4.value)) {
                        break;
                    }

                    ++var1;
                }
            }

            return var3;
        }
    }
}
