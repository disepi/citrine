package com.disepi.citrine.entity.hub;

import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Level;
import com.disepi.citrine.utils.NPC;

public class HivePlusNPC extends NPC {
    public static CustomEntityDefinition def = NPC.getDefinition("hivehub:npc_plus");

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }

    public HivePlusNPC(Level lvl, float x, float y, float z) {
        super(lvl, x, y, z);
    }

    @Override
    public boolean onUpdate(int tick) {
        this.headYaw += 2;
        this.yaw += 2;
        this.headYaw = this.headYaw%360;
        this.yaw = this.yaw%360;
        this.level.addEntityMovement(this, this.x, this.y, this.z, this.yaw, this.pitch, this.headYaw);
        return false;
    }
    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }
    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.f;
    }
}
