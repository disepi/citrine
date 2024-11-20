package com.disepi.citrine.data;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBone;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.*;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.maps.MapType;
import com.disepi.citrine.data.maps.TeamType;
import com.disepi.citrine.entity.hub.BoomboxNPC;
import com.disepi.citrine.entity.hub.BorderNPC;
import com.disepi.citrine.entity.hub.LootboxNPC;
import com.disepi.citrine.items.BlockBoombox;
import com.disepi.citrine.utils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.function.BiConsumer;

public class PlayerData {
    public Player player;
    public boolean canDoDamage;
    public boolean canTakeDamage;
    public boolean hasInfiniteHealth;
    public int immobileTicks;
    public LevelData pendingData;
    public boolean inventoryOpen = false;
    public MapType votedMap = null;
    public TeamType team = null;
    public BorderNPC border = null;
    public GameData refData = null;
    public boolean isSpectating = false;

    // anticheat
    public float[] violationMap = new float[64];

    public float lastX, lastY, lastZ = 0;
    public float lastPitch, lastYaw = 0;
    public int ignoreTicks = 0;
    public int lerpTicks = 0;


    public int teleportWaitTicks = 0;
    public Vector3f targetTeleportPos = null;
    public boolean onGround = true;
    public boolean touchedGroundSinceTp = false;
    public int offGroundTicks = 0;
    public int onGroundTicks = 0;
    public Vector3f lastGroundPos = null;
    public long lastBreakTime = 0;
    public long lastChatTime = 0;
    public float lastDelta = 0.f;
    public long lastAttackBufferTime = 0;
    public int attackBuffer = 0;
    public float lastSpeed = 0.f;
    public long lastMoveBufferTime = 0;
    public float moveBuffer = 0.f;
    public long lastSwingTime = 0;


    public PlayerData(Player p) {
        this.player = p;
        this.pendingData = null;
        this.canDoDamage = false;
        this.canTakeDamage = false;
        this.hasInfiniteHealth = true;

        this.lastX = (float) p.x;
        this.lastY = (float) p.y;
        this.lastZ = (float) p.z;
        this.lastPitch = (float) p.pitch;
        this.lastYaw = (float) p.yaw;

        this.lastGroundPos = p.getPosition().asVector3f();
        this.ignoreTicks = 5;
    }

    public void handleOutOfRegionPlace() {
        player.dataPacket(SoundUtil.getSoundPacket(Sound.NOTE_BASS, 1, 1, player));
        player.sendMessage("§c§l» §r§cYou can't modify blocks outside of your island border for §6" + this.refData.lastSecondDisplay + " more seconds§c!");
    }

    public boolean canInteractWorld(Level level, Block block) {
        if(this.player.gamemode == 2) return false;
        if(refData != null) {
            if(!refData.started || !refData.doneWarmup) return false;
            if(!refData.bordersDown) {
                Vector3f spawn = refData.saveData.spawns.get(team);
                if (!MathUtil.isInRegion(spawn, new Vector3f((float) block.x, (float) block.y, (float) block.z), 12.f)) {
                    handleOutOfRegionPlace();
                    return false;
                }
            }
        }
        return true;
    }

    public void handleDeath() {
        Collection<Item> items = new ArrayList<>();
        for(Item item : this.player.getInventory().slots.values())
            items.add(item.clone());

        player.getInventory().clearAll();
        player.setHealth(20);
        player.setAbsorption(0);
        sendReady();

        if(this.refData == null) return;
        if(this.refData.currentLevel != this.refData.gameLevel) return;

        LootboxNPC loot = new LootboxNPC(this.refData.currentLevel, (float) player.x, (float) player.y, (float) player.z);
        loot.items = items;
        loot.yaw = player.yaw;
        loot.headYaw = player.headYaw;
        loot.spawnToAll();

        player.setGamemode(2);
        player.setPositionAndRotation(refData.currentLevel.getSpawnLocation(), 0, 0, 0);
        player.sendPosition(refData.currentLevel.getSpawnLocation(), 0, 0, 2);

        Effect blind = Effect.getEffect(Effect.BLINDNESS);
        blind.setDuration(25);
        blind.setAmbient(true);
        blind.setAmplifier(255);
        blind.setVisible(false);
        blind.setColor(0,0,0);
        player.addEffect(blind);
        player.setAllowFlight(true);
        for(Player p : this.refData.players) {
            p.dataPacket(SoundUtil.getSoundPacket(Sound.MOB_GUARDIAN_DEATH, 999, 1, p));
            p.sendMessage("§6§l» §r§§" + this.team.format + "" + player.getName() + " §cdied");
        }

        player.sendTitle(TextFormat.RED + "You died!", TextFormat.YELLOW + "Now spectating.");
        GameItemHandlerUtil.handleUseHubItem(player); // send to hub
    }

