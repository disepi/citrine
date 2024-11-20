package com.disepi.citrine.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.ItemCompass;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.ItemCustomEdible;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.item.customitem.data.Offset;
import cn.nukkit.item.customitem.data.RenderOffsets;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.utils.SoundUtil;

public class GameCompassItem extends ItemCustom {

    public GameCompassItem() {
        super("hive:game_compass", TextFormat.AQUA + "Game Selector " + TextFormat.GRAY + "[Use]", "hivehub:compass");
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
        FormWindowSimple window = new FormWindowSimple(TextFormat.BOLD + "" + TextFormat.DARK_RED + "Teleporter", TextFormat.GRAY + "Where do you want to go?");

        // button
        ElementButton skywars = new ElementButton(TextFormat.BOLD + "" + TextFormat.AQUA + "Sky" + TextFormat.YELLOW + "Wars\n" + TextFormat.RESET + "" + TextFormat.DARK_GRAY + "Click to Play");
        window.addButton(skywars);

        // handler
        window.addHandler((player1, i) -> {
            FormResponseSimple response = window.getResponse();
            if(response == null) return;
            int clicked = response.getClickedButtonId();

            // sky wars
            if(clicked == 0) Citrine.findGameForPlayer(player1);
        });

        player.showFormWindow(window);
        return false;
    }
}
