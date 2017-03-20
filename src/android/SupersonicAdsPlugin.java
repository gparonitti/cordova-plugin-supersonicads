package com.blakgeek.cordova.plugin.supersonicads;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.supersonic.adapters.supersonicads.SupersonicConfig;
import com.supersonic.mediationsdk.logger.SupersonicError;
import com.supersonic.mediationsdk.model.Placement;
import com.supersonic.mediationsdk.sdk.InterstitialListener;
import com.supersonic.mediationsdk.sdk.OfferwallListener;
import com.supersonic.mediationsdk.sdk.RewardedVideoListener;
import com.supersonic.mediationsdk.sdk.Supersonic;
import com.supersonic.mediationsdk.sdk.SupersonicFactory;

public class SupersonicAdsPlugin extends CordovaPlugin {

    private static final String TAG = "[SupersonicAdsPlugin]";

    private static final String EVENT_INTERSTITIAL_INITIALIZED = "onInterstitialInitSuccess";
    private static final String EVENT_INTERSTITIAL_INIT_FAILED = "onInterstitialInitFailed";
    private static final String EVENT_INTERSTITIAL_LOAD_FAILED = "onInterstitialLoadFailed";
    private static final String EVENT_INTERSTITIAL_READY = "onInterstitialReady";
    private static final String EVENT_INTERSTITIAL_SHOWN = "onInterstitialShowSuccess";
    private static final String EVENT_INTERSTITIAL_SHOW_FAILED = "onInterstitialShowFailed";
    private static final String EVENT_INTERSTITIAL_CLICK = "onInterstitialClick";
    private static final String EVENT_INTERSTITIAL_CLOSED = "onInterstitialClose";

    private static final String EVENT_OFFERWALL_CLOSED = "offerwallClosed";
    private static final String EVENT_OFFERWALL_CREDIT_FAILED = "offerwallCreditFailed";
    private static final String EVENT_OFFERWALL_CREDITED = "offerwallCreditReceived";
    private static final String EVENT_OFFERWALL_SHOW_FAILED = "offerwallShowFailed";
    private static final String EVENT_OFFERWALL_OPENED = "offerwallOpened";
    private static final String EVENT_OFFERWALL_INIT_FAILED = "offerwallInitializationFailed";
    private static final String EVENT_OFFERWALL_INITIALIZED = "offerwallInitialized";

