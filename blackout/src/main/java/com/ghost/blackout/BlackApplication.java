package com.ghost.blackout;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.ghost.blackout.interfaces.ActivityPhase;
import com.ghost.blackout.services.BServiceS;

public class BlackApplication extends Application implements LifecycleObserver {

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(getObsever());
    }

    private String TAG ="MAIN";

    private DefaultLifecycleObserver getObsever(){
     return    new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
         //       Log.e("MAIN", "onCreate: ta vivo" );
                activityPhase = ActivityPhase.ACTIVE;
            }

            @Override
            public void onStart(@NonNull LifecycleOwner owner) {

            }

            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                activityPhase = ActivityPhase.ACTIVE;
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
              //  Log.e(TAG, "onPause: pause" );
                activityPhase = ActivityPhase.PAUSED;
                if(BServiceS.getInstance() != null){
                    BServiceS.getInstance().goService();
                }
            }

            @Override
            public void onStop(@NonNull LifecycleOwner owner) {
             //   Log.e(TAG, "onStop: stoping" );
                activityPhase = ActivityPhase.STOPPED;
                if(BServiceS.getInstance() != null){
                    BServiceS.getInstance().goService();
                }
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
             //   Log.e("MAIN", "onDestroy: se murio" );
            }
        };
    }

    public static ActivityPhase activityPhase = ActivityPhase.PAUSED;

}
