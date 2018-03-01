package com.packtpub.libgdx.bludbourne.UI;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.packtpub.libgdx.bludbourne.Component;
import com.packtpub.libgdx.bludbourne.ComponentObserver;
import com.packtpub.libgdx.bludbourne.Entity;
import com.packtpub.libgdx.bludbourne.EntityConfig;
import com.packtpub.libgdx.bludbourne.InventoryItem;
import com.packtpub.libgdx.bludbourne.InventoryItem.ItemTypeID;
import com.packtpub.libgdx.bludbourne.MapManager;
import com.packtpub.libgdx.bludbourne.Utility;
import com.packtpub.libgdx.bludbourne.audio.AudioManager;
import com.packtpub.libgdx.bludbourne.audio.AudioObserver;
import com.packtpub.libgdx.bludbourne.audio.AudioSubject;
import com.packtpub.libgdx.bludbourne.battle.BattleObserver;
import com.packtpub.libgdx.bludbourne.dialog.ConversationGraph;
import com.packtpub.libgdx.bludbourne.dialog.ConversationGraphObserver;
import com.packtpub.libgdx.bludbourne.profile.ProfileManager;
import com.packtpub.libgdx.bludbourne.profile.ProfileObserver;
import com.packtpub.libgdx.bludbourne.quest.QuestGraph;
import com.packtpub.libgdx.bludbourne.screens.MainGameScreen;
import com.packtpub.libgdx.bludbourne.sfx.ClockActor;
import com.packtpub.libgdx.bludbourne.sfx.ScreenTransitionAction;
import com.packtpub.libgdx.bludbourne.sfx.ScreenTransitionActor;
import com.packtpub.libgdx.bludbourne.sfx.ShakeCamera;

public class PlayerHUD implements Screen, AudioSubject, ProfileObserver,ComponentObserver,ConversationGraphObserver,StoreInventoryObserver, BattleObserver, InventoryObserver, StatusObserver {
    private static final String TAG = PlayerHUD.class.getSimpleName();

    private Stage stage;
    private Viewport viewport;
    private Camera camera;
    private Entity player;

    private StatusUI statusUI;
    private InventoryUI inventoryUI;
    private ConversationUI conversationUI;
    private StoreInventoryUI storeInventoryUI;
    private QuestUI questUI;
    private BattleUI battleUI;

    private Dialog messageBoxUI;
    private Json json;
    private MapManager mapMgr;

    private Array<AudioObserver> observers;
    private ScreenTransitionActor transitionActor;

    private ShakeCamera shakeCam;
    private ClockActor clock;

    private static final String INVENTORY_FULL = "Your inventory is full!";

    public PlayerHUD(Camera camera, Entity player, MapManager mapMgr) {
        this.camera = camera;
        this.player = player;
        this.mapMgr = mapMgr;
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);
        //_stage.setDebugAll(true);

        observers = new Array<AudioObserver>();
        transitionActor = new ScreenTransitionActor();

        shakeCam = new ShakeCamera(0,0, 30.0f);

        json = new Json();
        messageBoxUI = new Dialog("Message", Utility.STATUSUI_SKIN, "solidbackground"){
            {
                button("OK");
                text(INVENTORY_FULL);
            }
            @Override
            protected void result(final Object object){
                cancel();
                setVisible(false);
            }

        };
        clock = new ClockActor("0", Utility.STATUSUI_SKIN);
        clock.setPosition(stage.getWidth()-clock.getWidth(),0);
        clock.setRateOfTime(60);
        clock.setVisible(true);

        messageBoxUI.setVisible(false);
        messageBoxUI.pack();
        messageBoxUI.setPosition(stage.getWidth() / 2 - messageBoxUI.getWidth() / 2, stage.getHeight() / 2 - messageBoxUI.getHeight() / 2);

        statusUI = new StatusUI();
        statusUI.setVisible(true);
        statusUI.setPosition(0, 0);
        statusUI.setKeepWithinStage(false);
        statusUI.setMovable(false);

        inventoryUI = new InventoryUI();
        inventoryUI.setKeepWithinStage(false);
        inventoryUI.setMovable(false);
        inventoryUI.setVisible(false);
        inventoryUI.setPosition(statusUI.getWidth(), 0);

