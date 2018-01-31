package ipnossoft.rma.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import com.amplitude.api.Amplitude;
import com.amplitude.api.Identify;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.HitBuilders.AppViewBuilder;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ipnossoft.api.featuremanager.data.Subscription;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.sounds.BinauralSound;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import com.ipnossoft.api.soundlibrary.sounds.IsochronicSound;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import ipnossoft.rma.AppState;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.RelaxPropertyHandler;
import ipnossoft.rma.R.string;
import ipnossoft.rma.favorites.FavoriteFragment;
import ipnossoft.rma.feature.RelaxFeatureManager;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.media.SoundTrack;
import ipnossoft.rma.media.NativeMediaPlayerMonitor.Mode;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.onepf.oms.appstore.googleUtils.SkuDetails;

public class Analytics {
    public static final String APP_CODE_PREFIX = "RMA_";
    private static int NEW_USER_DIMENSION = 3;
    public static final String PREF_PENDING_INSTALL_ATTRIBUTION = "pendingInstallAttribution";
    private static int SESSION_COUNT_DIMENSION = 1;
    public static final String TAG = "Analytics";
    private static int USER_SUBSCRIBED_DIMENSION = 1;
    private static AppFlyerLogger appFlyerLogger;
    protected static Context applicationContext;
    private static AppEventsLogger facebookLogger;
    private static FirebaseAnalytics firebaseLogger;
    private static Tracker googleAnalyticsLogger;
    private static boolean initialized = false;
    private static long lastEventTime;
    private static boolean sessionStarted;
    private static float volumeOnScreenChange = -1.0F;
    private static float volumeOnScreenLoading = -1.0F;

    public Analytics() {
    }

    public static void addPromoCode(String var0) {
        Identify var1 = new Identify();
        var1.append("RMA_promo_codes", var0);
        firebaseLogger.setUserProperty("RMA_last_promo_code", var0);
        Amplitude.getInstance().identify(var1);
    }

    public static void addPurchaseInfo(String var0, String var1) {
        Identify var2 = new Identify();
        if(var0 != null && !var0.isEmpty()) {
            var2.append("RMA_purchase_tokens", var0);
        }

        if(var1 != null && !var1.isEmpty()) {
            var2.append("RMA_order_ids", var1);
        }

        Amplitude.getInstance().identify(var2);
    }

    private static Map<String, Object> appendBaseParameters(Map<String, String> var0) {
        HashMap var1 = new HashMap();
        if(var0 != null) {
            var1.putAll(var0);
        }

        var1.put("sounds_selected", Integer.valueOf(AppState.getNumberOfSoundsSelected()));
        var1.put("brainwaves_selected", Integer.valueOf(AppState.getNumberOfBrainwavesSelected()));
        var1.put("meditations_selected", Integer.valueOf(AppState.getNumberOfMeditationsSelected()));
        var1.put("is_playing", Boolean.valueOf(AppState.isPlaying()));
        var1.put("timer_running", Boolean.valueOf(AppState.isTimerRunning()));
        var1.put("screen", AppState.getLastScreen());
        String var2;
        if(RelaxMelodiesApp.isFreeVersion()) {
            var2 = "free";
        } else {
            var2 = "premium";
        }

        var1.put("app_version", var2);
        var1.put("app_platform", "android");
        return var1;
    }

    public static void attributeInstall(String var0, Context var1) {
        if(!isInitialized()) {
            Log.i("Analytics", "Delaying Install Attribution with Referrer:" + var0);
            PersistedDataManager.saveString("pendingInstallAttribution", var0, var1);
        } else {
            Identify var6 = new Identify();
            Map var5 = getHashMapFromQuery(var0);
            Iterator var2 = Arrays.asList(new String[]{"utm_source", "utm_medium", "utm_term", "utm_content", "utm_campaign"}).iterator();

            while(var2.hasNext()) {
                String var3 = (String)var2.next();
                String var4 = (String)var5.get(var3);
                if(var4 != null) {
                    var6.setOnce("RMA_" + var3, var4);
                    firebaseLogger.setUserProperty("RMA_" + var3, var4);
                }
            }

            Log.i("Analytics", "Attributing Install with Referrer:" + var5.toString());
            Amplitude.getInstance().identify(var6);
        }
    }

