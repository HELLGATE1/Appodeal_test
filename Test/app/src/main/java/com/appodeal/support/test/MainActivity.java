package com.appodeal.support.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;

public class MainActivity extends Activity {
private static long startTime;
private static long currentTime;
private static int counter = 30;
public static boolean show = true;
public static boolean showListView = true;
private TextView counter_tv;
private Button not_show_btn;
private ListView native_ad_lv;
private Handler handler_banner;
private Handler handler_interstitial;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    startTime = System.currentTimeMillis();
    counter_tv = findViewById(R.id.counter_tv);
    not_show_btn = findViewById(R.id.not_show_btn);
    native_ad_lv = findViewById(R.id.native_ad_lv);
    native_ad_lv.setVisibility(View.INVISIBLE);
    String[] array = new String[]{""};
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.item_tv, array);
    native_ad_lv.setAdapter(adapter);
    String appKey = "11c23a1e415967fb2fea7d96adcfe30db1a7511c60bf19d9";
    Appodeal.disableLocationPermissionCheck();
    Appodeal.setTesting(true);
    Appodeal.initialize(this, appKey, Appodeal.INTERSTITIAL | Appodeal.BANNER | Appodeal.NATIVE);
    Appodeal.show(this, Appodeal.BANNER_TOP);
    Appodeal.cache(MainActivity.this, Appodeal.NATIVE);

    handler_banner = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Appodeal.hide(MainActivity.this, Appodeal.BANNER);
        }
    };
    Appodeal.setBannerCallbacks(new BannerCallbacks() {
        @Override
        public void onBannerLoaded(int height, boolean isPrecache) {
        }

        @Override
        public void onBannerFailedToLoad() {
        }

        @Override
        public void onBannerShown() {
            handler_banner.sendEmptyMessageDelayed(0, 5000);
        }

        @Override
        public void onBannerClicked() {
        }
    });

    handler_interstitial = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (show == false) {
                counter_tv.setText("Ad is disabled");
                return;
            } else if (counter == 1) {
                counter_tv.setText("");
                counter = 30;
                Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
            } else if (counter <= 30 && counter > 1) {
                counter--;
                counter_tv.setText("Ad appears through " + counter);
                handler_interstitial.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };
    handler_interstitial.sendEmptyMessageDelayed(0, 1000);

    Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
        @Override
        public void onInterstitialLoaded(boolean isPrecache) {

        }

        @Override
        public void onInterstitialFailedToLoad() {

        }

        @Override
        public void onInterstitialShown() {

        }

        @Override
        public void onInterstitialClicked() {

        }

        @Override
        public void onInterstitialClosed() {
            if (show) {
                counter_tv.setText("Ad appears through " + counter);
                handler_interstitial.sendEmptyMessageDelayed(0, 1000);
            }
        }
    });


    View.OnClickListener butListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.not_show_btn:
                    if (showListView) {
                        showListView = false;
                        final NativeAdViewContentStream nav_stream = findViewById(R.id.native_ad);
                        nav_stream.setNativeAd(Appodeal.getNativeAds(1).get(0));
                        native_ad_lv.setVisibility(View.VISIBLE);
                    }
                    currentTime = System.currentTimeMillis();
                    if ((currentTime - startTime) / 1000 < 30) {
                        show = false;
                    }
                    break;
            }
        }
    };
    not_show_btn.setOnClickListener(butListener);
}

@Override
public void onResume() {
    super.onResume();
    Appodeal.onResume(this, Appodeal.BANNER);
}
}
