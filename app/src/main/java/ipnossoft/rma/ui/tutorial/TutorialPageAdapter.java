package ipnossoft.rma.ui.tutorial;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class TutorialPageAdapter extends FragmentStatePagerAdapter {
  private int count = 5;
  private List<TutorialFragment> tutorialFragmentList = new ArrayList();

  public TutorialPageAdapter(FragmentManager var1) {
    super(var1);

    for(int var2 = 0; var2 < this.count; ++var2) {
      this.tutorialFragmentList.add(new TutorialFragment());
    }

  }

  public int getCount() {
    return this.count;
  }

  public Fragment getItem(int var1) {
    return (Fragment)this.tutorialFragmentList.get(var1);
  }

  public int getItemPosition(Object var1) {
    return -1;
  }
}
