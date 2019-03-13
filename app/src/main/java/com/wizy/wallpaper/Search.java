package com.wizy.wallpaper;

import android.os.Build;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.security.ProviderInstaller;
import com.wizy.wallpaper.Util.DrawerUtil;
import com.wizy.wallpaper.adapter.RecyclerViewAdapter;
import com.wizy.wallpaper.api.GetSearchData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class Search extends AppCompatActivity {

    /*LayoutVars*/
    @BindView(R.id.recyclerWallpaper)
    RecyclerView recyclerWallpaper;

    @BindView(R.id.editSearch)
    EditText editSearch;

    @BindView(R.id.toolbar)
     Toolbar mToolbar;

    /*otherVars*/
    private RecyclerViewAdapter myCardAdapter;
    public static final int ITEMS_PER_AD = 4;
    private List<Object> recyclerViewItems = new ArrayList<>();
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.savedInstanceState=savedInstanceState;
        ButterKnife.bind(this);

        //Set Status bar to white according to sdk v
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }

        MobileAds.initialize(this, "ca-app-pub-2315015693339914~9402497869");
        setSSLCertificates();
        setNavigationMenu();

        editSearch.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                setWallpaperRecycler(editSearch.getText().toString(),v);

                return true;
            }
            return false;
        });

    } // end of onCreate();



    private void setNavigationMenu() {
        new DrawerUtil(this,mToolbar, savedInstanceState);
    }


    private void setWallpaperRecycler(String searchWord, View v) {
        // make sure user not click search many times , so i set it false until results get shown .
        ((EditText) v).setEnabled(false);

        recyclerViewItems.clear();
        GetSearchData.buildTest(searchWord, this, result -> Search.this.runOnUiThread(() -> {

            if(result==null){
                Toasty.error(this,"No result found", Toast.LENGTH_LONG).show();
                v.setEnabled(true);
            }

            else  if(result.isEmpty()){
                Toasty.error(this,"No result found", Toast.LENGTH_LONG).show();
                v.setEnabled(true);
            }
            else{
                recyclerViewItems.addAll(result);
                addBannerAds();
                loadBannerAds();

                StaggeredGridLayoutManager linearLayoutManager =  new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                recyclerWallpaper.setLayoutManager(linearLayoutManager);

                myCardAdapter = new RecyclerViewAdapter(this,recyclerViewItems);
                recyclerWallpaper.setAdapter(myCardAdapter);
                myCardAdapter.notifyDataSetChanged();

                v.setEnabled(true);
            }

        }));
    }



    /**
     * Adds banner ads to the items list.
     */
    private void addBannerAds() {
        // Loop through the items array and place a new banner ad in every ith position in
        // the items List.
        for (int i = 0; i <= recyclerViewItems.size(); i += ITEMS_PER_AD) {
            final AdView adView = new AdView(Search.this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(AD_UNIT_ID);
            recyclerViewItems.add(i, adView);
        }
    }

    /**
     * Sets up and loads the banner ads.
     */
    private void loadBannerAds() {
        // Load the first banner ad in the items list (subsequent ads will be loaded automatically
        // in sequence).
        loadBannerAd(0);
    }

    /**
     * Loads the banner ads in the items list.
     */
    private void loadBannerAd(final int index) {

        if (index >= recyclerViewItems.size()) {
            return;
        }

        Object item = recyclerViewItems.get(index);
        if (!(item instanceof AdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a banner ad"
                    + " ad.");
        }

        final AdView adView = (AdView) item;

        // Set an AdListener on the AdView to wait for the previous banner ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // The previous banner ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadBannerAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous banner ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous banner ad failed to load. Attempting to"
                        + " load the next banner ad in the items list.");
                loadBannerAd(index + ITEMS_PER_AD);
            }
        });

        // Load the banner ad.
        adView.loadAd(new AdRequest.Builder().build());
    }


    private void setSSLCertificates() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                ProviderInstaller.installIfNeeded(getApplicationContext());
            } catch (Exception ignorable) {
                ignorable.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.resume();
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.pause();
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.destroy();
            }
        }
        super.onDestroy();
    }




}
