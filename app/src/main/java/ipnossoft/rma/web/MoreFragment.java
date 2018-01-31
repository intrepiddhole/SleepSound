package ipnossoft.rma.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ipnossoft.api.featuremanager.FeatureManager;
import ipnossoft.rma.MainActivity;
import ipnossoft.rma.NavigationMenuItemFragment;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.RelaxPropertyHandler;
import ipnossoft.rma.free.R;
import ipnossoft.rma.preferences.ActionBarPreferenceActivity;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.SendMailUtils;

public class MoreFragment extends Fragment implements OnClickListener, NavigationMenuItemFragment {
  private static final String UTM_MEDIUM = "more_section";
  private RelaxMelodiesApp app;
  private LinearLayout blogButton;
  private ImageButton buttonWebProductFlappyMonster;
  private ImageButton buttonWebProductRelaxMeditation;
  private LinearLayout contactUsButton;
  private LinearLayout helpButton;
  private LinearLayout legalButton;
  private LinearLayout newsButton;
  private LinearLayout settingsButton;
  private LinearLayout shareButton;

  public MoreFragment() {
  }

  private void displayNoAdsBlog() {
    RelaxAnalytics.logBlogShown();
    RelaxAnalytics.logScreenBlog();
    String var1 = this.getString(R.string.app_lang);
    this.displayUrl(String.format(this.getString(R.string.web_link_blog), new Object[]{var1}), this.getString(R.string.blog_title));
  }

  private void displayUrl(String var1, String var2) {
    if(this.app.isForceBrowser()) {
      this.launchBrowser(var1);
    } else {
      this.launchWebview(var1, var2);
    }
  }

  private void launchBrowser(String var1) {
    this.safeStartActivity(new Intent("android.intent.action.VIEW", Uri.parse(var1)));
  }

  private void launchWebview(String var1, String var2) {
    Intent var3 = new Intent(this.getActivity(), WebViewActivity.class);
    var3.putExtra("url", var1);
    var3.putExtra("title", var2);
    this.safeStartActivity(var3);
  }

  private String localizeZendeskUrl(String var1) {
    return var1.replace("en_us", this.getResources().getConfiguration().locale.toString().toLowerCase().replace("_", "-"));
  }

  private void safeStartActivity(Intent var1) {
    if(var1.resolveActivity(this.getActivity().getPackageManager()) != null) {
      this.startActivity(var1);
    }

  }

  private void setOtherProducts(View var1) {
    if(this.app.getMarketName().compareTo("google") == 0) {
      this.buttonWebProductFlappyMonster = (ImageButton)var1.findViewById(R.id.more_flappy_product_button);
      this.buttonWebProductFlappyMonster.setOnClickListener(this);
    } else {
      this.buttonWebProductFlappyMonster = null;
      var1.findViewById(R.id.more_flappy_product_button).setVisibility(View.GONE);
    }

    if(this.app.getMarketName().compareTo("samsung") == 0) {
      this.buttonWebProductRelaxMeditation = null;
      var1.findViewById(R.id.other_products_layout).setVisibility(View.GONE);
    } else {
      this.buttonWebProductRelaxMeditation = (ImageButton)var1.findViewById(R.id.more_rmo_product_button);
      this.buttonWebProductRelaxMeditation.setOnClickListener(this);
    }
  }

  void displayAdsBlog() {
  }

