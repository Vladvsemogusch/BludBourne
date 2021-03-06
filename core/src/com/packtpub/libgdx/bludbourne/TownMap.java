package com.packtpub.libgdx.bludbourne;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.packtpub.libgdx.bludbourne.audio.AudioObserver;
import com.packtpub.libgdx.bludbourne.profile.ProfileManager;
import com.packtpub.libgdx.bludbourne.sfx.ParticleEffectFactory;

public class TownMap extends Map{
    private static final String TAG = PlayerPhysicsComponent.class.getSimpleName();

    private static String mapPath = "maps/town.tmx";
    private Json json;

    TownMap(){
        super(MapFactory.MapType.TOWN, mapPath);

        json = new Json();

        for( Vector2 position: npcStartPositions){
            Entity entity = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_GUARD_WALKING);
            entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position));
            mapEntities.add(entity);
        }

        //Special cases
        Entity blackSmith = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_BLACKSMITH);
        initSpecialEntityPosition(blackSmith);
        mapEntities.add(blackSmith);

        Entity mage = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_MAGE);
        initSpecialEntityPosition(mage);
        mapEntities.add(mage);

        Entity innKeeper = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_INNKEEPER);
        initSpecialEntityPosition(innKeeper);
        mapEntities.add(innKeeper);

        Entity townfolk1 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK1);
        initSpecialEntityPosition(townfolk1);
        mapEntities.add(townfolk1);

        Entity townfolk2 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK2);
        initSpecialEntityPosition(townfolk2);
        mapEntities.add(townfolk2);

        Entity townfolk3 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK3);
        initSpecialEntityPosition(townfolk3);
        mapEntities.add(townfolk3);

        Entity townfolk4 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK4);
        initSpecialEntityPosition(townfolk4);
        mapEntities.add(townfolk4);

        Entity townfolk5 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK5);
        initSpecialEntityPosition(townfolk5);
        mapEntities.add(townfolk5);

        Entity townfolk6 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK6);
        initSpecialEntityPosition(townfolk6);
        mapEntities.add(townfolk6);

        Entity townfolk7 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK7);
        initSpecialEntityPosition(townfolk7);
        mapEntities.add(townfolk7);

        Entity townfolk8 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK8);
        initSpecialEntityPosition(townfolk8);
        mapEntities.add(townfolk8);

        Entity townfolk9 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK9);
        initSpecialEntityPosition(townfolk9);
        mapEntities.add(townfolk9);

        Entity townfolk10 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK10);
        initSpecialEntityPosition(townfolk10);
        mapEntities.add(townfolk10);

        Entity townfolk11 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK11);
        initSpecialEntityPosition(townfolk11);
        mapEntities.add(townfolk11);

        Entity townfolk12 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK12);
        initSpecialEntityPosition(townfolk12);
        mapEntities.add(townfolk12);

        Entity townfolk13 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK13);
        initSpecialEntityPosition(townfolk13);
        mapEntities.add(townfolk13);

        Entity townfolk14 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK14);
        initSpecialEntityPosition(townfolk14);
        mapEntities.add(townfolk14);

        Entity townfolk15 = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_FOLK15);
        initSpecialEntityPosition(townfolk15);
        mapEntities.add(townfolk15);

        Array<Vector2> candleEffectPositions = getParticleEffectSpawnPositions(ParticleEffectFactory.ParticleEffectType.CANDLE_FIRE);
        for( Vector2 position: candleEffectPositions ){
            mapParticleEffects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.CANDLE_FIRE, position));
        }

        Array<Vector2> lanternEffectPositions = getParticleEffectSpawnPositions(ParticleEffectFactory.ParticleEffectType.LANTERN_FIRE);
        for( Vector2 position: lanternEffectPositions ){
            mapParticleEffects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.LANTERN_FIRE, position));
        }
    }

    @Override
    public void unloadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_TOWN);
    }

    @Override
    public void loadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_TOWN);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_TOWN);
    }

    private void initSpecialEntityPosition(Entity entity){
        Vector2 position = new Vector2(0,0);

        if( specialNPCStartPositions.containsKey(entity.getEntityConfig().getEntityID()) ) {
            position = specialNPCStartPositions.get(entity.getEntityConfig().getEntityID());
        }
        entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position));

        //Overwrite default if special config is found
        EntityConfig entityConfig = ProfileManager.getInstance().getProperty(entity.getEntityConfig().getEntityID(), EntityConfig.class);
        if( entityConfig != null ){
            entity.setEntityConfig(entityConfig);
        }
    }
}
