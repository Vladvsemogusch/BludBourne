package com.packtpub.libgdx.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.packtpub.libgdx.bludbourne.profile.ProfileManager;
import com.packtpub.libgdx.bludbourne.profile.ProfileObserver;
import com.packtpub.libgdx.bludbourne.sfx.ClockActor;

public class MapManager implements ProfileObserver {
    private static final String TAG = MapManager.class.getSimpleName();

    private Camera camera;
    private boolean mapChanged = false;
    private Map currentMap;
    private Entity player;
    private Entity currentSelectedEntity = null;
    private MapLayer currentLightMap = null;
    private MapLayer previousLightMap = null;
    private ClockActor.TimeOfDay timeOfDay = null;
    private float currentLightMapOpacity = 0;
    private float previousLightMapOpacity = 1;
    private boolean timeOfDayChanged = false;

    public MapManager(){
    }

    @Override
    public void onNotify(ProfileManager profileManager, ProfileEvent event) {
        switch(event){
            case PROFILE_LOADED:
                String currentMapName = profileManager.getProperty("currentMapType", String.class);
                MapFactory.MapType mapType;
                if( currentMapName == null || currentMapName.isEmpty() ){
                    mapType = MapFactory.MapType.TOWN;
                }else{
                    mapType = MapFactory.MapType.valueOf(currentMapName);
                }
                loadMap(mapType);

                Vector2 topWorldMapStartPosition = profileManager.getProperty("topWorldMapStartPosition", Vector2.class);
                if( topWorldMapStartPosition != null ){
                    MapFactory.getMap(MapFactory.MapType.TOP_WORLD).setPlayerStart(topWorldMapStartPosition);
                }

                Vector2 castleOfDoomMapStartPosition = profileManager.getProperty("castleOfDoomMapStartPosition", Vector2.class);
                if( castleOfDoomMapStartPosition != null ){
                    MapFactory.getMap(MapFactory.MapType.CASTLE_OF_DOOM).setPlayerStart(castleOfDoomMapStartPosition);
                }

                Vector2 townMapStartPosition = profileManager.getProperty("townMapStartPosition", Vector2.class);
                if( townMapStartPosition != null ){
                    MapFactory.getMap(MapFactory.MapType.TOWN).setPlayerStart(townMapStartPosition);
                }

                break;
            case SAVING_PROFILE:
                if( currentMap != null ){
                    profileManager.setProperty("currentMapType", currentMap.currentMapType.toString());
                }

                profileManager.setProperty("topWorldMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOP_WORLD).getPlayerStart() );
                profileManager.setProperty("castleOfDoomMapStartPosition", MapFactory.getMap(MapFactory.MapType.CASTLE_OF_DOOM).getPlayerStart() );
                profileManager.setProperty("townMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOWN).getPlayerStart() );
                break;
            case CLEAR_CURRENT_PROFILE:
                currentMap = null;
                profileManager.setProperty("currentMapType", MapFactory.MapType.TOWN.toString());

                MapFactory.clearCache();

                profileManager.setProperty("topWorldMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOP_WORLD).getPlayerStart() );
                profileManager.setProperty("castleOfDoomMapStartPosition", MapFactory.getMap(MapFactory.MapType.CASTLE_OF_DOOM).getPlayerStart() );
                profileManager.setProperty("townMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOWN).getPlayerStart() );
                break;
            default:
                break;
        }
    }

    public void loadMap(MapFactory.MapType mapType){
        Map map = MapFactory.getMap(mapType);

        if( map == null ){
            Gdx.app.debug(TAG, "Map does not exist!  ");
            return;
        }

        if( currentMap != null ){
            currentMap.unloadMusic();
            if( previousLightMap != null ){
                previousLightMap.setOpacity(0);
                previousLightMap = null;
            }
            if( currentLightMap != null ){
                currentLightMap.setOpacity(1);
                currentLightMap = null;
            }
        }

        map.loadMusic();

        currentMap = map;
        mapChanged = true;
        clearCurrentSelectedMapEntity();
        Gdx.app.debug(TAG, "Player Start: (" + currentMap.getPlayerStart().x + "," + currentMap.getPlayerStart().y + ")");
    }

    public void unregisterCurrentMapEntityObservers(){
        if( currentMap != null ){
            Array<Entity> entities = currentMap.getMapEntities();
            for(Entity entity: entities){
                entity.unregisterObservers();
            }

            Array<Entity> questEntities = currentMap.getMapQuestEntities();
            for(Entity questEntity: questEntities){
                questEntity.unregisterObservers();
            }
        }
    }

    public void registerCurrentMapEntityObservers(ComponentObserver observer){
        if( currentMap != null ){
            Array<Entity> entities = currentMap.getMapEntities();
            for(Entity entity: entities){
                entity.registerObserver(observer);
            }

            Array<Entity> questEntities = currentMap.getMapQuestEntities();
            for(Entity questEntity: questEntities){
                questEntity.registerObserver(observer);
            }
        }
    }


    public void disableCurrentmapMusic(){
        currentMap.unloadMusic();
    }

    public void enableCurrentmapMusic(){
        currentMap.loadMusic();
    }

    public void setClosestStartPositionFromScaledUnits(Vector2 position) {
        currentMap.setClosestStartPositionFromScaledUnits(position);
    }

    public MapLayer getCollisionLayer(){
        return currentMap.getCollisionLayer();
    }

    public MapLayer getPortalLayer(){
        return currentMap.getPortalLayer();
    }

    public Array<Vector2> getQuestItemSpawnPositions(String objectName, String objectTaskID) {
        return currentMap.getQuestItemSpawnPositions(objectName, objectTaskID);
    }

    public MapLayer getQuestDiscoverLayer(){
        return currentMap.getQuestDiscoverLayer();
    }

    public MapLayer getEnemySpawnLayer(){
        return currentMap.getEnemySpawnLayer();
    }

    public MapFactory.MapType getCurrentMapType(){
        return currentMap.getCurrentMapType();
    }

    public Vector2 getPlayerStartUnitScaled() {
        return currentMap.getPlayerStartUnitScaled();
    }

    public TiledMap getCurrentTiledMap(){
        if( currentMap == null ) {
            loadMap(MapFactory.MapType.TOWN);
        }
        return currentMap.getCurrentTiledMap();
    }

    public MapLayer getPreviousLightMapLayer(){
        return previousLightMap;
    }

    public MapLayer getCurrentLightMapLayer(){
        return currentLightMap;
    }

    public void updateLightMaps(ClockActor.TimeOfDay timeOfDay){
        if( timeOfDay != timeOfDay ){
            currentLightMapOpacity = 0;
            previousLightMapOpacity = 1;
            this.timeOfDay = timeOfDay;
            timeOfDayChanged = true;
            previousLightMap = currentLightMap;

            Gdx.app.debug(TAG, "Time of Day CHANGED");
        }
        switch(timeOfDay){
            case DAWN:
                currentLightMap = currentMap.getLightMapDawnLayer();
                break;
            case AFTERNOON:
                currentLightMap = currentMap.getLightMapAfternoonLayer();
                break;
            case DUSK:
                currentLightMap = currentMap.getLightMapDuskLayer();
                break;
            case NIGHT:
                currentLightMap = currentMap.getLightMapNightLayer();
                break;
            default:
                currentLightMap = currentMap.getLightMapAfternoonLayer();
                break;
        }

            if( timeOfDayChanged ){
                if( previousLightMap != null && previousLightMapOpacity != 0 ){
                    previousLightMap.setOpacity(previousLightMapOpacity);
                    previousLightMapOpacity = MathUtils.clamp(previousLightMapOpacity -= .05, 0, 1);

                    if( previousLightMapOpacity == 0 ){
                        previousLightMap = null;
                    }
                }

                if( currentLightMap != null && currentLightMapOpacity != 1 ) {
                    currentLightMap.setOpacity(currentLightMapOpacity);
                    currentLightMapOpacity = MathUtils.clamp(currentLightMapOpacity += .01, 0, 1);
                }
            }else{
                timeOfDayChanged = false;
            }
    }

    public void updateCurrentMapEntities(MapManager mapMgr, Batch batch, float delta){
        currentMap.updateMapEntities(mapMgr, batch, delta);
    }

    public void updateCurrentMapEffects(MapManager mapMgr, Batch batch, float delta){
        currentMap.updateMapEffects(mapMgr, batch, delta);
    }

    public final Array<Entity> getCurrentMapEntities(){
        return currentMap.getMapEntities();
    }

    public final Array<Entity> getCurrentMapQuestEntities(){
        return currentMap.getMapQuestEntities();
    }

    public void addMapQuestEntities(Array<Entity> entities){
        currentMap.getMapQuestEntities().addAll(entities);
    }

    public void removeMapQuestEntity(Entity entity){
        entity.unregisterObservers();

        Array<Vector2> positions = ProfileManager.getInstance().getProperty(entity.getEntityConfig().getEntityID(), Array.class);
        if( positions == null ) return;

        for( Vector2 position : positions){
            if( position.x == entity.getCurrentPosition().x &&
                    position.y == entity.getCurrentPosition().y ){
                positions.removeValue(position, true);
                break;
            }
        }
        currentMap.getMapQuestEntities().removeValue(entity, true);
        ProfileManager.getInstance().setProperty(entity.getEntityConfig().getEntityID(), positions);
    }

    public void clearAllMapQuestEntities(){
        currentMap.getMapQuestEntities().clear();
    }

    public Entity getCurrentSelectedMapEntity(){
        return currentSelectedEntity;
    }

    public void setCurrentSelectedMapEntity(Entity currentSelectedEntity) {
        this.currentSelectedEntity = currentSelectedEntity;
    }

    public void clearCurrentSelectedMapEntity(){
        if( currentSelectedEntity == null ) return;
        currentSelectedEntity.sendMessage(Component.MESSAGE.ENTITY_DESELECTED);
        currentSelectedEntity = null;
    }

    public void setPlayer(Entity entity){
        this.player = entity;
    }

    public Entity getPlayer(){
        return this.player;
    }

    public void setCamera(Camera camera){
        this.camera = camera;
    }

    public Camera getCamera(){
        return camera;
    }

    public boolean hasMapChanged(){
        return mapChanged;
    }

    public void setMapChanged(boolean hasMapChanged){
        this.mapChanged = hasMapChanged;
    }
}