    private static Bundle convertToStringBundle(Map<String, Object> var0) {
        Bundle var1 = new Bundle();
        if(var0 != null) {
            Iterator var2 = var0.keySet().iterator();

            while(var2.hasNext()) {
                String var3 = (String)var2.next();
                var1.putString(var3, String.valueOf(var0.get(var3)));
            }
        }

        return var1;
    }

    public static void endSession() {
        if(isInitialized() && sessionStarted) {
            sessionStarted = false;
            FlurryAgent.onEndSession(applicationContext);
            logMainVolumeChangeIfNecessary();
        }

    }

    private static String formatDate(long var0) {
        return ISO8601Utils.format(new Date(var0));
    }

    public static Map<String, String> getHashMapFromQuery(String var0) {
        int var3 = 0;
        LinkedHashMap var1 = new LinkedHashMap();
        String[] var8 = var0.split("&");

        for(int var4 = var8.length; var3 < var4; ++var3) {
            String var2 = var8[var3];
            int var5 = var2.indexOf("=");

            try {
                var1.put(URLDecoder.decode(var2.substring(0, var5), "UTF-8"), URLDecoder.decode(var2.substring(var5 + 1), "UTF-8"));
            } catch (UnsupportedEncodingException var6) {
                Log.e("Analytics", "Failed to get extract referer link parameters", var6);
            } catch (StringIndexOutOfBoundsException var7) {
                Log.e("Analytics", "Malformed query", var7);
            }
        }

        return var1;
    }

    private static String getMixType(boolean var0, boolean var1, boolean var2) {
        return var0?(var1?"sounds_with_meditation_and_binaural":(var2?"sounds_with_meditation_and_isochronic":"sounds_with_meditation")):(var1?"sounds_with_binaural":(var2?"sounds_with_isochronic":"only_sounds"));
    }

    @Nullable
    private static PackageInfo getPackageInfo() {
        try {
            PackageInfo var0 = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
            return var0;
        } catch (NameNotFoundException var1) {
            Log.e("Analytics", "Failed to get package info", var1);
            return null;
        }
    }

    protected static String getSoundCategory(Sound var0) {
        return var0 instanceof BinauralSound?"binaurals":(var0 instanceof IsochronicSound?"isochronics":(var0 instanceof GuidedMeditationSound?"meditations":"sounds"));
    }

    public static void initialize(Context var0) {
        applicationContext = var0;
        FlurryAgent.init(applicationContext, applicationContext.getString(R.string.flurry_api_key));
        facebookLogger = AppEventsLogger.newLogger(applicationContext);
        Amplitude.getInstance().initialize(var0, RelaxPropertyHandler.getInstance().getProperties().getProperty("amplitude.api.key")).enableForegroundTracking(RelaxMelodiesApp.getInstance());
        firebaseLogger = FirebaseAnalytics.getInstance(applicationContext);
        googleAnalyticsLogger = GoogleAnalytics.getInstance(applicationContext).newTracker(applicationContext.getResources().getString(R.string.google_analytics_tracker_id));
        googleAnalyticsLogger.enableAdvertisingIdCollection(true);
        googleAnalyticsLogger.enableAutoActivityTracking(false);
        Fabric.with(var0, new Kit[]{(new Builder()).core((new com.crashlytics.android.core.CrashlyticsCore.Builder()).disabled(RelaxMelodiesApp.getInstance().isDebug()).build()).build(), new Answers()});
        initialized = true;
        if(!sessionStarted) {
            startSession();
        }

        String var1 = PersistedDataManager.getString("pendingInstallAttribution", (String)null, var0);
        if(var1 != null) {
            attributeInstall(var1, var0);
        }

    }

    private static boolean isInitialized() {
        return initialized && facebookLogger != null && applicationContext != null && googleAnalyticsLogger != null;
    }

    private static void logAmplitude(String var0, Map<String, Object> var1) {
        Amplitude.getInstance().logEvent(var0, new JSONObject(var1));
    }

    public static void logEnteredBackground() {
        if(isInitialized()) {
            logEvent("default", "entered_background");
        }

    }

    public static void logEnteredForeground() {
        if(isInitialized()) {
            logEvent("default", "entered_foreground");
        }

    }

