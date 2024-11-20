package com.disepi.citrine.data;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.SetDisplayObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.custom.GameLevelData;
import com.disepi.citrine.data.custom.LobbyLevelData;
import com.disepi.citrine.data.maps.MapSaveData;
import com.disepi.citrine.data.maps.MapType;
import com.disepi.citrine.data.maps.TeamType;
import com.disepi.citrine.entity.hub.BorderNPC;
import com.disepi.citrine.entity.hub.SpawnVehicleNPC;
import com.disepi.citrine.utils.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static cn.nukkit.scoreboard.data.ScorerType.FAKE;

public class GameData {
    public Integer gameId;
    public boolean started;
    public int maxPlayers;
    public int minPlayers;
    public MapType selectedMap;
    public Level currentLevel;
    public Level lobbyLevel;
    public Level gameLevel;
    public String lobbyLevelName;
    public String gameLevelName;
    public MapSaveData saveData;
    public int waitTime = 30;
    public int defaultMin = 2;

    public CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<Player>();
    public boolean scheduledDelete = false;
    public boolean doneWarmup = false;
    public boolean sentStartMessage = false;
    public boolean bordersDown = false;

    public TimerMS startTimer = new TimerMS();
    public TimerMS warmupTimer = new TimerMS();
    public TimerMS borderTimer = new TimerMS();
    public long lastSecondDisplay;

    public boolean calculatedMutators = false;
    public boolean hasRedstoneMutator = false;
    public boolean hasHitSpeedMutator = false;

    public int getPlayerCount() {
        return this.players.size();
    }

    public String getJoinMessage(Player p) {
        return "§a§l» §r§7" + p.getName() + " joined. §8[" + this.getPlayerCount() + "/" + this.maxPlayers + "]";
    }

    public void addJoinMessage(Player p) {
        Citrine.sendMessageToLevel(this.lobbyLevel, this.getJoinMessage(p));
    }

    public void handleAdd(Player p, PlayerData d) {
        this.players.add(p);
        d.refData = this;
    }

    public void handleLeave(Player p, PlayerData d) {
        this.players.remove(p);
        if(d.border != null) d.border._despawnFrom(p);
        d.refData = null;
    }

    public String getTeamsString() {
        String base = "§f";
        for(Player p : this.players) {
            PlayerData pDat = Citrine.getData(p);
            if(!pDat.isSpectating)
                base += ChatSymbol.getTeamColor(Citrine.getData(p).team);
        }

        return base;
    }

    public void sendScoreboard(Player p) {
        if(!this.started || !this.doneWarmup || !sentStartMessage) return;

        SetScorePacket sc = ScoreboardUtil.getScorePacket();
        ArrayList<SetScorePacket.ScoreInfo> si = new ArrayList<>();

        // add scores
        if(!bordersDown) {
            String countDisp = "";
            if(lastSecondDisplay <= 9) countDisp = "0" + lastSecondDisplay;
            else countDisp = "" + lastSecondDisplay;

            si.add(ScoreboardUtil.getScoreInfo(-1, ChatSymbol.lengthSymbol + " §bWarmup §f00:" + countDisp, 0));
            si.add(ScoreboardUtil.getScoreInfo(-2, getTeamsString(), 1));
        }
        else {
            si.add(ScoreboardUtil.getScoreInfo(-1, ChatSymbol.lengthSymbol + " §f09:00", 0));
            si.add(ScoreboardUtil.getScoreInfo(-2, ChatSymbol.killSymbol + " 0", 1));
            si.add(ScoreboardUtil.getScoreInfo(-3, ChatSymbol.mysterySymbol + " 02:00", 2));
            si.add(ScoreboardUtil.getScoreInfo(-4, getTeamsString(), 3));

            PlayerData dat = Citrine.getData(p);
            if(dat != null && !dat.isSpectating)
                si.add(ScoreboardUtil.getScoreInfo(-5, ChatSymbol.getTeamColor(dat.team) + " §§" + dat.team.format + dat.team.name + " Team", 4));
        }

        sc.infos = si;

        // show scoreboard
        p.dataPacket(ScoreboardUtil.getScoreboardRemovePacket());
        p.dataPacket(ScoreboardUtil.getScoreboardShowPacket());
        p.dataPacket(sc);
    }