        conversationUI = new ConversationUI();
        conversationUI.setMovable(true);
        conversationUI.setVisible(false);
        conversationUI.setPosition(stage.getWidth() / 2, 0);
        conversationUI.setWidth(stage.getWidth() / 2);
        conversationUI.setHeight(stage.getHeight() / 2);

        storeInventoryUI = new StoreInventoryUI();
        storeInventoryUI.setMovable(false);
        storeInventoryUI.setVisible(false);
        storeInventoryUI.setPosition(0, 0);

        questUI = new QuestUI();
        questUI.setMovable(false);
        questUI.setVisible(false);
        questUI.setKeepWithinStage(false);
        questUI.setPosition(0, stage.getHeight() / 2);
        questUI.setWidth(stage.getWidth());
        questUI.setHeight(stage.getHeight() / 2);

        battleUI = new BattleUI();
        battleUI.setMovable(false);
        //removes all listeners including ones that handle focus
        battleUI.clearListeners();
        battleUI.setVisible(false);

        stage.addActor(battleUI);
        stage.addActor(questUI);
        stage.addActor(storeInventoryUI);
        stage.addActor(conversationUI);
        stage.addActor(messageBoxUI);
        stage.addActor(statusUI);
        stage.addActor(inventoryUI);
        stage.addActor(clock);

        battleUI.validate();
        questUI.validate();
        storeInventoryUI.validate();
        conversationUI.validate();
        messageBoxUI.validate();
        statusUI.validate();
        inventoryUI.validate();
        clock.validate();

        //add tooltips to the stage
        Array<Actor> actors = inventoryUI.getInventoryActors();
        for(Actor actor : actors){
            stage.addActor(actor);
        }

        Array<Actor> storeActors = storeInventoryUI.getInventoryActors();
        for(Actor actor : storeActors ){
            stage.addActor(actor);
        }

        stage.addActor(transitionActor);
        transitionActor.setVisible(false);

        //Observers
        player.registerObserver(this);
        statusUI.addObserver(this);
        storeInventoryUI.addObserver(this);
        inventoryUI.addObserver(battleUI.getCurrentState());
        inventoryUI.addObserver(this);
        battleUI.getCurrentState().addObserver(this);
        this.addObserver(AudioManager.getInstance());

