package ipnossoft.rma.favorites;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.data.Query;
import com.ipnossoft.api.soundlibrary.sounds.*;
import com.wefika.flowlayout.FlowLayout;
import ipnossoft.rma.NavigationMenuItemFragment;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.*;
import ipnossoft.rma.ui.dialog.RelaxDialog;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.Utils;
import java.io.File;
import java.util.*;
import org.json.*;

// Referenced classes of package ipnossoft.rma.favorites:
//            FavoriteSounds, FavoriteListViewAdapter, StaffFavoriteSounds, FavoriteStatusHandler


//cavaj
public class FavoriteFragment extends Fragment
        implements NavigationMenuItemFragment
{
  private static class LoadFavoritesTask extends AsyncTask
  {

    protected Object doInBackground(Object aobj[])
    {
      return doInBackground((Activity[])aobj);
    }

    protected Void doInBackground(Activity aactivity[])
    {
      if(aactivity.length > 0)
      {
        Activity aactivity_1 = aactivity[0];
        Log.d("FavoriteActivity", "Loading favorites.");
        FavoriteFragment.favoriteSoundsList.clear();
        try
        {
          FavoriteFragment.loadFavorites(aactivity_1);
          FavoriteFragment.saveFavorites(aactivity_1);
        }
        catch(IllegalArgumentException illegalargumentexception)
        {
          Log.d("FavoriteActivity", "Bad favorites format. Trying legacy format...", illegalargumentexception);
          FavoriteFragment.deleteSavedFavorites(aactivity_1);
          Toast.makeText(aactivity_1, R.string.favorite_activity_error_loading_legacy, Toast.LENGTH_LONG).show();
        }
      }
      return null;
    }

    protected void onPostExecute(Object obj)
    {
      onPostExecute((Void)obj);
    }

    protected void onPostExecute(Void void1)
    {
    }

    private LoadFavoritesTask()
    {
    }

  }


  public static final String FAVORITES_JSON_FILE = "favorites.json";
  public static final String PREFERENCE_FAVORITE_COUNT = "FAVORITES_COUNT";
  public static final String PREFERENCE_FAVORITE_LIST = "FAVORITES_JSON";
  private static final String PREF_KEY_NEXT = "next";
  private static final String TAG = "FavoriteActivity";
  private static List favoriteSoundsList = new ArrayList();
  private static int nextId = 0;
  private boolean alreadyShowingContextMenu;
  private int favoriteBrainwaveImageSize;
  private RecyclerView favoriteListRecyclerView;
  private FavoriteListViewAdapter favoriteListViewAdapter;
  private int favoriteSoundSubVolumeWidth;
  private RelaxDialog upgradeAlertDialog;

  public FavoriteFragment()
  {
    alreadyShowingContextMenu = false;
  }

  private void addIndividualSoundToLayout(FlowLayout flowlayout, LayoutInflater layoutinflater, SoundTrackInfo soundtrackinfo)
  {
    Sound sound = (Sound)SoundLibrary.getInstance().getSound(soundtrackinfo.getSoundId());
    View layoutinflater_1 = layoutinflater.inflate(R.layout.favorite_custom_dialog_sound_volume_layout, flowlayout, false);
    ImageView imageview = (ImageView)layoutinflater_1.findViewById(R.id.favorite_custom_dialog_sound_image);
    if((sound instanceof BinauralSound) || (sound instanceof IsochronicSound))
    {
      Glide.with(getActivity().getApplicationContext()).load(Integer.valueOf(sound.getFavoriteImageResourceId())).placeholder(sound.getFavoriteImageResourceId()).dontAnimate().into(imageview);
    } else {
      Glide.with(getActivity().getApplicationContext()).load(Integer.valueOf(sound.getNormalImageResourceId())).placeholder(sound.getNormalImageResourceId()).dontAnimate().into(imageview);
    }
    setupSubvolumeView(soundtrackinfo, layoutinflater_1);
    flowlayout.addView(layoutinflater_1);
  }

  private void addIndividualSoundsToLayout(FlowLayout flowlayout, List list, LayoutInflater layoutinflater)
  {
    for(Iterator list_1 = list.iterator(); list_1.hasNext(); addIndividualSoundToLayout(flowlayout, layoutinflater, (SoundTrackInfo)list_1.next())) { }
  }

  private View buildFavoriteDialogCustomView(FavoriteSounds favoritesounds)
  {
    List list = favoritesounds.getTrackInfos();
    GuidedMeditationSound guidedmeditationsound = favoritesounds.getGuidedMeditationInSelection();
    LayoutInflater layoutinflater = LayoutInflater.from(getActivity());
    View view = layoutinflater.inflate(R.layout.favorite_custom_dialog_view, null, false);
    if(guidedmeditationsound != null)
    {
      list = updateCustomDialogViewsForMeditation(favoritesounds, guidedmeditationsound, view);
    }
    addIndividualSoundsToLayout((FlowLayout)view.findViewById(R.id.favorite_custom_dialog_flow_layout), list, layoutinflater);
    return view;
  }

  private void changeLabel(final int i, final FavoriteSounds favoritesounds)
  {
    RelaxDialog.Builder builder = new RelaxDialog.Builder(getActivity(), RelaxDialog.RelaxDialogButtonOrientation.VERTICAL);
    builder.setTitle(R.string.favorite_activity_dialog_label_title);
    String temp = favoritesounds.getLabel();
    final EditText favoriteNameEditText = inflateAndInsertNameEditTextIntoDialog(builder, temp);
    View.OnClickListener favoritesounds_1 = new android.view.View.OnClickListener() {
      public void onClick(View view)
      {
        RelaxAnalytics.logUpdateFavorite();
        String view_1 = favoriteNameEditText.getText().toString();
        if(!view_1.equals(""))
        {
          favoritesounds.setLabel(view_1);
          FavoriteFragment.saveFavorites(getActivity());
          notifyAdapterForChange(i);
        }
      }
    };
    builder.setPositiveButton(R.string.favorite_activity_dialog_label_save, favoritesounds_1);
    builder.setNegativeButton(R.string.cancel, null);
    builder.showWithKeyboard();
  }

  private void confirmDeletion(final FavoriteSounds favoriteSounds)
  {
    String s = getString(R.string.favorite_activity_delete_title);
    String s1 = String.format(getString(R.string.favorite_activity_confirm_delete), new Object[] {
            truncateIfTooLong(favoriteSounds.getLabel(), 100)
    });
    ipnossoft.rma.ui.dialog.RelaxDialog.Builder builder = new ipnossoft.rma.ui.dialog.RelaxDialog.Builder(getActivity(), ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation.VERTICAL);
    builder.setTitle(s);
    builder.setMessage(s1);
    builder.setNegativeButton(R.string.dialog_label_no, null);
    builder.setPositiveButton(R.string.dialog_label_yes, new android.view.View.OnClickListener() {
      public void onClick(View view)
      {
        deleteFavorite(favoriteSounds);
        RelaxAnalytics.logDeleteFavorite();
      }
    });
    builder.show();
  }

  private void createNewFavorite(EditText edittext, SoundSelection soundselection, Activity activity, FavoriteSounds favoritesounds, StaffFavoriteSounds stafffavoritesounds)
  {
    String s = edittext.getText().toString();
    String edittext_1 = s;
    if(s.isEmpty())
    {
      edittext_1 = getNextFavoriteNameForEmptyName();
    }
    FavoriteSounds edittext_2 = new FavoriteSounds(soundselection, edittext_1);
    if(edittext_2 != null){
      setCustomFavoriteElements(favoritesounds, edittext_2);
    }else{
      if(stafffavoritesounds != null)
      {
        setCustomFavoriteElements(stafffavoritesounds, edittext_2);
      }
      favoriteSoundsList.add(edittext);
      saveFavorites(activity);
      RelaxAnalytics.logCreateFavorite(SoundManager.getInstance().getSelectedTracks());
      RelaxMelodiesApp.getInstance().completeTapJoyRewardAction();
      if(favoriteListViewAdapter != null)
      {
        favoriteListViewAdapter.setClickedFavoriteIndex(favoriteSoundsList.size() - 1);
        favoriteListViewAdapter.notifyDataSetChanged();
        favoriteListRecyclerView.scrollToPosition(favoriteListViewAdapter.getItemCount() - 1);
      }
      return;
    }
  }

  private void deleteFavorite(FavoriteSounds favoritesounds)
  {
    favoriteSoundsList.remove(favoritesounds);
    saveFavorites(getActivity());
    if(favoriteListViewAdapter != null)
    {
      favoriteListViewAdapter.notifyDataSetChanged();
      favoriteListViewAdapter.deSelectFirstCell();
    }
  }

  private static void deleteSavedFavorites(Activity activity)
  {
    SharedPreferences.Editor activity_1 = activity.getSharedPreferences("ipnossoft.rma.favorites", 0).edit();
    activity_1.clear();
    activity_1.apply();
  }

  public static int getFavoriteCount()
  {
    return favoriteSoundsList.size();
  }

  private String getNextFavoriteNameForEmptyName()
  {
    int i = favoriteSoundsList.size();
    boolean flag = false;
    boolean flag1;
    String s;
    do
    {
      int j = i + 1;
      s = getNextFavoriteNameWithIndex(j);
      flag1 = false;
      Iterator iterator = favoriteSoundsList.iterator();
      do
      {
        i = ((flag1) ? 1 : 0);
        if(!iterator.hasNext())
        {
          break;
        }
        if(!((FavoriteSounds)iterator.next()).getLabel().equalsIgnoreCase(s))
        {
          continue;
        }
        i = 1;
        break;
      } while(true);
      flag1 = flag;
      if(i == 0)
      {
        flag1 = true;
      }
      i = j;
      flag = flag1;
    } while(!flag1);
    return s;
  }

  private String getNextFavoriteNameWithIndex(int i)
  {
    return (new StringBuilder()).append(getString(R.string.favorite_title)).append(" #").append(i).toString();
  }

  private EditText inflateAndInsertNameEditTextIntoDialog(ipnossoft.rma.ui.dialog.RelaxDialog.Builder builder, String s)
  {
    View view = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.favorite_label_dialog, null, false);
    EditText edittext = (EditText)view.findViewById(R.id.favorite_label);
    edittext.getBackground().mutate().setColorFilter(getResources().getColor(R.color.relax_dialog_button_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
    builder.setCustomContentView(view);
    edittext.setText(s);
    return edittext;
  }

  private static FavoriteSounds loadFavoriteFromJsonLegacy(int i, String s, Activity activity)
          throws IllegalArgumentException
  {
    JSONArray jsonarray;
    try {
      JSONObject s_1 = new JSONObject(s);
      FavoriteSounds activity_1 = new FavoriteSounds(i, s_1.getString("label"), new ArrayList());
      jsonarray = s_1.getJSONArray("sounds");
      i = 0;
      while (true) {
        FavoriteSounds s_2 = activity_1;
        if (i >= jsonarray.length()) {
          return s_2;
        }
        SoundTrackInfo s_3 = loadFavoriteSoundFromJsonLegacy(jsonarray.getJSONObject(i));
        if (s_3 == null) {
          return s_2;
        }
        activity_1.add(s_3);
        i++;
      }
    }catch (Exception e) {
      Log.e("FavoriteActivity", "JSONException while building FavoriteSound object from JSONObject", e);
      return null;
    }
  }

  private static SoundTrackInfo loadFavoriteSoundFromJsonLegacy(JSONObject jsonobject)
          throws JSONException, IllegalArgumentException
  {
    final int i = jsonobject.getInt("id");
    float f = (float)jsonobject.getDouble("volume");
    Sound sound = SoundLibrary.getInstance().querySound(new Query() {
      public boolean where(Sound sound2)
      {
        return sound2.getSoundId() == i;
      }
    });
    Sound jsonobject_1 = sound;
    if(sound == null)
    {
      Sound sound1 = SoundLibrary.getInstance().querySound(new Query() {
        public boolean where(Sound sound2)
        {
          return sound2.getMediaResourceId() == i;
        }
      });
      jsonobject_1 = sound1;
      if(sound1 == null)
      {
        return null;
      }
    }
    return new SoundTrackInfo(jsonobject_1.getId(), f);
  }

  private static void loadFavorites(Activity activity)
          throws IllegalArgumentException
  {
    Object obj = new File(activity.getFilesDir().getPath(), "favorites.json");
    if(((File) (obj)).exists())
    {
      favoriteSoundsList = Utils.jsonFileToObjectList(((File) (obj)), FavoriteSounds.class);
    } else
    {
      SharedPreferences sharedpreferences = activity.getSharedPreferences("ipnossoft.rma.favorites", 0);
      String s = sharedpreferences.getString("FAVORITES_JSON", null);
      if(s == null)
      {
        favoriteSoundsList = new ArrayList();
        Log.d("FavoriteActivity", "Loading favorites from LEGACY format...");
        loadFavoritesLegacy(activity);
      } else
      {
        Log.d("FavoriteActivity", (new StringBuilder()).append("Loading favorites from json: ").append(s).toString());
        favoriteSoundsList = Utils.jsonToObjectList(s, FavoriteSounds.class);
        Collections.sort(favoriteSoundsList);
      }
      sharedpreferences.edit().remove("FAVORITES_JSON").apply();
    }
    obj = (new StringBuilder()).append("Loaded ");
    if(favoriteSoundsList == null)
    {
      String activity_1 = "NULL";
    } else
    {
      String activity_1 = (new StringBuilder()).append(favoriteSoundsList.size()).append("").toString();
    }
    Log.d("FavoriteActivity", ((StringBuilder) (obj)).append(activity).append(" favorites").toString());
  }

  private static void loadFavoritesLegacy(Activity activity)
          throws IllegalArgumentException
  {
    Iterator iterator = activity.getSharedPreferences("ipnossoft.rma.favorites", 0).getAll().entrySet().iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      Object obj = (java.util.Map.Entry)iterator.next();
      String s = (String)((java.util.Map.Entry) (obj)).getKey();
      if(s.equals("next"))
      {
        nextId = ((Integer)((java.util.Map.Entry) (obj)).getValue()).intValue();
      } else
      {
        obj = loadFavoriteFromJsonLegacy(Integer.valueOf(s).intValue(), (String)((java.util.Map.Entry) (obj)).getValue(), activity);
        if(obj != null)
        {
          favoriteSoundsList.add(obj);
        }
      }
    } while(true);
  }

  private void notifyAdapterForChange(int i)
  {
    if(favoriteListViewAdapter != null)
    {
      favoriteListViewAdapter.notifyItemChanged(i);
    }
  }

  private void playFavorite(FavoriteSounds favoritesounds)
  {
    SoundManager.getInstance().playSoundSelection(favoritesounds, getActivity());
    favoriteListViewAdapter.onFavoriteCellClicked(favoritesounds.getId());
  }

  public static void preloadFavorites(Activity activity)
  {
    Utils.executeTask(new LoadFavoritesTask(), new Activity[] {
            activity
    });
  }

  private List removeMeditationFromTrackInfos(List list)
  {
    ArrayList arraylist = new ArrayList(list);
    Iterator list_1 = list.iterator();
    do
    {
      if(!list_1.hasNext())
      {
        break;
      }
      SoundTrackInfo soundtrackinfo = (SoundTrackInfo)list_1.next();
      if(!((Sound)SoundLibrary.getInstance().getSound(soundtrackinfo.getSoundId())).getClass().equals(GuidedMeditationSound.class))
      {
        continue;
      }
      arraylist.remove(soundtrackinfo);
      break;
    } while(true);
    return arraylist;
  }

  private static void saveFavorites(Activity activity)
  {
    if(favoriteSoundsList != null)
    {
      Utils.writeObjectListToJsonFile(new File(activity.getFilesDir().getPath(), "favorites.json"), favoriteSoundsList, FavoriteSounds.class);
    }
  }

  private void setCustomFavoriteElements(FavoriteSounds favoritesounds, FavoriteSounds favoritesounds1)
  {
    favoritesounds1.setImageResourceEntryName(favoritesounds.getImageResourceEntryName());
    favoritesounds1.setSelectedImageResourceEntryName(favoritesounds.getSelectedImageResourceEntryName());
  }

  private void setupSubvolumeView(SoundTrackInfo soundtrackinfo, View view)
  {
    view = view.findViewWithTag("soundSubVolume");
    if(view != null)
    {
      view.setPadding(0, 0, (int)((1.0F - soundtrackinfo.getVolume()) * (float)favoriteSoundSubVolumeWidth), 0);
    }
  }

  private void showCreateFavoriteDialog(final Activity activity, final SoundSelection selectedSounds, final FavoriteSounds selectedFavorite, final StaffFavoriteSounds selectedStaffPick)
  {
    RelaxAnalytics.logCreateFavoriteDialog();
    ipnossoft.rma.ui.dialog.RelaxDialog.Builder builder = new ipnossoft.rma.ui.dialog.RelaxDialog.Builder(activity, ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation.VERTICAL);
    builder.setTitle(R.string.favorite_activity_dialog_label_title);
    final EditText favoriteNameEditText;
    if(selectedFavorite != null)
    {
      favoriteNameEditText = inflateAndInsertNameEditTextIntoDialog(builder, selectedFavorite.getLabel());
    } else
    if(selectedStaffPick != null)
    {
      favoriteNameEditText = inflateAndInsertNameEditTextIntoDialog(builder, selectedStaffPick.getLabel());
    } else
    {
      favoriteNameEditText = inflateAndInsertNameEditTextIntoDialog(builder, "");
    }
    View.OnClickListener activity_1 = new android.view.View.OnClickListener() {
      public void onClick(View view)
      {
        createNewFavorite(favoriteNameEditText, selectedSounds, activity, selectedFavorite, selectedStaffPick);
      }
    };
    builder.setPositiveButton(R.string.favorite_activity_dialog_label_save, activity_1);
    builder.setNegativeButton(R.string.cancel, null);
    builder.showWithKeyboard();
  }

  private String truncateIfTooLong(String s, int i)
  {
    String s1 = s;
    if(s.length() > i)
    {
      s1 = (new StringBuilder()).append(s.substring(0, i - 3)).append("...").toString();
    }
    return s1;
  }

  private List updateCustomDialogViewsForMeditation(FavoriteSounds favoritesounds, GuidedMeditationSound guidedmeditationsound, View view)
  {
    TextView view_1 = (TextView)view.findViewById(R.id.favorite_custom_dialog_meditation_label);
    view_1.setText(String.format(getActivity().getResources().getString(R.string.favorite_meditation_text), new Object[] {
            guidedmeditationsound.getName()
    }));
    view_1.setVisibility(View.VISIBLE);
    return removeMeditationFromTrackInfos(favoritesounds.getTrackInfos());
  }

  public void addCurrentSelection(Activity activity)
  {
    StaffFavoriteSounds stafffavoritesounds = favoriteListViewAdapter.getSelectedStaffPick();
    SoundSelection soundselection = SoundManager.getInstance().getSoundSelection();
    if(soundselection.size() == 0)
    {
      Utils.uniqueAlert(activity.getApplicationContext(), getResources().getString(R.string.favorite_activity_empty_selection));
      return;
    } else
    {
      showCreateFavoriteDialog(activity, soundselection, favoriteListViewAdapter.getSelectedFavorite(), stafffavoritesounds);
      return;
    }
  }

  public void clearSelection()
  {
    if(favoriteListViewAdapter != null)
    {
      favoriteListViewAdapter.clearSelection();
    }
  }

  public void onCreate(Bundle bundle)
  {
    super.onCreate(bundle);
    favoriteSoundSubVolumeWidth = (int)getActivity().getResources().getDimension(R.dimen.favorite_sound_sub_volume_width);
    favoriteBrainwaveImageSize = getActivity().getResources().getDimensionPixelSize(R.dimen.favorite_custom_dialog_brainwave_image_size);
  }

  public void onCreateContextMenu(final ContextMenu favoriteSounds, View view, android.view.ContextMenu.ContextMenuInfo contextmenuinfo)
  {
    if(!alreadyShowingContextMenu)
    {
      alreadyShowingContextMenu = true;
      final FavoriteSounds favoriteSounds_1 = favoriteListViewAdapter.getFavorite(favoriteListViewAdapter.getPosition());
      RelaxDialog.Builder view_1 = new ipnossoft.rma.ui.dialog.RelaxDialog.Builder(view.getContext(), ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation.VERTICAL);
      view_1.setTitle(truncateIfTooLong(favoriteSounds_1.getLabel(), 90));
      view_1.setCustomContentView(buildFavoriteDialogCustomView(favoriteSounds_1));
      view_1.setPositiveButton(R.string.favorite_activity_context_menu_play, new android.view.View.OnClickListener() {
        public void onClick(View view1)
        {
          if(!FavoriteStatusHandler.favoriteContainsLockedSounds(favoriteSounds_1))
          {
            RelaxAnalytics.logPlayFavoriteFromContextMenu();
            playFavorite(favoriteSounds_1);
          } else
          {
            FavoriteStatusHandler.showUpgradeAlert(getActivity(), upgradeAlertDialog);
          }
          alreadyShowingContextMenu = false;
        }
      });
      view_1.setNeutralButton(R.string.favorite_activity_context_menu_delete, new android.view.View.OnClickListener() {

        public void onClick(View view1)
        {
          confirmDeletion(favoriteSounds_1);
          alreadyShowingContextMenu = false;
        }
      });
      view_1.setNegativeButton(R.string.favorite_activity_context_menu_set_label, new android.view.View.OnClickListener() {

        public void onClick(View view1)
        {
          changeLabel(favoriteListViewAdapter.getPosition(), favoriteSounds_1);
          alreadyShowingContextMenu = false;
        }
      });
      view_1.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {

        public void onCancel(DialogInterface dialoginterface)
        {
          alreadyShowingContextMenu = false;
        }

      });
      view_1.show();
    }
  }

  public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
  {
    RelativeLayout layoutinflater_1 = (RelativeLayout)layoutinflater.inflate(R.layout.favorite, viewgroup, false);
    favoriteListRecyclerView = (RecyclerView)layoutinflater_1.findViewById(R.id.favorite_list_recycler_view);
    favoriteListRecyclerView.setHasFixedSize(true);
    favoriteListViewAdapter = new FavoriteListViewAdapter(getActivity(), R.layout.favorite_list_item, favoriteSoundsList, this);
    favoriteListRecyclerView.setAdapter(favoriteListViewAdapter);
    final int nbColumns = getResources().getInteger(R.integer.favorite_nb_columns);
    GridLayoutManager viewgroup_1 = new GridLayoutManager(getActivity(), nbColumns);
    viewgroup_1.setSpanSizeLookup(new android.support.v7.widget.GridLayoutManager.SpanSizeLookup() {

      public int getSpanSize(int i)
      {
        while(nbColumns == 1 || favoriteListViewAdapter.getItemViewType(i) == 2)
        {
          return 1;
        }
        return nbColumns;
      }
    });
    favoriteListRecyclerView.setLayoutManager(viewgroup_1);
    return favoriteListRecyclerView;
  }

  public void onPause()
  {
    super.onPause();
    favoriteListRecyclerView.setVisibility(View.GONE);
    favoriteListViewAdapter.stopAnimatingSelectedCell();
  }

  public void onResume()
  {
    super.onResume();
    favoriteListRecyclerView.setVisibility(View.VISIBLE);
    if(favoriteListViewAdapter != null)
    {
      favoriteListViewAdapter.notifyDataSetChanged();
    }
  }

  public void updateData()
  {
    if(favoriteListViewAdapter != null)
    {
      favoriteListViewAdapter.notifyDataSetChanged();
    }
  }














/*
    static boolean access$802(FavoriteFragment favoritefragment, boolean flag)
    {
        favoritefragment.alreadyShowingContextMenu = flag;
        return flag;
    }

*/

}
