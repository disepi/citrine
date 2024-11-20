package com.disepi.citrine.items;

import cn.nukkit.Player;
import cn.nukkit.item.ItemDragonBreath;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.utils.GameItemHandlerUtil;

public class GameHubItem extends ItemCustom {

    public GameHubItem() {
        super("hive:go_hub", TextFormat.YELLOW + "Back to Hub " + TextFormat.GRAY + "[Use]", "dragon_breath");
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .simpleBuilder(this, ItemCreativeCategory.NATURE)
                .allowOffHand(false)
                .build();
    }


    public boolean onClickAir(Player player, Vector3 directionVector) {
        GameItemHandlerUtil.handleUseHubItem(player);
        return false;
    }
}

