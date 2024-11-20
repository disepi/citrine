package com.disepi.citrine.entity.hub;

import cn.nukkit.Player;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.level.Level;
import cn.nukkit.potion.Effect;
import com.disepi.citrine.data.GameData;
import com.disepi.citrine.utils.NPC;

public class FrozenBoomboxNPC extends BoomboxNPC{

    public static CustomEntityDefinition def = NPC.getDefinition("hivecommon:frozen_boombox_ent");

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }

    public void handleHit(Player p) {
        Effect poison = Effect.getEffect(Effect.SLOWNESS);
        poison.setDuration(10 * 20);
        poison.setAmbient(true);
        poison.setAmplifier(3);
        poison.setVisible(false);
        poison.setColor(0,0,255);
        p.addEffect(poison);
    }

    public FrozenBoomboxNPC(Level lvl, float x, float y, float z, int explodeTicks, float baseDamage, float kbScalar, GameData data, Player placer) {
        super(lvl, x, y, z, explodeTicks, baseDamage, kbScalar, data, placer);
        this.doDamage = false;
        this.doKnockback = false;
    }
}
