package es.jesusgarce.duckhunt.models;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;

import java.util.Random;

public class Duck {
    private int id;
    private int type;
    private ObjectAnimator animationX;
    private ObjectAnimator animationY;
    private AnimatorSet animatorSet;
    private int posY;
    private boolean isLeft;
    private ImageView view;

    public Duck(int id) {
        this.id = id;
        generateSide();
        generateType();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ObjectAnimator getAnimationX() {
        return animationX;
    }

    public void setAnimationX(ObjectAnimator animationX) {
        this.animationX = animationX;
    }

    public ObjectAnimator getAnimationY() {
        return animationY;
    }

    public void setAnimationY(ObjectAnimator animationY) {
        this.animationY = animationY;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public ImageView getView() {
        return view;
    }

    public void setView(ImageView view) {
        this.view = view;
    }

    public AnimatorSet getAnimatorSet() {
        return animatorSet;
    }

    public void setAnimatorSet(AnimatorSet animatorSet) {
        this.animatorSet = animatorSet;
    }

    public void generateImageView(ImageView imageView, int maxWidthScreen, int maxHeightScreen){
        setView(imageView);
        generateAnimation(maxWidthScreen, maxHeightScreen);
    }

    private void generateType() {
        Random random = new Random();
        type = random.nextInt((4 - 1) + 1);
    }

    private void generateAnimation(int maxWidthScreen, final int maxHeightScreen) {

        view.setX(-view.getWidth());
        Random random = new Random();
        posY = random.nextInt((maxHeightScreen - 400) + 1);
        view.setY(posY);
        animationY = ObjectAnimator.ofFloat(view, "translationY", posY - 50f, posY + 50f);
        animationY.setInterpolator(new CycleInterpolator(3));
        animationY.setRepeatCount(ValueAnimator.INFINITE);
        animationY.setRepeatMode(ValueAnimator.RESTART);
        if (isLeft) {
            animationX = ObjectAnimator.ofFloat(view, "translationX", maxWidthScreen, -220f);
        } else {
            animationX = ObjectAnimator.ofFloat(view, "translationX", -90f, maxWidthScreen);
        }
        animationX.setRepeatCount(ValueAnimator.INFINITE);
        animationX.setRepeatMode(ValueAnimator.RESTART);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animationX, animationY);
        animatorSet.setDuration(1613);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Random random = new Random();
                int maxHeight = maxHeightScreen - view.getHeight();
                posY = random.nextInt((maxHeight - 400) + 1);
                view.setY(posY);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animatorSet.start();
    }

    private void generateSide() {
        Random random = new Random();
        int pos = random.nextInt((3 - 1) + 1);
        isLeft = pos == 1;
    }

    public void startDuck() {
        animatorSet.start();
    }

    public int duckValue() {
        switch (type) {
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                return 1;
        }
    }

    public void duckHunted() {
        animatorSet.end();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
