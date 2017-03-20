(function() {
    var cordovaRef = window.PhoneGap || window.cordova || window.Cordova;

    function SupersonicAds() {
    }

    SupersonicAds.prototype.init = function(appKey, userId, successCallback, failureCallback) {
        cordova.exec(successCallback, failureCallback, 'SupersonicAdsPlugin', 'init', [appKey, userId]);
    };

    SupersonicAds.prototype.showRewardedAd = function(placementName, claimSpace, successCallback, failureCallback) {
        cordova.exec(successCallback, failureCallback, 'SupersonicAdsPlugin', 'showRewardedAd', [placementName]);
    };

    SupersonicAds.prototype.showInterstitial = function(successCallback, failureCallback) {
        cordova.exec(successCallback, failureCallback, 'SupersonicAdsPlugin', 'showInterstitial', []);
    };

    SupersonicAds.prototype.loadInterstitial = function(successCallback, failureCallback) {
        cordova.exec(successCallback, failureCallback, 'SupersonicAdsPlugin', 'loadInterstitial', []);
    };

    SupersonicAds.prototype.isInterstitialReady = function(successCallback, failureCallback) {
        cordova.exec(successCallback, failureCallback, 'SupersonicAdsPlugin', 'isInterstitialReady', []);
    };

    SupersonicAds.prototype.showOfferwall = function(successCallback, failureCallback) {
        cordova.exec(successCallback, failureCallback, 'SupersonicAdsPlugin', 'showOfferwall', []);
    };

    if(cordovaRef && cordovaRef.addConstructor) {
        cordovaRef.addConstructor(init);
    } else {
        init();
    }

    function init() {
        if(!window.plugins) {
            window.plugins = {};
        }
        if(!window.plugins.TagManager) {
            window.plugins.TagManager = new SupersonicAds();
        }
    }

    if(typeof module != undefined && module.exports) {
        module.exports = new SupersonicAds();
    }
})();