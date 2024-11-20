package com.disepi.citrine.entity.hub;

import cn.nukkit.Player;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import com.disepi.citrine.utils.GameItemHandlerUtil;
import com.disepi.citrine.utils.NPC;

public class SpawnVehicleNPC extends NPC {
    public static CustomEntityDefinition def = NPC.getDefinition("hivesky:spawn_vehicle");

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }

    public SpawnVehicleNPC(Level lvl, float x, float y, float z) {
        super(lvl, x, y, z);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public float getWidth() {
        return 0.f;
    }

    @Override
    public float getHeight() {
        return 0.f;
    }
}