    public static void logEvent(String var0, String var1) {
        logEvent(var0, var1, (Map)null, (String)null, 1);
    }

    public static void logEvent(String var0, String var1, String var2, String var3, String var4, int var5) {
        HashMap var6 = new HashMap();
        var6.put(var2, var3);
        logEvent(var0, var1, var6, var4, var5);
    }

    public static void logEvent(String var0, String var1, Map<String, String> var2, String var3, int var4) {
        logEvent(var0, var1, var2, false, var3, var4);
    }

    private static void logEvent(String var0, String var1, Map<String, String> var2, boolean var3, String var4, int var5) {
        if(isInitialized()) {
            lastEventTime = System.currentTimeMillis();
            var2 = appendBaseParameters(var2);
            logEventFlury(var1, var2);
            logAmplitude(var1, var2);
            logEventFacebook(var1, var2);
            logGoogleAnalyticEvent(var0, var1, var3, var4, var5);
            logFirebaseAnalyticEvent(var1, var2);
            StringBuilder var6 = (new StringBuilder()).append("Event name: ").append(var1);
            if(var2 != null) {
                var0 = "    parameters: " + var2.toString();
            } else {
                var0 = "";
            }

            Log.d("Analytics", var6.append(var0).toString());
        }

    }

    private static void logEventFacebook(String var0, Map<String, Object> var1) {
        if(var1 != null) {
            facebookLogger.logEvent(var0, convertToStringBundle(var1));
        } else {
            facebookLogger.logEvent(var0);
        }
    }

    private static void logEventFlury(String var0, Map<String, Object> var1) {
        FlurryAgent.logEvent(var0, stringifyMap(var1));
    }

    public static void logEventUpgradeWithUpgradeReferer(Analytics.UpgradeReferer var0) {
        HashMap var1 = new HashMap();
        var1.put("upgrade_referer", var0.toString());
        logEvent("upgrade", "navigation_upgrade", var1, var0.toString(), 1);
    }

    public static void logEventWithSound(String var0, String var1, Sound var2) {
        HashMap var3 = new HashMap();
        if(var2 != null) {
            var3.put("sound_id", var2.getId());
            var3.put("sound_category", getSoundCategory(var2));
            var3.put("sound_volume", String.valueOf(SoundManager.getInstance().getVolumeForSoundId(var2.getId())));
            logEvent(var0, var1, var3, var2.getId(), 1);
        }

    }

    public static void logEventWithSounds(String var0, String var1, Map<String, String> var2, List<SoundTrack> var3) {
        Map var7 = var2;
        Object var11 = var2;
        if(var7 == null) {
            var11 = new HashMap();
        }

        ArrayList var13 = new ArrayList();
        ArrayList var8 = new ArrayList();
        HashSet var9 = new HashSet();
        Iterator var12 = var3.iterator();

        while(var12.hasNext()) {
            SoundTrack var10 = (SoundTrack)var12.next();
            var13.add(var10.getSound().getId());
            var8.add(String.valueOf(var10.getVolume()));
            var9.add(getSoundCategory(var10.getSound()));
        }

        boolean var4 = var9.contains("meditations");
        boolean var5 = var9.contains("isochronics");
        boolean var6 = var9.contains("binaurals");
        ((Map)var11).put("sound_count", String.valueOf(var13.size()));
        ((Map)var11).put("sound_ids", var13.toString());
        ((Map)var11).put("sound_volumes", var8.toString());
        ((Map)var11).put("contains_meditation", String.valueOf(var4));
        ((Map)var11).put("contains_isochronic", String.valueOf(var5));
        ((Map)var11).put("contains_binaural", String.valueOf(var6));
        logEvent(var0, var1, (Map)var11, getMixType(var4, var6, var5), var13.size());
    }

    private static void logFirebaseAnalyticEvent(String var0, Map<String, Object> var1) {
        firebaseLogger.logEvent(var0, BundleUtils.convertMapToBundle(var1));
    }

