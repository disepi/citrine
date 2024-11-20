package com.disepi.citrine.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.ItemPaper;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.maps.MapType;
import com.disepi.citrine.utils.SoundUtil;

public class GameMapSelectItem extends ItemCustom {

    public GameMapSelectItem() {
        super("hive:select_map",TextFormat.YELLOW + "Vote for Map " + TextFormat.GRAY + "[Use]", "paper");
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
        FormWindowSimple window = new FormWindowSimple("Vote for Map", "");

        // buttons
        for(MapType map : MapType.values()) {
            ElementButton mapButton = new ElementButton(map.name);
            window.addButton(mapButton);
        }

        // handler
        window.addHandler((player1, i) -> {
            FormResponseSimple response = window.getResponse();
            if(response == null) return;
            int clicked = response.getClickedButtonId();

            MapType votedFor = MapType.values()[clicked];
            Citrine.getData(player1).votedMap = votedFor;
            player1.sendMessage("§b§l» §r§7You voted for §e" + votedFor.name);
        });

        player.showFormWindow(window);
        return false;
    }
}
