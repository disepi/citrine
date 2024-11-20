package com.disepi.citrine.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.ItemEmerald;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.utils.CustElementButton;
import com.disepi.citrine.utils.SoundUtil;

public class GameBoosterPropItem extends ItemCustom {

    public GameBoosterPropItem() {
        super("hive:booster_item", TextFormat.GREEN + "Boosters " + TextFormat.GRAY + "[Use]", "emerald");
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .simpleBuilder(this, ItemCreativeCategory.NATURE)
                .allowOffHand(false)
                .build();
    }

    public boolean onClickAir(Player player, Vector3 directionVector) {
        // sound
        player.dataPacket(SoundUtil.getSoundPacket(Sound.RANDOM_POP, 1, 0.5f, player));

        // form
        FormWindowSimple window = new FormWindowSimple("XP Boosters", "Boosters temporarily increase the XP you earn in a game. Use them to gain access to unlocks quicker!\nYou don't have any boosters! You can buy boosters from our store.");

        // button
        ElementButton buy = new ElementButton("Buy Boosters");
        window.addButton(buy);

        // handler
        window.addHandler((player1, i) -> {
            FormResponseSimple response = window.getResponse();
            if(response == null) return;
            int clicked = response.getClickedButtonId();

            // buy boosters
            if(clicked == 0) {
                FormWindowSimple buyWindow = new FormWindowSimple("XP Boosters", "The following boosters are currently available on our store.");

                ElementButton opt1 = new ElementButton("All Games\n50 Percent 1 Hour");
                ElementButton opt2 = new ElementButton("FREE (1X) All Games\n50 Percent 1 Hour");
                buyWindow.addButton(opt1);
                buyWindow.addButton(opt2);
                player1.showFormWindow(buyWindow);
            }
        });

        player.showFormWindow(window);
        return false;
    }
}
