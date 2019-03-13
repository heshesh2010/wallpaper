// Generated code from Butter Knife. Do not modify!
package com.wizy.wallpaper;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.nightonke.boommenu.BoomMenuButton;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FullscreenActivity_ViewBinding implements Unbinder {
  private FullscreenActivity target;

  @UiThread
  public FullscreenActivity_ViewBinding(FullscreenActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public FullscreenActivity_ViewBinding(FullscreenActivity target, View source) {
    this.target = target;

    target.mContentView = Utils.findRequiredViewAsType(source, R.id.fullscreen_content, "field 'mContentView'", ImageView.class);
    target.bmb = Utils.findRequiredViewAsType(source, R.id.bmb, "field 'bmb'", BoomMenuButton.class);
    target.mToolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'mToolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    FullscreenActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mContentView = null;
    target.bmb = null;
    target.mToolbar = null;
  }
}
