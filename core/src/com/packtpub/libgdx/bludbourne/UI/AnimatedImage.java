package com.packtpub.libgdx.bludbourne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.packtpub.libgdx.bludbourne.Entity;

public class AnimatedImage extends Image {
    private static final String TAG = AnimatedImage.class.getSimpleName();
    private float frameTime = 0;
    protected Entity entity;
    private Entity.AnimationType currentAnimationType = Entity.AnimationType.IDLE;

    public AnimatedImage(){
        super();
    }

    public void setEntity(Entity entity){
        this.entity = entity;
        //set default
        setCurrentAnimation(Entity.AnimationType.IDLE);
    }

    public void setCurrentAnimation(Entity.AnimationType animationType){
        Animation<TextureRegion> animation = entity.getAnimation(animationType);
        if( animation == null ){
            Gdx.app.debug(TAG, "Animation type " + animationType.toString() + " does not exist!");
            return;
        }

        this.currentAnimationType = animationType;
        this.setDrawable(new TextureRegionDrawable(animation.getKeyFrame(0)));
        this.setScaling(Scaling.stretch);
        this.setAlign(Align.center);
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }

    @Override
    public void act(float delta){
        Drawable drawable = this.getDrawable();
        if( drawable == null ) {
            //Gdx.app.debug(TAG, "Drawable is NULL!");
            return;
        }
        frameTime = (frameTime + delta)%5;
        TextureRegion region = (TextureRegion) entity.getAnimation(currentAnimationType).getKeyFrame(frameTime, true);
        //Gdx.app.debug(TAG, "Keyframe number is " + animation.getKeyFrameIndex(frameTime));
        ((TextureRegionDrawable) drawable).setRegion(region);
        super.act(delta);
    }




}
