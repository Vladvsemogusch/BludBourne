package com.packtpub.libgdx.bludbourne.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.packtpub.libgdx.bludbourne.BludBourne;
import com.packtpub.libgdx.bludbourne.Utility;
import com.packtpub.libgdx.bludbourne.audio.AudioObserver;
import com.packtpub.libgdx.bludbourne.profile.ProfileManager;


public class LoadGameScreen extends GameScreen {
    private Stage stage;
	private BludBourne game;
	private List listItems;
	
	public LoadGameScreen(BludBourne inputGame){
	 	this.game = inputGame;

		//create
	 	stage = new Stage();
		TextButton loadButton = new TextButton("Load", Utility.STATUSUI_SKIN);
		TextButton backButton = new TextButton("Back",Utility.STATUSUI_SKIN);

		ProfileManager.getInstance().storeAllProfiles();
	 	listItems = new List(Utility.STATUSUI_SKIN, "inventory");
		Array<String> list = ProfileManager.getInstance().getProfileList();
	 	listItems.setItems(list);
		ScrollPane scrollPane = new ScrollPane(listItems);

		scrollPane.setOverscroll(false, false);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setScrollbarsOnTop(true);

		Table table = new Table();
		Table bottomTable = new Table();

		//Layout
		table.center();
		table.setFillParent(true);
		table.padBottom(loadButton.getHeight());
		table.add(scrollPane).center();

		bottomTable.setHeight(loadButton.getHeight());
		bottomTable.setWidth(Gdx.graphics.getWidth());
		bottomTable.center();
		bottomTable.add(loadButton).padRight(50);
		bottomTable.add(backButton);

	 	stage.addActor(table);
	 	stage.addActor(bottomTable);

		//Listeners
		backButton.addListener(new ClickListener() {
								   @Override
								   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
									   return true;
								   }

								   @Override
								   public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
									   game.setScreen(game.getScreenType(BludBourne.ScreenType.MainMenu));
								   }
							   }
		);

		loadButton.addListener(new ClickListener() {
								   @Override
								   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
									   return true;
								   }

								   @Override
								   public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
									   if( listItems.getSelected() == null ) return;
									   String fileName = listItems.getSelected().toString();
									   if (fileName != null && !fileName.isEmpty()) {
										   FileHandle file = ProfileManager.getInstance().getProfileFile(fileName);
										   if (file != null) {
											   ProfileManager.getInstance().setCurrentProfile(fileName);
											   LoadGameScreen.this.notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
											   game.setScreen(game.getScreenType(BludBourne.ScreenType.MainGame));
										   }
									   }
								   }

							   }
		);
	}
	
	@Override
	public void render(float delta) {
		if( delta == 0){
			return;
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	 	stage.act(delta);
	 	stage.draw();
	}

	@Override
	public void resize(int width, int height) {
	 	stage.getViewport().setScreenSize(width, height);
	}

	@Override
	public void show() {
		Array<String> list = ProfileManager.getInstance().getProfileList();
	 	listItems.setItems(list);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	 	stage.clear();
	 	stage.dispose();
	}

}