    private static void logGoogleAnalyticEvent(String var0, String var1, boolean var2, String var3, int var4) {
        if(var2) {
            googleAnalyticsLogger.setScreenName(var1);
            googleAnalyticsLogger.send((new ScreenViewBuilder()).build());
        } else {
            EventBuilder var5 = new EventBuilder();
            if(var3 != null) {
                var5.setLabel(var3);
            }

            googleAnalyticsLogger.send(var5.setCategory(var0).setAction(var1).setValue((long)var4).build());
        }
    }

    private static void logMainVolumeChangeIfNecessary() {
        if(volumeOnScreenLoading != volumeOnScreenChange) {
            String var0;
            if(volumeOnScreenChange > volumeOnScreenLoading) {
                var0 = "main_volume_increased";
            } else {
                var0 = "main_volume_decreased";
            }

            logEvent("bottom_cloud_menu", var0, "volume_delta", String.valueOf(Math.abs(volumeOnScreenLoading - volumeOnScreenChange)), (String)null, 1);
            volumeOnScreenLoading = -1.0F;
            volumeOnScreenChange = -1.0F;
        }

    }

    public static void logMainVolumeChanged(float var0, float var1) {
        if(volumeOnScreenLoading < 0.0F) {
            volumeOnScreenLoading = var0;
        }

        volumeOnScreenChange = var1;
    }

    private static void logPurchase(String var0, double var1, String var3) {
        if(isInitialized()) {
            logPurchaseFacebook(var1, var3);
            logPurchaseAppFlyer(var0, var1, var3);
            Log.i("Analytics", "Logged purchase: " + var0 + " at " + var1 + " " + var3);
        }

    }

    public static void logPurchase(SkuDetails var0) {
        if(var0 != null && var0.getJson() != null) {
            try {
                JSONObject var2 = new JSONObject(var0.getJson());
                int var1 = var2.getInt("price_amount_micros");
                String var4 = (String)var2.get("price_currency_code");
                logPurchase(var0.getSku(), (double)Integer.valueOf(var1).intValue() / 1000000.0D, var4);
            } catch (Exception var3) {
                Log.w("Analytics", "Failed logging purchase event from SkuDetails", var3);
                return;
            }
        }

    }

    private static void logPurchaseAppFlyer(String var0, double var1, String var3) {
        if(appFlyerLogger != null) {
            appFlyerLogger.trackEvent(var0, var1, var3);
        }

    }

    private static void logPurchaseFacebook(double var0, String var2) {
        facebookLogger.logPurchase(BigDecimal.valueOf(var0), Currency.getInstance(var2));
    }

    public static void logScreen(String var0) {
        if(!var0.equals(String.valueOf(AppState.getLastScreen()))) {
            logMainVolumeChangeIfNecessary();
            AppState.setLastScreen(var0);
        }

        logEvent("default", var0, (Map)null, true, (String)null, 1);
    }

    public static void logScreenLocked() {
        if(isInitialized()) {
            logEvent("default", "screen_locked");
        }

    }

    public static void logScreenUnlocked() {
        if(isInitialized()) {
            logEvent("default", "screen_unlocked");
        }

    }

    private static void logSessionStartIfNecessary() {
        if(System.currentTimeMillis() - lastEventTime > 1800000L && !SoundManager.getInstance().isPlaying()) {
            logEvent("default", "session_started");
        }

    }

    public static void onCreate(boolean var0) {
        if(!isInitialized()) {
            initialize(RelaxMelodiesApp.getInstance().getApplicationContext());
        }

        setDimension(USER_SUBSCRIBED_DIMENSION, String.valueOf(RelaxFeatureManager.getInstance().hasActiveSubscription()));
        int var1 = PersistedDataManager.getInteger("app_startup_counter", 0, applicationContext);
        setMetrics(SESSION_COUNT_DIMENSION, (float)var1);
        setDimension(NEW_USER_DIMENSION, String.valueOf(var0));
    }

