package com.disepi.citrine.data.custom;

import cn.nukkit.Player;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemBookEnchanted;
import cn.nukkit.item.ItemNameTag;
import cn.nukkit.item.ItemTotem;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.entity.hub.HivePlusNPC;
import com.disepi.citrine.entity.hub.ReplayNPC;
import com.disepi.citrine.entity.hub.SkyNPC;
import com.disepi.citrine.entity.hub.SoonNPC;
import com.disepi.citrine.items.BlockBoombox;
import com.disepi.citrine.items.GameCompassItem;

public class HubLevelData extends LevelData {
    public HubLevelData(Level l) {
        super(l);
    }

    public void handleIntroduce(Player p) {
        PlayerData data = Citrine.getData(p);

        // cant die in hub
        data.canTakeDamage = false;
        data.canDoDamage = false;

        p.getInventory().clearAll();
        p.setGamemode(2);
        p.setExperience(0, 0);

        p.getInventory().setItem(0, new GameCompassItem());
        p.getInventory().setItem(8, new ItemBookEnchanted());
        p.getInventory().setItem(7, new ItemNameTag());
        p.getInventory().setItem(6, new ItemTotem(0));
        //p.sendMessage("§8§l[§a§lLR§8§l] §rYou're now on a 1 day login streak. XP boost: +1%");
    }

    public void addSoonNPC(float x, float z) {
        SoonNPC soon = new SoonNPC(this.level, x, 48.f, z);
        soon.yaw = 90;
        soon.setScale(2);
        soon.spawnToAll();
    }

    public void handleMove(Player player, float x, float y, float z, float pitch, float yaw) {
        // tp after falling down
        if(y < 20)
            this.introduce(player, true);
    }

    public void setupScene() {
        float y = 48.f;
        SkyNPC skywars = new SkyNPC(this.level, -28.5f, y, -27.5f);
        skywars.yaw = 90;
        skywars.setScale(2);
        skywars.spawnToAll();

        addSoonNPC(-28.5f, -23.5f);
        addSoonNPC(-29.5f, -19.5f);
        addSoonNPC(-30.5f, -15.5f);
        addSoonNPC(-31.5f, -11.5f);
        addSoonNPC(-28.5f, -31.5f);
        addSoonNPC(-31.5f, -11.5f);
        addSoonNPC(-29.5f, -35.5f);
        addSoonNPC(-30.5f, -39.5f);
        addSoonNPC(-31.5f, -43.5f);

        HivePlusNPC plus = new HivePlusNPC(this.level, -45.5f, 47.f, -34.5f);
        plus.yaw = 0;
        plus.setScale(1.25f);
        plus.spawnToAll();

        ReplayNPC replay = new ReplayNPC(this.level, -45.5f, 47.f, -20.5f);
        replay.yaw = 130;
        plus.spawnToAll();
    }
}
