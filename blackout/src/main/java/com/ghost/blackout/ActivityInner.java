package com.ghost.blackout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;

public class ActivityInner extends AppCompatActivity implements MaxAdListener {

    public static int code_permi = 5054;
    public static boolean isActive = false;
    public static String idInters;
    MaxInterstitialAd  interstitialAd;
    public static int typeClose = 0;
    private boolean isLoading;
    public static int typeFinish = 0;

    private void loadInters(){
        interstitialAd = new MaxInterstitialAd( idInters, this );
        interstitialAd.setListener( this );

        // Load the first ad
         interstitialAd.loadAd();
         isLoading = true;
         moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner);
        isActive = true;
        loadInters();
        typeFinish = 1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActive = false;
        Log.e("MAIN", "onDestroy: se fue" );
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        isLoading = false;
        interstitialAd.showAd();
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {
        if(typeClose == 0){
            finishAndRemoveTask();
        }else{
            finishAffinity();
        }

     //
    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
isLoading = false;
        if(typeClose == 0){
            finishAndRemoveTask();
        }else{
            finishAffinity();
        }
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        isLoading = false;
        if(typeClose == 0){
            finishAndRemoveTask();
        }else{
            finishAffinity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isLoading){
            moveTaskToBack(true);
        }
    }
}