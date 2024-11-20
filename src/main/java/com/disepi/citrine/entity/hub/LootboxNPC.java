package com.disepi.citrine.entity.hub;

import cn.nukkit.Player;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.utils.GameItemHandlerUtil;
import com.disepi.citrine.utils.NPC;

import java.util.Collection;

public class LootboxNPC extends NPC {
    public static CustomEntityDefinition def = NPC.getDefinition("hivesky:death_crate");
    public Collection<Item> items;
    public boolean didAttack = false;

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }

    public LootboxNPC(Level lvl, float x, float y, float z) {
        super(lvl, x, y, z);
    }


    @Override
    public boolean attack(EntityDamageEvent source) {
        if(!didAttack) {
            for (Item item : items)
                this.level.dropItem(this.getPosition(), item);
            this.items.clear();

            level.addSound(this.getPosition(), Sound.BLOCK_SCAFFOLDING_PLACE, 1, 1.5f);
            level.addParticleEffect(this.getPosition(), ParticleEffect.EXPLOSION_DEATH);
            didAttack = true;
        }

        this.kill();
        this.despawnFromAll();
        return true;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        doGravity();
        return super.entityBaseTick(tickDiff);
    }

    @Override
    public float getWidth() {
        return 0.8f;
    }

    @Override
    public float getHeight() {
        return 0.6f;
    }
}
