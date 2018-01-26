package ua.pp.oped.bludbourne;

import com.badlogic.gdx.utils.Json;

/**
 * Created by Vlad on 26.01.2018.
 */

public class EntityFactory {

    private static Json json = new Json();

    public static enum EntityType {
        PLAYER,
        DEMO_PLAYER,
        NPC
    }

    public static String PLAYER_CONFIG = "scripts/player.json";

    public static Entity getEntity(EntityType entityType) {
        Entity entity = null;
        switch (entityType) {
            case PLAYER:
                entity = new Entity(
                        new PlayerInputComponent(),
                        new PlayerPhysicsComponent(),
                        new PlayerGraphicsComponent()
                );
                entity.setEntityConfig(
                        Entity.getEntityConfig(EntityFactory.PLAYER_CONFIG)
                );
                entity.sendMessage(
                        Component.MESSAGE.LOAD_ANIMATIONS,
                        json.toJson(entity.getEntityConfig())
                );
                return entity;
            case DEMO_PLAYER:
                entity = new Entity(
                        new NPCImputComponent(),
                        new PlayerPhysicsComponent(),
                        new PlayerGraphicsComponent()
                );
                return entity;
            case NPC:
                entity = new Entity(
                        new NPCInputComponent(),
                        new NOCPhysicsComponent(),
                        new NPCGrahicsComponent()
                );
                return entity;
            default:
                return null;
        }


    }
}

}
