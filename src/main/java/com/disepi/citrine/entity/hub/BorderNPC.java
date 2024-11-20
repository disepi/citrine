package com.disepi.citrine.entity.hub;

import cn.nukkit.Player;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import com.disepi.citrine.utils.GameItemHandlerUtil;
import com.disepi.citrine.utils.NPC;

public class BorderNPC extends NPC {
    public static CustomEntityDefinition def = NPC.getDefinition("hivesky:world_border");

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }

    public BorderNPC(Level lvl, float x, float y, float z) {
        super(lvl, x, y, z);
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        return false;
    }

    @Override
    protected void checkChunks() {

    }

    @Override
    public void spawnToAll() {

    }
    @Override
    public void spawnTo(Player player) {

    }

    public void _spawnTo(Player player) {
        player.dataPacket(createAddEntityPacket());
    }

    public void _despawnFrom(Player player) {
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = this.getId();
        player.dataPacket(pk);
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
