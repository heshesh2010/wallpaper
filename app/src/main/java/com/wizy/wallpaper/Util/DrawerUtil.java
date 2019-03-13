package com.wizy.wallpaper.Util;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wizy.wallpaper.Favorites;
import com.wizy.wallpaper.MainActivity;
import com.wizy.wallpaper.R;
import com.wizy.wallpaper.menu.DrawerAdapter;
import com.wizy.wallpaper.menu.DrawerItem;
import com.wizy.wallpaper.menu.SimpleItem;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import java.util.Arrays;


public class DrawerUtil implements DrawerAdapter.OnItemSelectedListener{

    private static final int POS_Home = 0;
    private static final int POS_FAVORITE = 1;
    private static final int POS_REVIEW = 2;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;
    Activity activity;
    Intent myIntent;
    RecyclerView list;
    public DrawerUtil(Activity activity, Toolbar mToolbar, Bundle savedInstanceState) {
        this.activity=activity;


        slidingRootNav = new SlidingRootNavBuilder(activity)
                .withToolbarMenuToggle(mToolbar)
                .withMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withContentClickableWhenMenuOpened(false)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

       DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_Home),
                createItemFor(POS_FAVORITE),
                createItemFor(POS_REVIEW)));
        adapter.setListener(this);

         list = activity.findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(activity));
        list.setAdapter(adapter);
        slidingRootNav.closeMenu(true);
     //  adapter.setSelected(POS_Home);
    }

    @Override
    public void onItemSelected(int position) {

        list.setVisibility(View.GONE);
     switch (position) {
          case POS_Home:
              myIntent = new Intent(activity, MainActivity.class);
              activity.startActivity(myIntent);
              activity.invalidateOptionsMenu();
              slidingRootNav.closeMenu();
              activity.finish();

              break;
          case POS_FAVORITE:
              myIntent = new Intent(activity, Favorites.class);
              activity.startActivity(myIntent);
              activity.invalidateOptionsMenu();
              slidingRootNav.closeMenu();
              activity.finish();

              break;
          case POS_REVIEW:
              break;

          default:
              break;
      }


        slidingRootNav.closeMenu();

    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.browser_actions_bg_grey))
                .withTextTint(color(R.color.common_google_signin_btn_text_dark_focused))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return activity.getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = activity.getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(activity, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(activity, res);
    }

}
