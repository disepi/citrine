package com.disepi.citrine.items;

import cn.nukkit.Player;
import cn.nukkit.item.ItemTotem;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.utils.GameItemHandlerUtil;

public class GameCosmeticPickerItem extends ItemCustom {

    public GameCosmeticPickerItem() {
        super("hive:cosmetic_picker", TextFormat.GREEN + "Your Locker " + TextFormat.GRAY + "[Use]", "totem");
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .simpleBuilder(this, ItemCreativeCategory.NATURE)
                .allowOffHand(false)
                .build();
    }

    public boolean onClickAir(Player player, Vector3 directionVector) {
        GameItemHandlerUtil.handleUseCosmeticPicker(player);
        return false;
    }
}
