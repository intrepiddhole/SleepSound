package ipnossoft.rma;

import android.os.Build.VERSION;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import java.util.Iterator;

import ipnossoft.rma.free.R;

public class FragmentSwitcher {
  private Fragment lastShownFragment;
  private final FragmentManager manager;

  public FragmentSwitcher(MainActivity var1) {
    this.manager = var1.getSupportFragmentManager();
  }

  private void hideAllNavigationFragments(FragmentTransaction var1, Class var2) {
    Iterator var3 = this.manager.getFragments().iterator();

    while(true) {
      Fragment var4;
      do {
        do {
          if(!var3.hasNext()) {
            return;
          }

          var4 = (Fragment)var3.next();
        } while(!(var4 instanceof NavigationMenuItemFragment));
      } while(var2 != null && var2.isInstance(var4));

      var4.onPause();
      var1.hide(var4);
    }
  }

  public Fragment getFragmentWithClass(Class var1) {
    Iterator var2 = this.manager.getFragments().iterator();

    Fragment var3;
    do {
      if(!var2.hasNext()) {
        return null;
      }

      var3 = (Fragment)var2.next();
    } while(!var1.isInstance(var3));

    return var3;
  }

  public void switchFragment(Fragment var1, boolean var2) {
    FragmentTransaction var3 = this.manager.beginTransaction();
    if(var2 && VERSION.SDK_INT >= 19) {
      var3.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
    }

    this.hideAllNavigationFragments(var3, var1.getClass());
    Fragment var4 = this.getFragmentWithClass(var1.getClass());
    if(var4 != null && var4 != var1) {
      var3.remove(var4);
    }

    if(var1.getId() != 0 && this.manager.findFragmentById(var1.getId()) != null) {
      var1.onResume();
      var3.show(var1);
    } else {
      var3.add(R.id.top_fragment, var1);
    }

    var3.commit();
  }

  public void switchHome() {
    FragmentTransaction var1 = this.manager.beginTransaction();
    if(VERSION.SDK_INT >= 19) {
      var1.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
    }

    this.hideAllNavigationFragments(var1, (Class)null);
    var1.commit();
  }
}
