package com.disepi.citrine.entity.hub;

import cn.nukkit.Player;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.utils.NPC;

public class SkyNPC extends NPC {
    public static CustomEntityDefinition def = NPC.getDefinition("hivehub:game_sky");

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }

    public SkyNPC(Level lvl, float x, float y, float z) {
        super(lvl, x, y, z);
        this.setNameTagAlwaysVisible(true);
        this.setNameTag(TextFormat.BOLD + "" + TextFormat.AQUA + "Sky" + TextFormat.YELLOW + "Wars");
    }

    void handleClick(Player p) {
        Citrine.findGameForPlayer(p);
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        handleClick(player);
        return false;
    }
    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }
    @Override
    public float getWidth() {
        return 0.8f;
    }

    @Override
    public float getHeight() {
        return 2.25f;
    }
}
