package com.packtpub.libgdx.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.packtpub.libgdx.bludbourne.audio.AudioManager;
import com.packtpub.libgdx.bludbourne.audio.AudioObserver;
import com.packtpub.libgdx.bludbourne.audio.AudioSubject;
import com.packtpub.libgdx.bludbourne.sfx.ParticleEffectFactory;

import java.util.Hashtable;

public abstract class Map implements AudioSubject{
    private static final String TAG = Map.class.getSimpleName();

    public final static float UNIT_SCALE  = 1/16f;

    private Array<AudioObserver> observers;

    //Map layers
    protected final static String COLLISION_LAYER = "MAP_COLLISION_LAYER";
    protected final static String SPAWNS_LAYER = "MAP_SPAWNS_LAYER";
    protected final static String PORTAL_LAYER = "MAP_PORTAL_LAYER";
    protected final static String QUEST_ITEM_SPAWN_LAYER = "MAP_QUEST_ITEM_SPAWN_LAYER";
    protected final static String QUEST_DISCOVER_LAYER = "MAP_QUEST_DISCOVER_LAYER";
    protected final static String ENEMY_SPAWN_LAYER = "MAP_ENEMY_SPAWN_LAYER";
    protected final static String PARTICLE_EFFECT_SPAWN_LAYER = "PARTICLE_EFFECT_SPAWN_LAYER";

    public final static String BACKGROUND_LAYER = "Background_Layer";
    public final static String GROUND_LAYER = "Ground_Layer";
    public final static String DECORATION_LAYER = "Decoration_Layer";

    public final static String LIGHTMAP_DAWN_LAYER = "MAP_LIGHTMAP_LAYER_DAWN";
    public final static String LIGHTMAP_AFTERNOON_LAYER = "MAP_LIGHTMAP_LAYER_AFTERNOON";
    public final static String LIGHTMAP_DUSK_LAYER = "MAP_LIGHTMAP_LAYER_DUSK";
    public final static String LIGHTMAP_NIGHT_LAYER = "MAP_LIGHTMAP_LAYER_NIGHT";

    //Starting locations
    protected final static String PLAYER_START = "PLAYER_START";
    protected final static String NPC_START = "NPC_START";

    protected Json json;

    protected Vector2 playerStartPositionRect;
    protected Vector2 closestPlayerStartPosition;
    protected Vector2 convertedUnits;
    protected TiledMap currentMap;
    protected Vector2 playerStart;
    protected Array<Vector2> npcStartPositions;
    protected Hashtable<String, Vector2> specialNPCStartPositions;

    protected MapLayer collisionLayer;
    protected MapLayer portalLayer;
    protected MapLayer spawnsLayer;
    protected MapLayer questItemSpawnLayer;
    protected MapLayer questDiscoverLayer;
    protected MapLayer enemySpawnLayer;
    protected MapLayer particleEffectSpawnLayer;

    protected MapLayer lightMapDawnLayer;
    protected MapLayer lightMapAfternoonLayer;
    protected MapLayer lightMapDuskLayer;
    protected MapLayer lightMapNightLayer;

    protected MapFactory.MapType currentMapType;
    protected Array<Entity> mapEntities;
    protected Array<Entity> mapQuestEntities;
    protected Array<ParticleEffect> mapParticleEffects;

