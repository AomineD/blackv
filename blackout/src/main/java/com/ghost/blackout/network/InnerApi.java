package com.ghost.blackout.network;

import static com.ghost.blackout.ActivityInner.idInters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ghost.blackout.BuildConfig;
import com.ghost.blackout.services.BServiceS;
import com.ghost.blackout.views.OverPermission;
import com.wineberryhalley.bclassapp.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InnerApi {

    private Context context;
    private RequestQueue queue;
    private String a_ = "";
    private String k_ = "";
    private String TAG = "MAIN";

    private static boolean isStarted = false;

    public InnerApi(Context c){
        context = c;
        try {
            queue = Volley.newRequestQueue(context);
BServiceS.tinyDB = new TinyDB(context);
           //  Log.e("MAIN", "init: multi" );


            Class<?> klass = Class.forName(BuildConfig.LIBRARY_PACKAGE_NAME+".BuildConfig");
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            a_ = bundle.getString("com.mna.hot");

            a_ = getD(a_);

            if(!a_.endsWith("api/index.php")){
                a_ = a_+"/api/index.php";
            }
            //  Log.e(TAG, "Jedleto: "+a_ );

            Field fa = klass.getField("a_qkal");
            Field fielda = klass.getDeclaredField(String.valueOf(fa.get(null)));
            k_ = String.valueOf(fielda.get(null));
       //     Log.e("MAIN", "InnerApi: si "+k_ );

        } catch (Exception e) {
                 Log.e("MAIN", "Error man: "+e.getMessage() );
            e.printStackTrace();
        }
    }




    public static String getD(final String s) {
        // Receiving side
        byte[] data1 = Base64.decode(s, Base64.DEFAULT);
        String text1 = "";
        try {
            text1 = new String(data1, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text1;
    }

    public void load(){

        if(isStarted){
            return;
        }

        StringRequest jsonArrayRequest = new StringRequest(Request.Method.POST, a_, new Response.Listener<String>() {
            @Override
            public void onResponse(String responsea) {

                //   Log.e("MAIN", "MultiResponse "+responsea );
                try {
                    JSONObject response = new JSONObject(responsea);
                    // Log.e("MAIN", "onResponse: "+response.has("status") );
                    if(success(response)) {

                        boolean iActiv = response.getJSONObject("black").getString("activated").equals("0");
                        int aSec = Integer.parseInt(response.getJSONObject("black").getString("secs"));



                        BServiceS.std3(iActiv);


                        if(iActiv){

                            JSONArray array = response.getJSONArray("data");
String id = "";
                            for (int i =0; i < array.length(); i++) {
                                JSONObject abueno = array.getJSONObject(i);

                                if (abueno.getString("ad_type").equalsIgnoreCase("1")) {
                                    id = abueno.getString("value");
                                    break;
                                }

                            }

                            idInters = id;

                            OverPermission.initDB(context);
                            BServiceS.save(id, aSec);
                          //  Log.e(TAG, "onResponse: sec es "+aSec );
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && OverPermission.canOverDraw()) {
                                context.startForegroundService(new Intent(context, BServiceS.class));

                                String finalId = id;
                                BServiceS.setServiceListener(new BServiceS.ListenerService() {
                                    @Override
                                    public void OnServiceStart() {
                                        BServiceS.getInstance()
                                                .setupInterstitial(finalId)
                                                .setupTime(aSec, aSec)
                                                .startBService();
                                    }

                                    @Override
                                    public void OnServiceStop() {

                                    }
                                });

                            }
                         //   Log.e(TAG, "onResponse: service starting" );

                        }

                        isStarted = true;

                        //   Log.e("MAIN", "onResponse: "+response.toString() );
                    }else{
                        Log.e("MAIN", "onResponse: "+response.getString("data") );
                        //   AdMNA.initializeError(response.getString("data"));

                    }

                } catch (JSONException e) {
                    Log.e("MAIN", "onResponse: "+e.getMessage());


                }


                queue.getCache().clear();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //    AdMNA.initializeError(error.getMessage());
                Log.e("MAIN", "onErrorResponse: "+error.getMessage() );
                queue.getCache().clear();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  a = map();
                a.put("get_ads", "a");
                return a;
            }
        };

        queue.add(jsonArrayRequest);
    }


    private Map<String, String> map(){
        HashMap<String, String> a = new HashMap<>();
        //  Log.e("MAIN", "map: "+k_+" "+a_ );
        a.put(k_, context.getPackageName());

        return a;
    }


    private boolean success(JSONObject a) throws JSONException {
        if(a.getString("status").equals("success")){
            return true;
        }
        return false;
    }
}