    public static void refreshUserDimension() {
        Subscription var1 = RelaxFeatureManager.getInstance().getActiveSubscription();
        Identify var0 = new Identify();
        if(var1 != null) {
            var0.setOnce("RMA_paid", true);
            firebaseLogger.setUserProperty("RMA_paid", "true");
            if(var1.isFromPromoCode()) {
                var0.set("RMA_subscribed_promo", true);
                firebaseLogger.setUserProperty("RMA_subscribed_promo", "true");
            } else {
                var0.unset("RMA_subscribed_promo");
                firebaseLogger.setUserProperty("RMA_subscribed_promo", (String)null);
            }

            var0.set("RMA_subscribed", true);
            firebaseLogger.setUserProperty("RMA_subscribed", "true");
            var0.set("RMA_last_subscription", var1.getIdentifier());
            firebaseLogger.setUserProperty("RMA_last_subscription", var1.getIdentifier());
        } else {
            var0.unset("RMA_subscribed");
            firebaseLogger.setUserProperty("RMA_subscribed", (String)null);
        }

        var0.set("RMA_favorite_count", FavoriteFragment.getFavoriteCount());
        firebaseLogger.setUserProperty("RMA_favorite_count", String.valueOf(FavoriteFragment.getFavoriteCount()));
        var0.set("RMA_has_premium_sounds", RelaxMelodiesApp.isPremium().booleanValue());
        firebaseLogger.setUserProperty("RMA_has_premium_sounds", String.valueOf(RelaxMelodiesApp.isPremium().booleanValue()));
        var0.setOnce("RMA_has_android", true);
        firebaseLogger.setUserProperty("RMA_has_android", "true");
        var0.set("RMA_loop_mode", PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("useNativePlayer", Mode.OFF.value));
        firebaseLogger.setUserProperty("RMA_loop_mode", String.valueOf(PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("useNativePlayer", Mode.OFF.value)));
        PackageInfo var2 = getPackageInfo();
        if(var2 != null) {
            var0.setOnce("RMA_first_install", formatDate(var2.firstInstallTime));
            firebaseLogger.setUserProperty("RMA_first_install", formatDate(var2.firstInstallTime));
        }

        Amplitude.getInstance().identify(var0);
    }

    public static void setAppFlyerLogger(AppFlyerLogger var0) {
        appFlyerLogger = var0;
    }

    private static void setDimension(int var0, String var1) {
        googleAnalyticsLogger.send(((AppViewBuilder)(new AppViewBuilder()).setCustomDimension(var0, var1)).build());
    }

    private static void setMetrics(int var0, float var1) {
        googleAnalyticsLogger.send(((AppViewBuilder)(new AppViewBuilder()).setCustomMetric(var0, var1)).build());
    }

    public static void setUserPropertyMax(String var0, int var1) {
        String var2 = "analytics_max_" + var0;
        if(var1 > PersistedDataManager.getInteger(var2, 0, applicationContext)) {
            Log.d("Analytics", "Setting User Property MAX reached: " + String.valueOf(var1));
            PersistedDataManager.saveInteger(var2, var1, applicationContext);
            Identify var3 = new Identify();
            var3.set("RMA_" + var0, var1);
            firebaseLogger.setUserProperty("RMA_" + var0, String.valueOf(var1));
            Amplitude.getInstance().identify(var3);
        }

    }

    public static void setUserPropertyOnce(String var0, boolean var1) {
        Log.d("Analytics", "Setting User Property Flag Once : " + String.valueOf(var1));
        Identify var2 = new Identify();
        var2.setOnce("RMA_" + var0, var1);
        FirebaseAnalytics var3 = firebaseLogger;
        String var4 = "RMA_" + var0;
        if(var1) {
            var0 = "true";
        } else {
            var0 = "false";
        }

        var3.setUserProperty(var4, var0);
        Amplitude.getInstance().identify(var2);
    }

    public static void startSession() {
        if(isInitialized() && !sessionStarted) {
            sessionStarted = true;
            FlurryAgent.onStartSession(applicationContext);
            logSessionStartIfNecessary();
            refreshUserDimension();
        }

    }

    private static Map<String, String> stringifyMap(Map<String, Object> var0) {
        HashMap var1 = new HashMap();
        Iterator var2 = var0.keySet().iterator();

        while(var2.hasNext()) {
            String var3 = (String)var2.next();
            var1.put(var3, String.valueOf(var0.get(var3)));
        }

        return var1;
    }

