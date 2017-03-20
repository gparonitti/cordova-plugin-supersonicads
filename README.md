# Supersonic Ads Cordova Plugin
Add support for [Supersonic Ads](https://www.supersonic.com/) to your Cordova and Phonegap based mobile apps. 
This is a fork from the original project by [Carlos \"blakgeek\" Lawton](https://github.com/blakgeek). To him all my thanks. I've simple update 
the android version of this plugin to let it work with the last release of the supersonics add sdk (and I modified the plugin methods accordingly) 

## How do I install it? ##

If you're like me and using [CLI](http://cordova.apache.org/):
```
cordova plugin add cordova-plugin-supersonicads
```

or

```
cordova plugin add https://github.com/gparonitti/cordova-plugin-supersonicads
```

or

```
phonegap local plugin add https://github.com/gparonitti/cordova-plugin-supersonicads
```

## How do I use it? ##

```javascript 1.8
document.addEventListener('deviceready', function() {
  const ssAds = cordova.require('cordova-plugin-supersonicads.SupersonicAds')
  const SSADS_KEY =  'whatever_key_provided_by_supersonic_add'
  const advId = 'user_advertising_id'
	
  ssAds.init(
    SSADS_KEY,
    advId,
    () => loadInterstitial('SupersonicAds init success!! Loading Content!',
    () => console.log('SupersonicAds init error!! Loading Content anyway!')
  )
    
  function loadInterstitial (msg) {
    console.log(msg)
    ssAds.loadInterstitial(
      res => console.log(`SupersonicAds loadInterstitial:  ${res}`),
      err => console.log(`SupersonicAds loadInterstitial error: ${err}`)
    )
  }
    
}, false);
```
The you can use the following methods:

```javascript
  ssAds.showRewardedAd(placementName, claimSpace, successCallback, failureCallback);

  ssAds.showInterstitial(successCallback, failureCallback);

  ssAds.isInterstitialReady(successCallback, failureCallback);

  ssAds.showOfferwall(successCallback, failureCallback);
```
