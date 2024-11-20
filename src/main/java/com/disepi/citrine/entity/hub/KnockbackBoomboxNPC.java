package com.disepi.citrine.entity.hub;

import cn.nukkit.Player;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.level.Level;
import cn.nukkit.potion.Effect;
import com.disepi.citrine.data.GameData;
import com.disepi.citrine.utils.NPC;

public class KnockbackBoomboxNPC extends BoomboxNPC{

    public static CustomEntityDefinition def = NPC.getDefinition("hivecommon:kb_boombox_ent");

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }

    public KnockbackBoomboxNPC(Level lvl, float x, float y, float z, int explodeTicks, float baseDamage, float kbScalar, GameData data, Player placer) {
        super(lvl, x, y, z, explodeTicks, baseDamage, kbScalar, data, placer);
        this.doDamage = false;
        this.kbScalar = 4;
        this.explodeTicks = 20;
        this.placerOnly = true;
    }
}
