package com.wizy.wallpaper;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.RelativeLayout;

import com.wizy.wallpaper.Util.DrawerUtil;
import com.wizy.wallpaper.adapter.DatabaseAdapter;
import com.wizy.wallpaper.adapter.FavAdapter;
import com.wizy.wallpaper.models.Fav;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Favorites extends AppCompatActivity {

    /*LayoutVars*/
    @BindView(R.id.favRecycler)
     RecyclerView favRecycler;
    @BindView(R.id.toolbar)
     Toolbar mToolbar;
    @BindView(R.id.notFoundRL)
    RelativeLayout notFoundLayout;

    /*otherVars*/
    private List<Fav> myFav = new ArrayList<>();
    Bundle savedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ButterKnife.bind(this);
        this.savedInstanceState=savedInstanceState;

        setNavigationMenu();
        setAdapter();

    }

    private void setNavigationMenu() {
        new DrawerUtil(this,mToolbar, savedInstanceState);
    }


    private void setAdapter() {
        DatabaseAdapter mDbHelper = DatabaseAdapter.getInstance(Favorites.this);
        mDbHelper.openConnection();

        Cursor getAllRecords  = mDbHelper.getAllItemRecords();
        if (getAllRecords==null) {
            notFoundLayout.setVisibility(View.VISIBLE);
            return;
        }
        else if (getAllRecords.moveToFirst()) {
            notFoundLayout.setVisibility(View.GONE);
            do {
                String url = getAllRecords.getString(getAllRecords
                        .getColumnIndex("url"));
                String imgId = getAllRecords.getString(getAllRecords
                        .getColumnIndex("img_id"));
                Fav fav = new Fav();
                fav.setUrl(url);
                fav.setImg_id(imgId);
                myFav.add(fav);
            } while (getAllRecords.moveToNext());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            favRecycler.setLayoutManager(linearLayoutManager);
            FavAdapter myFavAdapter= new FavAdapter(myFav, this);
            favRecycler.setAdapter(myFavAdapter);
        }
        getAllRecords.close();
    }
}
