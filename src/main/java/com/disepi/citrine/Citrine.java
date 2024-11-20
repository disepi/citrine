package com.disepi.citrine;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.CraftingManager;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.potion.Effect;
import com.disepi.citrine.anticheat.AnticheatMain;
import com.disepi.citrine.data.GameData;
import com.disepi.citrine.data.LevelData;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.data.custom.HubLevelData;
import com.disepi.citrine.data.custom.LobbyLevelData;
import com.disepi.citrine.data.maps.MapSaveData;
import com.disepi.citrine.data.maps.MapType;
import com.disepi.citrine.data.maps.TeamType;
import com.disepi.citrine.events.InventoryPickupItem;
import com.disepi.citrine.items.BlockBoombox;
import com.disepi.citrine.items.BlockFrozenBoombox;
import com.disepi.citrine.items.BlockKnockbackBoombox;
import com.disepi.citrine.items.BlockPoisonBoombox;
import com.disepi.citrine.items.loot.tables.*;
import com.disepi.citrine.utils.BlockUtil;
import com.disepi.citrine.utils.Log;
import com.disepi.citrine.utils.PluginStorage;
import com.disepi.citrine.utils.ScoreboardUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Citrine {
    public static Map<Player, PlayerData> data = new HashMap<Player, PlayerData>();
    public static Map<Level, LevelData> levelData = new HashMap<Level, LevelData>();
    public static Map<Integer, GameData> gameData = new HashMap<Integer, GameData>();
    public static Map<MapType, MapSaveData> mapSaveData = new HashMap<MapType, MapSaveData>();

    public static Server server;

    // Map data
    public static String hubLevelName = "hivesummerhub";
    public static Level hubLevel;

    // Game data
    public static Integer gameIdCounter;

    // Tables
    public static ChestTable chestTable = new ChestTable();
    public static EnderChestTable enderTable = new EnderChestTable();

    public static EmeraldOreTable emeraldOreTable = new EmeraldOreTable();
    public static IronOreTable ironOreTable = new IronOreTable();
    public static DiamondOreTable diamondOreTable = new DiamondOreTable();
    public static GoldOreTable goldOreTable = new GoldOreTable();
    public static SpellOreTable spellOreTable = new SpellOreTable();

    // Mutator chances
    public static float redstoneMutatorChance = 0.15f;
    public static float hitSpeedMutatorChance = 0.15f;

    public static void killEntitiesInLevel(Level l) {
        for(Entity ent : l.getEntities()) {
            if(!(ent instanceof Player)) {
                ent.despawnFromAll();
                ent.kill();
            }
        }
    }

    public static void killAllEntities() {
        for(Level lvl : server.getLevels().values())
            killEntitiesInLevel(lvl);
    }

    public static void blindPlayer(Player p) {
        Effect blind = Effect.getEffect(Effect.BLINDNESS);
        blind.setDuration(25);
        blind.setAmbient(true);
        blind.setAmplifier(255);
        blind.setVisible(false);
        blind.setColor(0,0,0);
        p.addEffect(blind);
    }

    public static void init() {
        // remove all crafting
        CraftingManager craft = server.getCraftingManager();
        craft.rebuildPacket();

        // anticheat
        AnticheatMain.initializeChecks();

        killAllEntities();

        // blocks
        //BlockUtil.boomboxId = new BlockBoombox().getId();
        //BlockUtil.poisonBoomboxId = new BlockPoisonBoombox().getId();
        //BlockUtil.frozenBoomboxId = new BlockFrozenBoombox().getId();
        //BlockUtil.kbBoomboxId = new BlockKnockbackBoombox().getId();

        // setup hub data
        server.loadLevel(hubLevelName);
        hubLevel = server.getLevelByName(hubLevelName);
        HubLevelData hubData = new HubLevelData(hubLevel);
        levelData.put(hubLevel, hubData);
        hubLevel.setSpawnLocation(new Vector3(-56.5f, 51, -27.5f));
        hubData.setupScene();
        hubData.canEdit = false;
        hubData.spawnYaw = -90.f;

        for (Player plr : server.getOnlinePlayers().values()) {
            hubData.introduce(plr, false);
            server.sendRecipeList(plr);
        }

        // map data- forgive the shit code
        MapSaveData ivory = new MapSaveData(MapType.IVORY);
        ivory.mysteryChestPos = new Vector3f(-0.5f, 40, -0.5f);
        ivory.spawns.put(TeamType.RED, new Vector3f(27.5f,42.f,-65.5f));
        ivory.spawns.put(TeamType.GRAY, new Vector3f(65.5f,42.f,-28.5f));
        ivory.spawns.put(TeamType.PURPLE, new Vector3f(69.5F,42.f,3.5F));
        ivory.spawns.put(TeamType.LIME, new Vector3f(64.5F,42.f,27.5F));
        ivory.spawns.put(TeamType.AQUA, new Vector3f(27.5f,42.f,64.5f));
        ivory.spawns.put(TeamType.YELLOW, new Vector3f(-4.5F,42.f,69.5F));
        ivory.spawns.put(TeamType.DARK_GRAY, new Vector3f(-28.5F,42.f,64.5F));
        ivory.spawns.put(TeamType.GOLD, new Vector3f(-65.5F,42.f,27.5F));
        ivory.spawns.put(TeamType.BLUE, new Vector3f(-70.5F,42.f,-4.5F));
        ivory.spawns.put(TeamType.GREEN, new Vector3f(-65.5f,42.f,-28.5f));
        ivory.spawns.put(TeamType.MAGENTA, new Vector3f(-28.5F,42.f,-65.5F));
        ivory.spawns.put(TeamType.CYAN, new Vector3f(3.5F,42.f,-70.5F));

        ivory.flags.put(new Vector3f(-0.5f, 44, 65.5f), 90.f);
        ivory.flags.put(new Vector3f(-24.5f, 44, 60.5f), 0.f);
        ivory.flags.put(new Vector3f(-61.5f, 44, 23.5f), 90.f);
        ivory.flags.put(new Vector3f(-66.5f, 44, -0.5f), -180.f);
        ivory.flags.put(new Vector3f(-61.5f, 44, -24.5f), 90.f);
        ivory.flags.put(new Vector3f(-24.5f, 44, -61.5f), -180.f);
        ivory.flags.put(new Vector3f(-0.5f, 44, -66.5f), -90.f);
        ivory.flags.put(new Vector3f(23.5f, 44, -61.5f), -180.f);
        ivory.flags.put(new Vector3f(60.5f, 44, -24.5f), -90.f);
        ivory.flags.put(new Vector3f(65.5f, 44, -0.5f), 0.f);
        ivory.flags.put(new Vector3f(60.5f, 44, 23.5f), -90.f);
        ivory.flags.put(new Vector3f(23.5f, 44, 60.5f), 0.f);
        ivory.flags.put(new Vector3f(-61.5f, 44, 23.5f), 90.f);

        ivory.chests = new Vector3f[]{ new Vector3f(-79.0f, 33.0f, 3.0f),
                new Vector3f(-75.0f, 38.0f, -7.0f),
                new Vector3f(-75.0f, 38.0f, -3.0f),
                new Vector3f(-68.0f, 38.0f, -33.0f),
                new Vector3f(-68.0f, 38.0f, 31.0f),
                new Vector3f(-64.0f, 38.0f, -33.0f),
                new Vector3f(-64.0f, 38.0f, 31.0f),
                new Vector3f(-58.0f, 33.0f, -37.0f),
                new Vector3f(-58.0f, 33.0f, 35.0f),
                new Vector3f(-37.0f, 33.0f, -58.0f),
                new Vector3f(-37.0f, 33.0f, 56.0f),
                new Vector3f(-33.0f, 38.0f, -68.0f),
                new Vector3f(-33.0f, 38.0f, -64.0f),
                new Vector3f(-33.0f, 38.0f, 62.0f),
                new Vector3f(-33.0f, 38.0f, 66.0f),
                new Vector3f(-14.0f, 40.0f, -14.0f),
                new Vector3f(-13.0f, 41.0f, 5.0f),
                new Vector3f(-7.0f, 38.0f, 73.0f),
                new Vector3f(-5.0f, 33.0f, -79.0f),
                new Vector3f(-3.0f, 38.0f, 73.0f),
                new Vector3f(1.0f, 38.0f, -75.0f),
                new Vector3f(3.0f, 33.0f, 77.0f),
                new Vector3f(5.0f, 38.0f, -75.0f),
                new Vector3f(11.0f, 41.0f, -7.0f),
                new Vector3f(12.0f, 40.0f, 12.0f),
                new Vector3f(31.0f, 38.0f, -68.0f),
                new Vector3f(31.0f, 38.0f, -64.0f),
                new Vector3f(31.0f, 38.0f, 62.0f),
                new Vector3f(31.0f, 38.0f, 66.0f),
                new Vector3f(35.0f, 33.0f, -58.0f),
                new Vector3f(35.0f, 33.0f, 56.0f),
                new Vector3f(56.0f, 33.0f, -37.0f),
                new Vector3f(56.0f, 33.0f, 35.0f),
                new Vector3f(62.0f, 38.0f, -33.0f),
                new Vector3f(62.0f, 38.0f, 31.0f),
                new Vector3f(66.0f, 38.0f, -33.0f),
                new Vector3f(66.0f, 38.0f, 31.0f),
                new Vector3f(73.0f, 38.0f, 1.0f),
                new Vector3f(73.0f, 38.0f, 5.0f),
                new Vector3f(77.0f, 33.0f, -5.0f),
                new Vector3f(152.0f, -63.0f, 198.0f)};

        ivory.enderChests = new Vector3f[]{ new Vector3f(-7.0f, 41.0f, 1.0f),
                new Vector3f(-1.0f, 40.0f, -20.0f),
                new Vector3f(-1.0f, 40.0f, 18.0f),
                new Vector3f(5.0f, 41.0f, -3.0f)};

        Citrine.mapSaveData.put(ivory.map, ivory);

        MapSaveData baroque = new MapSaveData(MapType.BAROQUE);
        baroque.mysteryChestPos = new Vector3f(1.5f, 83, 0.5f);
        baroque.spawns.put(TeamType.RED, new Vector3f(35.5f,82.f,-61.5f));
        baroque.spawns.put(TeamType.GRAY, new Vector3f(63.5f,82.f,-39.5f));
        baroque.spawns.put(TeamType.PURPLE, new Vector3f(73.5f,82.f,-2.5f));
        baroque.spawns.put(TeamType.LIME, new Vector3f(63.5f,82.f,34.5f));
        baroque.spawns.put(TeamType.AQUA, new Vector3f(41.5f,82.f,62.5f));
        baroque.spawns.put(TeamType.YELLOW, new Vector3f(4.5f,82.f,72.5f));
        baroque.spawns.put(TeamType.DARK_GRAY, new Vector3f(-32.5f,82.f,62.5f));
        baroque.spawns.put(TeamType.GOLD, new Vector3f(-60.5F,82.f,40.5f));
        baroque.spawns.put(TeamType.BLUE, new Vector3f(-70.5F,82.f,3.5f));
        baroque.spawns.put(TeamType.GREEN, new Vector3f(-60.5f,82.f,-33.5f));
        baroque.spawns.put(TeamType.MAGENTA, new Vector3f(-38.5f,82.f,-61.5F));
        baroque.spawns.put(TeamType.CYAN, new Vector3f(-1.5f,82.f,-71.5f));

        // did not bother with angles
        baroque.flags.put(new Vector3f(4.5f, 93.f, 68.5f), 90.f);
        baroque.flags.put(new Vector3f(41.5f, 93.f, 58.5f), 0.f);
        baroque.flags.put(new Vector3f(59.5f, 93.f, 34.5f), 90.f);
        baroque.flags.put(new Vector3f(69.5f, 93.f, -2.5f), -180.f);
        baroque.flags.put(new Vector3f(59.5f, 93.f, -39.5f), 90.f);
        baroque.flags.put(new Vector3f(35.5f, 93.f, -57.5f), -180.f);
        baroque.flags.put(new Vector3f(-1.5f, 93.f, -67.5f), -90.f);
        baroque.flags.put(new Vector3f(-38.5f, 93.f, -57.5f), -180.f);
        baroque.flags.put(new Vector3f(-56.5f, 93.f, -33.5f), -90.f);
        baroque.flags.put(new Vector3f(-66.5f, 93.f, 3.5f), 0.f);
        baroque.flags.put(new Vector3f(-56.5f, 93.f, 40.5f), -90.f);
        baroque.flags.put(new Vector3f(-32.5f, 93.f, 58.5f), 0.f);
        //baroque.flags.put(new Vector3f(-61.5f, 94.f, 23.5f), 90.f);

        baroque.chests = new Vector3f[]{ new Vector3f(-79.0f, 79.0f, 4.0f),
                new Vector3f(-77.0f, 77.0f, -8.0f),
                new Vector3f(-75.0f, 71.0f, -1.0f),
                new Vector3f(-69.0f, 79.0f, -33.0f),
                new Vector3f(-69.0f, 79.0f, 41.0f),
                new Vector3f(-67.0f, 77.0f, -45.0f),
                new Vector3f(-67.0f, 77.0f, 29.0f),
                new Vector3f(-65.0f, 71.0f, -38.0f),
                new Vector3f(-65.0f, 71.0f, 36.0f),
                new Vector3f(-44.0f, 77.0f, 68.0f),
                new Vector3f(-40.0f, 79.0f, -70.0f),
                new Vector3f(-37.0f, 71.0f, 66.0f),
                new Vector3f(-35.0f, 71.0f, -66.0f),
                new Vector3f(-32.0f, 79.0f, 70.0f),
                new Vector3f(-28.0f, 77.0f, -68.0f),
                new Vector3f(-18.0f, 78.0f, 3.0f),
                new Vector3f(-7.0f, 77.0f, 78.0f),
                new Vector3f(-3.0f, 79.0f, -80.0f),
                new Vector3f(-2.0f, 78.0f, -19.0f),
                new Vector3f(0.0f, 71.0f, 76.0f),
                new Vector3f(2.0f, 71.0f, -76.0f),
                new Vector3f(4.0f, 78.0f, 19.0f),
                new Vector3f(5.0f, 79.0f, 80.0f),
                new Vector3f(9.0f, 77.0f, -78.0f),
                new Vector3f(20.0f, 78.0f, -3.0f),
                new Vector3f(30.0f, 77.0f, 68.0f),
                new Vector3f(34.0f, 79.0f, -70.0f),
                new Vector3f(37.0f, 71.0f, 66.0f),
                new Vector3f(39.0f, 71.0f, -66.0f),
                new Vector3f(42.0f, 79.0f, 70.0f),
                new Vector3f(46.0f, 77.0f, -68.0f),
                new Vector3f(67.0f, 71.0f, -36.0f),
                new Vector3f(67.0f, 71.0f, 38.0f),
                new Vector3f(69.0f, 77.0f, -29.0f),
                new Vector3f(69.0f, 77.0f, 45.0f),
                new Vector3f(71.0f, 79.0f, -41.0f),
                new Vector3f(71.0f, 79.0f, 33.0f),
                new Vector3f(77.0f, 71.0f, 1.0f),
                new Vector3f(79.0f, 77.0f, 8.0f),
                new Vector3f(81.0f, 79.0f, -4.0f)};

        baroque.enderChests = new Vector3f[]{ new Vector3f(-18.0f, 78.0f, -3.0f),
                new Vector3f(-2.0f, 78.0f, 19.0f),
                new Vector3f(4.0f, 78.0f, -19.0f),
                new Vector3f(20.0f, 78.0f, 3.0f)};

        Citrine.mapSaveData.put(baroque.map, baroque);
    }


    public static void tickPlayer(Player p) {
        PlayerData dat = getData(p);
        if(dat == null) return;

        p.getFoodData().setLevel(p.getFoodData().getMaxLevel());
        if(dat.hasInfiniteHealth) p.setHealth(20);

        dat.immobileTicks--;
        if(dat.immobileTicks < 0) dat.immobileTicks = 0;
        p.setImmobile(dat.immobileTicks > 0);

        p.setAbsorption((int)p.getAbsorption());

        // custom item pickup
        for (Entity entity : p.level.getNearbyEntities(p.boundingBox.grow(1.5f, 1.5f, 1.5f), p)) {
            entity.scheduleUpdate();

            if (!entity.isAlive() || !p.isAlive())
                continue;

            InventoryPickupItem.cancelPickup = false;
            p.pickupEntity(entity, true);
            InventoryPickupItem.cancelPickup = true;
        }
    }

    public static void clearPlayer(Player player) {
        player.setAbsorption(0);
        player.setHealth(20);
        ScoreboardUtil.hideBoard(player);

        // PROBAbly shouldnt do this but fucki t
        for(int i = 0; i < 29; i++) player.removeEffect(i);
    }

    public static void switchPlayerLevel(LevelData level, PlayerData player) {
        level.level.setTime(18000);
        player.player.setAllowFlight(false);
        clearPlayer(player.player);
        player.isSpectating = false;

        Position pos = level.level.getSpawnLocation();

        for(Entity ent : player.player.level.getEntities())
            ent.despawnFrom(player.player);

        player.player.usedChunks.clear();

        // BAD DIMENSION SWITCH CODE AHEAD

        // player.pendingData = level;
        // player.handleSwitchDimension(1, pos);
        //player.sendEmptyChunks(6, pos);
        //player.sendDimChangeAck();
        //player.player.level = level.level;
        //player.votedMap = null;
        //level.introduce(player.player, false);
        //player.requestChunks(4, pos);

        //Server.getInstance().loadLevel("nether");
        //Level nether = Server.getInstance().getLevelByName("nether");
        //player.pendingData = level;
        //player.player.switchLevel(nether);
        //player.stopSounds();

        player.player.setLevel(level.level);
        player.requestChunks(4, pos);
        player.votedMap = null;
        level.introduce(player.player, false);
    }

    public static void sendMessageToLevel(Level level, String message) {
        Log.s(message);
        for(Player player : server.getOnlinePlayers().values()) {
            if(player.level != level) continue;
            player.sendMessage(message);
        }
    }

    public static boolean cloneLevel(String name, String storeName) {
        String dataPath = server.getDataPath();
        PluginStorage.copyDir(dataPath + "storage/" + name, dataPath + "worlds/" + storeName, true);
        return true;
    }

    public static void handleGameTick() {
        for(GameData dat : Citrine.gameData.values()) {
            dat.handleUpdate();

            if(dat.scheduledDelete) cleanUpGame(dat);
        }
    }
    public static void cleanUpGame(GameData dat) {
        Log.s("cleaning up");
        Citrine.gameData.remove(dat);
        Citrine.gameData.remove(dat.gameId, dat);

        String lobbyName = dat.lobbyLevelName;
        String gameName = dat.gameLevelName;

        if(dat.lobbyLevel != null) {
            Citrine.server.unloadLevel(dat.lobbyLevel, true);
            dat.lobbyLevel = null;
        }

        if(dat.gameLevel != null) {
            Citrine.server.unloadLevel(dat.gameLevel, true);
            dat.gameLevel = null;
        }

        String dataPath = Citrine.server.getDataPath();
        if(lobbyName != "") PluginStorage.deleteRecursive(new File(dataPath + "worlds/" + lobbyName));
        if(gameName != "") PluginStorage.deleteRecursive(new File(dataPath + "worlds/" + gameName));
    }

    public static void handleJoinGame(Player p, GameData gameDat) {
        PlayerData data = getData(p);
        if(gameDat.getPlayerCount() + data.getBandSize() > gameDat.maxPlayers) return;
        gameDat.handleAdd(p, data);
        switchPlayerLevel(getLevelData(gameDat.currentLevel), data);
    }

    public static GameData allocateGame() {
        Citrine.gameIdCounter = Math.toIntExact(System.currentTimeMillis() % Integer.MAX_VALUE);

        // Clone and load the world into server
        String storeName = Citrine.gameIdCounter + "-skyhub";
        cloneLevel("skyhub", storeName);
        server.loadLevel(storeName);

        // Setup lobby data
        Level lobbyLevel = server.getLevelByName(storeName);
        LobbyLevelData lobbyData = new LobbyLevelData(lobbyLevel);
        levelData.put(lobbyLevel, lobbyData);
        lobbyLevel.setSpawnLocation(new Vector3(59.5f, 73.f, -32.5f));
        lobbyData.setupScene();
        lobbyData.canEdit = false;
        lobbyData.spawnYaw = 0;

        // Setup game data
        GameData newGame = new GameData(Citrine.gameIdCounter, lobbyLevel);
        lobbyData.assignedGame = newGame;
        Citrine.gameData.put(Citrine.gameIdCounter, newGame);

        return newGame;
    }

    public static void findGameForPlayer(Player p) {
        p.sendMessage("§b§l» §r§7§7Finding you a game of Solos...");

        for(GameData dat : Citrine.gameData.values()) {
            if(dat.players.contains(p)) dat.players.remove(p);
        }

        if(Citrine.gameData.isEmpty()) {
            handleJoinGame(p, allocateGame());
            return;
        }

        PlayerData playerDat = getData(p);
        int bandSize = playerDat.getBandSize(); // need to add party system
        for(GameData dat : Citrine.gameData.values()) {
            if(!dat.started && dat.getPlayerCount() + bandSize <= dat.maxPlayers) {
                handleJoinGame(p, dat);
                return;
            }
        }

        handleJoinGame(p, allocateGame());
    }

    public static PlayerData getData(Player player) {
        if(!data.containsKey(player)) return null;
        return data.get(player);
    }

    public static void addData(Player player) {
        data.put(player, new PlayerData(player));
    }

    public static void removeData(Player player) {
        if(!data.containsKey(player)) return;
        data.remove(player);
    }

    public static LevelData getLevelData(Level level) {
        if(!levelData.containsKey(level)) return null;
        return levelData.get(level);
    }

}
