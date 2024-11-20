package com.disepi.citrine.entity.hub;

import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Level;
import com.disepi.citrine.utils.NPC;

public class ReplayNPC extends NPC {
    public static CustomEntityDefinition def = NPC.getDefinition("hivehub:npc_replay");

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }

    public ReplayNPC(Level lvl, float x, float y, float z) {
        super(lvl, x, y, z);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }
    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }
    @Override
    public float getHeight() {
        return 1.f;
    }
}
