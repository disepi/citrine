package com.disepi.citrine.utils;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveObjectivePacket;
import cn.nukkit.network.protocol.SetDisplayObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;

import java.util.ArrayList;

import static cn.nukkit.scoreboard.data.ScorerType.FAKE;

public class ScoreboardUtil {

    public static DataPacket getScoreboardShowPacket() {
        SetDisplayObjectivePacket packetSetObjective = new SetDisplayObjectivePacket();
        packetSetObjective.criteriaName = "dummy";
        packetSetObjective.displayName = "support.playhive.com/ui";
        packetSetObjective.objectiveName = "1";
        packetSetObjective.sortOrder = SortOrder.ASCENDING;
        packetSetObjective.displaySlot = DisplaySlot.SIDEBAR;
        return packetSetObjective;
    }

    public static DataPacket getScoreboardRemovePacket() {
        RemoveObjectivePacket packetSetObjective = new RemoveObjectivePacket ();
        packetSetObjective.objectiveName = "1";
        return packetSetObjective;
    }

    public static void showBoard(Player p) {
        p.dataPacket(getScoreboardShowPacket());
    }

    public static void hideBoard(Player p) {
        p.dataPacket(getScoreboardRemovePacket());
    }

    public static SetScorePacket.ScoreInfo getScoreInfo(int score, String name, int id) {
        SetScorePacket.ScoreInfo scoreInf = new SetScorePacket.ScoreInfo(id, "1", 0);
        scoreInf.score = score;
        scoreInf.name = name;
        scoreInf.type = FAKE;
        return scoreInf;
    }

    public static SetScorePacket getScorePacket() {
        SetScorePacket sc = new SetScorePacket();
        sc.action = SetScorePacket.Action.SET;
        return sc;
    }
}
