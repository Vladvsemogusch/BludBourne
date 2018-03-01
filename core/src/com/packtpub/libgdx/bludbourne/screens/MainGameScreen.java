package com.packtpub.libgdx.bludbourne.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Json;

import com.packtpub.libgdx.bludbourne.BludBourne;
import com.packtpub.libgdx.bludbourne.MapFactory;
import com.packtpub.libgdx.bludbourne.MapManager;
import com.packtpub.libgdx.bludbourne.Entity;
import com.packtpub.libgdx.bludbourne.EntityFactory;
import com.packtpub.libgdx.bludbourne.Map;
import com.packtpub.libgdx.bludbourne.UI.PlayerHUD;
import com.packtpub.libgdx.bludbourne.audio.AudioManager;
import com.packtpub.libgdx.bludbourne.audio.AudioObserver;
import com.packtpub.libgdx.bludbourne.profile.ProfileManager;
import com.packtpub.libgdx.bludbourne.Component;

public class MainGameScreen extends GameScreen {
	private static final String TAG = MainGameScreen.class.getSimpleName();

	public static class VIEWPORT {
		public static float viewportWidth;
		public static float viewportHeight;
		public static float virtualWidth;
		public static float virtualHeight;
		public static float physicalWidth;
		public static float physicalHeight;
		public static float aspectRatio;
	}

	public static enum GameState {
		SAVING,
		LOADING,
		RUNNING,
		PAUSED,
		GAME_OVER
	}
	private static GameState gameState;

	protected OrthogonalTiledMapRenderer mapRenderer;
	protected MapManager mapMgr;
	protected OrthographicCamera camera;
	protected OrthographicCamera hudCamera;

	private Json json;
	private BludBourne game;
	private InputMultiplexer multiplexer;

	private Entity player;
	private PlayerHUD playerHUD;

	public MainGameScreen(BludBourne inputGame){
	 	this.game = inputGame;
	 	mapMgr = new MapManager();
	 	json = new Json();

		setGameState(GameState.RUNNING);

		//_camera setup
		setupViewport(10, 10);

		//get the current size
	 	camera = new OrthographicCamera();
	 	camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

	 	player = EntityFactory.getEntity(EntityFactory.EntityType.PLAYER);
	 	mapMgr.setPlayer(player);
	 	mapMgr.setCamera(camera);

	 	hudCamera = new OrthographicCamera();
	 	hudCamera.setToOrtho(false, VIEWPORT.physicalWidth, VIEWPORT.physicalHeight);

	 	playerHUD = new PlayerHUD(hudCamera, player, mapMgr);

	 	multiplexer = new InputMultiplexer();
	 	multiplexer.addProcessor(playerHUD.getStage());
	 	multiplexer.addProcessor(player.getInputProcessor());
		Gdx.input.setInputProcessor(multiplexer);

		//Gdx.app.debug(TAG, "UnitScale value is: " + mapRenderer.getUnitScale());
	}

