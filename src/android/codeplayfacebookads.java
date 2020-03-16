package cordova.plugin.codeplay.facebookads.free;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.ads.AudienceNetworkAds;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.ads.*;

import static org.apache.cordova.Whitelist.TAG;

/**
 * This class echoes a string called from JavaScript.
 */
public class codeplayfacebookads extends CordovaPlugin {

  private AdView adView;
  private InterstitialAd interstitialAd;

  private final String TAG = NativeAdActivity.class.getSimpleName();
  private NativeAd nativeAd;

  private ViewGroup parentView;
  static boolean isFirstTime = true;
  static boolean isInterstitialLoad = false;
  static boolean isRewardVideoLoad = false;
  private RewardedVideoAd rewardedVideoAd;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

    JSONObject opts = args.optJSONObject(0);

    Context testParameter = (cordova.getActivity()).getBaseContext();
    if (isFirstTime)
      AudienceNetworkAds.initialize(testParameter);

    isFirstTime = false;

    if (action.equals("showBannerAds")) {

      String isTesting;
      String bannerid;

      try {
        bannerid = opts.optString("bannerid");
      } catch (NullPointerException e) {
        callbackContext.error("Please pass the bannerid");
        return false;
      }

      try {
        isTesting = opts.optString("isTesting");
      } catch (NullPointerException e) {
        callbackContext.error("Please pass isTesting value");
        return false;
      }

      // Banner size set here getBannerAdSize(BANNER SIZE)
      adView = new AdView(testParameter, bannerid, getBannerAdSize(""));

      if (Boolean.parseBoolean(isTesting)) {
        SharedPreferences adPrefs = cordova.getActivity().getSharedPreferences("FBAdPrefs", 0);
        String deviceIdHash = adPrefs.getString("deviceIdHash", (String) null);
        AdSettings.addTestDevice(deviceIdHash);
      }

      facebookBannerAdsShow(callbackContext);

      // String message = args.getString(0);
      // this.coolMethod(message, callbackContext);
      return true;
    }

