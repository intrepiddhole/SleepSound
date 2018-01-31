package ipnossoft.rma.upgrade;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import com.bumptech.glide.Glide;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.animation.ElasticInterpolator;
import ipnossoft.rma.free.R;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class SubscriptionAnimatedImages extends RelativeLayout {
  private int currentPosition;
  private float imageHeight;
  private SubscriptionAnimatedImages.TransitionIn[] inTransitions = new SubscriptionAnimatedImages.TransitionIn[4];
  private boolean initializedAnimations = false;
  private SubscriptionAnimatedImages.TransitionOut[] outTransitions = new SubscriptionAnimatedImages.TransitionOut[4];
  private ImageView[] overlays;
  private boolean premium;
  private Queue<SubscriptionAnimatedImages.Transition> transitionQueue = new LinkedList();
  private SubscriptionAnimatedImages.TransitionFinishedCallback transitionQueueListener;

  public SubscriptionAnimatedImages(Context var1) {
    super(var1);
    this.commonInit();
  }

  public SubscriptionAnimatedImages(Context var1, AttributeSet var2) {
    super(var1, var2);
    this.commonInit();
  }

  public SubscriptionAnimatedImages(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
    this.commonInit();
  }

  private void addTransition(int var1, int var2) {
    boolean var4 = this.removeSkippedInTransitionsFromQueue();
    boolean var3;
    if(this.transitionQueue.size() == 0) {
      var3 = true;
    } else {
      var3 = false;
    }

    if(var1 > -1 && !var4) {
      this.transitionQueue.add(this.outTransitions[var1]);
    }

    this.transitionQueue.add(this.inTransitions[var2]);
    if(var3) {
      this.startNextTransition();
    }

  }

  private float calculateTargetPosition(int var1) {
    return 0.58F * (float)var1 - this.imageHeight;
  }

  private void commonInit() {
    this.premium = RelaxMelodiesApp.isPremium().booleanValue();
    this.imageHeight = this.getResources().getDimension(R.dimen.subscription_image_height);
    byte var1;
    if(this.premium) {
      var1 = 1;
    } else {
      var1 = 4;
    }

    this.overlays = new ImageView[var1];

    for(int var2 = 0; var2 < var1; ++var2) {
      this.overlays[var2] = this.createOverlayImageView();
      this.addView(this.overlays[var2]);
    }

    this.loadAllImages();
  }

  private ImageView createImageView() {
    ImageView var1 = new ImageView(this.getContext());
    var1.setLayoutParams(new LayoutParams(-1, (int)this.imageHeight));
    var1.setScaleType(ScaleType.CENTER_INSIDE);
    return var1;
  }

  private ImageView createOverlayImageView() {
    ImageView var1 = this.createImageView();
    var1.setY((float)((int)(-this.imageHeight)));
    return var1;
  }

  private void createTransitionFinishedCallback() {
    this.transitionQueueListener = new SubscriptionAnimatedImages.TransitionFinishedCallback() {
      public void transitionFinished(Object var1) {
        SubscriptionAnimatedImages.this.transitionQueue.poll();
        if(SubscriptionAnimatedImages.this.transitionQueue.size() > 0) {
          SubscriptionAnimatedImages.this.startNextTransition();
        }

      }
    };
  }

  private void loadAllImages() {
    byte var1;
    if(this.premium) {
      var1 = 0;
    } else {
      var1 = 1;
    }

    Glide.with(this.getContext()).load(Integer.valueOf(R.drawable.upgrade_meditation_1)).into(this.overlays[var1]);
    if(!this.premium) {
      Glide.with(this.getContext()).load(Integer.valueOf(R.drawable.upgrade_sons_1)).into(this.overlays[0]);
      Glide.with(this.getContext()).load(Integer.valueOf(R.drawable.upgrade_background_1)).into(this.overlays[2]);
      Glide.with(this.getContext()).load(Integer.valueOf(R.drawable.upgrade_ads_1)).into(this.overlays[3]);
    }

  }

  private boolean removeSkippedInTransitionsFromQueue() {
    Iterator var1 = this.transitionQueue.iterator();
    boolean var3 = false;

    while(true) {
      SubscriptionAnimatedImages.Transition var2;
      do {
        if(!var1.hasNext()) {
          return var3;
        }

        var2 = (SubscriptionAnimatedImages.Transition)var1.next();
      } while((!(var2 instanceof SubscriptionAnimatedImages.TransitionIn) || var2.inProgress) && !var3);

      var3 = true;
      var1.remove();
    }
  }

  private void startNextTransition() {
    SubscriptionAnimatedImages.Transition var1 = (SubscriptionAnimatedImages.Transition)this.transitionQueue.peek();
    if(var1 != null) {
      var1.start();
    }

  }

  public void initializeAnimations() {
    this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
      public void onGlobalLayout() {
        if(!SubscriptionAnimatedImages.this.initializedAnimations) {
          if(SubscriptionAnimatedImages.this.transitionQueueListener == null) {
            SubscriptionAnimatedImages.this.createTransitionFinishedCallback();
          }

          int var1 = 0;

          while(true) {
            byte var2;
            if(SubscriptionAnimatedImages.this.premium) {
              var2 = 1;
            } else {
              var2 = 4;
            }

            if(var1 >= var2) {
              (new Handler()).postDelayed(new Runnable() {
                public void run() {
                  SubscriptionAnimatedImages.this.addTransition(-1, 0);
                }
              }, 600L);
              break;
            }

            SubscriptionAnimatedImages.this.inTransitions[var1] = SubscriptionAnimatedImages.this.new TransitionIn(var1, SubscriptionAnimatedImages.this.transitionQueueListener);
            SubscriptionAnimatedImages.this.outTransitions[var1] = SubscriptionAnimatedImages.this.new TransitionOut(var1, SubscriptionAnimatedImages.this.transitionQueueListener);
            ++var1;
          }
        }

        SubscriptionAnimatedImages.this.initializedAnimations = true;
      }
    });
  }

  public void setCurrentImage(int var1) {
    this.addTransition(this.currentPosition, var1);
    this.currentPosition = var1;
  }

  abstract class Transition {
    protected SubscriptionAnimatedImages.TransitionFinishedCallback callback;
    protected ImageView imageOverlay;
    public boolean inProgress;
    public int position;

    public Transition(int var2, SubscriptionAnimatedImages.TransitionFinishedCallback var3) {
      this.imageOverlay = SubscriptionAnimatedImages.this.overlays[var2];
      this.callback = var3;
    }

    public abstract void doStart();

    protected void setViewsLayerType(int var1) {
      this.imageOverlay.setLayerType(var1, (Paint)null);
    }

    protected void setViewsVisibility(int var1) {
      this.imageOverlay.setVisibility(var1);
    }

    public void start() {
      this.inProgress = true;
      this.setViewsLayerType(2);
      this.doStart();
    }
  }

  interface TransitionFinishedCallback {
    void transitionFinished(Object var1);
  }

  class TransitionIn extends SubscriptionAnimatedImages.Transition {
    ObjectAnimator dropAnimation;

    public TransitionIn(int var2, SubscriptionAnimatedImages.TransitionFinishedCallback var3) {
      super(var2, var3);
      var2 = SubscriptionAnimatedImages.this.getMeasuredHeight();
      this.dropAnimation = ObjectAnimator.ofFloat(this.imageOverlay, "translationY", new float[]{-SubscriptionAnimatedImages.this.imageHeight, SubscriptionAnimatedImages.this.calculateTargetPosition(var2)});
      this.dropAnimation.setInterpolator(new ElasticInterpolator(0.6D));
      this.dropAnimation.setDuration(2000L);
      this.dropAnimation.addListener(this.createTranslationTransitionListener(var3));
    }

    @NonNull
    private AnimatorListener createTranslationTransitionListener(final SubscriptionAnimatedImages.TransitionFinishedCallback var1) {
      return new AnimatorListener() {
        public void onAnimationCancel(Animator var1x) {
          TransitionIn.this.setViewsLayerType(0);
          TransitionIn.this.inProgress = false;
        }

        public void onAnimationEnd(Animator var1x) {
          TransitionIn.this.setViewsLayerType(0);
          TransitionIn.this.inProgress = false;
          var1.transitionFinished(this);
        }

        public void onAnimationRepeat(Animator var1x) {
        }

        public void onAnimationStart(Animator var1x) {
        }
      };
    }

    public void doStart() {
      this.setViewsVisibility(0);
      this.dropAnimation.start();
    }
  }

  class TransitionOut extends SubscriptionAnimatedImages.Transition {
    ObjectAnimator pullUpAnimation;

    public TransitionOut(int var2, SubscriptionAnimatedImages.TransitionFinishedCallback var3) {
      super(var2, var3);
      var2 = SubscriptionAnimatedImages.this.getMeasuredHeight();
      this.pullUpAnimation = ObjectAnimator.ofFloat(this.imageOverlay, "translationY", new float[]{SubscriptionAnimatedImages.this.calculateTargetPosition(var2), -SubscriptionAnimatedImages.this.imageHeight});
      this.pullUpAnimation.setDuration(500L);
      this.pullUpAnimation.addListener(this.createTranslationTransitionListener(var3));
    }

    @NonNull
    private AnimatorListener createTranslationTransitionListener(final SubscriptionAnimatedImages.TransitionFinishedCallback var1) {
      return new AnimatorListener() {
        public void onAnimationCancel(Animator var1x) {
          TransitionOut.this.setViewsLayerType(0);
          TransitionOut.this.setViewsVisibility(4);
          TransitionOut.this.inProgress = false;
        }

        public void onAnimationEnd(Animator var1x) {
          TransitionOut.this.setViewsLayerType(0);
          TransitionOut.this.setViewsVisibility(4);
          TransitionOut.this.inProgress = false;
          var1.transitionFinished(this);
        }

        public void onAnimationRepeat(Animator var1x) {
        }

        public void onAnimationStart(Animator var1x) {
        }
      };
    }

    public void doStart() {
      this.pullUpAnimation.start();
    }
  }
}
