package com.wizy.wallpaper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.security.ProviderInstaller;
import com.view.jameson.library.CardScaleHelper;
import com.wizy.wallpaper.Util.CustomSwipeRefreshLoadMoreListener;
import com.wizy.wallpaper.Util.CustomSwipeToRefresh;
import com.wizy.wallpaper.Util.DrawerUtil;
import com.wizy.wallpaper.adapter.CardRecyclerViewAdapter;
import com.wizy.wallpaper.adapter.ChipAdapter;
import com.wizy.wallpaper.api.GetSearchData;
import com.wizy.wallpaper.models.Results;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {


    /*LayoutVars*/
    @BindView(R.id.recyclerWallpaper)
    RecyclerView recyclerWallpaper ;

    @BindView(R.id.swipeToRefresh)
    CustomSwipeToRefresh mSwipeRefreshLayout ;

    @BindView(R.id.toolbar)
     Toolbar mToolbar;

    /*otherVars*/
    private CardRecyclerViewAdapter myCardAdapter;
    private List<String> chip = new ArrayList<>();

    private CardScaleHelper mCardScaleHelper = null;
    private final Context context = this;
    public static final int ITEMS_PER_AD = 10;
    public List<Object> recyclerViewItems = new ArrayList<>();
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    Bundle savedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        CustomSwipeRefreshLoadMoreListener loadMoreListener = new CustomSwipeRefreshLoadMoreListener(this);
        mSwipeRefreshLayout.setLoadMoreListener(loadMoreListener);
        MobileAds.initialize(this, "ca-app-pub-2315015693339914~9402497869");
        init();
        if(!isConnected(this)) buildDialog(this).show();
        else {
            // we have internet connection, so it is save to connect to the internet here
            setWallpaperRecycler();
        }

    }




    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet connection.");
        builder.setMessage("You have no internet connection");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        return builder;
    }


    private void init() {
        setSSLCertificates();
        setChipsRecycler();
        setNavigationMenu();



            mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(false);

            // get Last chip clicked title
            SharedPreferences prefs = getSharedPreferences("wallpaper", Context.MODE_PRIVATE);
            String restoredText = prefs.getString("chip", "minimal");
            if (restoredText != null) {
                refreshWallPaper(restoredText);
            }
        });

    }



    // first time call
    private void setWallpaperRecycler() {

        //set default chip to minimal
        SharedPreferences.Editor editor = getSharedPreferences("wallpaper", MODE_PRIVATE).edit();
        editor.putString("chip", "minimal");
        editor.apply();

        GetSearchData.buildTest(chip.get(1),this, result -> MainActivity.this.runOnUiThread(() -> {
            if(result==null){
                Toasty.error(MainActivity.this,R.string.no_data_found, Toast.LENGTH_LONG).show();
            }

            else if(result.isEmpty()){
                Toasty.error(MainActivity.this,R.string.no_data_found, Toast.LENGTH_LONG).show();

            }
            else {

                recyclerViewItems.addAll(result);

                addBannerAds();
                loadBannerAds();


                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                recyclerWallpaper.setLayoutManager(linearLayoutManager);


                myCardAdapter = new CardRecyclerViewAdapter(MainActivity.this, recyclerViewItems);
                recyclerWallpaper.setAdapter(myCardAdapter);

                mCardScaleHelper = new CardScaleHelper();
                mCardScaleHelper.setCurrentItemPos(0);
                mCardScaleHelper.attachToRecyclerView(recyclerWallpaper);
            }
        }));
    }


 // called when LoadMore from SwipeRefresh
    public void refreshWallPaper(String chipStr) {
        recyclerViewItems.clear();

        GetSearchData.buildTest(chipStr,this, result -> MainActivity.this.runOnUiThread(() -> {

            if(result==null){
                Toasty.error(MainActivity.this,R.string.no_data_found, Toast.LENGTH_LONG).show();
            }

            else  if(result.isEmpty()){
                Toasty.error(MainActivity.this,R.string.no_data_found,Toasty.LENGTH_SHORT).show();

            }
            else {
                // Stop showing the swipe refresh layout.
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setLoading(false);

                recyclerViewItems.addAll(result);
                addBannerAds();
                loadBannerAds();


                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                recyclerWallpaper.setLayoutManager(linearLayoutManager);


                myCardAdapter = new CardRecyclerViewAdapter(MainActivity.this, recyclerViewItems);
                recyclerWallpaper.setAdapter(myCardAdapter);

                mCardScaleHelper = new CardScaleHelper();
                mCardScaleHelper.setCurrentItemPos(0);
                recyclerWallpaper.setOnFlingListener(null);
                mCardScaleHelper.attachToRecyclerView(recyclerWallpaper);
            }
        }));
    }


    // called when clicked on any chip
    public void setNewWallpapers(String chipStr, ImageButton view) {
        // make sure chip button not clicked many times

        recyclerViewItems.clear();
        GetSearchData.buildTest(chipStr,this, result -> MainActivity.this.runOnUiThread(() -> {

            if(result==null){
                view.setEnabled(true);
                Toasty.error(MainActivity.this,R.string.no_data_found, Toast.LENGTH_LONG).show();
            }

            else  if(result.isEmpty()){
                Toasty.error(MainActivity.this,R.string.no_data_found, Toast.LENGTH_LONG).show();
                view.setEnabled(true);

            }
            else {
                // Stop showing the swipe refresh layout.
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setLoading(false);
                recyclerViewItems.addAll(result);
                MainActivity.this.addBannerAds();
                MainActivity.this.loadBannerAds();


                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                recyclerWallpaper.setLayoutManager(linearLayoutManager);


                myCardAdapter = new CardRecyclerViewAdapter(MainActivity.this, recyclerViewItems);
                recyclerWallpaper.setAdapter(myCardAdapter);

                mCardScaleHelper = new CardScaleHelper();
                mCardScaleHelper.setCurrentItemPos(0);
                recyclerWallpaper.setOnFlingListener(null);
                mCardScaleHelper.attachToRecyclerView(recyclerWallpaper);
                view.setEnabled(true);

            }
        }));
    }

    private void setChipsRecycler() {

        chip.add("SEARCH");
        chip.add("MINIMAL");
        chip.add("DARK");
        chip.add("COLORS");
        chip.add("NATURE");
        chip.add("SEASONS");
        chip.add("ART");
        chip.add("SPACE");
        chip.add("FLORAL ENVY");
        chip.add("WILDLIFE");


        RecyclerView recyclerViewChips = findViewById(R.id.recyclerChip);
        ChipAdapter myChipAdapter= new ChipAdapter(this,chip,loadScreenIcons()) {
            @Override
            public void clickListener(String chip, ImageButton view) {
                setNewWallpapers(chip,view);
            }
        };
        LinearLayoutManager mLayoutManagerComment = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewChips.setLayoutManager(mLayoutManagerComment);
        recyclerViewChips.setItemAnimator(new DefaultItemAnimator());
        recyclerViewChips.setAdapter(myChipAdapter);
        recyclerViewChips.bringToFront();
    }

// to load chips background icons
    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.chipsIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    /**
     * Adds banner ads to the items list.
     */
    private void addBannerAds() {
        // Loop through the items array and place a new banner ad in every ith position in
        // the items List.
        for (int i = 0; i <= recyclerViewItems.size(); i += ITEMS_PER_AD) {
            final AdView adView = new AdView(MainActivity.this);
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
                ProviderInstaller.installIfNeeded(context);
            } catch (Exception ignorable) {
                ignorable.printStackTrace();
            }
        }
    }

    private void setNavigationMenu() {
        new DrawerUtil(this,mToolbar,savedInstanceState);
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
