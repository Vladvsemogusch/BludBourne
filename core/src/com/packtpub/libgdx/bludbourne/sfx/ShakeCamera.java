package com.packtpub.libgdx.bludbourne.sfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ShakeCamera {
	private static final String TAG = ShakeCamera.class.getSimpleName();
	
	private boolean isShaking = false;
	private float origShakeRadius = 30.0f;
	private float shakeRadius;
	private float randomAngle;
	private Vector2 offset;
	private Vector2 currentPosition;
	private Vector2 origPosition;

	
	public ShakeCamera(float x, float y, float shakeRadius){
		this.origPosition = new Vector2(x,y);
		this.shakeRadius = shakeRadius;
		this.origShakeRadius = shakeRadius;
		this.offset = new Vector2();
		this.currentPosition = new Vector2();
		reset();
	}
	
	public boolean isCameraShaking(){
		return isShaking;
	}
	
	public void startShaking(){
	 	isShaking = true;
	}
	
	private void seedRandomAngle(){
	 	randomAngle = MathUtils.random(1, 360);
	}
	
	private void computeCameraOffset(){
		float sine = MathUtils.sinDeg(randomAngle);
		float cosine = MathUtils.cosDeg(randomAngle);

		//Gdx.app.debug(TAG, "Sine of " + randomAngle + " is: " + sine);
		//Gdx.app.debug(TAG, "Cosine of " + randomAngle + " is: " + cosine);

	 	offset.x =  cosine * shakeRadius;
	 	offset.y =  sine * shakeRadius;

		//Gdx.app.debug(TAG, "Offset is x:" + offset.x + " , y: " + offset.y );
	}
	
	private void computeCurrentPosition(){
	 	currentPosition.x = origPosition.x + offset.x;
	 	currentPosition.y = origPosition.y + offset.y;

		//Gdx.app.debug(TAG, "Current position is x:" + currentPosition.x + " , y: " + currentPosition.y );
	}
	
	private void diminishShake(){
		//Gdx.app.debug(TAG, "Current shakeRadius is: " + shakeRadius + " randomAngle is: " + randomAngle);

		if( shakeRadius < 2.0 ){
			//Gdx.app.debug(TAG, "Done shaking");
			reset();
			return;
		}
		
	 	isShaking = true;
	 	shakeRadius *= .9f;
		//Gdx.app.debug(TAG, "New shakeRadius is: " + shakeRadius);


	 	randomAngle = MathUtils.random(1, 360);
		//Gdx.app.debug(TAG, "New random angle: " + randomAngle);
	}

	public void reset(){
	 	shakeRadius = origShakeRadius;
	 	isShaking = false;
		seedRandomAngle();
	 	currentPosition.x = origPosition.x;
	 	currentPosition.y = origPosition.y;
	}
	
	public Vector2 getNewShakePosition(){
		computeCameraOffset();
		computeCurrentPosition();
		diminishShake();
		return currentPosition;
	}
}
