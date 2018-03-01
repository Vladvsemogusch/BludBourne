package com.packtpub.libgdx.bludbourne.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.packtpub.libgdx.bludbourne.BludBourne;
import com.packtpub.libgdx.bludbourne.Utility;

public class CreditScreen extends GameScreen {
    private static String CREDITS_PATH = "licenses/credits.txt";
    private Stage stage;
    private ScrollPane scrollPane;
    private BludBourne game;

    public CreditScreen(BludBourne inputGame){
        this.game = inputGame;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        //Get text
        FileHandle file = Gdx.files.internal(CREDITS_PATH);
        String textString = file.readString();

        Label text = new Label(textString, Utility.STATUSUI_SKIN, "credits");
        text.setAlignment(Align.top | Align.center);
        text.setWrap(true);

        scrollPane = new ScrollPane(text);
        scrollPane.addListener(new ClickListener() {
                                    @Override
                                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                        return true;
                                    }

                                    @Override
                                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                        scrollPane.setScrollY(0);
                                        scrollPane.updateVisualScroll();
                                        game.setScreen(game.getScreenType(BludBourne.ScreenType.MainMenu));
                                    }
                               }
        );

        Table table = new Table();
        table.center();
        table.setFillParent(true);
        table.defaults().width(Gdx.graphics.getWidth());
        table.add(scrollPane);

        stage.addActor(table);
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

        scrollPane.setScrollY(scrollPane.getScrollY()+delta*20);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().setScreenSize(width, height);
    }

    @Override
    public void show() {
        scrollPane.setVisible(true);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        scrollPane.setVisible(false);
        scrollPane.setScrollY(0);
        scrollPane.updateVisualScroll();
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
        scrollPane = null;
        stage.dispose();
    }
}