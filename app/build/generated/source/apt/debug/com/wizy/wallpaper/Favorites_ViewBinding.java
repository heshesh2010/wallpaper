// Generated code from Butter Knife. Do not modify!
package com.wizy.wallpaper;

import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class Favorites_ViewBinding implements Unbinder {
  private Favorites target;

  @UiThread
  public Favorites_ViewBinding(Favorites target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public Favorites_ViewBinding(Favorites target, View source) {
    this.target = target;

    target.favRecycler = Utils.findRequiredViewAsType(source, R.id.favRecycler, "field 'favRecycler'", RecyclerView.class);
    target.mToolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'mToolbar'", Toolbar.class);
    target.notFoundLayout = Utils.findRequiredViewAsType(source, R.id.notFoundRL, "field 'notFoundLayout'", RelativeLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    Favorites target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.favRecycler = null;
    target.mToolbar = null;
    target.notFoundLayout = null;
  }
}
