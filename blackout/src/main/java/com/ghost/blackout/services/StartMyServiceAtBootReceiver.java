package com.ghost.blackout.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.airbnb.lottie.L;
import com.ghost.blackout.network.InnerApi;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.e("MAIN", "call onReceive ACTION_BOOT_COMPLETED");
         //   i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

              InnerApi  innerApi = new InnerApi(context);

                innerApi.load(true);
        }
    }
}
