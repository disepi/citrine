package com.disepi.citrine.data.custom;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSwordDiamond;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.potion.Effect;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.GameData;
import com.disepi.citrine.data.LevelData;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.entity.hub.BorderNPC;
import com.disepi.citrine.entity.hub.FlagNPC;
import com.disepi.citrine.entity.hub.GoHubNPC;
import com.disepi.citrine.entity.hub.SpawnVehicleNPC;
import com.disepi.citrine.items.loot.TableEntry;
import com.disepi.citrine.items.loot.tables.EnderChestTable;
import com.disepi.citrine.utils.Log;
import com.disepi.citrine.utils.MathUtil;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameLevelData extends LevelData {

    public GameData assignedGame;

    public GameLevelData(Level l) {
        super(l);
    }

    public void handleMove(Player player, float x, float y, float z, float pitch, float yaw) {
        if(assignedGame.sentStartMessage && !assignedGame.bordersDown) {
            PlayerData dat = Citrine.getData(player);
            if(!MathUtil.isInRegion(getPlayerSpawn(dat), new Vector3f(x,y,z), 12.f) || y < -0) {
                teleportToSpawn(player, dat);
                Citrine.blindPlayer(player);
                return;
            }
        }

        // out of bound
        if(!MathUtil.isInRegion(new Vector3f(0,0, 0), new Vector3f(x,y,z), 256) || y > 1024)
            teleportToSpawn(player, Citrine.getData(player));

    }

    public void handleLeave(Player p) {
        assignedGame.handleLeave(p, Citrine.getData(p));
    }

    public void setupScene() {
        // create all the flags
        for(var entry : assignedGame.saveData.flags.entrySet()) {
            float yaw = entry.getValue() + 90 + 180.f;
            Vector3f pos = entry.getKey();
            FlagNPC flag = new FlagNPC(this.level, pos.x, pos.y - 1.f, pos.z);
            flag.yaw = yaw;
            flag.headYaw = yaw;
            flag.spawnToAll();
        }

        // create all the spawn vehicles
        Vector3f mid = new Vector3f(0.5f, 0, 0.5f);
        for(var entry : assignedGame.saveData.spawns.entrySet()) {
            float yaw = (float) MathUtil.getRotationsToPosition(entry.getValue(), mid).x;
            Vector3f pos = entry.getValue();
            SpawnVehicleNPC vehicle = new SpawnVehicleNPC(this.level, pos.x, pos.y, pos.z);
            vehicle.yaw = yaw;
            vehicle.headYaw = yaw;
            vehicle.spawnToAll();
        }

        // allow editing temporarily
        this.canEdit = true;
        this.canPlace = true;
        this.canModify = true;
        this.canBreak = true;
        this.canUpdate = true;

        // fill all normal chests
        for(Vector3f pos : this.assignedGame.saveData.chests) {
            BlockChest chest = (BlockChest) level.getBlock((int) pos.x, (int) pos.y, (int) pos.z);
            chest.createBlockEntity();
            Inventory inv = chest.getBlockEntity().getInventory();
            CopyOnWriteArrayList<Item> items = Citrine.chestTable.getItems(6);

            int slot = 0;
            for(Item item : items) {
                inv.setItem(slot, item);
                slot++;
            }
        }

        // fill all ender chests
        BlockChest bChest = new BlockChest();
        for(Vector3f pos : this.assignedGame.saveData.enderChests) {
            BlockEnderChest eChest = (BlockEnderChest)level.getBlock((int) pos.x, (int) pos.y, (int) pos.z);
            BlockFace face = eChest.getBlockFace();
            bChest.setBlockFace(face);
            level.setBlock(pos.asVector3(), bChest);
            BlockChest chest = (BlockChest) level.getBlock((int) pos.x, (int) pos.y, (int) pos.z);
            chest.createBlockEntity();
            Inventory inv = chest.getBlockEntity().getInventory();
            CopyOnWriteArrayList<Item> items = Citrine.enderTable.getItems(6);

            int slot = 0;
            for(Item item : items) {
                inv.setItem(slot, item);
                slot++;
            }
        }

        // disable editing again
        this.canEdit = false;
        this.canPlace = false;
        this.canModify = false;
        this.canBreak = false;
        this.canUpdate = false;

        // redstone mutator
        if(this.assignedGame.hasRedstoneMutator) {
            int radius = 80;
            for (int x = -radius; x < radius; x++) {
                for (int y = -radius; y < radius; y++) {
                    for (int z = -radius; z < radius; z++) {
                        Block block = level.getBlock(x, y, z, true);
                        if (block.isFullBlock() && block.isSolid())
                            level.setBlock(new Vector3(block.x, block.y, block.z), new BlockOreRedstone());
                    }
                }
            }
        }

        // debug logging for chests
        //Vector3f[] chestsarr = new Vector3f[]{ new Vector3f(1, 1, 1), new Vector3f(1, 1, 1), new Vector3f(1, 1, 1) };
        //String logMsg = "Vector3f[] array = new Vector3f[]{ ";
        //for(Vector3f pos : echests) logMsg += "new Vector3f(" + pos.x + "f, " + pos.y + "f, " + pos.z + "f), \n";
        //Log.s(logMsg);
    }

    public Vector3f getPlayerSpawn(PlayerData dat) {
        return this.assignedGame.saveData.spawns.get(dat.team);
    }

    public void teleportToSpawn(Player p, PlayerData dat) {
        Vector3f _spawnPos = getPlayerSpawn(dat);
        Vector3 spawnPos = _spawnPos.asVector3();
        float yaw = (float) MathUtil.getRotationsToPosition(_spawnPos, new Vector3f(0.5f, 0, 0.5f)).x;

        p.setPositionAndRotation(spawnPos, yaw, 0, yaw);
        p.sendPosition(spawnPos, yaw, 0, 2);
    }

    public void introduce(Player p, boolean teleportOnly) {
        p.getInventory().clearAll();
        p.usedChunks.clear();
        PlayerData dat = Citrine.getData(p);
        dat.immobileTicks = Integer.MAX_VALUE;
        p.setLevel(this.level);

        Vector3f _spawnPos = this.assignedGame.saveData.spawns.get(dat.team);
        teleportToSpawn(p, dat);

        BorderNPC border = new BorderNPC(this.level, _spawnPos.x, _spawnPos.y, _spawnPos.z);
        border.setScale(8.f);
        dat.border = border;
        border._spawnTo(p);
    }
}