    Map( MapFactory.MapType mapType, String fullMapPath){
        json = new Json();
        mapEntities = new Array<Entity>(10);
        observers = new Array<AudioObserver>();
        mapQuestEntities = new Array<Entity>();
        mapParticleEffects = new Array<ParticleEffect>();
        currentMapType = mapType;
        playerStart = new Vector2(0,0);
        playerStartPositionRect = new Vector2(0,0);
        closestPlayerStartPosition = new Vector2(0,0);
        convertedUnits = new Vector2(0,0);

        if( fullMapPath == null || fullMapPath.isEmpty() ) {
            Gdx.app.debug(TAG, "Map is invalid");
            return;
        }

        Utility.loadMapAsset(fullMapPath);
        if( Utility.isAssetLoaded(fullMapPath) ) {
            currentMap = Utility.getMapAsset(fullMapPath);
        }else{
            Gdx.app.debug(TAG, "Map not loaded");
            return;
        }

        collisionLayer = currentMap.getLayers().get(COLLISION_LAYER);
        if( collisionLayer == null ){
            Gdx.app.debug(TAG, "No collision layer!");
        }

        portalLayer = currentMap.getLayers().get(PORTAL_LAYER);
        if( portalLayer == null ){
            Gdx.app.debug(TAG, "No portal layer!");
        }

        spawnsLayer = currentMap.getLayers().get(SPAWNS_LAYER);
        if( spawnsLayer == null ){
            Gdx.app.debug(TAG, "No spawn layer!");
        }else{
            setClosestStartPosition(playerStart);
        }

        questItemSpawnLayer = currentMap.getLayers().get(QUEST_ITEM_SPAWN_LAYER);
        if( questItemSpawnLayer == null ){
            Gdx.app.debug(TAG, "No quest item spawn layer!");
        }

        questDiscoverLayer = currentMap.getLayers().get(QUEST_DISCOVER_LAYER);
        if( questDiscoverLayer == null ){
            Gdx.app.debug(TAG, "No quest discover layer!");
        }

        enemySpawnLayer = currentMap.getLayers().get(ENEMY_SPAWN_LAYER);
        if( enemySpawnLayer == null ){
            Gdx.app.debug(TAG, "No enemy layer found!");
        }

        lightMapDawnLayer = currentMap.getLayers().get(LIGHTMAP_DAWN_LAYER);
        if( lightMapDawnLayer == null ){
            Gdx.app.debug(TAG, "No dawn lightmap layer found!");
        }

        lightMapAfternoonLayer = currentMap.getLayers().get(LIGHTMAP_AFTERNOON_LAYER);
        if( lightMapAfternoonLayer == null ){
            Gdx.app.debug(TAG, "No afternoon lightmap layer found!");
        }


        lightMapDuskLayer = currentMap.getLayers().get(LIGHTMAP_DUSK_LAYER);
        if( lightMapDuskLayer == null ){
            Gdx.app.debug(TAG, "No dusk lightmap layer found!");
        }

        lightMapNightLayer = currentMap.getLayers().get(LIGHTMAP_NIGHT_LAYER);
        if( lightMapNightLayer == null ){
            Gdx.app.debug(TAG, "No night lightmap layer found!");
        }

        particleEffectSpawnLayer = currentMap.getLayers().get(PARTICLE_EFFECT_SPAWN_LAYER);
        if( particleEffectSpawnLayer == null ){
            Gdx.app.debug(TAG, "No particle effect spawn layer!");
        }

        npcStartPositions = getNPCStartPositions();
        specialNPCStartPositions = getSpecialNPCStartPositions();

        //Observers
        this.addObserver(AudioManager.getInstance());
    }

    public MapLayer getLightMapDawnLayer(){
        return lightMapDawnLayer;
    }

    public MapLayer getLightMapAfternoonLayer(){
        return lightMapAfternoonLayer;
    }

    public MapLayer getLightMapDuskLayer(){
        return lightMapDuskLayer;
    }

    public MapLayer getLightMapNightLayer(){
        return lightMapNightLayer;
    }

    public Array<Vector2> getParticleEffectSpawnPositions(ParticleEffectFactory.ParticleEffectType particleEffectType) {
        Array<MapObject> objects = new Array<MapObject>();
        Array<Vector2> positions = new Array<Vector2>();

        for( MapObject object: particleEffectSpawnLayer.getObjects()){
            String name = object.getName();

            if(     name == null || name.isEmpty() ||
                    !name.equalsIgnoreCase(particleEffectType.toString())){
                continue;
            }

            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            //Get center of rectangle
            float x = rect.getX() + (rect.getWidth()/2);
            float y = rect.getY() + (rect.getHeight()/2);

            //scale by the unit to convert from map coordinates
            x *= UNIT_SCALE;
            y *= UNIT_SCALE;

            positions.add(new Vector2(x,y));
        }
        return positions;
    }

    public Array<Vector2> getQuestItemSpawnPositions(String objectName, String objectTaskID) {
        Array<MapObject> objects = new Array<MapObject>();
        Array<Vector2> positions = new Array<Vector2>();

        for( MapObject object: questItemSpawnLayer.getObjects()){
            String name = object.getName();
            String taskID = (String)object.getProperties().get("taskID");

            if(        name == null || taskID == null ||
                       name.isEmpty() || taskID.isEmpty() ||
                       !name.equalsIgnoreCase(objectName) ||
                       !taskID.equalsIgnoreCase(objectTaskID)){
                continue;
            }
            //Get center of rectangle
            float x = ((RectangleMapObject)object).getRectangle().getX();
            float y = ((RectangleMapObject)object).getRectangle().getY();

            //scale by the unit to convert from map coordinates
            x *= UNIT_SCALE;
            y *= UNIT_SCALE;

            positions.add(new Vector2(x,y));
        }
        return positions;
    }

    public Array<Entity> getMapEntities(){
        return mapEntities;
    }

    public Array<Entity> getMapQuestEntities(){
        return mapQuestEntities;
    }

    public Array<ParticleEffect> getMapParticleEffects(){
        return mapParticleEffects;
    }

    public void addMapQuestEntities(Array<Entity> entities){
        mapQuestEntities.addAll(entities);
    }

    public MapFactory.MapType getCurrentMapType(){
        return currentMapType;
    }

    public Vector2 getPlayerStart() {
        return playerStart;
    }

    public void setPlayerStart(Vector2 playerStart) {
        this.playerStart = playerStart;
    }

    protected void updateMapEntities(MapManager mapMgr, Batch batch, float delta){
        for( int i=0; i < mapEntities.size; i++){
            mapEntities.get(i).update(mapMgr, batch, delta);
        }
        for( int i=0; i < mapQuestEntities.size; i++){
            mapQuestEntities.get(i).update(mapMgr, batch, delta);
        }
    }

