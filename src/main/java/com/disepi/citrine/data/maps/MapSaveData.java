package com.disepi.citrine.data.maps;

import cn.nukkit.math.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class MapSaveData {
    public MapType map;
    public Vector3f mysteryChestPos;
    public Map<TeamType, Vector3f> spawns = new HashMap<>();
    public Map<Vector3f, Float> flags = new HashMap<>();
    public Vector3f[] chests;
    public Vector3f[] enderChests;


    public MapSaveData(MapType map) {
        this.map = map;
    }
}
