package com.ghost.blackout.views;

import static com.ghost.blackout.ActivityInner.code_permi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.ghost.blackout.R;
import com.ghost.blackout.services.BServiceS;
import com.wineberryhalley.bclassapp.BottomBaseShet;
import com.wineberryhalley.bclassapp.TinyDB;

import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class OverPermission extends BottomBaseShet {
    @Override
    public int layoutID() {
        return R.layout.over_layout;
    }

    AppCompatActivity activity;

    public OverPermission(AppCompatActivity appCompatActivity) {
        this.activity = appCompatActivity;
        initDB(activity);
    }

    private static TinyDB tinyDB;

    public static void initDB(Context c) {
        if (tinyDB == null)
            tinyDB = new TinyDB(c);
    }

    public void showRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "showRequest: vamo bien " + (Settings.canDrawOverlays(activity)));
            if (activity != null && !Settings.canDrawOverlays(activity))
                show(activity.getSupportFragmentManager(), "madpermisa");
            else if (Settings.canDrawOverlays(activity)) {
                tinyDB.putBoolean("canOveras", true);
            }
        }
    }

    public static boolean canOverDraw() {
        return tinyDB.getBoolean("canOveras", false);
    }



    private TextView desc;
    private View layoutButtons;
    private LottieAnimationView animation;
    @Override
    public void OnStart() {
        View notnot = find(R.id.not_now);
        View grant = find(R.id.grant_btn);

        animation = find(R.id.animation);
        desc = find(R.id.desc_inf);
        layoutButtons = find(R.id.buttons_lay);

        notnot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        grant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestPermission();
            }
        });
    }

    private String TAG ="MAIN";

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult: aja "+result.getResultCode() );
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(activity)) {

                            layoutButtons.setVisibility(View.GONE);

                            ((TextView)find(R.id.title_inf)).setText(R.string.info_granted);
                            desc.setVisibility(View.GONE);
                            animation.setVisibility(View.VISIBLE);
                            animation.playAnimation();
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    dismissAllowingStateLoss();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        initService();
                                    }
                                }
                            }, 2000);
                            Log.e(TAG, "onActivityResult: todo bien" );
                        }

                }
            });

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initService() {

        activity.startForegroundService(new Intent(activity, BServiceS.class));
        //    Log.e(TAG, "onResponse: id es "+id );
        BServiceS.setServiceListener(new BServiceS.ListenerService() {
            @Override
            public void OnServiceStart() {
                BServiceS.getInstance()
                        .startBService();
            }

            @Override
            public void OnServiceStop() {

            }
        });

    }


    private void RequestPermission() {
        // Check if Android M or higher



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(activity)) {
                    if ("xiaomi".equals(Build.MANUFACTURER.toLowerCase(Locale.ROOT))) {
                        final Intent intent =new Intent("miui.intent.action.APP_PERM_EDITOR");
                        intent.setClassName("com.miui.securitycenter",
                                "com.miui.permcenter.permissions.PermissionsEditorActivity");
                        intent.putExtra("extra_pkgname", activity.getPackageName());
                        someActivityResultLauncher.launch(intent);
                    }else {
                        Intent overlaySettings = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
                        someActivityResultLauncher.launch(overlaySettings);
                    }
                }
            }


    }
}
