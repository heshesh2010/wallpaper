package com.wizy.wallpaper.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CustomSwipeToRefresh extends SwipeRefreshLayout{




    // Save X position in pixels when user touch press down.
    private float pressDownX;
    // Save X position in pixels when user touch release up.
    private float pressUpX;

    private int scaledTouchSlop = 0;

    private RecyclerView recyclerView = null;

    private boolean loading = false;

    private CustomSwipeRefreshLoadMoreListener loadMoreListener;

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        if(this.loading)
        {
            // Show progress dialog.
            this.setRefreshing(true);
        }else
        {
            // Hide progress dialog
            this.setRefreshing(false);

            // Clear old press X value.
            pressUpX = 0;
            pressDownX = 0;
        }
    }

    public CustomSwipeRefreshLoadMoreListener getLoadMoreListener() {
        return loadMoreListener;
    }

    public void setLoadMoreListener(CustomSwipeRefreshLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public CustomSwipeToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Get ViewConfiguration object.
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        // Get minimum distance for touch move action.
        scaledTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(recyclerView == null)
        {
            int childCount = getChildCount();
            if(childCount > 0)
            {
                for( int i=0;i<childCount;i++) {
                    View firstChild = getChildAt(i);
                    if (firstChild instanceof RecyclerView) {
                        recyclerView = (RecyclerView) firstChild;
                        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (canLoadMoreData()) {
                                    loadMoreData();
                                }
                            }
                        });
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int pressAction = ev.getAction();
        float xPosition = ev.getX();

        if(pressAction == MotionEvent.ACTION_UP)
        {
            // If press action up.
            pressUpX = xPosition;
        }else if(pressAction == MotionEvent.ACTION_DOWN)
        {
            // If press action down.
            pressDownX = xPosition;
        }else if(pressAction == MotionEvent.ACTION_MOVE)
        {
            // If press action move.
            if(canLoadMoreData())
            {
                loadMoreData();
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean canLoadMoreData()
    {
        boolean ret = false;

        // If just loading, then can not load more data again.
        if(loading)
        {
            ret = false;
        }else
        {
            float deltaX = pressDownX - pressUpX;

            LinearLayoutManager recyclerViewLayoutManager = null;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if(layoutManager instanceof LinearLayoutManager)
            {
                recyclerViewLayoutManager = (LinearLayoutManager)layoutManager;

            }

            int firstCompleteVisibleItemPosition = 0;
            if (recyclerViewLayoutManager != null) {
                firstCompleteVisibleItemPosition = recyclerViewLayoutManager.findFirstCompletelyVisibleItemPosition();
            }
            int lastCompleteVisibleItemPosition = 0;
            if (recyclerViewLayoutManager != null) {
                lastCompleteVisibleItemPosition = recyclerViewLayoutManager.findLastCompletelyVisibleItemPosition();
            }
            int recyclerViewItemCount  ;

            if(recyclerView.getAdapter()==null){
                recyclerViewItemCount=0;
            }
            else{
                recyclerViewItemCount=   recyclerView.getAdapter().getItemCount();
            }


            float deltaXAbs = Math.abs(deltaX);

            if(deltaXAbs > scaledTouchSlop)
            {
                if(deltaX > 0) {
                    // If scroll from right to left at RecyclerView endding.
                    if(lastCompleteVisibleItemPosition==(recyclerViewItemCount - 1))
                    {
                        ret = true;
                    }

                }else
                {
                    // If scroll from left to right at RecyclerView beginning.
                    if (firstCompleteVisibleItemPosition == 0) {
                        ret = true;
                    }
                }
            }

        }

        return ret;
    }

    private void loadMoreData() {
        if (loadMoreListener != null) {
            setLoading(true);
            loadMoreListener.loadMoreData();
        }
    }

}