    public class EventCategories {
        public static final String Binaurals = "binaurals";
        public static final String Brainwaves = "brainwaves";
        public static final String CloudMenu = "bottom_cloud_menu";
        public static final String Default = "default";
        public static final String Download = "download";
        public static final String Favorites = "favorites";
        public static final String Isochronics = "isochronics";
        public static final String Meditations = "meditations";
        public static final String More = "more";
        public static final String OpeningDialog = "opening_dialog";
        public static final String Settings = "settings";
        public static final String Sounds = "sounds";
        public static final String Subvolume = "sub_volume";
        public static final String Timer = "timer";
        public static final String Upgrade = "upgrade";
        public static final String Walkthrough = "walkthrough";

        public EventCategories() {
        }
    }

    public class EventNames {
        public static final String ActiveTimer = "active_timer";
        public static final String AdClick = "ad_click";
        public static final String CancelFeature = "cancel_feature";
        public static final String ChangedTimerStopApp = "changed_timer_closing_app";
        public static final String ClearSounds = "clear_selection";
        public static final String CreateFavorite = "create_favorite";
        public static final String CreateFavoriteResult = "create_favorite_result";
        public static final String DeleteFavorite = "delete_favorite";
        public static final String DownloadFeature = "download_feature";
        public static final String EnteredBackground = "entered_background";
        public static final String EnteredForeground = "entered_foreground";
        public static final String FeedbackDialog = "feedback_dialog";
        public static final String FeedbackDialogResult = "feedback_dialog_result";
        public static final String GiftsDialog = "gifts_dialog";
        public static final String GuidedMeditationCompletion = "meditation_stopped";
        public static final String MainVolumeDecrease = "main_volume_decreased";
        public static final String MainVolumeIncrease = "main_volume_increased";
        public static final String MoreActionContactUs = "more_action_contact_us";
        public static final String MoreActionShare = "more_action_share";
        public static final String NavigationBlog = "navigation_blog";
        public static final String NavigationFavorite = "navigation_favorite";
        public static final String NavigationGuidedMeditation = "navigation_guided_meditation";
        public static final String NavigationHelp = "navigation_help";
        public static final String NavigationLegal = "navigation_legal";
        public static final String NavigationMore = "navigation_more";
        public static final String NavigationNews = "navigation_news";
        public static final String NavigationOtherProduct = "navigation_other_product";
        public static final String NavigationSettings = "navigation_settings";
        public static final String NavigationSound = "navigation_sounds";
        public static final String NavigationTimer = "navigation_timer";
        public static final String NavigationUpgradePage = "navigation_upgrade";
        public static final String PauseAllSounds = "pause_all_sounds";
        public static final String PlayAllSounds = "resume_all_sounds";
        public static final String PlayFavorite = "play_favorite";
        public static final String PlayFavoriteInContextMenu = "play_favorite_in_context_menu";
        public static final String PlaySound = "sound_play";
        public static final String PlayStaffPick = "play_staff_pick";
        public static final String PreReviewDialog = "pre_review_dialog";
        public static final String PreReviewDialogResult = "pre_review_dialog_result";
        public static final String ReviewDialog = "review_dialog";
        public static final String ReviewDialogResult = "review_dialog_result";
        public static final String ScreenLocked = "screen_locked";
        public static final String ScreenUnlocked = "screen_unlocked";
        public static final String SessionStarted = "session_started";
        public static final String SettingsActionActivationCode = "settings_activation_code";
        public static final String SettingsActionActivationCodeFailed = "settings_activation_code_failed";
        public static final String SettingsActionActivationCodeSuccess = "settings_activation_code_succeed";
        public static final String SettingsActionLoopCorrectionMode = "settings_loop_correct_mode";
        public static final String SettingsActionResetSoundsVolume = "settings_reset_sounds_volume";
        public static final String SettingsActionShowUpgradeSubscription = "settings_upgrade_subscription";
        public static final String SettingsActionShowWalkthrough = "settings_show_walkthrough";
        public static final String ShowBinauralInfoDialog = "show_binaural_dialog";
        public static final String ShowIsochronicInfoDialog = "show_isochronic_dialog";
        public static final String ShowNewProductDialog = "show_new_product_dialog";
        public static final String ShowNewProductDialogResult = "show_new_product_dialog_result";
        public static final String ShowSubVolumeDoubleTap = "sub_volume_double_tap";
        public static final String ShowSubVolumeLongPress = "sub_volume_long_press";
        public static final String SpecialOfferPopup = "special_offer_popup";
        public static final String StopFavorite = "stop_favorite";
        public static final String StopSound = "sound_stop";
        public static final String SubVolumeChanged = "sub_volume_changed";
        public static final String SubscriptionActivated = "subscription_activated";
        public static final String SubscriptionDeactivated = "subscription_deactivated";
        public static final String SubscriptionProcessCanceled = "subscription_process_failed";
        public static final String SubscriptionProcessCompleted = "subscription_process_succeed";
        public static final String SubscriptionProcessTriggered = "subscription_process";
        public static final String SubscriptionUpgradeProcessCanceled = "subscription_upgrade_process_failed";
        public static final String SubscriptionUpgradeProcessCompleted = "subscription_upgrade_process_succeed";
        public static final String SubscriptionUpgradeProcessTriggered = "subscription_upgrade_process";
        public static final String TimerCompleted = "timer_completed";
        public static final String UpdateFavorite = "update_favorite";

