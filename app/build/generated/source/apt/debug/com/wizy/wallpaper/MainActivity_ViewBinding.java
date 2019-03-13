// Generated code from Butter Knife. Do not modify!
package com.wizy.wallpaper;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.wizy.wallpaper.Util.CustomSwipeToRefresh;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(MainActivity target, View source) {
    this.target = target;

    target.recyclerWallpaper = Utils.findRequiredViewAsType(source, R.id.recyclerWallpaper, "field 'recyclerWallpaper'", RecyclerView.class);
    target.mSwipeRefreshLayout = Utils.findRequiredViewAsType(source, R.id.swipeToRefresh, "field 'mSwipeRefreshLayout'", CustomSwipeToRefresh.class);
    target.mToolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'mToolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recyclerWallpaper = null;
    target.mSwipeRefreshLayout = null;
    target.mToolbar = null;
  }
}