    private static final String EVENT_REWARDED_VIDEO_REWARDED = "rewardedVideoRewardReceived";
    private static final String EVENT_REWARDED_VIDEO_ENDED = "rewardedVideoEnded";
    private static final String EVENT_REWARDED_VIDEO_STARTED = "rewardedVideoStarted";
    private static final String EVENT_REWARDED_VIDEO_AVAILBILITY_CHANGED = "rewardedVideoAvailabilityChanged";
    private static final String EVENT_REWARDED_VIDEO_CLOSED = "rewardedVideoClosed";
    private static final String EVENT_REWARDED_VIDEO_OPENED = "rewardedVideoOpened";
    private static final String EVENT_REWARDED_VIDEO_INIT_FAILED = "rewardedVideoInitializationFailed";
    private static final String EVENT_REWARDED_VIDEO_INITIALIZED = "rewardedVideoInitialized";
    private Supersonic supersonic;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        pluginInit();
    }

    @Override
    public void onReset() {
        Log.i(TAG, "onReset");
        super.onReset();
//            if (supersonic != null) {}
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        if (supersonic != null) {
            Log.i(TAG, "onResume");
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        if (supersonic != null) {
            Log.i(TAG, "onPause");
        }
    }


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            Log.i(TAG, action);
            if (action.equals("init")) {
                init(args.getString(0), args.getString(1));
                callbackContext.success();
            }
            else if (action.equals("showRewardedAd")) {
                if (args.isNull(0)) {
                    supersonic.showRewardedVideo();
                }
                else {
                    supersonic.showRewardedVideo(args.optString(0));
                }
                callbackContext.success();
            }
            else if (action.equals("loadInterstitial")) {
                supersonic.loadInterstitial();
                callbackContext.success();
            }
            else if (action.equals("showInterstitial")) {
                if (args.isNull(0)) {
                    supersonic.showInterstitial();
                }
                else {
                    supersonic.showInterstitial(args.optString(0));
                }
                callbackContext.success();
            }
            else if (action.equals("isInterstitialReady")) {
                supersonic.isInterstitialReady();
                callbackContext.success();
            }
            else if (action.equals("showOfferwall")) {
                supersonic.showOfferwall();
                callbackContext.success();
            }
            else {
                callbackContext.error("Unknown Action");
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e("SupersonicAdsPlugin", e.getMessage());

            callbackContext.error("SupersonicAdsPlugin: " + e.getMessage());
            return false;
        }
    }

    private void init(String appKey, String userId) {
        Log.i(TAG, "Listener Initialized!!!");
        supersonic.setInterstitialListener(interstitialListener);
        supersonic.setOfferwallListener(offerwallListener);
        supersonic.setRewardedVideoListener(rewardedVideoListener);

        Log.i(TAG, String.format("key: %s, userId: %s", appKey, userId));
        supersonic.initInterstitial(this.cordova.getActivity(), appKey, userId);
        supersonic.initOfferwall(this.cordova.getActivity(), appKey, userId);
        supersonic.initRewardedVideo(this.cordova.getActivity(), appKey, userId);

//        pluginInit();

        // Change config at runtime example #1 - set client side callbacks for the offerwall product
        SupersonicConfig.getConfigObj().setClientSideCallbacks(true);
    }

    private void pluginInit() {
        Log.i(TAG, "pluginInit for cordova");
        supersonic = SupersonicFactory.getInstance();

//        supersonic.setInterstitialListener(interstitialListener);
//        supersonic.setOfferwallListener(offerwallListener);
//        supersonic.setRewardedVideoListener(rewardedVideoListener);
    }

    private void fireEvent(final String event) {
        final CordovaWebView view = this.webView;
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.loadUrl("javascript:cordova.fireWindowEvent('" + event + "');");
            }
        });
    }

    private void fireEvent(final String event, final JSONObject data) {

        final CordovaWebView view = this.webView;
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.loadUrl(String.format("javascript:cordova.fireWindowEvent('%s', %s);", event, data.toString()));
            }
        });
    }

    //Rewarded video listener section
    RewardedVideoListener rewardedVideoListener = new RewardedVideoListener() {

        @Override
        public void onRewardedVideoInitSuccess() {
            // Supported: All
            // Invoked when initialization of RewardedVideo has finished successfully
            Log.d(TAG, "onRewardedVideoInitSuccess");
            fireEvent(EVENT_REWARDED_VIDEO_INITIALIZED);
        }


        @Override
        public void onRewardedVideoInitFail(SupersonicError supersonicError) {
            //Supported: Supersonic, Vungle, Applovin, UnityAds
            //Invoked when RewardedVideo initialization process has failed. SupersonicError
            //contains the reason for the failure.
            Log.d(TAG, "onRewardedVideoInitFail : " + supersonicError.toString());

            JSONObject data = new JSONObject();
            JSONObject error = new JSONObject();
            try {
                error.put("code", supersonicError.getErrorCode());
                error.put("message", supersonicError.getErrorMessage());
                data.put("error", error);
            } catch (JSONException e) {
            }
            fireEvent(EVENT_REWARDED_VIDEO_INIT_FAILED, data);
        }

        //Invoked when RewardedVideo call to show a rewarded video has failed
        //SupersonicError contains the reason for the failure.
        @Override
        public void onRewardedVideoShowFail(SupersonicError se) {
        }

        @Override
        public void onRewardedVideoAdOpened() {
            //Supported: Flurry, Vungle, AppLovin, UnityAds
            //Invoked when the RewardedVideo ad view has opened.
            Log.d(TAG, "onRewardedVideoAdOpened");

            fireEvent(EVENT_REWARDED_VIDEO_OPENED);
        }


        @Override
        public void onRewardedVideoAdClosed() {
            //Supported: All
            //Invoked when the user is about to return to the application after closing the
            //RewardedVideo ad.

            Log.d(TAG, "onRewardedVideoAdClosed");
            fireEvent(EVENT_REWARDED_VIDEO_CLOSED);
        }


        @Override
        public void onVideoAvailabilityChanged(boolean available) {
            //Supported: All
            //Invoked when there is a change in the ad availability status.
            //@param - available - value will change to true when rewarded videos
            //are available - you can present a video by calling showRewardedVideo(). value will
            //change to false when no videos are available.

            Log.d(TAG, "onVideoAvailabilityChanged : " + available);
            JSONObject data = new JSONObject();
            try {
                data.put("available", available);
            } catch (JSONException e) {
            }
            fireEvent(EVENT_REWARDED_VIDEO_AVAILBILITY_CHANGED, data);

        }


        @Override
        public void onVideoStart() {
            //Supported: Adcolony, Vungle, AppLovin, UniyAds
            //Invoked when the video ad starts playing.
            Log.d(TAG, "onVideoStart");
            fireEvent(EVENT_REWARDED_VIDEO_STARTED);
        }

        @Override
        public void onVideoEnd() {
            //Supported: Flurry, Vungle, AppLovin, UnityAds
            //Invoked when the video ad finishes playing.
            Log.d(TAG, "onVideoEnd");
            fireEvent(EVENT_REWARDED_VIDEO_ENDED);
        }


        @Override
        public void onRewardedVideoAdRewarded(Placement placement) {
            //Supported: All
            // Invoked when the user get rewarded from watching the video.
            // When using server-to-server callbacks you may ignore this events and wait
            // for server callback in order to reward your user.
            // @param - Placement

            // Get the information of the placement
            String rewardName = placement.getRewardName();
            int rewardAmount = placement.getRewardAmount();
            Log.d(TAG, "onRewardedVideoAdRewarded: rewardName=" + rewardName + ", rewardAmount=" + rewardAmount);
            JSONObject data = new JSONObject();
            JSONObject wrapper = new JSONObject();
            try {
//                data.put("id", placement.getId());
                data.put("name", placement.getPlacementName());
                data.put("reward", placement.getRewardName());
                data.put("amount", placement.getRewardAmount());
                wrapper.put("placement", data);
            } catch (JSONException e) {
            }
            fireEvent(EVENT_REWARDED_VIDEO_REWARDED, wrapper);
        }
    };

    OfferwallListener offerwallListener = new OfferwallListener() {


        @Override
        public void onOfferwallInitSuccess() {
            //  Invoked when the OfferWall is prepared an ready to be shown to the user
            Log.d(TAG, "onOfferwallInitSuccess");
            fireEvent(EVENT_OFFERWALL_INITIALIZED);

        }


        @Override
        public void onOfferwallInitFail(SupersonicError supersonicError) {
            //  Invoked when the OfferWall does not load for the user.
            Log.d(TAG, "onOfferwallInitFail : " + supersonicError.toString());
            fireEvent(EVENT_OFFERWALL_INIT_FAILED);
        }

        @Override
        public void onOfferwallOpened() {
            //Invoked when the OfferWall successfully loads for the user.
            Log.d(TAG, "onOfferwallOpened");
            fireEvent(EVENT_OFFERWALL_OPENED);
        }

        /**
         *
         */
        @Override
        public void onOfferwallShowFail(SupersonicError supersonicError) {
            //Invoked when the method 'showOfferWall' is called and the OfferWall fails to load.
            //Handle initialization error here.
            //@param supersonicError - A SupersonicError Object which represents the reason of 'showOfferWall' failure.
            Log.d(TAG, "onOfferwallShowFail : " + supersonicError.toString());

            JSONObject data = new JSONObject();
            JSONObject error = new JSONObject();
            try {
                error.put("code", supersonicError.getErrorCode());
                error.put("message", supersonicError.getErrorMessage());
                data.put("error", error);
            } catch (JSONException e) {
            }
            fireEvent(EVENT_OFFERWALL_SHOW_FAILED, data);
        }


        @Override
        public boolean onOfferwallAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {

            // Invoked each time the user completes an Offer.
            // Award the user with the credit amount corresponding to the value of the ‘credits’
            // parameter.
            // @param credits - The number of credits the user has earned.
            // @param totalCredits - The total number of credits ever earned by the user.
            // @param totalCreditsFlag - In some cases, we won’t be able to provide the exact
            // amount of credits since the last event (specifically if the user clears
            // the app’s data). In this case the ‘credits’ will be equal to the ‘totalCredits’,
            // and this flag will be ‘true’.
            // @return boolean - true if you received the callback and rewarded the user,
            // otherwise false.
            Log.d(TAG, "onOfferwallAdCredited : " + credits + " ," + totalCredits + " ," + totalCreditsFlag);
            JSONObject data = new JSONObject();
            JSONObject wrapper = new JSONObject();
            try {
                data.put("amount", credits);
                data.put("total", totalCredits);
                data.put("estimated", totalCreditsFlag);
                wrapper.put("credit", data);
            } catch (JSONException e) {
            }
            fireEvent(EVENT_OFFERWALL_CREDITED, wrapper);
            return true;
        }


        @Override
        public void onGetOfferwallCreditsFail(SupersonicError supersonicError) {

            // Invoked when the method 'getOfferWallCredits' fails to retrieve
            // the user's credit balance info.
            // @param supersonicError -A SupersonicError Object which represents the reason of 'getOfferWallCredits'
            // failure.
            Log.d(TAG, "onGetOfferwallCreditsFail : " + supersonicError.toString());

            JSONObject data = new JSONObject();
            JSONObject error = new JSONObject();
            try {
                error.put("code", supersonicError.getErrorCode());
                error.put("message", supersonicError.getErrorMessage());
                data.put("error", error);
            } catch (JSONException e) {
            }
            fireEvent(EVENT_OFFERWALL_CREDIT_FAILED, data);
        }

        @Override
        public void onOfferwallClosed() {
            // Invoked when the user is about to return to the application after closing
            //  the Offerwall.
            Log.d(TAG, "onOfferwallClosed");
            fireEvent(EVENT_OFFERWALL_CLOSED);
        }
    };

    InterstitialListener interstitialListener = new InterstitialListener() {

        @Override
        public void onInterstitialInitSuccess() {
            // Invoked when Interstitial initialization process completes successfully.
            Log.d(TAG, "onInterstitialInitSuccess");
            fireEvent(EVENT_INTERSTITIAL_INITIALIZED);
        }

        @Override
        public void onInterstitialInitFailed(SupersonicError supersonicError) {
            //Invoked when Interstitial initialization process is failed.
            //@param supersonicError - A SupersonicError Object which represents the reason of initialization failure.
            Log.d(TAG, "onInterstitialInitFailed : " + supersonicError.toString());

            JSONObject data = new JSONObject();
            JSONObject error = new JSONObject();
            try {
                error.put("code", supersonicError.getErrorCode());
                error.put("message", supersonicError.getErrorMessage());
                data.put("error", error);
            } catch (JSONException e) {
            }
            fireEvent(EVENT_INTERSTITIAL_INIT_FAILED, data);
        }

        @Override
        public void onInterstitialReady() {
            //Invoked when interstitial availability state has changed.
            //@param available - boolean - true when ad is ready to be displayed, otherwise false.
            Log.d(TAG, "Interstitial is Ready");
            JSONObject data = new JSONObject();
            try {
                data.put("available", true);
            } catch (JSONException e) {
            }
            fireEvent(EVENT_INTERSTITIAL_READY, data);
        }

        @Override
        public void onInterstitialLoadFailed(SupersonicError supersonicError){
            Log.d(TAG, "onInterstitialLoadFailed : " + supersonicError.toString());

            JSONObject data = new JSONObject();
            JSONObject error = new JSONObject();
            try {
                error.put("code", supersonicError.getErrorCode());
                error.put("message", supersonicError.getErrorMessage());
                data.put("error", error);
            } catch (JSONException e) {
            }
            fireEvent(EVENT_INTERSTITIAL_LOAD_FAILED, data);
        }

        @Override
        public void onInterstitialShowSuccess() {
            //Invoked when the ad was opened and shown successfully.
            Log.d(TAG, "onInterstitialShowSuccess");
            fireEvent(EVENT_INTERSTITIAL_SHOWN);
        }

        @Override
        public void onInterstitialShowFailed(SupersonicError supersonicError) {
            //Invoked when Interstitial ad failed to show.
            //@param supersonicError - A SupersonicError object representing the error
            Log.d(TAG, "onInterstitialShowFailed : " + supersonicError.toString());

            JSONObject data = new JSONObject();
            JSONObject error = new JSONObject();
            try {
                error.put("code", supersonicError.getErrorCode());
                error.put("message", supersonicError.getErrorMessage());
                data.put("error", error);
            } catch (JSONException e) {
            }
            fireEvent(EVENT_INTERSTITIAL_SHOW_FAILED, data);
        }

        @Override
        public void onInterstitialClick() {
            //Invoked when the end user clicked on the interstitial ad.
            Log.d(TAG, "onInterstitialClick");
            fireEvent(EVENT_INTERSTITIAL_CLICK);
        }

        @Override
        public void onInterstitialClose() {
            //Invoked when the ad is closed and the user is about to return to the application
            Log.d(TAG, "onInterstitialClose");
            fireEvent(EVENT_INTERSTITIAL_CLOSED);
        }

        @Override
        public void onInterstitialOpen(){}
    };
}
