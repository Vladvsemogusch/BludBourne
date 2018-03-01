package com.packtpub.libgdx.bludbourne;

import java.util.Hashtable;

public class MapFactory {
    //All maps for the game
    private static Hashtable<MapType,Map> mapTable = new Hashtable<MapType, Map>();

    public static enum MapType{
        TOP_WORLD,
        TOWN,
        CASTLE_OF_DOOM
    }

    static public Map getMap(MapType mapType){
        Map map = null;
        switch(mapType){
            case TOP_WORLD:
                map = mapTable.get(MapType.TOP_WORLD);
                if( map == null ){
                    map = new TopWorldMap();
                    mapTable.put(MapType.TOP_WORLD, map);
                }
                break;
            case TOWN:
                map = mapTable.get(MapType.TOWN);
                if( map == null ){
                    map = new TownMap();
                    mapTable.put(MapType.TOWN, map);
                }
                break;
            case CASTLE_OF_DOOM:
                map = mapTable.get(MapType.CASTLE_OF_DOOM);
                if( map == null ){
                    map = new CastleDoomMap();
                    mapTable.put(MapType.CASTLE_OF_DOOM, map);
                }
                break;
            default:
                break;
        }
        return map;
    }

    public static void clearCache(){
        for( Map map: mapTable.values()){
            map.dispose();
        }
        mapTable.clear();
    }
}
