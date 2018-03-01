package com.packtpub.libgdx.bludbourne;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.util.Hashtable;

public abstract class GraphicsComponent extends ComponentSubject implements Component {
    protected TextureRegion currentFrame = null;
    protected float frameTime = 0f;
    protected Entity.State currentState;
    protected Entity.Direction currentDirection;
    protected Json json;
    protected Vector2 currentPosition;
    protected Hashtable<Entity.AnimationType, Animation> animations;
    protected ShapeRenderer shapeRenderer;

    protected GraphicsComponent(){
        currentPosition = new Vector2(0,0);
        currentState = Entity.State.WALKING;
        currentDirection = Entity.Direction.DOWN;
        json = new Json();
        animations = new Hashtable<Entity.AnimationType, Animation>();
        shapeRenderer = new ShapeRenderer();
    }

    public abstract void update(Entity entity, MapManager mapManager, Batch batch, float delta);

    protected void updateAnimations(float delta){
        frameTime = (frameTime + delta)%5; //Want to avoid overflow

        //Look into the appropriate variable when changing position
        Animation<TextureRegion> animation;
        switch (currentDirection) {
            case DOWN:
                if (currentState == Entity.State.WALKING) {
                    animation = animations.get(Entity.AnimationType.WALK_DOWN);
                    if( animation == null ) return;
                    currentFrame =  animation.getKeyFrame(frameTime);
                } else if(currentState == Entity.State.IDLE) {
                    animation = animations.get(Entity.AnimationType.IDLE);
                    if( animation == null ) return;
                    currentFrame = animation.getKeyFrame(0);
                } else if(currentState == Entity.State.IMMOBILE) {
                    animation = animations.get(Entity.AnimationType.IMMOBILE);
                    if( animation == null ) return;
                    currentFrame = animation.getKeyFrame(frameTime);
                }
                break;
            case LEFT:
                if (currentState == Entity.State.WALKING) {
                    animation = animations.get(Entity.AnimationType.WALK_LEFT);
                    if( animation == null ) return;
                    currentFrame = animation.getKeyFrame(frameTime);
                } else if(currentState == Entity.State.IDLE) {
                    animation = animations.get(Entity.AnimationType.WALK_LEFT);
                    if( animation == null ) return;
                    currentFrame = animation.getKeyFrame(0);
                } else if(currentState == Entity.State.IMMOBILE) {
                    animation = animations.get(Entity.AnimationType.IMMOBILE);
                    if( animation == null ) return;
                    currentFrame = animation.getKeyFrame(frameTime);
                }
                break;
            case UP:
                if (currentState == Entity.State.WALKING) {
                    animation = animations.get(Entity.AnimationType.WALK_UP);
                    if( animation == null ) return;
                    currentFrame = animation.getKeyFrame(frameTime);
                } else if(currentState == Entity.State.IDLE) {
                    animation = animations.get(Entity.AnimationType.WALK_UP);
                    if( animation == null ) return;
                    currentFrame = animation.getKeyFrame(0);
                } else if(currentState == Entity.State.IMMOBILE) {
                    animation = animations.get(Entity.AnimationType.IMMOBILE);
                    if( animation == null ) return;
                    currentFrame = animation.getKeyFrame(frameTime);
                }
                break;
            case RIGHT:
                if (currentState == Entity.State.WALKING) {
                    animation = animations.get(Entity.AnimationType.WALK_RIGHT);
                    if( animation == null ) return;
                    currentFrame = animation.getKeyFrame(frameTime);
                } else if(currentState == Entity.State.IDLE) {
                    animation = animations.get(Entity.AnimationType.WALK_RIGHT);
                    if( animation == null ) return;
                    currentFrame = animation.getKeyFrame(0);
                } else if(currentState == Entity.State.IMMOBILE) {
                    animation = animations.get(Entity.AnimationType.IMMOBILE);
                    if( animation == null ) return;
                    currentFrame = animation.getKeyFrame(frameTime);
                }
                break;
            default:
                break;
        }
    }

    //Specific to two frame animations where each frame is stored in a separate texture
    protected Animation loadAnimation(String firstTexture, String secondTexture, Array<GridPoint2> points, float frameDuration){
        Utility.loadTextureAsset(firstTexture);
        Texture texture1 = Utility.getTextureAsset(firstTexture);

        Utility.loadTextureAsset(secondTexture);
        Texture texture2 = Utility.getTextureAsset(secondTexture);

        TextureRegion[][] texture1Frames = TextureRegion.split(texture1, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT);
        TextureRegion[][] texture2Frames = TextureRegion.split(texture2, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT);

        Array<TextureRegion> animationKeyFrames = new Array<TextureRegion>(2);

        GridPoint2 point = points.first();

        animationKeyFrames.add(texture1Frames[point.x][point.y]);
        animationKeyFrames.add(texture2Frames[point.x][point.y]);

        return new Animation(frameDuration, animationKeyFrames, Animation.PlayMode.LOOP);
    }

    protected Animation loadAnimation(String textureName, Array<GridPoint2> points, float frameDuration){
        Utility.loadTextureAsset(textureName);
        Texture texture = Utility.getTextureAsset(textureName);

        TextureRegion[][] textureFrames = TextureRegion.split(texture, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT);

        Array<TextureRegion> animationKeyFrames = new Array<TextureRegion>(points.size);

        for( GridPoint2 point : points){
            animationKeyFrames.add(textureFrames[point.x][point.y]);
        }

        return new Animation(frameDuration, animationKeyFrames, Animation.PlayMode.LOOP);
    }

    public Animation getAnimation(Entity.AnimationType type){
        return animations.get(type);
    }
}
