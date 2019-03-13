// Generated code from Butter Knife. Do not modify!
package com.wizy.wallpaper;

import android.view.View;
import android.widget.EditText;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class Search_ViewBinding implements Unbinder {
  private Search target;

  @UiThread
  public Search_ViewBinding(Search target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public Search_ViewBinding(Search target, View source) {
    this.target = target;

    target.recyclerWallpaper = Utils.findRequiredViewAsType(source, R.id.recyclerWallpaper, "field 'recyclerWallpaper'", RecyclerView.class);
    target.editSearch = Utils.findRequiredViewAsType(source, R.id.editSearch, "field 'editSearch'", EditText.class);
    target.mToolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'mToolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    Search target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recyclerWallpaper = null;
    target.editSearch = null;
    target.mToolbar = null;
  }
}