        //Listeners
        ImageButton inventoryButton = statusUI.getInventoryButton();
        inventoryButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                inventoryUI.setVisible(inventoryUI.isVisible() ? false : true);
            }
        });

        ImageButton questButton = statusUI.getQuestButton();
        questButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                questUI.setVisible(questUI.isVisible() ? false : true);
            }
        });

        conversationUI.getCloseButton().addListener(new ClickListener() {
                                                         @Override
                                                         public void clicked(InputEvent event, float x, float y) {
                                                             conversationUI.setVisible(false);
                                                             PlayerHUD.this.mapMgr.clearCurrentSelectedMapEntity();
                                                         }
                                                     }
        );

        storeInventoryUI.getCloseButton().addListener(new ClickListener() {
                                                           @Override
                                                           public void clicked(InputEvent event, float x, float y) {
                                                               storeInventoryUI.savePlayerInventory();
                                                               storeInventoryUI.cleanupStoreInventory();
                                                               storeInventoryUI.setVisible(false);
                                                               PlayerHUD.this.mapMgr.clearCurrentSelectedMapEntity();
                                                           }
                                                       }
        );

        //Music/Sound loading
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_LEVEL_UP_FANFARE);
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_COIN_RUSTLE);
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_CREATURE_PAIN);
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_PLAYER_PAIN);
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_PLAYER_WAND_ATTACK);
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_EATING);
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_DRINKING);
    }

    public Stage getStage() {
        return stage;
    }

    public ClockActor.TimeOfDay getCurrentTimeOfDay(){
        return clock.getCurrentTimeOfDay();
    }

    public void updateEntityObservers(){
        mapMgr.unregisterCurrentMapEntityObservers();
        questUI.initQuests(mapMgr);
        mapMgr.registerCurrentMapEntityObservers(this);
    }

    public void addTransitionToScreen(){
        transitionActor.setVisible(true);
        stage.addAction(
                Actions.sequence(
                        Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 1), transitionActor)));
    }

    @Override
    public void onNotify(ProfileManager profileManager, ProfileEvent event) {
        switch(event){
            case PROFILE_LOADED:
                boolean firstTime = profileManager.getIsNewProfile();

                if( firstTime ){
                    InventoryUI.clearInventoryItems(inventoryUI.getInventorySlotTable());
                    InventoryUI.clearInventoryItems(inventoryUI.getEquipSlotTable());
                    inventoryUI.resetEquipSlots();

                    questUI.setQuests(new Array<QuestGraph>());

                    //add default items if first time
                    Array<ItemTypeID> items = player.getEntityConfig().getInventory();
                    Array<InventoryItemLocation> itemLocations = new Array<InventoryItemLocation>();
                    for( int i = 0; i < items.size; i++){
                        itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.PLAYER_INVENTORY));
                    }
                    InventoryUI.populateInventory(inventoryUI.getInventorySlotTable(), itemLocations, inventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
                    profileManager.setProperty("playerInventory", InventoryUI.getInventory(inventoryUI.getInventorySlotTable()));

                    //start the player with some money
                    statusUI.setGoldValue(20);
                    statusUI.setStatusForLevel(1);

                    clock.setTotalTime(60 * 60 * 12); //start at noon
                    profileManager.setProperty("currentTime", clock.getTotalTime());
                }else{
                    int goldVal = profileManager.getProperty("currentPlayerGP", Integer.class);

                    Array<InventoryItemLocation> inventory = profileManager.getProperty("playerInventory", Array.class);
                    InventoryUI.populateInventory(inventoryUI.getInventorySlotTable(), inventory, inventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);

                    Array<InventoryItemLocation> equipInventory = profileManager.getProperty("playerEquipInventory", Array.class);
                    if( equipInventory != null && equipInventory.size > 0 ){
                        inventoryUI.resetEquipSlots();
                        InventoryUI.populateInventory(inventoryUI.getEquipSlotTable(), equipInventory, inventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
                    }

                    Array<QuestGraph> quests = profileManager.getProperty("playerQuests", Array.class);
                    questUI.setQuests(quests);

                    int xpMaxVal = profileManager.getProperty("currentPlayerXPMax", Integer.class);
                    int xpVal = profileManager.getProperty("currentPlayerXP", Integer.class);

                    int hpMaxVal = profileManager.getProperty("currentPlayerHPMax", Integer.class);
                    int hpVal = profileManager.getProperty("currentPlayerHP", Integer.class);

                    int mpMaxVal = profileManager.getProperty("currentPlayerMPMax", Integer.class);
                    int mpVal = profileManager.getProperty("currentPlayerMP", Integer.class);

                    int levelVal = profileManager.getProperty("currentPlayerLevel", Integer.class);

                    //set the current max values first
                    statusUI.setXPValueMax(xpMaxVal);
                    statusUI.setHPValueMax(hpMaxVal);
                    statusUI.setMPValueMax(mpMaxVal);

                    statusUI.setXPValue(xpVal);
                    statusUI.setHPValue(hpVal);
                    statusUI.setMPValue(mpVal);

                    //then add in current values
                    statusUI.setGoldValue(goldVal);
                    statusUI.setLevelValue(levelVal);

                    float totalTime = profileManager.getProperty("currentTime", Float.class);
                    clock.setTotalTime(totalTime);
                }

            break;
            case SAVING_PROFILE:
                profileManager.setProperty("playerQuests", questUI.getQuests());
                profileManager.setProperty("playerInventory", InventoryUI.getInventory(inventoryUI.getInventorySlotTable()));
                profileManager.setProperty("playerEquipInventory", InventoryUI.getInventory(inventoryUI.getEquipSlotTable()));
                profileManager.setProperty("currentPlayerGP", statusUI.getGoldValue() );
                profileManager.setProperty("currentPlayerLevel", statusUI.getLevelValue() );
                profileManager.setProperty("currentPlayerXP", statusUI.getXPValue() );
                profileManager.setProperty("currentPlayerXPMax", statusUI.getXPValueMax() );
                profileManager.setProperty("currentPlayerHP", statusUI.getHPValue() );
                profileManager.setProperty("currentPlayerHPMax", statusUI.getHPValueMax() );
                profileManager.setProperty("currentPlayerMP", statusUI.getMPValue() );
                profileManager.setProperty("currentPlayerMPMax", statusUI.getMPValueMax() );
                profileManager.setProperty("currentTime", clock.getTotalTime());
                break;
            case CLEAR_CURRENT_PROFILE:
                profileManager.setProperty("playerQuests", new Array<QuestGraph>());
                profileManager.setProperty("playerInventory", new Array<InventoryItemLocation>());
                profileManager.setProperty("playerEquipInventory", new Array<InventoryItemLocation>());
                profileManager.setProperty("currentPlayerGP", 0 );
                profileManager.setProperty("currentPlayerLevel",0 );
                profileManager.setProperty("currentPlayerXP", 0 );
                profileManager.setProperty("currentPlayerXPMax", 0 );
                profileManager.setProperty("currentPlayerHP", 0 );
                profileManager.setProperty("currentPlayerHPMax", 0 );
                profileManager.setProperty("currentPlayerMP", 0 );
                profileManager.setProperty("currentPlayerMPMax", 0 );
                profileManager.setProperty("currentTime", 0);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(String value, ComponentEvent event) {
        switch(event) {
            case LOAD_CONVERSATION:
                EntityConfig config = json.fromJson(EntityConfig.class, value);

                //Check to see if there is a version loading into properties
                if( config.getItemTypeID().equalsIgnoreCase(InventoryItem.ItemTypeID.NONE.toString()) ) {
                    EntityConfig configReturnProperty = ProfileManager.getInstance().getProperty(config.getEntityID(), EntityConfig.class);
                    if( configReturnProperty != null ){
                        config = configReturnProperty;
                    }
                }

                conversationUI.loadConversation(config);
                conversationUI.getCurrentConversationGraph().addObserver(this);
                break;
            case SHOW_CONVERSATION:
                EntityConfig configShow = json.fromJson(EntityConfig.class, value);

                if( configShow.getEntityID().equalsIgnoreCase(conversationUI.getCurrentEntityID())) {
                    conversationUI.setVisible(true);
                }
                break;
            case HIDE_CONVERSATION:
                EntityConfig configHide = json.fromJson(EntityConfig.class, value);
                if( configHide.getEntityID().equalsIgnoreCase(conversationUI.getCurrentEntityID())) {
                    conversationUI.setVisible(false);
                }
                break;
            case QUEST_LOCATION_DISCOVERED:
                String[] string = value.split(Component.MESSAGE_TOKEN);
                String questID = string[0];
                String questTaskID = string[1];


                questUI.questTaskComplete(questID, questTaskID);
                updateEntityObservers();
                break;
            case ENEMY_SPAWN_LOCATION_CHANGED:
                String enemyZoneID = value;
                battleUI.battleZoneTriggered(Integer.parseInt(enemyZoneID));
                break;
            case PLAYER_HAS_MOVED:
                if( battleUI.isBattleReady() ){
                    addTransitionToScreen();
                    MainGameScreen.setGameState(MainGameScreen.GameState.SAVING);
                    mapMgr.disableCurrentmapMusic();
                    notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
                    battleUI.toBack();
                    battleUI.setVisible(true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(ConversationGraph graph, ConversationCommandEvent event) {
        switch(event) {
            case LOAD_STORE_INVENTORY:
                Entity selectedEntity = mapMgr.getCurrentSelectedMapEntity();
                if( selectedEntity == null ){
                    break;
                }

                Array<InventoryItemLocation> inventory =  InventoryUI.getInventory(inventoryUI.getInventorySlotTable());
                storeInventoryUI.loadPlayerInventory(inventory);

                Array<InventoryItem.ItemTypeID> items  = selectedEntity.getEntityConfig().getInventory();
                Array<InventoryItemLocation> itemLocations = new Array<InventoryItemLocation>();
                for( int i = 0; i < items.size; i++){
                    itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.STORE_INVENTORY));
                }

                storeInventoryUI.loadStoreInventory(itemLocations);

                conversationUI.setVisible(false);
                storeInventoryUI.toFront();
                storeInventoryUI.setVisible(true);
                break;
            case EXIT_CONVERSATION:
                conversationUI.setVisible(false);
                mapMgr.clearCurrentSelectedMapEntity();
                break;
            case ACCEPT_QUEST:
                Entity currentlySelectedEntity = mapMgr.getCurrentSelectedMapEntity();
                if( currentlySelectedEntity == null ){
                    break;
                }
                EntityConfig config = currentlySelectedEntity.getEntityConfig();

                QuestGraph questGraph = questUI.loadQuest(config.getQuestConfigPath());

                if( questGraph != null ){
                    //Update conversation dialog
                    config.setConversationConfigPath(QuestUI.RETURN_QUEST);
                    config.setCurrentQuestID(questGraph.getQuestID());
                    ProfileManager.getInstance().setProperty(config.getEntityID(), config);
                    updateEntityObservers();
                }

                conversationUI.setVisible(false);
                mapMgr.clearCurrentSelectedMapEntity();
                break;
            case RETURN_QUEST:
                Entity returnEntity = mapMgr.getCurrentSelectedMapEntity();
                if( returnEntity == null ){
                    break;
                }
                EntityConfig configReturn = returnEntity.getEntityConfig();

                EntityConfig configReturnProperty = ProfileManager.getInstance().getProperty(configReturn.getEntityID(), EntityConfig.class);
                if( configReturnProperty == null ) return;

                String questID = configReturnProperty.getCurrentQuestID();

                if( questUI.isQuestReadyForReturn(questID) ){
                    notify(AudioObserver.AudioCommand.MUSIC_PLAY_ONCE, AudioObserver.AudioTypeEvent.MUSIC_LEVEL_UP_FANFARE);
                    QuestGraph quest = questUI.getQuestByID(questID);
                    statusUI.addXPValue(quest.getXpReward());
                    statusUI.addGoldValue(quest.getGoldReward());
                    notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_COIN_RUSTLE);
                    inventoryUI.removeQuestItemFromInventory(questID);
                    configReturnProperty.setConversationConfigPath(QuestUI.FINISHED_QUEST);
                    ProfileManager.getInstance().setProperty(configReturnProperty.getEntityID(), configReturnProperty);
                }

                conversationUI.setVisible(false);
                mapMgr.clearCurrentSelectedMapEntity();

                break;
            case ADD_ENTITY_TO_INVENTORY:
                Entity entity = mapMgr.getCurrentSelectedMapEntity();
                if( entity == null ){
                    break;
                }

                if( inventoryUI.doesInventoryHaveSpace() ){
                    inventoryUI.addEntityToInventory(entity, entity.getEntityConfig().getCurrentQuestID());
                    mapMgr.clearCurrentSelectedMapEntity();
                    conversationUI.setVisible(false);
                    entity.unregisterObservers();
                    mapMgr.removeMapQuestEntity(entity);
                    questUI.updateQuests(mapMgr);
                }else{
                    mapMgr.clearCurrentSelectedMapEntity();
                    conversationUI.setVisible(false);
                    messageBoxUI.setVisible(true);
                }

                break;
            case NONE:
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(String value, StoreInventoryEvent event) {
        switch (event) {
            case PLAYER_GP_TOTAL_UPDATED:
                int val = Integer.valueOf(value);
                statusUI.setGoldValue(val);
                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_COIN_RUSTLE);
                break;
            case PLAYER_INVENTORY_UPDATED:
                Array<InventoryItemLocation> items = json.fromJson(Array.class, value);
                InventoryUI.populateInventory(inventoryUI.getInventorySlotTable(), items, inventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(int value, StatusEvent event) {
        switch(event) {
            case UPDATED_GP:
                storeInventoryUI.setPlayerGP(value);
                ProfileManager.getInstance().setProperty("currentPlayerGP", statusUI.getGoldValue());
                break;
            case UPDATED_HP:
                ProfileManager.getInstance().setProperty("currentPlayerHP", statusUI.getHPValue());
                break;
            case UPDATED_LEVEL:
                ProfileManager.getInstance().setProperty("currentPlayerLevel", statusUI.getLevelValue());
                break;
            case UPDATED_MP:
                ProfileManager.getInstance().setProperty("currentPlayerMP", statusUI.getMPValue());
                break;
            case UPDATED_XP:
                ProfileManager.getInstance().setProperty("currentPlayerXP", statusUI.getXPValue());
                break;
            case LEVELED_UP:
                notify(AudioObserver.AudioCommand.MUSIC_PLAY_ONCE, AudioObserver.AudioTypeEvent.MUSIC_LEVEL_UP_FANFARE);
                break;
            default:
                break;
        }
    }

    @Override
    public void show() {
        shakeCam.reset();
    }

    @Override
    public void render(float delta) {
        if( shakeCam.isCameraShaking() ){
            Vector2 shakeCoords = shakeCam.getNewShakePosition();
            camera.position.x = shakeCoords.x + stage.getWidth()/2;
            camera.position.y = shakeCoords.y + stage.getHeight()/2;
        }
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        battleUI.resetDefaults();
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void onNotify(Entity enemyEntity, BattleEvent event) {
        switch (event) {
            case OPPONENT_HIT_DAMAGE:
                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_CREATURE_PAIN);
                break;
            case OPPONENT_DEFEATED:
                MainGameScreen.setGameState(MainGameScreen.GameState.RUNNING);
                int goldReward = Integer.parseInt(enemyEntity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_GP_REWARD.toString()));
                statusUI.addGoldValue(goldReward);
                int xpReward = Integer.parseInt(enemyEntity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_XP_REWARD.toString()));
                statusUI.addXPValue(xpReward);
                notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
                mapMgr.enableCurrentmapMusic();
                addTransitionToScreen();
                battleUI.setVisible(false);
                break;
            case PLAYER_RUNNING:
                MainGameScreen.setGameState(MainGameScreen.GameState.RUNNING);
                notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
                mapMgr.enableCurrentmapMusic();
                addTransitionToScreen();
                battleUI.setVisible(false);
                break;
            case PLAYER_HIT_DAMAGE:
                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_PLAYER_PAIN);
                int hpVal = ProfileManager.getInstance().getProperty("currentPlayerHP", Integer.class);
                statusUI.setHPValue(hpVal);
                shakeCam.startShaking();

                if( hpVal <= 0 ){
                    shakeCam.reset();
                    notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
                    addTransitionToScreen();
                    battleUI.setVisible(false);
                    MainGameScreen.setGameState(MainGameScreen.GameState.GAME_OVER);
                }
                break;
            case PLAYER_USED_MAGIC:
                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_PLAYER_WAND_ATTACK);
                int mpVal = ProfileManager.getInstance().getProperty("currentPlayerMP", Integer.class);
                statusUI.setMPValue(mpVal);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(String value, InventoryEvent event) {
        switch(event){
            case ITEM_CONSUMED:
                String[] strings = value.split(Component.MESSAGE_TOKEN);
                if( strings.length != 2) return;

                int type = Integer.parseInt(strings[0]);
                int typeValue = Integer.parseInt(strings[1]);

                if( InventoryItem.doesRestoreHP(type) ){
                    notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_EATING);
                    statusUI.addHPValue(typeValue);
                }else if( InventoryItem.doesRestoreMP(type) ){
                    notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_DRINKING);
                    statusUI.addMPValue(typeValue);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void addObserver(AudioObserver audioObserver) {
        observers.add(audioObserver);
    }

    @Override
    public void removeObserver(AudioObserver audioObserver) {
        observers.removeValue(audioObserver, true);
    }

    @Override
    public void removeAllObservers() {
        observers.removeAll(observers, true);
    }

    @Override
    public void notify(AudioObserver.AudioCommand command, AudioObserver.AudioTypeEvent event) {
        for(AudioObserver observer: observers){
            observer.onNotify(command, event);
        }
    }




}
