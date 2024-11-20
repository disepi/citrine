package com.disepi.citrine;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.provider.CustomClassEntityProvider;
import cn.nukkit.event.Listener;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.NukkitRunnable;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.data.GameData;
import com.disepi.citrine.data.LevelData;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.data.custom.LobbyLevelData;
import com.disepi.citrine.entity.hub.*;
import com.disepi.citrine.events.*;
import com.disepi.citrine.items.*;
import com.disepi.citrine.utils.GameItemHandlerUtil;
import com.disepi.citrine.utils.Log;
import com.disepi.citrine.utils.NPC;
import com.disepi.citrine.utils.PluginStorage;

import java.util.List;

public class Main extends PluginBase {

    // Initialize events
    public void addEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public void registerEntity(Class npc) {
        Entity.registerCustomEntity(new CustomClassEntityProvider(npc));
    }

    // Register custom entities/items
    public void onLoad() {
        // Registering custom entities:
        registerEntity(NPC.class);
        registerEntity(SkyNPC.class);
        registerEntity(SoonNPC.class);
        registerEntity(HivePlusNPC.class);
        registerEntity(ReplayNPC.class);
        registerEntity(GoHubNPC.class);
        registerEntity(CosmeticLockerNPC.class);
        registerEntity(FlagNPC.class);
        registerEntity(SpawnVehicleNPC.class);
        registerEntity(BorderNPC.class);
        registerEntity(BoomboxNPC.class);
        registerEntity(PoisonBoomboxNPC.class);
        registerEntity(FrozenBoomboxNPC.class);
        registerEntity(KnockbackBoomboxNPC.class);
        registerEntity(LootboxNPC.class);

        // Registering custom items/blocks:
        //Block.registerCustomBlock(List.of(BlockBoombox.class, BlockPoisonBoombox.class, BlockFrozenBoombox.class, BlockKnockbackBoombox.class));
        Item.registerCustomItem(List.of(GameBoosterPropItem.class, GameCompassItem.class, GameCosmeticPickerItem.class, GameHubItem.class, GameMapSelectItem.class, SpellSwiftnessItem.class, SpellLifeItem.class));
    }

    // Upon plugin load
    @Override
    public void onEnable() {
        super.onEnable();

        // Set instances
        Log.setLogger(this.getLogger());
        Log.s(TextFormat.DARK_GREEN + "Allocated Citrine");
        PluginStorage.dataFolder = getDataFolder();
        Citrine.server = getServer();
        PluginStorage.plug = this;

        // Events
        addEvent(new PlayerSendPacket());
        addEvent(new EntityDamage());
        addEvent(new EntityDamageByEntity());
        addEvent(new PlayerOutgoingPacket());
        addEvent(new PlayerJoin());
        addEvent(new PlayerQuit());

        // Block events
        addEvent(new BlockBreak());
        addEvent(new BlockBurn());
        addEvent(new BlockFade());
        addEvent(new BlockFall());
        addEvent(new BlockFromTo());
        addEvent(new BlockGrow());
        addEvent(new BlockIgnite());
        addEvent(new BlockPlace());
        addEvent(new BlockSpread());
        addEvent(new BlockUpdate());
        addEvent(new LeavesDecay());
        addEvent(new LiquidFlow());

        // Misc
        addEvent(new PlayerDropItem());
        addEvent(new WeatherChange());
        addEvent(new CraftItem());
        addEvent(new LightningStrike());
        addEvent(new PlayerDeath());
        addEvent(new InventoryPickupItem());

        // Initialize players that were already in the server - maybe "/reload" was called
        for (Player plr : getServer().getOnlinePlayers().values())
            Citrine.addData(plr);

        // Initialize Citrine
        Citrine.init();

        // Tick event for Citrine
        new NukkitRunnable() {
            @Override
            public void run() {
                for(Player p : getServer().getOnlinePlayers().values()) {
                    Citrine.tickPlayer(p);
                }

                Citrine.handleGameTick();
            }
        }.runTaskTimer(this, 50, 1);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        for(GameData dat : Citrine.gameData.values()) {
            for(Player p : dat.players) {
                PlayerData pDat = Citrine.getData(p);
                if(pDat != null && pDat.border != null) pDat.border._despawnFrom(p);
            }
            Citrine.cleanUpGame(dat);
        }

        Log.s(TextFormat.RED + "Deallocated Citrine");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.isOp()) sender.setOp(false);

        if (command.getName().toLowerCase().contains("hub")) {
            GameItemHandlerUtil.handleUseHubItem(sender.asPlayer());
            return true;
        }

        if (command.getName().toLowerCase().contains("q")) {
            Citrine.findGameForPlayer((Player)sender);
            return true;
        }

        if (command.getName().toLowerCase().contains("forcestart")) {
            LevelData levelData = Citrine.getLevelData(((Player) sender).level);
            if(levelData instanceof LobbyLevelData) {
                LobbyLevelData lobbyLevelData = (LobbyLevelData) levelData;
                lobbyLevelData.assignedGame.defaultMin = 1;
                lobbyLevelData.assignedGame.minPlayers = 1;
                lobbyLevelData.assignedGame.waitTime = 5;
            }

            return true;
        }

        /*
        if (command.getName().toLowerCase().contains("chestfind")) {
            Log.s("Finding chests in area...");

            Level level = sender.asPlayer().getLevel();

            int x = 0;
            int y = 0;
            int z = 0;

            int radius = 256;
            int yRadius = 128;
            for (int x2 = -radius; x2 < radius; x2++) {
                for (int y2 = -yRadius; y2 < yRadius; y2++) {
                    for (int z2 = -radius; z2 < radius; z2++) {
                        Block block = level.getBlock((int) (x + x2), (int) (y + y2), (int) (z + z2), true);
                        if(block instanceof BlockChest) {
                            Log.s("new Vector3f(" + block.x + "f, " + block.y + "f, " + block.z + "f),");
                        }
                    }
                }
            }
            return true;
        }

        if (command.getName().toLowerCase().contains("enderfind")) {
            Log.s("Finding ender chests in area...");

            Level level = sender.asPlayer().getLevel();

            int x = 0;
            int y = 0;
            int z = 0;

            int radius = 256;
            int yRadius = 128;
            for (int x2 = -radius; x2 < radius; x2++) {
                for (int y2 = -yRadius; y2 < yRadius; y2++) {
                    for (int z2 = -radius; z2 < radius; z2++) {
                        Block block = level.getBlock((int) (x + x2), (int) (y + y2), (int) (z + z2), true);
                        if(block instanceof BlockEnderChest) {
                            Log.s("new Vector3f(" + block.x + "f, " + block.y + "f, " + block.z + "f),");
                        }
                    }
                }
            }
            return true;
        }*/
        return super.onCommand(sender, command, label, args);
    }
}
