package com.disepi.citrine.data;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import com.disepi.citrine.Citrine;

public class LevelData {
    public Level level;
    public float spawnPitch, spawnYaw;

    // Master setting
    public boolean canEdit;

    // Sub setting
    public boolean canBreak;
    public boolean canBurn;
    public boolean canModify;
    public boolean canIgnite;
    public boolean canPlace;
    public boolean canFade;
    public boolean canFall;
    public boolean canGrow;
    public boolean canRedstone;
    public boolean canSpread;
    public boolean canDecay;
    public boolean canUpdate;
    public boolean canFlow;
    public boolean canDrop;

    public LevelData(Level l) {
        this.level = l;
    }

    public void resetPos(Player p) {
        p.setPositionAndRotation(level.getSpawnLocation(), this.spawnYaw, this.spawnPitch, this.spawnYaw);
        p.sendPosition(level.getSpawnLocation(), this.spawnYaw, this.spawnPitch, 2);
    }

    public void introduce(Player p, boolean teleportOnly) {
        if(!teleportOnly) p.usedChunks.clear();
        PlayerData dat = Citrine.getData(p);
        dat.immobileTicks = 20;
        if(!teleportOnly) p.setLevel(this.level);
        resetPos(p);
        if(teleportOnly) return;
        this.handleIntroduce(p);
    }

    public void handleLeave(Player p) {

    }

    public void setupScene() {

    }

    public void handleMove(Player player, float x, float y, float z, float pitch, float yaw) {

    }

    public void handleIntroduce(Player p) {
    }
}
