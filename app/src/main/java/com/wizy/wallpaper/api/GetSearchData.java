package com.wizy.wallpaper.api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.annotation.NonNull;

import com.wizy.wallpaper.models.Results;
import com.wizy.wallpaper.models.Search;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.xml.transform.Result;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.wizy.wallpaper.BuildConfig.API_URL;
import static com.wizy.wallpaper.BuildConfig.CLIENT_ID;

public class GetSearchData {
    private static Retrofit retrofit = null;
    private static  X509TrustManager trustManager;
     private static ProgressDialog progressDoalog;

    private static Retrofit getRetrofitClient(Activity activity) {
        if (retrofit == null) {
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
                trustManager = (X509TrustManager) trustManagers[0];
            }catch (NoSuchAlgorithmException  | KeyStoreException e){
                e.printStackTrace();
            }

            OkHttpClient client=new OkHttpClient();
            try {
                client = new OkHttpClient.Builder()
                        .sslSocketFactory(new TLSSocketFactory(),trustManager)
                        .build();
           } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .callbackExecutor(Executors.newSingleThreadExecutor())
                    .build();
            progressDoalog = new ProgressDialog(activity);
            progressDoalog.setMax(100);
            progressDoalog.setMessage("Its loading....");
            progressDoalog.setTitle("Please wait until download is finish ");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDoalog.setCancelable(false);
            // show it
            progressDoalog.show();
        }

        return retrofit;
    }

    public static void buildTest(String query, Activity activity, getEndless mGetEndless){

        Call<List<Results>> call = getRetrofitClient(activity).create(Unsplash.class).getSearch(query,"portrait",30,CLIENT_ID);

        call.enqueue(new Callback<List<Results>>() {
            @Override
            public void onResponse(@NonNull Call<List<Results>> call, @NonNull Response<List<Results>> response) {
                mGetEndless.get(response.body());
                progressDoalog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<List<Results>> call, @NonNull Throwable t) {
                progressDoalog.dismiss();
                Log.v("error", t.getLocalizedMessage());
            }
        });
    }

    public interface getEndless{
        void get(List<Results> endless);
    }

}

