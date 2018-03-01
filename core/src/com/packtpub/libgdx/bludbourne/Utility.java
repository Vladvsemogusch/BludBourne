package com.packtpub.libgdx.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

public final class Utility {
	public static final AssetManager assetManager = new AssetManager();
	private static final String TAG = Utility.class.getSimpleName();
	private static InternalFileHandleResolver filePathResolver =  new InternalFileHandleResolver();

	private final static String STATUSUI_TEXTURE_ATLAS_PATH = "skins/statusui.atlas";
	private final static String STATUSUI_SKIN_PATH = "skins/statusui.json";
	private final static String ITEMS_TEXTURE_ATLAS_PATH = "skins/items.atlas";
	private final static String ITEMS_SKIN_PATH = "skins/items.json";

	public static TextureAtlas STATUSUI_TEXTUREATLAS = new TextureAtlas(STATUSUI_TEXTURE_ATLAS_PATH);
	public static TextureAtlas ITEMS_TEXTUREATLAS = new TextureAtlas(ITEMS_TEXTURE_ATLAS_PATH);
	public static Skin STATUSUI_SKIN = new Skin(Gdx.files.internal(STATUSUI_SKIN_PATH), STATUSUI_TEXTUREATLAS);

	public static void unloadAsset(String assetFilenamePath){
	// once the asset manager is done loading
	if( assetManager.isLoaded(assetFilenamePath) ){
	 	assetManager.unload(assetFilenamePath);
		} else {
			Gdx.app.debug(TAG, "Asset is not loaded; Nothing to unload: " + assetFilenamePath );
		}
	}

	public static float loadCompleted(){
		return assetManager.getProgress();
	}

	public static int numberAssetsQueued(){
		return assetManager.getQueuedAssets();
	}

   	public static boolean updateAssetLoading(){
		return assetManager.update();
	}

	public static boolean isAssetLoaded(String fileName){
	   return assetManager.isLoaded(fileName);

	}

	public static void loadMapAsset(String mapFilenamePath){
		if( mapFilenamePath == null || mapFilenamePath.isEmpty() ){
		   return;
		}

		if( assetManager.isLoaded(mapFilenamePath) ){
			return;
		}

	   //load asset
		if( filePathResolver.resolve(mapFilenamePath).exists() ){
		 	assetManager.setLoader(TiledMap.class, new TmxMapLoader(filePathResolver));
		 	assetManager.load(mapFilenamePath, TiledMap.class);
			//Until we add loading screen, just block until we load the map
		 	assetManager.finishLoadingAsset(mapFilenamePath);
			Gdx.app.debug(TAG, "Map loaded!: " + mapFilenamePath);
		}
		else{
			Gdx.app.debug(TAG, "Map doesn't exist!: " + mapFilenamePath );
		}
	}


	public static TiledMap getMapAsset(String mapFilenamePath){
		TiledMap map = null;

		// once the asset manager is done loading
		if( assetManager.isLoaded(mapFilenamePath) ){
			map = assetManager.get(mapFilenamePath,TiledMap.class);
		} else {
			Gdx.app.debug(TAG, "Map is not loaded: " + mapFilenamePath );
		}

		return map;
	}

	public static void loadSoundAsset(String soundFilenamePath){
		if( soundFilenamePath == null || soundFilenamePath.isEmpty() ){
			return;
		}

		if( assetManager.isLoaded(soundFilenamePath) ){
			return;
		}

		//load asset
		if( filePathResolver.resolve(soundFilenamePath).exists() ){
		 	assetManager.setLoader(Sound.class, new SoundLoader(filePathResolver));
		 	assetManager.load(soundFilenamePath, Sound.class);
			//Until we add loading screen, just block until we load the map
		 	assetManager.finishLoadingAsset(soundFilenamePath);
			Gdx.app.debug(TAG, "Sound loaded!: " + soundFilenamePath);
		}
		else{
			Gdx.app.debug(TAG, "Sound doesn't exist!: " + soundFilenamePath );
		}
	}


	public static Sound getSoundAsset(String soundFilenamePath){
		Sound sound = null;

		// once the asset manager is done loading
		if( assetManager.isLoaded(soundFilenamePath) ){
			sound = assetManager.get(soundFilenamePath,Sound.class);
		} else {
			Gdx.app.debug(TAG, "Sound is not loaded: " + soundFilenamePath );
		}

		return sound;
	}

	public static void loadMusicAsset(String musicFilenamePath){
		if( musicFilenamePath == null || musicFilenamePath.isEmpty() ){
			return;
		}

		if( assetManager.isLoaded(musicFilenamePath) ){
			return;
		}

		//load asset
		if( filePathResolver.resolve(musicFilenamePath).exists() ){
		 	assetManager.setLoader(Music.class, new MusicLoader(filePathResolver));
		 	assetManager.load(musicFilenamePath, Music.class);
			//Until we add loading screen, just block until we load the map
		 	assetManager.finishLoadingAsset(musicFilenamePath);
			Gdx.app.debug(TAG, "Music loaded!: " + musicFilenamePath);
		}
		else{
			Gdx.app.debug(TAG, "Music doesn't exist!: " + musicFilenamePath );
		}
	}


	public static Music getMusicAsset(String musicFilenamePath){
		Music music = null;

		// once the asset manager is done loading
		if( assetManager.isLoaded(musicFilenamePath) ){
			music = assetManager.get(musicFilenamePath,Music.class);
		} else {
			Gdx.app.debug(TAG, "Music is not loaded: " + musicFilenamePath );
		}

		return music;
	}


	public static void loadTextureAsset(String textureFilenamePath){
		if( textureFilenamePath == null || textureFilenamePath.isEmpty() ){
			return;
		}

		if( assetManager.isLoaded(textureFilenamePath) ){
			return;
		}

		//load asset
		if( filePathResolver.resolve(textureFilenamePath).exists() ){
		 	assetManager.setLoader(Texture.class, new TextureLoader(filePathResolver));
		 	assetManager.load(textureFilenamePath, Texture.class);
			//Until we add loading screen, just block until we load the map
		 	assetManager.finishLoadingAsset(textureFilenamePath);
		}
		else{
			Gdx.app.debug(TAG, "Texture doesn't exist!: " + textureFilenamePath );
		}
	}

	public static Texture getTextureAsset(String textureFilenamePath){
		Texture texture = null;

		// once the asset manager is done loading
		if( assetManager.isLoaded(textureFilenamePath) ){
			texture = assetManager.get(textureFilenamePath,Texture.class);
		} else {
			Gdx.app.debug(TAG, "Texture is not loaded: " + textureFilenamePath );
		}

		return texture;
	}


}
