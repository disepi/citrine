package com.disepi.citrine.entity.hub;

import cn.nukkit.Player;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import com.disepi.citrine.utils.NPC;

public class SoonNPC extends NPC {
    public static CustomEntityDefinition def = NPC.getDefinition("hivehub:game_soon");

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }

    public SoonNPC(Level lvl, float x, float y, float z) {
        super(lvl, x, y, z);
        this.setNameTagAlwaysVisible(true);
        this.setNameTag("Coming Soon...");
    }

    void handleClick(Player p) {
        FormWindowSimple window = new FormWindowSimple("Unavailable", "This gamemode is not available.");
        window.addButton(new ElementButton("Okay"));
        p.showFormWindow(window);
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
        return 2.f;
    }
}