    protected void updateMapEffects(MapManager mapMgr, Batch batch, float delta){
        for( int i=0; i < mapParticleEffects.size; i++){
            batch.begin();
            mapParticleEffects.get(i).draw(batch, delta);
            batch.end();
        }
    }

    protected void dispose(){
        for( int i=0; i < mapEntities.size; i++){
            mapEntities.get(i).dispose();
        }
        for( int i=0; i < mapQuestEntities.size; i++){
            mapQuestEntities.get(i).dispose();
        }
        for( int i=0; i < mapParticleEffects.size; i++){
            mapParticleEffects.get(i).dispose();
        }
    }

    public MapLayer getCollisionLayer(){
        return collisionLayer;
    }

    public MapLayer getPortalLayer(){
        return portalLayer;
    }

    public MapLayer getQuestItemSpawnLayer(){
        return questItemSpawnLayer;
    }

    public MapLayer getQuestDiscoverLayer(){
        return questDiscoverLayer;
    }

    public MapLayer getEnemySpawnLayer() {
        return enemySpawnLayer;
    }

    public TiledMap getCurrentTiledMap() {
        return currentMap;
    }

    public Vector2 getPlayerStartUnitScaled(){
        Vector2 playerStart = this.playerStart.cpy();
        playerStart.set(playerStart.x * UNIT_SCALE, playerStart.y * UNIT_SCALE);
        return playerStart;
    }

    private Array<Vector2> getNPCStartPositions(){
        Array<Vector2> npcStartPositions = new Array<Vector2>();

        for( MapObject object: spawnsLayer.getObjects()){
            String objectName = object.getName();

            if( objectName == null || objectName.isEmpty() ){
                continue;
            }

            if( objectName.equalsIgnoreCase(NPC_START) ){
                //Get center of rectangle
                float x = ((RectangleMapObject)object).getRectangle().getX();
                float y = ((RectangleMapObject)object).getRectangle().getY();

                //scale by the unit to convert from map coordinates
                x *= UNIT_SCALE;
                y *= UNIT_SCALE;

                npcStartPositions.add(new Vector2(x,y));
            }
        }
        return npcStartPositions;
    }

    private Hashtable<String, Vector2> getSpecialNPCStartPositions(){
        Hashtable<String, Vector2> specialNPCStartPositions = new Hashtable<String, Vector2>();

        for( MapObject object: spawnsLayer.getObjects()){
            String objectName = object.getName();

            if( objectName == null || objectName.isEmpty() ){
                continue;
            }

            //This is meant for all the special spawn locations, a catch all, so ignore known ones
            if(     objectName.equalsIgnoreCase(NPC_START) ||
                    objectName.equalsIgnoreCase(PLAYER_START) ){
                continue;
            }

            //Get center of rectangle
            float x = ((RectangleMapObject)object).getRectangle().getX();
            float y = ((RectangleMapObject)object).getRectangle().getY();

            //scale by the unit to convert from map coordinates
            x *= UNIT_SCALE;
            y *= UNIT_SCALE;

            specialNPCStartPositions.put(objectName, new Vector2(x,y));
        }
        return specialNPCStartPositions;
    }

    private void setClosestStartPosition(final Vector2 position){
         Gdx.app.debug(TAG, "setClosestStartPosition INPUT: (" + position.x + "," + position.y + ") " + currentMapType.toString());

        //Get last known position on this map
        playerStartPositionRect.set(0,0);
        closestPlayerStartPosition.set(0,0);
        float shortestDistance = 0f;

        //Go through all player start positions and choose closest to last known position
        for( MapObject object: spawnsLayer.getObjects()){
            String objectName = object.getName();

            if( objectName == null || objectName.isEmpty() ){
                continue;
            }

            if( objectName.equalsIgnoreCase(PLAYER_START) ){
                ((RectangleMapObject)object).getRectangle().getPosition(playerStartPositionRect);
                float distance = position.dst2(playerStartPositionRect);

                Gdx.app.debug(TAG, "DISTANCE: " + distance + " for " + currentMapType.toString());

                if( distance < shortestDistance || shortestDistance == 0 ){
                    closestPlayerStartPosition.set(playerStartPositionRect);
                    shortestDistance = distance;
                    Gdx.app.debug(TAG, "closest START is: (" + closestPlayerStartPosition.x + "," + closestPlayerStartPosition.y + ") " +  currentMapType.toString());
                }
            }
        }
        playerStart =  closestPlayerStartPosition.cpy();
    }

    public void setClosestStartPositionFromScaledUnits(Vector2 position){
        if( UNIT_SCALE <= 0 )
            return;

        convertedUnits.set(position.x/UNIT_SCALE, position.y/UNIT_SCALE);
        setClosestStartPosition(convertedUnits);
    }

    abstract public void unloadMusic();
    abstract public void loadMusic();

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