    public boolean handleBlockBreak(Level level, Block block) {
        if(this.isSpectating) return false;
        if(!canInteractWorld(level, block)) return false;

        if(block instanceof BlockOreRedstone || block instanceof BlockOreRedstoneGlowing) {
            Vector3 blockPos = new Vector3(block.x, block.y, block.z);
            if(player.getAbsorption() >= 10.0f) {
                player.dataPacket(SoundUtil.getSoundPacket(Sound.CONDUIT_ATTACK, 1, 1, player));
                player.sendMessage("§c§l» §r§cYou can only have §65 extra hearts §cactive!");
                GameItemHandlerUtil.breakBlock(blockPos, true, level);
                return false;
            }
            level.addSound(blockPos, Sound.BLOCK_END_PORTAL_FRAME_FILL);
            GameItemHandlerUtil.breakBlock(blockPos, true, level);
            player.setAbsorption(((int)player.getAbsorption())+2);
            player.sendAttributes();
            return false;
        }
        else if(block instanceof BlockOreGold) {
            Random rand = new Random();
            if(rand.nextFloat() < 0.1f) {
                new BoomboxNPC(block.level, (float) block.x + 0.5f, (float) block.y, (float) block.z + 0.5f, 3 * 20, 6, 1, Citrine.getData(player).refData, player).spawnToAll();
                player.sendMessage(TextFormat.GOLD + "" + TextFormat.BOLD + "»" + TextFormat.RESET + TextFormat.GRAY + TextFormat.ITALIC + " That wasn't very lucky...");
            } else {
                Vector3 dropPos = new Vector3((float) block.x + 0.5f, (float) block.y, (float) block.z + 0.5f);

                level.dropItem(dropPos, Citrine.goldOreTable.getRandomItem());
                if(rand.nextFloat() < 0.5f)
                    level.dropItem(dropPos, Citrine.goldOreTable.getRandomItem());
            }

            GameItemHandlerUtil.breakBlock(new Vector3(block.x, block.y, block.z), true, level);
            return false;
        }
        else if(block instanceof BlockOreIron) {
            level.dropItem(new Vector3((float) block.x + 0.5f, (float) block.y, (float) block.z + 0.5f), Citrine.ironOreTable.getRandomItem());
            GameItemHandlerUtil.breakBlock(new Vector3(block.x, block.y, block.z), true, level);
            return false;
        }
        else if(block instanceof BlockOreDiamond) {
            level.dropItem(new Vector3((float) block.x + 0.5f, (float) block.y, (float) block.z + 0.5f), Citrine.diamondOreTable.getRandomItem());
            GameItemHandlerUtil.breakBlock(new Vector3(block.x, block.y, block.z), true, level);
            return false;
        }
        else if(block instanceof BlockOreEmerald) {
            level.dropItem(new Vector3((float) block.x + 0.5f, (float) block.y, (float) block.z + 0.5f), Citrine.emeraldOreTable.getRandomItem());
            GameItemHandlerUtil.breakBlock(new Vector3(block.x, block.y, block.z), true, level);
            return false;
        }
        else if(block instanceof BlockOreLapis) {
            Item item = Citrine.spellOreTable.getRandomItem();
            if(item instanceof ItemBone) {
                player.sendMessage(TextFormat.BLUE + "" + TextFormat.BOLD + "»" + TextFormat.RESET + TextFormat.GRAY + TextFormat.ITALIC + " No spellbook for you! Maybe next time?");
                player.dataPacket(SoundUtil.getSoundPacket(Sound.BLOCK_SCAFFOLDING_BREAK, 1, 0.5f, player));
            }
            else
                level.dropItem(new Vector3((float) block.x + 0.5f, (float) block.y, (float) block.z + 0.5f), item);
            GameItemHandlerUtil.breakBlock(new Vector3(block.x, block.y, block.z), true, level);
            return false;
        }

        return true;
    }