	@Override
	public void show() {
		ProfileManager.getInstance().addObserver(mapMgr);
		ProfileManager.getInstance().addObserver(playerHUD);

		setGameState(GameState.LOADING);
		Gdx.input.setInputProcessor(multiplexer);


		if( mapRenderer == null ){
		 	mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), Map.UNIT_SCALE);
		}
	}

	@Override
	public void hide() {
		if( gameState != GameState.GAME_OVER ){
			setGameState(GameState.SAVING);
		}

		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void render(float delta) {
		if( gameState == GameState.GAME_OVER ){
		 	game.setScreen(game.getScreenType(BludBourne.ScreenType.GameOver));
		}

		if( gameState == GameState.PAUSED ){
		 	player.updateInput(delta);
		 	playerHUD.render(delta);
			return;
		}
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	 	mapRenderer.setView(camera);

	 	mapRenderer.getBatch().enableBlending();
	 	mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		if( mapMgr.hasMapChanged() ){
		 	mapRenderer.setMap(mapMgr.getCurrentTiledMap());
		 	player.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(mapMgr.getPlayerStartUnitScaled()));

		 	camera.position.set(mapMgr.getPlayerStartUnitScaled().x, mapMgr.getPlayerStartUnitScaled().y, 0f);
		 	camera.update();

		 	playerHUD.updateEntityObservers();

		 	mapMgr.setMapChanged(false);

		 	playerHUD.addTransitionToScreen();
		}

	 	mapMgr.updateLightMaps(playerHUD.getCurrentTimeOfDay());
		TiledMapImageLayer lightMap = (TiledMapImageLayer)mapMgr.getCurrentLightMapLayer();
		TiledMapImageLayer previousLightMap = (TiledMapImageLayer)mapMgr.getPreviousLightMapLayer();

		if( lightMap != null) {
		 	mapRenderer.getBatch().begin();
			TiledMapTileLayer backgroundMapLayer = (TiledMapTileLayer)mapMgr.getCurrentTiledMap().getLayers().get(Map.BACKGROUND_LAYER);
			if( backgroundMapLayer != null ){
			 	mapRenderer.renderTileLayer(backgroundMapLayer);
			}

			TiledMapTileLayer groundMapLayer = (TiledMapTileLayer)mapMgr.getCurrentTiledMap().getLayers().get(Map.GROUND_LAYER);
			if( groundMapLayer != null ){
			 	mapRenderer.renderTileLayer(groundMapLayer);
			}

			TiledMapTileLayer decorationMapLayer = (TiledMapTileLayer)mapMgr.getCurrentTiledMap().getLayers().get(Map.DECORATION_LAYER);
			if( decorationMapLayer != null ){
			 	mapRenderer.renderTileLayer(decorationMapLayer);
			}

		 	mapRenderer.getBatch().end();

		 	mapMgr.updateCurrentMapEntities(mapMgr, mapRenderer.getBatch(), delta);
		 	player.update(mapMgr, mapRenderer.getBatch(), delta);
		 	mapMgr.updateCurrentMapEffects(mapMgr, mapRenderer.getBatch(), delta);

		 	mapRenderer.getBatch().begin();
		 	mapRenderer.getBatch().setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA);

		 	mapRenderer.renderImageLayer(lightMap);
		 	mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		 	mapRenderer.getBatch().end();

			if( previousLightMap != null ){
			 	mapRenderer.getBatch().begin();
			 	mapRenderer.getBatch().setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
			 	mapRenderer.renderImageLayer(previousLightMap);
			 	mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			 	mapRenderer.getBatch().end();
			}
		}else{
		 	mapRenderer.render();
		 	mapMgr.updateCurrentMapEntities(mapMgr, mapRenderer.getBatch(), delta);
		 	player.update(mapMgr, mapRenderer.getBatch(), delta);
		 	mapMgr.updateCurrentMapEffects(mapMgr, mapRenderer.getBatch(), delta);
		}

	 	playerHUD.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		setupViewport(10, 10);
	 	camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
	 	playerHUD.resize((int) VIEWPORT.physicalWidth, (int) VIEWPORT.physicalHeight);
	}

	@Override
	public void pause() {
		setGameState(GameState.SAVING);
	 	playerHUD.pause();
	}

	@Override
	public void resume() {
		setGameState(GameState.LOADING);
	 	playerHUD.resume();
	}

	@Override
	public void dispose() {
		if( player != null ){
		 	player.unregisterObservers();
		 	player.dispose();
		}

		if( mapRenderer != null ){
		 	mapRenderer.dispose();
		}

		AudioManager.getInstance().dispose();
		MapFactory.clearCache();
	}

	public static void setGameState(GameState gameState){
		switch(gameState){
			case RUNNING:
			 	MainGameScreen.gameState = GameState.RUNNING;
				break;
			case LOADING:
				ProfileManager.getInstance().loadProfile();
				MainGameScreen.gameState = GameState.RUNNING;
				break;
			case SAVING:
				ProfileManager.getInstance().saveProfile();
				MainGameScreen.gameState = GameState.PAUSED;
				break;
			case PAUSED:
				if( gameState == GameState.PAUSED ){
					MainGameScreen.gameState = GameState.RUNNING;
				}else if( gameState == GameState.RUNNING ){
					MainGameScreen.gameState = GameState.PAUSED;
				}
				break;
			case GAME_OVER:
				MainGameScreen.gameState = GameState.GAME_OVER;
				break;
			default:
				MainGameScreen.gameState = GameState.RUNNING;
				break;
		}

	}

	private void setupViewport(int width, int height){
		//Make the viewport a percentage of the total display area
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;

		//Current viewport dimensions
		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

		//pixel dimensions of display
		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

		//aspect ratio for current viewport
		VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

		//update viewport if there could be skewing
		if( VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio){
			//Letterbox left and right
			VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth/VIEWPORT.physicalHeight);
			VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		}else{
			//letterbox above and below
			VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
			VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight/VIEWPORT.physicalWidth);
		}

		Gdx.app.debug(TAG, "WorldRenderer: virtual: (" + VIEWPORT.virtualWidth + "," + VIEWPORT.virtualHeight + ")" );
		Gdx.app.debug(TAG, "WorldRenderer: viewport: (" + VIEWPORT.viewportWidth + "," + VIEWPORT.viewportHeight + ")" );
		Gdx.app.debug(TAG, "WorldRenderer: physical: (" + VIEWPORT.physicalWidth + "," + VIEWPORT.physicalHeight + ")" );
	}
}
