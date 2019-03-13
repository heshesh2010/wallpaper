package com.wizy.wallpaper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.wizy.wallpaper.Util.DownloadUtil;
import com.wizy.wallpaper.Util.DrawerUtil;
import com.wizy.wallpaper.adapter.DatabaseAdapter;
import com.wizy.wallpaper.models.Download;
import com.wizy.wallpaper.models.Results;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;



public class FullscreenActivity extends AppCompatActivity {

    /*LayoutVars*/

    @BindView(R.id.fullscreen_content)
    ImageView mContentView ;

    @BindView(R.id.bmb)
    BoomMenuButton bmb;

    @BindView(R.id.toolbar)
     Toolbar mToolbar;

    ShineButton shineButton ;
    Results model;
    private static final int REQUEST_PERMISSIONS = 200;
    private final Download mDownload = new Download();
    boolean ifLessThankSDKN;
    Bundle savedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        ButterKnife.bind(this);
        this.savedInstanceState=savedInstanceState;
        shineButton  = findViewById(R.id.heart);
        shineButton.init(this);

        setNavigationMenu();
        setBoomMenu();

        // Get Image object from MainActivity or search activity
        model = (Results) getIntent().getSerializableExtra("Result");

        setImage(mContentView, model);

        shineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like();
            }
        });
    }



    private void setBoomMenu() {

        HamButton.Builder builder ;
        Typeface typeface = ResourcesCompat.getFont(this, R.font.montserrat);

        // to set lock screen wallpaper, phone android version should be 24(N) or more
        // if android version lower than 24, user will not see lock and both buttons
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            ifLessThankSDKN=true;
        }

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {

            switch (i) {
                case 0:
                    builder = new HamButton.Builder().listener(index -> {
                        if(ifLessThankSDKN){
                            Toasty.error(FullscreenActivity.this,"This option can't run on device lower than android version 24(N ").show();
                        }
                        else {
                            setLockScreen();
                        }
                    });


                    builder.normalColor(Color.WHITE)
                            .normalTextColor(Color.BLACK)
                            .textSize(20)
                            .typeface(typeface)
                            .pieceColorRes(R.color.line1)
                            .normalTextRes(R.string.set_lock_screen);

                    bmb.addBuilder(builder);

                    break;
                case 1:
                    builder = new HamButton.Builder().listener(index -> setHomeScreen());;
                    builder.normalColor(Color.WHITE)
                            .typeface(typeface)
                            .pieceColorRes(R.color.line2)
                            .normalTextColor(Color.BLACK)
                            .textSize(20)
                            .normalTextRes(R.string.set_home_screen);
                    bmb.addBuilder(builder);
                    break;
                case 2:
                    builder = new HamButton.Builder().listener(index -> {
                        if(ifLessThankSDKN){
                            Toasty.error(FullscreenActivity.this,"This option can't run on device lower than android version 24(N ").show();
                        }
                        else{
                            setBoth();

                        }
                    });
                    builder.normalColor(Color.WHITE)
                            .typeface(typeface)
                            .normalTextColor(Color.BLACK)
                            .textSize(20)
                            .pieceColorRes(R.color.line3)
                            .normalTextRes(R.string.set_both);
                    bmb.addBuilder(builder);
                    break;
                case 3:
                    builder = new HamButton.Builder().listener(index -> downloadListener(model.getUrls().getFit(),model.getId()));;
                    builder.normalColor(Color.WHITE)
                            .typeface(typeface)
                            .normalTextColor(Color.BLACK)
                            .textSize(20)
                            .pieceColorRes(R.color.line4)
                            .normalTextRes(R.string.download);
                    bmb.addBuilder(builder);
                    break;
            }


        }


    }


    private void setNavigationMenu() {
        new DrawerUtil(this,mToolbar, savedInstanceState);
    }



    private void setImage(ImageView mImageView, Results result) {

        Glide.with(this)

                .asBitmap()
                .placeholder(R.drawable.pace_holder)
                .load(result.getUrls().getFit())
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(20)))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mImageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }



// for like button
    private void like() {
        DatabaseAdapter mDbHelper = DatabaseAdapter.getInstance(this);
        mDbHelper.openConnection();
        if (!mDbHelper.checkItemInDb(model.getId())) {
            long insertResult = mDbHelper.insertItemRecord(model.getId(), model.getUrls().getFit());
            if (insertResult!=-1)
                Toasty.success(this, R.string.likesuccess, Toast.LENGTH_LONG, true).show();
        }
        mDbHelper.closeConnection();
    }

    // set both lockScreen and homeScreen
    private void setBoth() {
        WallpaperManager wpm = WallpaperManager.getInstance(this);
        Glide.with(this)
                .asBitmap()
                .load(model.getUrls().getFit())
                .apply(new RequestOptions().onlyRetrieveFromCache(true).transform(new CenterCrop(), new RoundedCorners(20)))
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            wpm.setBitmap(resource,null,true,WallpaperManager.FLAG_SYSTEM); //home screen
                            wpm.setBitmap(resource,null,true,WallpaperManager.FLAG_LOCK); //lock screen
                            Toasty.success(getApplicationContext(), R.string.lockandhomewallpaper, Toast.LENGTH_LONG, true).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
// set lockScreen
    private void setLockScreen() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Glide.with(this)
                .asBitmap()
                .load(model.getUrls().getFit())
                .apply(new RequestOptions().onlyRetrieveFromCache(true).transform(new CenterCrop(), new RoundedCorners(20)))
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK);
                            Toasty.success(getApplicationContext(), R.string.lockscreenwallpaper, Toast.LENGTH_LONG, true).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    // set homeScreen
    private void setHomeScreen() {
        WallpaperManager wpm = WallpaperManager.getInstance(this);
        Glide.with(this)
                .asBitmap()
                .load(model.getUrls().getFit())
                .apply(new RequestOptions().onlyRetrieveFromCache(true).transform(new CenterCrop(), new RoundedCorners(20)))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                wpm.setBitmap(resource,null,true,WallpaperManager.FLAG_SYSTEM);
                            }
                            else
                                wpm.setBitmap(resource);
                            Toasty.success(getApplicationContext(), R.string.homescreenwallpaper, Toast.LENGTH_LONG, true).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    // for download option
    public void downloadListener(String url,String id) {
        if(checkPermission(FullscreenActivity.this)){
            DownloadUtil.start(this,id,url);
        }
        else{
            ActivityCompat.requestPermissions(FullscreenActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
            mDownload.setUrl(url);
            mDownload.setId(id);
        }
    }

    private static boolean checkPermission(Activity activity){
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_PERMISSIONS){
            DownloadUtil.start(this,mDownload.getId(),mDownload.getUrl());
        }
    }


}
