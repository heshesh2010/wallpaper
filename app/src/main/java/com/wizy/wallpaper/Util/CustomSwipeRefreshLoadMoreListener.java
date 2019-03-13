package com.wizy.wallpaper.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.wizy.wallpaper.MainActivity;

public class CustomSwipeRefreshLoadMoreListener {

    private MainActivity customSwipeRefreshLayoutActivity;

    public CustomSwipeRefreshLoadMoreListener(MainActivity customSwipeRefreshLayoutActivity) {
        this.customSwipeRefreshLayoutActivity = customSwipeRefreshLayoutActivity;
    }

    void loadMoreData()
    {

        SharedPreferences prefs = customSwipeRefreshLayoutActivity.getSharedPreferences("wallpaper", Context.MODE_PRIVATE);
        String restoredText = prefs.getString("chip", null);
        if (restoredText != null) {
            customSwipeRefreshLayoutActivity.refreshWallPaper(restoredText);
        }
    }

}
