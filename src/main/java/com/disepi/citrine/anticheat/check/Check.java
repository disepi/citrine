package com.disepi.citrine.anticheat.check;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.MovePlayerPacket;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.anticheat.AnticheatMain;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.utils.Log;
import com.disepi.citrine.utils.PluginStorage;

public class Check {
    public String name;
    public String codeName;
    public String category;
    public float maxViolationScale;
    public int checkId;

    public Check(String name, String codeName, String category, float maxViolationScale) {
        this.name = name;
        this.codeName = codeName;
        this.category = category;
        this.maxViolationScale = maxViolationScale;
        this.checkId = AnticheatMain.checkAmount++;
    }

    public void violate(Player player, PlayerData data, float amount, boolean punish, String info) {
        Log.s(TextFormat.DARK_GRAY + "[Flareon] " + TextFormat.GRAY + player.getName() + TextFormat.RED + " failed " + TextFormat.GOLD + this.name + TextFormat.DARK_GRAY + " [heat=" + data.violationMap[this.checkId] + "/" + this.maxViolationScale + "] [" + info + "]");
        data.violationMap[this.checkId] += amount;
        if (punish && getViolationScale(data) > this.maxViolationScale)
            punish(player, data); // We failed the check repeatedly, punish
    }

    public void reward(PlayerData data, float amount) {
        data.violationMap[this.checkId] -= amount;
        if (data.violationMap[this.checkId] < 0)
            data.violationMap[this.checkId] = 0;
    }

    public void resetVl(PlayerData data) {
        data.violationMap[this.checkId] = 0;
    }

    public float getViolationScale(PlayerData data) {
        return data.violationMap[this.checkId];
    }

    public void punish(Player p, PlayerData d) {
        String m = TextFormat.DARK_GRAY + "[Flareon] " + TextFormat.RED + "Kicking " + TextFormat.GRAY + p.getName() + " for " + TextFormat.GOLD + this.name + ":" + this.category;
        for(Player ps : PluginStorage.plug.getServer().getOnlinePlayers().values()) {
            if(ps == p) continue;
            ps.sendMessage(m);
        }
        Log.s(m);

        String message = TextFormat.YELLOW + "You were disconnected.\n\n" + TextFormat.GRAY + "Error: " + this.codeName;
        p.sendMessage(message);
        //p.close(message, message);

        Citrine.getData(p).handleDeath();
        Citrine.getLevelData(p.level).handleLeave(p);
        p.usedChunks.clear();
        p.despawnFromAll();
        p.setLevel(Citrine.hubLevel);
        Citrine.getLevelData(p.level).introduce(p, false);
        Citrine.getData(p).hasInfiniteHealth = true;
        Citrine.clearPlayer(p);
        Citrine.blindPlayer(p);
    }

    public boolean check(MovePlayerPacket e, PlayerData d, Player p) {
        return true;
    }
    public boolean checkBreak(PlayerData d, Player p) {
        return true;
    }
    public boolean checkAttack(PlayerData d, Player p) {
        return true;
    }
}