  @SuppressLint({"DefaultLocale"})
  public void onClick(View var1) {
    String var3;
    if(var1.getId() == this.newsButton.getId()) {
      RelaxAnalytics.logNewsShown();
      RelaxAnalytics.logScreenNews();
      if(!this.app.isForceBrowser()) {
        this.safeStartActivity(new Intent(this.getActivity(), NewsActivity.class));
        return;
      }

      var3 = this.getString(R.string.web_news_category_name);
      String var2 = this.getString(R.string.app_lang);
      this.launchBrowser(String.format(this.getString(R.string.web_link_news), new Object[]{var3, this.app.getMarketName(), var2}));
    } else {
      if(var1.getId() == this.legalButton.getId()) {
        RelaxAnalytics.logLegalShown();
        RelaxAnalytics.logScreenLegal();
        var3 = this.getString(R.string.app_lang);
        this.displayUrl(String.format(this.getString(R.string.web_link_legal), new Object[]{var3}), this.getString(R.string.web_button_label_legal));
        return;
      }

      if(var1.getId() == this.shareButton.getId()) {
        RelaxAnalytics.logShare();
        SendMailUtils.sendShareMail(this.getActivity());
        return;
      }

      if(var1.getId() == this.contactUsButton.getId()) {
        RelaxAnalytics.logContactUs();
        SendMailUtils.sendSupportMail(this.getActivity());
        return;
      }

      if(var1.getId() == this.settingsButton.getId()) {
        RelaxAnalytics.logSettingsShown();
        this.safeStartActivity(new Intent(this.getActivity(), ActionBarPreferenceActivity.class));
        return;
      }

      if(var1.getId() == this.blogButton.getId()) {
        if(!FeatureManager.getInstance().hasActiveSubscription() && !RelaxMelodiesApp.isPremium().booleanValue()) {
          this.displayAdsBlog();
          return;
        }

        this.displayNoAdsBlog();
        return;
      }

      if(var1.getId() == this.helpButton.getId()) {
        RelaxAnalytics.logHelpShown();
        RelaxAnalytics.logScreenHelp();
        this.displayUrl(this.localizeZendeskUrl(RelaxPropertyHandler.getInstance().getProperties().getProperty(RelaxPropertyHandler.ZENDESK_SUPPORT_URL)), this.getString(R.string.web_button_label_help));
        return;
      }

      if(var1.getId() == this.buttonWebProductRelaxMeditation.getId()) {
        RelaxAnalytics.logOpenOtherProduct("relax_meditation_oriental");
        this.safeStartActivity(new Intent("android.intent.action.VIEW", Uri.parse(((RelaxMelodiesApp)((MainActivity)this.getActivity()).getApplicationContext()).getRelaxMeditationProductLink("more_section"))));
        return;
      }

      if(this.buttonWebProductFlappyMonster != null && var1.getId() == this.buttonWebProductFlappyMonster.getId()) {
        RelaxAnalytics.logOpenOtherProduct("flappy_monster");
        this.safeStartActivity(new Intent("android.intent.action.VIEW", Uri.parse(((RelaxMelodiesApp)((MainActivity)this.getActivity()).getApplicationContext()).getFlappyMonsterProductLink())));
        return;
      }
    }

  }

  public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
    this.app = (RelaxMelodiesApp)((MainActivity)this.getActivity()).getApplicationContext();
    View var4 = var1.inflate(R.layout.more, var2, false);
    this.settingsButton = (LinearLayout)var4.findViewById(R.id.more_button_settings_selectable);
    this.settingsButton.setOnClickListener(this);
    this.blogButton = (LinearLayout)var4.findViewById(R.id.more_button_relaxation_tips_selectable);
    this.blogButton.setOnClickListener(this);
    this.shareButton = (LinearLayout)var4.findViewById(R.id.more_button_share_selectable);
    this.shareButton.setOnClickListener(this);
    this.helpButton = (LinearLayout)var4.findViewById(R.id.more_button_help_selectable);
    this.helpButton.setOnClickListener(this);
    this.contactUsButton = (LinearLayout)var4.findViewById(R.id.more_button_contact_us_selectable);
    this.contactUsButton.setOnClickListener(this);
    this.newsButton = (LinearLayout)var4.findViewById(R.id.more_button_news_selectable);
    this.newsButton.setOnClickListener(this);
    this.legalButton = (LinearLayout)var4.findViewById(R.id.more_button_legal_selectable);
    this.legalButton.setOnClickListener(this);
    this.setOtherProducts(var4);
    return var4;
  }

  public void onStart() {
    super.onStart();
    if(this.getView() != null) {
      TextView var1 = (TextView)this.getView().findViewById(R.id.more_button_badge_label);
      int var2 = this.app.getNewsService().unreadCount();
      if(var2 <= 0) {
        var1.setVisibility(View.GONE);
        return;
      }

      var1.setVisibility(View.VISIBLE);
      var1.setText(String.valueOf(var2));
    }

  }
}