    if (action.equals("hideBannerAds")) {

      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (adView != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
            adView = null;
          }
          callbackContext.success("Facebook banner Ads hide");

          // PluginResult result = new PluginResult(PluginResult.Status.OK, "");
          // callbackContext.sendPluginResult(result);
        }
      });

      return true;

    }

    if (action.equals("loadNativeAds")) {

    }

    if (action.equals("loadInterstitialAds")) {

      String isTesting;
      String interstitialid;

      try {
        interstitialid = opts.optString("interstitialid");
      } catch (NullPointerException e) {
        callbackContext.error("Please pass the interstitial ad id");
        return false;
      }

      try {
        isTesting = opts.optString("isTesting");
      } catch (NullPointerException e) {
        callbackContext.error("Please pass isTesting value");
        return false;
      }

      interstitialAd = new InterstitialAd(testParameter, interstitialid);

      if (Boolean.parseBoolean(isTesting)) {
        SharedPreferences adPrefs = cordova.getActivity().getSharedPreferences("FBAdPrefs", 0);
        String deviceIdHash = adPrefs.getString("deviceIdHash", (String) null);
        AdSettings.addTestDevice(deviceIdHash);
      }

      facebookInterstitialAdsLoad(callbackContext);

      return true;
    }

    if (action.equals("showInterstitialAds")) {

      if (isInterstitialLoad) {
        interstitialAd.show();
        callbackContext.success("Facebook interstitial Ads Showing");
      } else
        callbackContext.error(
            "First initialize the facebook interstitial ads '	cordova.plugins.codeplayfacebookads.loadInterstitialAds(options,success,fail);'");

      return true;
    }

    if (action.equals("loadRewardVideoAd")) {

      String isTesting;
      String videoid;

      try {
        videoid = opts.optString("videoid");
      } catch (NullPointerException e) {
        callbackContext.error("Please pass the videoid");
        return false;
      }

      try {
        isTesting = opts.optString("isTesting");
      } catch (NullPointerException e) {
        callbackContext.error("Please pass isTesting value");
        return false;
      }

      rewardedVideoAd = new RewardedVideoAd(testParameter, videoid);

      if (Boolean.parseBoolean(isTesting)) {
        SharedPreferences adPrefs = cordova.getActivity().getSharedPreferences("FBAdPrefs", 0);
        String deviceIdHash = adPrefs.getString("deviceIdHash", (String) null);
        AdSettings.addTestDevice(deviceIdHash);
      }

      facebookRewardVideoAds(callbackContext);

      return true;
    }

    if (action.equals("showRewardVideoAd")) {

      if (isRewardVideoLoad) {
        interstitialAd.show();
        // callbackContext.success("Facebook interstitial Ads Loaded");
      } else
        callbackContext.error(
            "First initialize the facebook Video ads '	cordova.plugins.codeplayfacebookads.loadRewardVideoAd(videoid,success,fail);'");

      return true;
    }

    return false;
  }

  private void facebookNativeAds(CallbackContext callbackContext) {

    nativeAd = new NativeAd(this, "YOUR_PLACEMENT_ID");

    nativeAd.setAdListener(new NativeAdListener() {
      @Override
      public void onMediaDownloaded(Ad ad) {
        // Native ad finished downloading all assets
        // Log.e(TAG, "Native ad finished downloading all assets.");
        callbackContext.success("Native ad finished downloading all assets.");
      }

      @Override
      public void onError(Ad ad, AdError adError) {
        // Native ad failed to load
        // Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
        callbackContext.error("Native ad failed to load: " + adError.getErrorMessage());
      }

      @Override
      public void onAdLoaded(Ad ad) {
        // Native ad is loaded and ready to be displayed
        // Log.d(TAG, "Native ad is loaded and ready to be displayed!");
        callbackContext.success("Native ad is loaded and ready to be displayed!");
      }

      @Override
      public void onAdClicked(Ad ad) {
        // Native ad clicked
        // Log.d(TAG, "Native ad clicked!");
        callbackContext.success("Native ad clicked!");
      }

      @Override
      public void onLoggingImpression(Ad ad) {
        // Native ad impression
        // Log.d(TAG, "Native ad impression logged!");
        callbackContext.success("Native ad impression logged!");
      }

    });
    nativeAd.loadAd();

  }

  private void facebookRewardVideoAds(CallbackContext callbackContext) {

    rewardedVideoAd.setAdListener(new RewardedVideoAdListener() {
      @Override
      public void onError(Ad ad, AdError error) {
        // Rewarded video ad failed to load
        // Log.e(TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());

        callbackContext.error("Rewarded video ad failed to load: " + error.getErrorMessage());
      }

      @Override
      public void onAdLoaded(Ad ad) {
        // Rewarded video ad is loaded and ready to be displayed
        // Log.d(TAG, "Rewarded video ad is loaded and ready to be displayed!");
        callbackContext.success("Rewarded video ad is loaded and ready to be displayed!");
        rewardedVideoAd.show();
      }

      @Override
      public void onAdClicked(Ad ad) {
        // Rewarded video ad clicked
        // Log.d(TAG, "Rewarded video ad clicked!");
        callbackContext.success("Rewarded video ad clicked!");

      }

      @Override
      public void onLoggingImpression(Ad ad) {
        // Rewarded Video ad impression - the event will fire when the
        // video starts playing
        // Log.d(TAG, "Rewarded video ad impression logged!");
        callbackContext.success("Rewarded video ad impression logged!");
      }

      @Override
      public void onRewardedVideoCompleted() {
        // Rewarded Video View Complete - the video has been played to the end.
        // You can use this event to initialize your reward
        // Log.d(TAG, "Rewarded video completed!");

        callbackContext.success("Rewarded video completed!");
        // Call method to give reward
        // giveReward();
      }

      @Override
      public void onRewardedVideoClosed() {
        // The Rewarded Video ad was closed - this can occur during the video
        // by closing the app, or closing the end card.
        // Log.d(TAG, "Rewarded video ad closed!");
        callbackContext.success("Rewarded video ad closed!");
      }
    });
    rewardedVideoAd.loadAd();

  }

  private void facebookBannerAdsShow(CallbackContext callbackContext) {
    cordova.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {

        View view = webView.getView();
        ViewGroup wvParentView = (ViewGroup) view.getParent();
        if (parentView == null) {
          parentView = new LinearLayout(webView.getContext());
        }

        if (wvParentView != null && wvParentView != parentView) {
          ViewGroup rootView = (ViewGroup) (view.getParent());
          wvParentView.removeView(view);
          ((LinearLayout) parentView).setOrientation(LinearLayout.VERTICAL);
          parentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
          view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));
          parentView.addView(view);
          rootView.addView(parentView);
        }

        parentView.addView(adView);
        parentView.bringToFront();
        parentView.requestLayout();
        parentView.requestFocus();

      }
    });

    adView.setAdListener(new AdListener() {
      @Override
      public void onError(Ad ad, AdError adError) {
        callbackContext.error(adError.getErrorMessage());
      }

      @Override
      public void onAdLoaded(Ad ad) {
        callbackContext.success("Facebook banner Ads loaded");
      }

      @Override
      public void onAdClicked(Ad ad) {
        callbackContext.success("Facebook banner Ads clicked");
      }

      @Override
      public void onLoggingImpression(Ad ad) {
        callbackContext.success("Facebook Ads impression logged");
      }
    });

    // Request an ad
    adView.loadAd();
  }

  private void facebookInterstitialAdsLoad(CallbackContext callbackContext) {
    interstitialAd.setAdListener(new InterstitialAdListener() {
      @Override
      public void onInterstitialDisplayed(Ad ad) {
        isInterstitialLoad = false;
        callbackContext.success("Facebook interstitial Ads displayed.");
      }

      @Override
      public void onInterstitialDismissed(Ad ad) {
        callbackContext.success("Facebook interstitial Ads dismissed");
      }

      @Override
      public void onError(Ad ad, AdError adError) {
        isInterstitialLoad = false;
        callbackContext.error("Facebook interstitial Ads failed to load: " + adError.getErrorMessage());
      }

      @Override
      public void onAdLoaded(Ad ad) {
        isInterstitialLoad = true;
        callbackContext.success("Facebook interstitial Ads is loaded and ready to be displayed!");
      }

      @Override
      public void onAdClicked(Ad ad) {
        callbackContext.success("Facebook interstitial Ads clicked!");
      }

      @Override
      public void onLoggingImpression(Ad ad) {
        callbackContext.success("Facebook interstitial Ads impression logged!");
      }
    });

    // For auto play video ads, it's recommended to load the ad
    // at least 30 seconds before it is shown
    interstitialAd.loadAd();
  }

  protected AdSize getBannerAdSize(String str) {
    AdSize sz;
    if ("BANNER".equals(str)) {
      sz = AdSize.BANNER_HEIGHT_50;
      // other size not supported by facebook audience network: FULL_BANNER,
      // MEDIUM_RECTANGLE, LEADERBOARD, SKYSCRAPER
      // } else if ("SMART_BANNER".equals(str)) {
    } else {
      sz = isTablet() ? AdSize.BANNER_HEIGHT_90 : AdSize.BANNER_HEIGHT_50;
    }

    return sz;
  }

  public boolean isTablet() {
    Configuration conf = cordova.getActivity().getResources().getConfiguration();
    boolean xlarge = ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
    boolean large = ((conf.screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
    return (xlarge || large);
  }

  @Override
  public void onDestroy() {
    if (adView != null) {
      adView.destroy();
    }
    if (interstitialAd != null) {
      interstitialAd.destroy();
    }
    super.onDestroy();
  }

}