        public EventNames() {
        }
    }

    public class ParamNames {
        public static final String CancelFeatureName = "feature_name";
        public static final String CreateFavoriteResultName = "favorite_name";
        public static final String DownloadFeatureName = "feature_name";
        public static final String FavoriteContainsBinaural = "contains_binaural";
        public static final String FavoriteContainsIsochronic = "contains_isochronic";
        public static final String FavoriteContainsMeditation = "contains_meditation";
        public static final String FeedbackDialogResultAnswer = "answer";
        public static final String MixName = "mix_name";
        public static final String NavigationOtherProductName = "product_name";
        public static final String Percentage = "percentage";
        public static final String PreReviewDialogResultAnswer = "answer";
        public static final String ReviewDialogResultAnswer = "answer";
        public static final String SettingsActionActivationCodeResultSuccessfulFeatureName = "feature_name";
        public static final String SettingsActionActivationCodeResultWasSuccessful = "was_successful";
        public static final String SettingsActionLoopCorrectionModeValue = "loop_value";
        public static final String SettingsActionResetSoundsVolumeDidReset = "did_reset";
        public static final String ShowNewProductDialogResultAnswer = "answer";
        public static final String SoundCategory = "sound_category";
        public static final String SoundCount = "sound_count";
        public static final String SoundId = "sound_id";
        public static final String SoundIds = "sound_ids";
        public static final String SoundName = "sound_name";
        public static final String SoundPlayedLocation = "sound_played_location";
        public static final String SoundVolume = "sound_volume";
        public static final String SoundVolumeDelta = "volume_delta";
        public static final String SoundVolumes = "sound_volumes";
        public static final String SpecialOfferPopupAnswer = "answer";
        public static final String SubscriptionFeatureId = "feature_id";
        public static final String SubscriptionTier = "tier";
        public static final String TimerValue = "timer_value";
        public static final String TimerWillCloseApp = "will_close_app";
        public static final String UpgradeReferer = "upgrade_referer";

        public ParamNames() {
        }
    }

    public class Screens {
        public static final String BinauralInfo = "screen_binaural_info";
        public static final String Blog = "screen_blog";
        public static final String Favorites = "screen_favorites";
        public static final String Help = "screen_help";
        public static final String IsochronicInfo = "screen_isochronic_info";
        public static final String Legal = "screen_legal";
        public static final String Meditations = "screen_meditations";
        public static final String More = "screen_more";
        public static final String News = "screen_news";
        public static final String Settings = "screen_settings";
        public static final String Sounds = "screen_sounds";
        public static final String Timer = "screen_timer";
        public static final String Trial = "screen_trial";
        public static final String TutorialStepPrefix = "screen_tutorial_";
        public static final String Upgrade = "screen_upgrade";

        public Screens() {
        }
    }

    public static enum SoundPlayedLocation {
        binaural_info_dialog,
        guided_meditation_screen,
        isochronic_info_dialog,
        sound_screen;

        private SoundPlayedLocation() {
        }
    }

    public static enum UpgradeReferer {
        binaural,
        binaural_info_dialog,
        cloud_menu,
        favorites,
        guided_meditation,
        isochronic,
        isochronic_info_dialog,
        notification,
        sounds;

        private UpgradeReferer() {
        }
    }
}
