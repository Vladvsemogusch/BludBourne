package com.packtpub.libgdx.bludbourne;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Screen;
import com.packtpub.libgdx.bludbourne.screens.CreditScreen;
import com.packtpub.libgdx.bludbourne.screens.CutSceneScreen;
import com.packtpub.libgdx.bludbourne.screens.GameOverScreen;
import com.packtpub.libgdx.bludbourne.screens.LoadGameScreen;
import com.packtpub.libgdx.bludbourne.screens.MainGameScreen;
import com.packtpub.libgdx.bludbourne.screens.MainMenuScreen;
import com.packtpub.libgdx.bludbourne.screens.NewGameScreen;


public class BludBourne extends Game {

	private static MainGameScreen mainGameScreen;
	private static MainMenuScreen mainMenuScreen;
	private static LoadGameScreen loadGameScreen;
	private static NewGameScreen newGameScreen;
	private static GameOverScreen gameOverScreen;
	private static CutSceneScreen cutSceneScreen;
	private static CreditScreen creditScreen;

	public static enum ScreenType{
		MainMenu,
		MainGame,
		LoadGame,
		NewGame,
		GameOver,
		WatchIntro,
		Credits
	}

	public Screen getScreenType(ScreenType screenType){
		switch(screenType){
			case MainMenu:
				return mainMenuScreen;
			case MainGame:
				return mainGameScreen;
			case LoadGame:
				return loadGameScreen;
			case NewGame:
				return newGameScreen;
			case GameOver:
				return gameOverScreen;
			case WatchIntro:
				return cutSceneScreen;
			case Credits:
				return creditScreen;
			default:
				return mainMenuScreen;
		}

	}

	@Override
	public void create(){
	 	mainGameScreen = new MainGameScreen(this);
	 	mainMenuScreen = new MainMenuScreen(this);
	 	loadGameScreen = new LoadGameScreen(this);
	 	newGameScreen = new NewGameScreen(this);
	 	gameOverScreen = new GameOverScreen(this);
	 	cutSceneScreen = new CutSceneScreen(this);
	 	creditScreen = new CreditScreen(this);
		setScreen(mainMenuScreen);
	}

	@Override
	public void dispose(){
	 	mainGameScreen.dispose();
	 	mainMenuScreen.dispose();
	 	loadGameScreen.dispose();
	 	newGameScreen.dispose();
	 	gameOverScreen.dispose();
	 	creditScreen.dispose();
	}

}
