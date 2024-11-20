package com.disepi.citrine.entity.hub;

import cn.nukkit.Player;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.utils.GameItemHandlerUtil;
import com.disepi.citrine.utils.NPC;

public class GoHubNPC extends NPC {
    public static CustomEntityDefinition def = NPC.getDefinition("hivehub:custom_hub");

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }

    public GoHubNPC(Level lvl, float x, float y, float z) {
        super(lvl, x, y, z);
        this.setNameTagAlwaysVisible(true);
        this.setNameTag(TextFormat.YELLOW + "Back to Hub");
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        GameItemHandlerUtil.handleUseHubItem(player);
        return false;
    }
    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }
    @Override
    public float getWidth() {
        return 1.f;
    }

    @Override
    public float getHeight() {
        return 1.f;
    }
}
