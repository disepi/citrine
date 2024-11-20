package com.disepi.citrine.utils;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

public class NPC extends Entity implements CustomEntity {

    public static CustomEntityDefinition def = NPC.getDefinition("");;

    public static CustomEntityDefinition getDefinition(String identifier) {
        CustomEntityDefinition _def = CustomEntityDefinition.builder().identifier(identifier)
                .summonable(true)
                .spawnEgg(false)
                .build();
        return _def;
    }

    public NPC(Level lvl, float x, float y, float z) {
        super(lvl.getChunk(((int)x) >> 4, ((int)z) >> 4), Entity.getDefaultNBT(new Vector3(x, y, z)));
    }

    public void setAngles(float pitch, float yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void handleInput() {

    }

    public void doGravity() {
        // bad fix but it does the job
        if(!this.level.getBlock(new Vector3(this.x, this.y - 0.5f, this.z).floor()).isSolid()) {
            this.y -= 0.5f;
        }
    }

    @Override
    public int getNetworkId() {
        return getDefinition().getRuntimeId();
    }

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }
}