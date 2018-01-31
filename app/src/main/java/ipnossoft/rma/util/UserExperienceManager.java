package ipnossoft.rma.util;

import android.content.Context;
import android.util.Log;
import com.facebook.device.yearclass.YearClass;
import ipnossoft.rma.RelaxMelodiesApp;

public class UserExperienceManager {
    private static String TAG = "UserExperienceManager";
    private static final int bitmapHeapCacheMax;
    private static final boolean buttonCacheAllowed;
    private static int displayDensity = -1;
    private static final UserExperienceManager.Experience experience;
    private static Boolean lowPerformanceDevice;
    private static final int maxMemoryMB;
    private static Runtime runtime = Runtime.getRuntime();
    private static final int soundViewPagerOfflinePages;

    static {
        maxMemoryMB = (int)(runtime.maxMemory() / 1024L / 1024L);
        if(maxMemoryMB <= 32) {
            experience = UserExperienceManager.Experience.LOW;
            soundViewPagerOfflinePages = 1;
            buttonCacheAllowed = false;
            bitmapHeapCacheMax = 10;
        } else if(maxMemoryMB <= 64) {
            experience = UserExperienceManager.Experience.NORMAL;
            soundViewPagerOfflinePages = 1;
            buttonCacheAllowed = true;
            bitmapHeapCacheMax = 20;
        } else {
            experience = UserExperienceManager.Experience.HIGH;
            soundViewPagerOfflinePages = 15;
            buttonCacheAllowed = true;
            bitmapHeapCacheMax = 0;
        }

        Log.d(TAG, "User experience set to: " + experience.toString() + " maxMemory=" + maxMemoryMB + "MB");
    }

    public UserExperienceManager() {
    }

    public static int getBitmapHeapCacheMax() {
        return bitmapHeapCacheMax;
    }

    public static int getDisplayDensity(Context var0) {
        if(displayDensity == -1) {
            displayDensity = var0.getResources().getDisplayMetrics().densityDpi;
        }

        return displayDensity;
    }

    public static UserExperienceManager.Experience getExperience() {
        return experience;
    }

    public static int getMaxMemoryMB() {
        return maxMemoryMB;
    }

    public static UserExperienceManager.ScreenSize getScreenSize(Context var0) {
        return (var0.getResources().getConfiguration().screenLayout & 15) == 4?UserExperienceManager.ScreenSize.ExtraLarge:((var0.getResources().getConfiguration().screenLayout & 15) == 3?UserExperienceManager.ScreenSize.Large:((var0.getResources().getConfiguration().screenLayout & 15) == 2?UserExperienceManager.ScreenSize.Normal:((var0.getResources().getConfiguration().screenLayout & 15) == 1?UserExperienceManager.ScreenSize.Small:UserExperienceManager.ScreenSize.Unknown)));
    }

    public static int getSoundViewPagerOfflinePages() {
        return soundViewPagerOfflinePages;
    }

    public static boolean isButtonCacheAllowed() {
        return buttonCacheAllowed;
    }

    public static boolean isLowPerformanceDevice() {
        if(lowPerformanceDevice == null) {
            boolean var0;
            if(YearClass.get(RelaxMelodiesApp.getInstance().getApplicationContext()) < 2013) {
                var0 = true;
            } else {
                var0 = false;
            }

            lowPerformanceDevice = Boolean.valueOf(var0);
        }

        return lowPerformanceDevice.booleanValue();
    }

    public static enum Experience {
        HIGH,
        LOW,
        NORMAL;

        private Experience() {
        }
    }

    public static enum ScreenSize {
        ExtraLarge,
        Large,
        Normal,
        Small,
        Unknown;

        private ScreenSize() {
        }
    }
}
