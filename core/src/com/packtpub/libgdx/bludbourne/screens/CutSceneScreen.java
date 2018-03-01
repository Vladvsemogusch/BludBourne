package com.packtpub.libgdx.bludbourne.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.packtpub.libgdx.bludbourne.BludBourne;
import com.packtpub.libgdx.bludbourne.Entity;
import com.packtpub.libgdx.bludbourne.EntityFactory;
import com.packtpub.libgdx.bludbourne.Map;
import com.packtpub.libgdx.bludbourne.MapFactory;
import com.packtpub.libgdx.bludbourne.UI.AnimatedImage;
import com.packtpub.libgdx.bludbourne.Utility;
import com.packtpub.libgdx.bludbourne.audio.AudioObserver;
import com.packtpub.libgdx.bludbourne.battle.MonsterFactory;
import com.packtpub.libgdx.bludbourne.profile.ProfileManager;
import com.packtpub.libgdx.bludbourne.sfx.ScreenTransitionAction;
import com.packtpub.libgdx.bludbourne.sfx.ScreenTransitionActor;

public class CutSceneScreen extends MainGameScreen {
    private BludBourne game;
    private Stage stage;
    private Viewport viewport;
    private Stage _UIStage;
    private Viewport _UIViewport;
    private Actor followingActor;
    private Dialog messageBoxUI;
    private Label label;
    private boolean isCameraFixed = true;
    private ScreenTransitionActor transitionActor;
    private Action introCutSceneAction;
    private Action switchScreenAction;
    private Action setupScene01;
    private Action setupScene02;
    private Action setupScene03;
    private Action setupScene04;
    private Action setupScene05;

    private AnimatedImage animBlackSmith;
    private AnimatedImage animInnKeeper;
    private AnimatedImage animMage;
    private AnimatedImage animFire;
    private AnimatedImage animDemon;

    public CutSceneScreen(BludBourne game) {
        super(game);

        this.game = game;

        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);

        _UIViewport = new ScreenViewport(hudCamera);
        _UIStage = new Stage(_UIViewport);

        label = new Label("Test", Utility.STATUSUI_SKIN);
        label.setWrap(true);

        messageBoxUI = new Dialog("", Utility.STATUSUI_SKIN, "solidbackground");
        messageBoxUI.setVisible(false);
        messageBoxUI.getContentTable().add(label).width(stage.getWidth()/2).pad(10, 10, 10, 0);
        messageBoxUI.pack();
        messageBoxUI.setPosition(stage.getWidth() / 2 - messageBoxUI.getWidth() / 2, stage.getHeight() - messageBoxUI.getHeight());