    public boolean handleBlockPlace(Level level, Block block) {
        if(this.isSpectating) return false;
        if(!canInteractWorld(level, block)) return false;
        return true;
    }

    // party will always be 1 size for now
    public int getBandSize() {
        return 1;
    }

    public void handleSwitchDimension(int dimension, Position pos) {
        ChangeDimensionPacket pk = new ChangeDimensionPacket();
        pk.dimension = dimension;
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;
        pk.respawn = false;
        player.dataPacket(pk);

        this.stopSounds();

        //ContainerClosePacket containerClosePacket = new ContainerClosePacket();
        //containerClosePacket.windowId = ContainerIds.INVENTORY;
        //player.handleDataPacket(containerClosePacket);
    }

    public void sendDimChangeAck() {
        PlayerActionPacket actionPacket = new PlayerActionPacket();
        actionPacket.entityId = player.getId();
        actionPacket.action = PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK;
        actionPacket.x = 0;
        actionPacket.y = 0;
        actionPacket.z = 0;
        actionPacket.face = 0;
        actionPacket.resultPosition = new BlockVector3();
        player.dataPacket(actionPacket);
    }

    public void sendNewChunkPublisher(Vector3 pos, int CHUNK_RADIUS) {
        NetworkChunkPublisherUpdatePacket pub = new NetworkChunkPublisherUpdatePacket();
        pub.position = pos.asBlockVector3();
        pub.radius = CHUNK_RADIUS;
        player.dataPacket(pub);
    }

    public void stopSounds() {
        StopSoundPacket stopSoundPacket = new StopSoundPacket();
        stopSoundPacket.stopAll = true;
        stopSoundPacket.name = "";
        player.dataPacket(stopSoundPacket);
    }

    public void sendReady() {
        PlayStatusPacket playStatusPacket = new PlayStatusPacket();
        playStatusPacket.status = PlayStatusPacket.PLAYER_SPAWN;
        player.dataPacket(playStatusPacket);
    }

    public void sendEmptyChunks(int CHUNK_RADIUS, Position pos) {
        sendNewChunkPublisher(pos, CHUNK_RADIUS);

        for (int cX = pos.getChunkX() - CHUNK_RADIUS; cX <= pos.getChunkX() + CHUNK_RADIUS; cX++) {
            for (int cZ = pos.getChunkZ() - CHUNK_RADIUS; cZ <= pos.getChunkZ() + CHUNK_RADIUS; cZ++) {
                LevelChunkPacket chunkData = new LevelChunkPacket();
                chunkData.chunkX = cX;
                chunkData.chunkZ = cZ;
                chunkData.subChunkCount = 0;
                chunkData.data = PluginStorage.EMPTY_CHUNK_DATA;
                chunkData.cacheEnabled = false;
                player.dataPacket(chunkData);
            }
        }
    }

    public void chunkRequestCallback(long timestamp, int x, int z, int subChunkCount, byte[] payload) {
        LevelChunkPacket pk = new LevelChunkPacket();
        pk.chunkX = x;
        pk.chunkZ = z;
        pk.subChunkCount = subChunkCount;
        pk.data = payload;
        player.dataPacket(pk);
    }

    public void requestChunks(int CHUNK_RADIUS, Position pos) {
        Level cur = player.getLevel();
        sendNewChunkPublisher(pos, CHUNK_RADIUS);

        for (int cX = pos.getChunkX() - CHUNK_RADIUS; cX <= pos.getChunkX() + CHUNK_RADIUS; cX++) {
            for (int cZ = pos.getChunkZ() - CHUNK_RADIUS; cZ <= pos.getChunkZ() + CHUNK_RADIUS; cZ++) {
                BaseFullChunk chunk = cur.getChunk(cX, cZ);
                if (chunk == null) continue;
                long timestamp = chunk.getChanges();
                int finalCX = cX;
                int finalCZ = cZ;
                BiConsumer<BinaryStream, Integer> callback = (stream, subchunks) ->
                        chunkRequestCallback(timestamp, finalCX, finalCZ, subchunks, stream.getBuffer());
                ((Anvil)cur.requireProvider()).serialize((BaseChunk) chunk, callback, cur.getDimensionData());
            }
        }
    }
}