    public void playSoundToPlayers(Sound sound, float volume, float pitch) {
        for (Player p : this.players)
            p.dataPacket(SoundUtil.getSoundPacket(sound, volume, pitch, p));
    }

    public void calculateMutators() {
        if(new Random().nextFloat() <= Citrine.redstoneMutatorChance) this.hasRedstoneMutator = true;
        if(new Random().nextFloat() <= Citrine.hitSpeedMutatorChance) this.hasHitSpeedMutator = true;
    }


    boolean didEffects = false;

    public void handleUpdate() {
        int alive = 0;
        Player aliveP = null;

        CopyOnWriteArrayList<Player> _players = new CopyOnWriteArrayList<Player>();
        for(Player p : this.players) {
            if(p != null) {
                PlayerData dat = Citrine.getData(p);
                if(dat != null) {
                    if(dat.isSpectating) {
                        dat.canTakeDamage = false;
                        dat.canDoDamage = false;
                        Effect invis = Effect.getEffect(Effect.INVISIBILITY);
                        invis.setDuration(999);
                        invis.setAmbient(false);
                        invis.setAmplifier(255);
                        invis.setVisible(false);
                        invis.setColor(0,0,0);
                        p.addEffect(invis);
                    }
                    else {
                        aliveP = p;
                        alive++;
                    }
                    _players.add(p);
                }
            }
        }
        this.players = _players;

        // clear if empty
        //if(getPlayerCount() == 0) {
        //    scheduledDelete = true;
        //    return;
        //}

        if(minPlayers != 1 && alive <= 1 && this.bordersDown && aliveP != null) {
            if(didEffects) return;
            String title = TextFormat.RED + "" + TextFormat.BOLD + "Game OVER!";
            String subtitle = TextFormat.BOLD + "" + aliveP.getName() + " won";
            String text = TextFormat.RED + "" + TextFormat.BOLD + "Game OVER!\n" + TextFormat.GREEN + "" + aliveP.getName() + " won.";

            for(Player p : this.players) {
                p.sendTitle(title, subtitle);
                p.sendMessage(text);
                GameItemHandlerUtil.handleUseHubItem(p);
                p.getOffhandInventory().setItem(0, new ItemTotem());
                EntityEventPacket pk = new EntityEventPacket();
                pk.eid = p.getId();
                pk.event = EntityEventPacket.CONSUME_TOTEM;
                p.dataPacket(pk);
                p.getOffhandInventory().clear(0);
            }

            scheduledDelete = true;
            didEffects = true;
            return;
        }

        // anti item spam
        if(this.currentLevel.getEntities().length > 512) {
            for(Entity ent : this.currentLevel.getEntities()) {
                if(ent instanceof EntityItem) {
                    ent.kill();
                    ent.despawnFromAll();
                }
            }
        }

        if(!this.started) {
            int playerAmt = players.size();
            int needed = this.minPlayers - playerAmt;
            boolean needsPlayers = needed >= 1;
            boolean isLoadingWorld = false;

            String titleBar = "";
            if(needsPlayers) {
                titleBar = needed + " " + TextFormat.YELLOW + "players needed to start...";
                startTimer.reset();
            } else {
                long secondsLeft = (((waitTime*1000) - startTimer.getTimeElapsed()) / 1000);
                if(secondsLeft < 0) secondsLeft = 0;

                titleBar = TextFormat.GREEN + "Starting game in " + TextFormat.BOLD;
                if(secondsLeft <= 5) {

                    if(this.selectedMap == null) {
                        Map<MapType, Integer> votes = new HashMap<>();
                        for(MapType type : MapType.values()) votes.put(type, 0);

                        for(Player p : this.players) {
                            PlayerData dat = Citrine.getData(p);
                            if(dat == null) continue;
                            if(dat.votedMap != null) {
                                Integer votedAmt = votes.get(dat.votedMap);
                                votes.put(dat.votedMap, votedAmt + 1);
                            }
                        }

                        Integer max = Collections.max(votes.values());
                        MapType selectedMap = null;
                        for (Map.Entry<MapType, Integer> entry : votes.entrySet()) {
                            if (entry.getValue() == max) {
                                selectedMap = entry.getKey();
                                break;
                            }
                        }

                        Citrine.sendMessageToLevel(this.lobbyLevel, "§b§l» §r§a§lVoting has ended!");
                        Citrine.sendMessageToLevel(this.lobbyLevel, "§b§l» §r§e" + selectedMap.name +" §7won with §f" + max + " §7votes!");
                        this.selectedMap = selectedMap;

                        if(!this.calculatedMutators) {
                            calculateMutators();
                            this.calculatedMutators = true;

                            if(this.hasRedstoneMutator) Citrine.sendMessageToLevel(this.lobbyLevel, "§b§l» §r§a§lRedstone mutator is active!");
                            if(this.hasHitSpeedMutator) Citrine.sendMessageToLevel(this.lobbyLevel, "§b§l» §r§a§lHit speed mutator is active!");
                        }
                    }

                    titleBar += TextFormat.RED;
                    if(secondsLeft != lastSecondDisplay && secondsLeft != 0) {
                        playSoundToPlayers(Sound.RANDOM_CLICK, 1, 1);
                    }
                }
                titleBar += secondsLeft;

                lastSecondDisplay = secondsLeft;

                if(secondsLeft == 0) {
                    titleBar = TextFormat.GREEN + "Preparing world...";
                    isLoadingWorld = true;
                }
            }

            for (Player p : players)
                p.sendActionBar(titleBar, 0, 9, 9);

            if(isLoadingWorld && this.gameLevel == null) {
                for(Player player : this.players) {
                    Citrine.getData(player).immobileTicks = 999;
                }

                // load map
                String mapSelectedName = this.selectedMap.name.toLowerCase();
                this.gameLevelName = this.gameId + "-" + mapSelectedName;
                Citrine.cloneLevel(mapSelectedName, gameLevelName);
                Citrine.server.loadLevel(gameLevelName);
                this.gameLevel = Citrine.server.getLevelByName(gameLevelName);

                // get map save data
                this.saveData = Citrine.mapSaveData.get(this.selectedMap);

                // setup data
                GameLevelData gameLevelDat = new GameLevelData(gameLevel);
                gameLevelDat.assignedGame = this;
                Citrine.levelData.put(gameLevel, gameLevelDat);
                gameLevelDat.setupScene();
                gameLevelDat.canEdit = false;
                gameLevelDat.spawnYaw = 0;

                // set teams
                TeamType[] types = TeamType.values();
                List<TeamType> list = Arrays.asList(types);
                Collections.shuffle(list);
                list.toArray(types);

                for(int i = 0; i < this.players.size(); i++) {
                    PlayerData p = Citrine.getData(this.players.get(i));
                    p.team = types[i];
                }

                for(Player player : this.players)
                    Citrine.switchPlayerLevel(gameLevelDat, Citrine.getData(player));

                this.started = true;
                this.warmupTimer.reset();
                this.lastSecondDisplay = 5;
                this.currentLevel = gameLevel;
            }
        }
        else {
            if(!doneWarmup) {
                long secondsLeft = (((10 * 1000) - warmupTimer.getTimeElapsed()) / 1000);
                if (secondsLeft < 0) secondsLeft = 0;

                if (secondsLeft != 0) {
                    TextFormat barColor = TextFormat.YELLOW;
                    if (secondsLeft <= 3) {
                        barColor = TextFormat.RED;
                        if (secondsLeft != lastSecondDisplay)
                            playSoundToPlayers(Sound.RANDOM_TOAST, 1, 1.5f);
                        lastSecondDisplay = secondsLeft;
                    }

                    String startString = "§eGame Start " + TextFormat.BOLD + "» " + TextFormat.RESET + "" + barColor;
                    boolean switched = false;
                    for (int i = 0; i < 10; i++) {
                        if (i > secondsLeft - 1 && !switched) {
                            startString += TextFormat.DARK_GRAY;
                            switched = true;
                        }
                        startString += "▌";
                    }
                    startString += TextFormat.RESET + " " + secondsLeft;

                    for (Player p : players)
                        p.sendActionBar(startString, 0, 9, 9);
                }
                else doneWarmup = true;
                return;
            }

            if(!sentStartMessage) {
                // send spawn message
                Citrine.sendMessageToLevel(this.currentLevel, "§d§l» §r§b§lSkyWars: §e§lLucky Ores");
                Citrine.sendMessageToLevel(this.currentLevel, "§7§l» §rGather resources by mining lucky ores and claiming the mystery chest. Last team standing wins!");
                for (Player p : players) {
                    PlayerData dat = Citrine.getData(p);
                    //dat.canTakeDamage = true;
                    dat.canDoDamage = true;
                    dat.hasInfiniteHealth = false;
                    dat.immobileTicks = 0;
                    TextFormat teamColor = dat.team.format;
                    p.sendMessage("§§" + teamColor + "§l» §rYou are on the §§" + teamColor + dat.team.name + " Team!");
                    p.setGamemode(0);

                    PlayerInventory inv = p.getInventory();
                    if(inv != null) {
                        inv.setItem(0, new ItemSwordStone());
                        inv.setItem(1, new ItemPickaxeIron());
                        inv.setItem(2, new ItemAxeStone());
                    }
                }

                // get rid of spawn vehicles
                for(Entity ent : this.gameLevel.getEntities()) {
                    if(ent instanceof SpawnVehicleNPC) {
                        ent.despawnFromAll();
                        ent.kill();
                    }
                }

                GameLevelData gameLevel = (GameLevelData) Citrine.getLevelData(this.gameLevel);
                gameLevel.canEdit = true;
                gameLevel.canBreak = true;
                gameLevel.canDrop = true;
                gameLevel.canIgnite = true;
                gameLevel.canModify = true;
                gameLevel.canPlace = true;
                sentStartMessage = true;
                bordersDown = false;
                borderTimer.reset();
            }

            if(!bordersDown) {
                long secondsLeft = (((30 * 1000) - borderTimer.getTimeElapsed()) / 1000);
                if (secondsLeft <= 0) secondsLeft = 0;

                if (secondsLeft != lastSecondDisplay) {
                    if (secondsLeft == 15 || secondsLeft == 10 || secondsLeft == 5) {
                        if(secondsLeft == 15) for(Player p : this.players) Citrine.getData(p).canTakeDamage = true;
                        Citrine.sendMessageToLevel(this.currentLevel, "§6§l» §r§f" + secondsLeft + " §7seconds left of warmup!");
                        playSoundToPlayers(Sound.BUBBLE_POP, 1, 1);
                    }

                    if(secondsLeft == 0) {
                        for(Player p : this.players) {
                            PlayerData dat = Citrine.getData(p);
                            dat.canTakeDamage = true;
                            dat.canDoDamage = true;

                            BorderNPC npc = Citrine.getData(p).border;
                            if(npc == null) continue;
                            npc._despawnFrom(p);
                            npc.kill();
                        }

                        Citrine.sendMessageToLevel(this.currentLevel, "§a§l» §r§7Warmup over and borders removed! §eFight!");
                        playSoundToPlayers(Sound.ITEM_TRIDENT_RETURN, 1, 1);
                        bordersDown = true;
                    }
                }

                for(Player p : players) sendScoreboard(p);
                lastSecondDisplay = secondsLeft;
                return;
            }


            for(Player p : players) sendScoreboard(p);
        }
    }

    public GameData(Integer gameId, Level waitingLobby) {
        this.gameId = gameId;
        this.started = false;
        this.maxPlayers = 12;
        this.minPlayers = defaultMin;
        this.currentLevel = waitingLobby;
        this.lobbyLevel = waitingLobby;
        this.selectedMap = null;

        this.lobbyLevelName = waitingLobby.getName();
        this.gameLevelName = "";
    }
}
