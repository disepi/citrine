package com.disepi.citrine.data.custom;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.SetScorePacket;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.GameData;
import com.disepi.citrine.data.LevelData;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.entity.hub.CosmeticLockerNPC;
import com.disepi.citrine.entity.hub.GoHubNPC;
import com.disepi.citrine.items.GameBoosterPropItem;
import com.disepi.citrine.items.GameCosmeticPickerItem;
import com.disepi.citrine.items.GameHubItem;
import com.disepi.citrine.items.GameMapSelectItem;
import com.disepi.citrine.utils.ChatSymbol;
import com.disepi.citrine.utils.MathUtil;
import com.disepi.citrine.utils.ScoreboardUtil;

import java.util.ArrayList;

public class LobbyLevelData extends LevelData {

    public GameData assignedGame;

    public LobbyLevelData(Level l) {
        super(l);
    }

    public void handleMove(Player player, float x, float y, float z, float pitch, float yaw) {
        if(y < 0 || !MathUtil.isInRegion(new Vector3f(0,0, 0), new Vector3f(x,y,z), 256)) resetPos(player);
    }

    public void handleLeave(Player p) {
        assignedGame.handleLeave(p, Citrine.getData(p));
    }

    public void setupScene() {
        CosmeticLockerNPC cosmetic = new CosmeticLockerNPC(this.level, 56.5f, 72.5f, -28.5f);
        cosmetic.yaw = -125;
        cosmetic.spawnToAll();

        GoHubNPC hub = new GoHubNPC(this.level, 62.5f, 72.5f, -28.5f);
        hub.setScale(0.75f);
        hub.yaw = 125;
        hub.spawnToAll();
        this.canBurn = false;
    }


    public void handleIntroduce(Player p) {
        PlayerData data = Citrine.getData(p);
        data.canTakeDamage = false;
        data.canDoDamage = false;

        p.getInventory().clearAll();
        p.setGamemode(2);
        p.setExperience(0, 1);


        p.getInventory().setItem(0, new GameMapSelectItem());
        p.getInventory().setItem(1, new GameCosmeticPickerItem());
        p.getInventory().setItem(7, new GameBoosterPropItem());
        p.getInventory().setItem(8, new GameHubItem());

        this.assignedGame.addJoinMessage(p);

        if(!this.assignedGame.started) {
            SetScorePacket sc = ScoreboardUtil.getScorePacket();
            ArrayList<SetScorePacket.ScoreInfo> si = new ArrayList<>();
            si.add(ScoreboardUtil.getScoreInfo(-1, ChatSymbol.levelSymbol + " §aLevel §f1§7/75", 0));
            sc.infos = si;
            ScoreboardUtil.hideBoard(p);
            ScoreboardUtil.showBoard(p);
            p.dataPacket(sc);
        }
    }
}
