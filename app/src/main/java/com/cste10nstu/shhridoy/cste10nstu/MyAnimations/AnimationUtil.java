package com.cste10nstu.shhridoy.cste10nstu.MyAnimations;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;


public class AnimationUtil {

    public static void animate(View view , boolean goesDown){
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", goesDown ? 200 : -200, 0);
        animatorTranslateY.setDuration(1000);

        //ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(holder.itemView,"translationX",-50,50,-30,30,-20,20,-5,5,0);
        //animatorTranslateX.setDuration(1000);

        //animatorSet.playTogether(animatorTranslateX,animatorTranslateY);

        animatorSet.playTogether(animatorTranslateY);
        animatorSet.start();

    }

    public static void animate2(View view){
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", 200, 0);
        animatorTranslateY.setDuration(1000);

        ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view,"translationX",-50,50,-30,30,-20,20,-5,5,0);
        animatorTranslateX.setDuration(1000);

        animatorSet.playTogether(animatorTranslateX,animatorTranslateY);

        //animatorSet.playTogether(animatorTranslateY);
        animatorSet.start();

    }

    public static void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
    }

    public static void bottomToUpAnimation(View view, int duration) {
        TranslateAnimation animation1 = new TranslateAnimation(0.0f, 0.0f, 1000.0f, 0.0f); // new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation1.setDuration(duration); // animation duration
        //animation1.setRepeatCount(4); // animation repeat count if u want to repeat
        animation1.setFillAfter(true);
        view .startAnimation(animation1);//your_view for mine is imageView
    }

    public static void rightToLeftAnimation (View view, int duration) {
        TranslateAnimation animation = new TranslateAnimation(1000.0f, 0.0f, 0.0f, 0.0f); // new TranslateAnimation (float fromXDelta,float toXDelta, float fromYDelta, float toYDelta)
        animation.setDuration(duration); // animation duration
        //animation.setRepeatCount(8); // animation repeat count
        //animation.setRepeatMode(2); // repeat animation (left to right, right to left)
        animation.setFillAfter(true);
        view.startAnimation(animation);//your_view for mine is imageView
    }

    public static void leftToRightAnimation (View view, int duration) {
        TranslateAnimation animation = new TranslateAnimation(-1000.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(duration); // animation duration
        //animation.setRepeatCount(8); // animation repeat count
        //animation.setRepeatMode(2); // repeat animation (left to right, right to left)
        animation.setFillAfter(true);
        view.startAnimation(animation);//your_view for mine is imageView
    }
}