        followingActor = new Actor();
        followingActor.setPosition(0, 0);

        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_INTRO_CUTSCENE);

        animBlackSmith = getAnimatedImage(EntityFactory.EntityName.TOWN_BLACKSMITH);
        animInnKeeper = getAnimatedImage(EntityFactory.EntityName.TOWN_INNKEEPER);
        animMage = getAnimatedImage(EntityFactory.EntityName.TOWN_MAGE);
        animFire = getAnimatedImage(EntityFactory.EntityName.FIRE);
        animDemon = getAnimatedImage(MonsterFactory.MonsterEntityType.MONSTER042);

        //Actions
        switchScreenAction = new RunnableAction(){
            @Override
            public void run() {
                CutSceneScreen.this.game.setScreen(CutSceneScreen.this.game.getScreenType(BludBourne.ScreenType.MainMenu));
            }
        };

        setupScene01 = new RunnableAction() {
            @Override
            public void run() {
                hideMessage();
                mapMgr.loadMap(MapFactory.MapType.TOWN);
                mapMgr.disableCurrentmapMusic();
                setCameraPosition(10, 16);

                animBlackSmith.setVisible(true);
                animInnKeeper.setVisible(true);
                animMage.setVisible(true);

                animBlackSmith.setPosition(10, 16);
                animInnKeeper.setPosition(12, 15);
                animMage.setPosition(11, 17);

                animDemon.setVisible(false);
                animFire.setVisible(false);
            }
        };

        setupScene02 = new RunnableAction() {
            @Override
            public void run() {
                hideMessage();
                mapMgr.loadMap(MapFactory.MapType.TOP_WORLD);
                mapMgr.disableCurrentmapMusic();
                setCameraPosition(50, 30);

                animBlackSmith.setPosition(50, 30);
                animInnKeeper.setPosition(52, 30);
                animMage.setPosition(50, 28);

                animFire.setPosition(52, 28);
                animFire.setVisible(true);
            }
        };

        setupScene03 = new RunnableAction() {
            @Override
            public void run() {
                animDemon.setPosition(52, 28);
                animDemon.setVisible(true);
                hideMessage();
            }
        };

        setupScene04 = new RunnableAction() {
            @Override
            public void run() {
                hideMessage();
                animBlackSmith.setVisible(false);
                animInnKeeper.setVisible(false);
                animMage.setVisible(false);
                animFire.setVisible(false);

                mapMgr.loadMap(MapFactory.MapType.TOP_WORLD);
                mapMgr.disableCurrentmapMusic();

                animDemon.setVisible(true);
                animDemon.setScale(1, 1);
                animDemon.setSize(16 * Map.UNIT_SCALE, 16 * Map.UNIT_SCALE);
                animDemon.setPosition(50, 40);

                followActor(animDemon);
            }
        };

        setupScene05 = new RunnableAction() {
            @Override
            public void run() {
                hideMessage();
                animBlackSmith.setVisible(false);
                animInnKeeper.setVisible(false);
                animMage.setVisible(false);
                animFire.setVisible(false);

                mapMgr.loadMap(MapFactory.MapType.CASTLE_OF_DOOM);
                mapMgr.disableCurrentmapMusic();
                followActor(animDemon);

                animDemon.setVisible(true);
                animDemon.setPosition(15, 1);
            }
        };

        transitionActor = new ScreenTransitionActor();

         //layout
        stage.addActor(animMage);
        stage.addActor(animBlackSmith);
        stage.addActor(animInnKeeper);
        stage.addActor(animFire);
        stage.addActor(animDemon);
        stage.addActor(transitionActor);

        _UIStage.addActor(messageBoxUI);
    }

    private Action getCutsceneAction(){
        setupScene01.reset();
        setupScene02.reset();
        setupScene03.reset();
        setupScene04.reset();
        setupScene05.reset();
        switchScreenAction.reset();

        return Actions.sequence(
                Actions.addAction(setupScene01),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3), transitionActor),
                Actions.delay(3),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("BLACKSMITH: We have planned this long enough. The time is now! I have had enough talk...");
                            }
                        }),
                Actions.delay(7),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("MAGE: This is dark magic you fool. We must proceed with caution, or this could end badly for all of us");
                            }
                        }),
                Actions.delay(7),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("INNKEEPER: Both of you need to keep it down. If we get caught using black magic, we will all be hanged!");
                            }
                        }),
                Actions.delay(5),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3), transitionActor),
                Actions.delay(3),
                Actions.addAction(setupScene02),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3), transitionActor),
                Actions.delay(3),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("BLACKSMITH: Now, let's get on with this. I don't like the cemeteries very much...");
                            }
                        }
                ),
                Actions.delay(7),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("MAGE: I told you, we can't rush the spell. Bringing someone back to life isn't simple!");
                            }
                        }
                ),
                Actions.delay(7),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("INNKEEPER: I know you loved your daughter, but this just isn't right...");
                            }
                        }
                ),
                Actions.delay(7),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("BLACKSMITH: You have never had a child of your own. You just don't understand!");
                            }
                        }
                ),
                Actions.delay(7),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("MAGE: You both need to concentrate, wait...Oh no, something is wrong!!");
                            }
                        }
                ),
                Actions.delay(7),
                Actions.addAction(setupScene03),
                Actions.addAction(Actions.fadeOut(2), animDemon),
                Actions.delay(2),
                Actions.addAction(Actions.fadeIn(2), animDemon),
                Actions.delay(2),
                Actions.addAction(Actions.fadeOut(2), animDemon),
                Actions.delay(2),
                Actions.addAction(Actions.fadeIn(2), animDemon),
                Actions.delay(2),
                Actions.addAction(Actions.fadeOut(2), animDemon),
                Actions.delay(2),
                Actions.addAction(Actions.fadeIn(2), animDemon),
                Actions.delay(2),
                Actions.addAction(Actions.scaleBy(40, 40, 5, Interpolation.linear), animDemon),
                Actions.delay(5),
                Actions.addAction(Actions.moveBy(20, 0), animDemon),
                Actions.delay(2),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("BLACKSMITH: What...What have we done...");
                            }
                        }
                ),
                Actions.delay(3),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3), transitionActor),
                Actions.delay(3),
                Actions.addAction(setupScene04),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3), transitionActor),
                Actions.addAction(Actions.moveTo(54, 65, 13, Interpolation.linear), animDemon),
                Actions.delay(10),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3), transitionActor),
                Actions.delay(3),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3), transitionActor),
                Actions.addAction(setupScene05),
                Actions.addAction(Actions.moveTo(15, 76, 15, Interpolation.linear), animDemon),
                Actions.delay(15),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("DEMON: I will now send my legions of demons to destroy these sacks of meat!");
                            }
                        }
                ),
                Actions.delay(5),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3), transitionActor),
                Actions.delay(5),
                Actions.after(switchScreenAction)
        );

    }

    private AnimatedImage getAnimatedImage(EntityFactory.EntityName entityName){
        Entity entity = EntityFactory.getInstance().getEntityByName(entityName);
        return setEntityAnimation(entity);
    }

    private AnimatedImage getAnimatedImage(MonsterFactory.MonsterEntityType entityName){
        Entity entity = MonsterFactory.getInstance().getMonster(entityName);
        return setEntityAnimation(entity);
    }

    private AnimatedImage setEntityAnimation(Entity entity){
        final AnimatedImage animEntity = new AnimatedImage();
        animEntity.setEntity(entity);
        animEntity.setSize(animEntity.getWidth() * Map.UNIT_SCALE, animEntity.getHeight() * Map.UNIT_SCALE);
        return animEntity;
    }

    public void followActor(Actor actor){
        followingActor = actor;
        isCameraFixed = false;
    }

    public void setCameraPosition(float x, float y){
        camera.position.set(x, y, 0f);
        isCameraFixed = true;
    }

    public void showMessage(String message){
        label.setText(message);
        messageBoxUI.pack();
        messageBoxUI.setVisible(true);
    }

    public void hideMessage(){
        messageBoxUI.setVisible(false);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);

        mapRenderer.getBatch().enableBlending();
        mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if( mapMgr.hasMapChanged() ){
            mapRenderer.setMap(mapMgr.getCurrentTiledMap());
            mapMgr.setMapChanged(false);
        }

        mapRenderer.render();

        if( !isCameraFixed ){
            camera.position.set(followingActor.getX(), followingActor.getY(), 0f);
        }
        camera.update();

        _UIStage.act(delta);
        _UIStage.draw();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        introCutSceneAction = getCutsceneAction();
        stage.addAction(introCutSceneAction);
        notify(AudioObserver.AudioCommand.MUSIC_STOP_ALL, AudioObserver.AudioTypeEvent.NONE);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_INTRO_CUTSCENE);
        ProfileManager.getInstance().removeAllObservers();
        if( mapRenderer == null ){
            mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), Map.UNIT_SCALE);
        }
    }

    @Override
    public void hide() {
        notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_INTRO_CUTSCENE);
        ProfileManager.getInstance().removeAllObservers();
        Gdx.input.setInputProcessor(null);
    }


}
