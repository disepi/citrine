package com.disepi.citrine.items;

import cn.nukkit.Player;
import cn.nukkit.item.ItemDragonBreath;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.utils.GameItemHandlerUtil;

public class SpellLifeItem extends ItemCustom {

    public SpellLifeItem() {
        super("hive:spell_life", TextFormat.LIGHT_PURPLE + "Spell of Life " + TextFormat.GRAY + "[Use]", "book_enchanted");
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .simpleBuilder(this, ItemCreativeCategory.NATURE)
                .allowOffHand(false)
                .foil(true)
                .build();
    }


    public boolean onClickAir(Player player, Vector3 directionVector) {
        GameItemHandlerUtil.handleUseSpellLife(player, this);
        return false;
    }
}

