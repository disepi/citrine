package com.disepi.citrine.events;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.inventory.transaction.EnchantTransaction;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.RepairItemTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.*;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.*;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.anticheat.AnticheatMain;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.LevelData;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.items.BlockBoombox;
import com.disepi.citrine.items.BlockFrozenBoombox;
import com.disepi.citrine.items.BlockKnockbackBoombox;
import com.disepi.citrine.items.BlockPoisonBoombox;
import com.disepi.citrine.utils.BlockUtil;
import com.disepi.citrine.utils.Log;
import com.disepi.citrine.utils.MathUtil;
import com.disepi.citrine.utils.NPC;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static cn.nukkit.Player.CRAFTING_SMALL;
import static cn.nukkit.entity.Entity.DATA_FLAGS;
import static cn.nukkit.entity.Entity.DATA_FLAG_ACTION;
import static cn.nukkit.level.Level.BLOCK_UPDATE_TOUCH;
import static cn.nukkit.level.Level.DIMENSION_NETHER;
import static cn.nukkit.network.protocol.InteractPacket.ACTION_OPEN_INVENTORY;
import static cn.nukkit.network.protocol.InventoryTransactionPacket.*;
import static com.disepi.citrine.utils.MathUtil.DEG_RAD;

public class PlayerSendPacket implements Listener {

    private static Field getField(Class clazz, String fieldName)
            throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

    // copied function from nukkit
    public Item useBreakOn(Vector3 vector, BlockFace face, Item item, Player player, boolean createParticles, Level level) {
        if (player != null && player.getGamemode() > 2) {
            return null;
        }
        Block target = level.getBlock(vector);
        Item[] drops;
        int dropExp = target.getDropExp();

        if (item == null)
            item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);

        if (player != null) {
            if (player.getGamemode() == 2) {
                Tag tag = item.getNamedTagEntry("CanDestroy");
                boolean canBreak = false;
                if (tag instanceof ListTag) {
                    for (Tag v : ((ListTag<? extends Tag>) tag).getAll()) {
                        if (v instanceof StringTag) {
                            Item entry = Item.fromString(((StringTag) v).data);
                            if (entry.getId() > 0 && entry.getBlockUnsafe() != null && entry.getBlockUnsafe().getId() == target.getId()) {
                                canBreak = true;
                                break;
                            }
                        }
                    }
                }
                if (!canBreak) {
                    return null;
                }
            }

            double breakTime = target.getBreakTime(item, player);
            // level in
            // block
            // class

            if (player.isCreative() && breakTime > 0.15) {
                breakTime = 0.15;
            }

            if (player.hasEffect(Effect.SWIFTNESS)) {
                breakTime *= 1 - (0.2 * (player.getEffect(Effect.SWIFTNESS).getAmplifier() + 1));
            }

            if (player.hasEffect(Effect.MINING_FATIGUE)) {
                breakTime *= 1 - (0.3 * (player.getEffect(Effect.MINING_FATIGUE).getAmplifier() + 1));
            }

            Enchantment eff = item.getEnchantment(Enchantment.ID_EFFICIENCY);

            if (eff != null && eff.getLevel() > 0) {
                breakTime *= 1 - (0.3 * eff.getLevel());
            }

            breakTime -= 0.15;

            Item[] eventDrops = new Item[]{target.toItem()};

            BlockBreakEvent ev = new BlockBreakEvent(player, target, face, item, eventDrops, player.isCreative(),
                    (player.lastBreak + breakTime * 1000) > System.currentTimeMillis());

            level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled())
                return null;
            drops = ev.getDrops();
            dropExp = ev.getDropExp();
        }
        else {
            drops = new Item[]{target.toItem()};
        }

        Block above = level.getBlock(new Vector3(target.x, target.y + 1, target.z));
        if (above != null) {
            if (above.getId() == Item.FIRE) {
                level.setBlock(above, Block.get(BlockID.AIR), true);
            }
        }

        if (createParticles) {
            Map<Integer, Player> players = level.getChunkPlayers((int) target.x >> 4, (int) target.z >> 4);

            level.addParticle(new DestroyBlockParticle(target.add(0.5), target), players.values());

            if (player != null) {
                players.remove(player.getLoaderId());
            }
        }

        // Close BlockEntity before we check onBreak
        BlockEntity blockEntity = level.getBlockEntity(target);
        if (blockEntity != null) {
            blockEntity.onBreak();
            blockEntity.close();

            level.updateComparatorOutputLevel(target);
        }

        target.onBreak(item);
        item.useOn(target);
        if (item.isTool() && item.getDamage() >= item.getMaxDurability())
            item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);


        // drops
        for (Item drop : drops) {
            if (drop.getCount() > 0 && !(drop.getBlock() instanceof BlockChest) && drop.getBlock().isSolid()) {
                level.dropItem(vector.add(0.5, 0.5, 0.5), drop);
            }
        }

        PlayerData data = Citrine.getData(player);
        for (Check c : AnticheatMain.checks) {
            if (!c.checkBreak(data, player)) break;
        }

        return item;
    }

    // copied function from nukkit
    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz, Player player, boolean playSound, Level level) {
        Block target = level.getBlock(vector);
        Block block = target.getSide(face);

        if (block.y > 255 || block.y < 0)
            return null;

        if (block.y > 127 && level.getDimension() == DIMENSION_NETHER)
            return null;



        if (player != null) {
            PlayerInteractEvent ev = new PlayerInteractEvent(player, item, target, face,
                    target.getId() == 0 ? PlayerInteractEvent.Action.RIGHT_CLICK_AIR : PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK);

            if (player.getGamemode() > 2) {
                ev.setCancelled();
            }

            if(!player.isOp() && level.isInSpawnRadius(target))
                ev.setCancelled();

            level.getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                target.onUpdate(BLOCK_UPDATE_TOUCH);
                if ((!player.isSneaking() || player.getInventory().getItemInHand().isNull()) && target.canBeActivated() && target.onActivate(item, player)) {
                    if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                        item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
                    }
                    return item;
                }

                if (item.canBeActivated() && item.onActivate(level, player, block, target, face, fx, fy, fz)) {
                    if (item.getCount() <= 0) {
                        item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
                        return item;
                    }
                }
            } else {
                if(item.getId() == ItemID.BUCKET && ItemBucket.getDamageByTarget(item.getDamage()) == BlockID.WATER) {
                    player.getLevel().sendBlocks(new Player[]{player}, new Block[]{Block.get(Block.AIR, 0, target)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
                }
                return null;
            }

            if(item.getId() == ItemID.BUCKET && ItemBucket.getDamageByTarget(item.getDamage()) == BlockID.WATER) {
                player.getLevel().sendBlocks(new Player[] {player}, new Block[] {Block.get(Block.AIR, 0, target)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
            }
        } else if (target.canBeActivated() && target.onActivate(item, player)) {
            if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
            }
            return item;
        }
        Block hand;
        if (item.canBePlaced()) {
            hand = item.getBlock();
            hand.position(block);
        } else {
            return null;
        }

        if (!(block.canBeReplaced() || (hand.getId() == Item.SLAB && block.getId() == Item.SLAB))) {
            return null;
        }

        if (target.canBeReplaced()) {
            block = target;
            hand.position(block);
        }

        if (!hand.canPassThrough() && hand.getBoundingBox() != null) {
            Entity[] entities = level.getCollidingEntities(hand.getBoundingBox());
            int realCount = 0;
            for (Entity e : entities) {
                if (e instanceof EntityArrow || e instanceof EntityItem || (e instanceof Player && ((Player) e).isSpectator())) {
                    continue;
                }
                ++realCount;
            }

            if (player != null) {
                Vector3 diff = player.getNextPosition().subtract(player.getPosition());
                if (diff.lengthSquared() > 0.00001) {
                    AxisAlignedBB bb = player.getBoundingBox().getOffsetBoundingBox(diff.x, diff.y, diff.z);
                    if (hand.getBoundingBox().intersectsWith(bb)) {
                        ++realCount;
                    }
                }
            }

            if (realCount > 0) {
                return null; // Entity in block
            }
        }

        if (player != null) {
            BlockPlaceEvent event = new BlockPlaceEvent(player, hand, block, target, item);
            if (player.getGamemode() == 2) {
                Tag tag = item.getNamedTagEntry("CanPlaceOn");
                boolean canPlace = false;
                if (tag instanceof ListTag) {
                    for (Tag v : ((ListTag<Tag>) tag).getAll()) {
                        if (v instanceof StringTag) {
                            Item entry = Item.fromString(((StringTag) v).data);
                            if (entry.getId() > 0 && entry.getBlockUnsafe() != null && entry.getBlockUnsafe().getId() == target.getId()) {
                                canPlace = true;
                                break;
                            }
                        }
                    }
                }
                if (!canPlace) {
                    event.setCancelled();
                }
            }
            if(!player.isOp() && level.isInSpawnRadius(target)) {
                event.setCancelled();
            }

            level.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return null;
            }
        }

        // handle placing boomboxes
        // could be done way better
        int id = hand.getId();
        if(id == BlockUtil.boomboxId || id == BlockUtil.poisonBoomboxId || id == BlockUtil.frozenBoomboxId || id == BlockUtil.kbBoomboxId) {
            if (player != null && !player.isCreative())
                item.setCount(item.getCount() - 1);
            if (item.getCount() <= 0)
                item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);

            if(id == BlockUtil.boomboxId)
                BlockBoombox.handlePlace(item, block, target, face, fx, fy, fz, player);
            else if(id == BlockUtil.poisonBoomboxId)
                BlockPoisonBoombox.handlePlace(item, block, target, face, fx, fy, fz, player);
            else if(id == BlockUtil.frozenBoomboxId)
                BlockFrozenBoombox.handlePlace(item, block, target, face, fx, fy, fz, player);
            else if(id == BlockUtil.kbBoomboxId)
                BlockKnockbackBoombox.handlePlace(item, block, target, face, fx, fy, fz, player);


            player.level.sendBlocks(new Player[]{player}, new Block[]{target, block}, UpdateBlockPacket.FLAG_NOGRAPHIC);
            player.level.sendBlocks(new Player[]{player}, new Block[]{target.getLevelBlockAtLayer(1), block.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_NOGRAPHIC, 1);
            return item;
        }


        if (!hand.place(item, block, target, face, fx, fy, fz, player))
            return null;


        if (player != null) {
            if (!player.isCreative()) {
                item.setCount(item.getCount() - 1);
            }
        }

        if (playSound) {
            level.addLevelSoundEvent(hand, LevelSoundEventPacket.SOUND_PLACE, GlobalBlockPalette.getOrCreateRuntimeId(hand.getId(), hand.getDamage()));
        }

        if (item.getCount() <= 0) {
            item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        }
        return item;
    }


    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz, Player player, Level level) {
        return useItemOn(vector, item, face, fx, fy, fz, player, true, level);
    }

    Vector2f calcAngle(Vector3f src, Vector3f dst) {
        Vector3f diff = dst.subtract(src);

        diff.y = (float) (diff.y / (Math.sqrt(diff.x * diff.x + diff.y * diff.y + diff.z * diff.z)));
        return new Vector2f((float) (Math.asin(diff.y) * -DEG_RAD), (float) -Math.atan2(diff.x, diff.z) * DEG_RAD);
    }

    public static float absAngle(float a) {
        return (360 + (a % 360)) % 360;
    }

    public static float angleDelta(float a, float b) {
        float delta = Math.abs(absAngle(a) - absAngle(b));
        float sign = absAngle(a) > absAngle(b) || delta >= 180 ? -1 : 1;
        return (180 - Math.abs(delta - 180)) * sign;
    }

    // copied from nukkit
    public boolean attack_entity(Entity entity, EntityDamageEvent source) {
        //火焰保护附魔实现
        if (entity.hasEffect(Effect.FIRE_RESISTANCE)
                && (source.getCause() == EntityDamageEvent.DamageCause.FIRE
                || source.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || source.getCause() == EntityDamageEvent.DamageCause.LAVA)) {
            return false;
        }

        //水生生物免疫溺水
        if (entity instanceof EntitySwimmable swimmable && !swimmable.canDrown() && source.getCause() == EntityDamageEvent.DamageCause.DROWNING)
            return false;

        //飞行生物免疫摔伤
        if (entity instanceof EntityFlyable flyable && !flyable.hasFallingDamage() && source.getCause() == EntityDamageEvent.DamageCause.FALL)
            return false;

        //事件回调函数
        entity.getServer().getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return false;
        }

        // Make fire aspect to set the target in fire before dealing any damage so the target is in fire on death even if killed by the first hit
        if (source instanceof EntityDamageByEntityEvent) {
            Enchantment[] enchantments = ((EntityDamageByEntityEvent) source).getWeaponEnchantments();
            if (enchantments != null) {
                for (Enchantment enchantment : enchantments) {
                    enchantment.doAttack(((EntityDamageByEntityEvent) source).getDamager(), entity);
                }
            }
        }

        //吸收伤害实现
        if (entity.getAbsorption() > 0) {  // Damage Absorption
            entity.setAbsorption(Math.max(0, entity.getAbsorption() + source.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION)));
        }

        //修改最后一次伤害
        entity.setLastDamageCause(source);

        //计算血量
        float newHealth = entity.getHealth() - source.getFinalDamage();


        Entity attacker = source instanceof EntityDamageByEntityEvent ? ((EntityDamageByEntityEvent) source).getDamager() : null;

        entity.setHealth(newHealth);

        if (!(entity instanceof EntityArmorStand)) {
            entity.level.getVibrationManager().callVibrationEvent(new VibrationEvent(attacker, entity.clone(), VibrationType.ENTITY_DAMAGE));
        }

        return true;
    }

    // copied from nukkit
    public boolean attack_entityliving(EntityLiving entityLiving, EntityDamageEvent source) throws NoSuchFieldException, IllegalAccessException {
        if (entityLiving.noDamageTicks > 0 && source.getCause() != EntityDamageEvent.DamageCause.SUICIDE) {//ignore it if the cause is SUICIDE
            return false;
        } else if (entityLiving.getAttackTime() > 0 && !entityLiving.isAttackTimeByShieldKb()) {
            EntityDamageEvent lastCause = entityLiving.getLastDamageCause();
            if (lastCause != null && lastCause.getDamage() >= source.getDamage()) {
                return false;
            }
        }

        if (attack_entity(entityLiving, source)) {
            if (source instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
                if (source instanceof EntityDamageByChildEntityEvent) {
                    damager = ((EntityDamageByChildEntityEvent) source).getChild();
                }

                //Critical hit
                //if (damager instanceof Player && !damager.onGround) {
                //    AnimatePacket animate = new AnimatePacket();
                //    animate.action = AnimatePacket.Action.CRITICAL_HIT;
                //    animate.eid = entityLiving.getId();
                //
                //    entityLiving.getLevel().addChunkPacket(damager.getChunkX(), damager.getChunkZ(), animate);
                //    entityLiving.getLevel().addSound(entityLiving, Sound.GAME_PLAYER_ATTACK_STRONG);
                //    Log.s("fuck");
                //    source.setDamage(source.getDamage() * 1.5f);
                //}

                if (damager.isOnFire() && !(damager instanceof Player)) {
                    entityLiving.setOnFire(2 * entityLiving.getServer().getDifficulty());
                }

                double deltaX = entityLiving.x - damager.x;
                double deltaZ = entityLiving.z - damager.z;
                entityLiving.knockBack(damager, source.getDamage(), deltaX, deltaZ, ((EntityDamageByEntityEvent) source).getKnockBack());
            }

            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = entityLiving.getId();
            pk.event = entityLiving.getHealth() <= 0 ? EntityEventPacket.DEATH_ANIMATION : EntityEventPacket.HURT_ANIMATION;


            Class cls = entityLiving.getClass();
            Field _spawnedField = getField(cls, "hasSpawned");
            Field _attackTime = getField(cls, "attackTime");
            _attackTime.setAccessible(true);
            _spawnedField.setAccessible(true);

            Server.broadcastPacket(((Map<Integer, Player>)_spawnedField.get(entityLiving)).values(), pk);

            _attackTime.set(entityLiving, source.getAttackCooldown());
            //entityLiving.attackTimeByShieldKb = false;
            entityLiving.scheduleUpdate();

            return true;
        } else {
            return false;
        }
    }

    // copied from nukkit
    public boolean attack_player(Player player, EntityDamageEvent source) throws NoSuchFieldException, IllegalAccessException {
        if (!player.isAlive()) {
            return false;
        }

        if (player.isSpectator() || player.isCreative()) {
            //source.setCancelled();
            return false;
        } else if (player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT) && source.getCause() == EntityDamageEvent.DamageCause.FALL) {
            //source.setCancelled();
            return false;
        } else if (source.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (player.getLevel().getBlock(player.getPosition().floor().add(0.5, -1, 0.5)).getId() == Block.SLIME_BLOCK) {
                if (!player.isSneaking()) {
                    //source.setCancelled();
                    player.resetFallDistance();
                    return false;
                }
            }
        }

        if (attack_entityliving(player, source)) { //!source.isCancelled()
            if (player.getLastDamageCause() == source && player.spawned) {
                if (source instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
                    Entity damager = entityDamageByEntityEvent.getDamager();
                    if (damager instanceof Player) {
                        ((Player) damager).getFoodData().updateFoodExpLevel(0.1);
                    }
                }
                EntityEventPacket pk = new EntityEventPacket();
                pk.eid = player.getId();
                pk.event = EntityEventPacket.HURT_ANIMATION;
                player.dataPacket(pk);
            }
            return true;
        } else {
            return false;
        }
    }


    @EventHandler
    public void onPlayerPacketSend(DataPacketReceiveEvent event) throws NoSuchFieldException, IllegalAccessException {
        DataPacket dataPacket = event.getPacket();
        Player player = event.getPlayer();

        // Cancel all sounds
        if(dataPacket instanceof LevelSoundEventPacket) {
            event.setCancelled(true);
            return;
        }
        else if(dataPacket instanceof RequestChunkRadiusPacket) {
            PlayerData data = Citrine.getData(player);
            if(data != null) data.sendReady();
        }
        else if(dataPacket instanceof PlayerActionPacket) {
            PlayerActionPacket action = (PlayerActionPacket)dataPacket;
            if(action.action == PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK) {
                PlayerData data = Citrine.getData(player);
                data.stopSounds();

                if(data.pendingData != null) {
                    LevelData pending = data.pendingData;
                    player.switchLevel(pending.level);
                    data.stopSounds();

                    data.player.setPosition(pending.level.getSpawnLocation());
                    data.requestChunks(6, pending.level.getSpawnLocation());

                    pending.introduce(player, true);
                    pending.handleIntroduce(player);
                    data.pendingData = null;
                }
            }
        }
        else if(dataPacket instanceof InteractPacket) {
            InteractPacket interact = (InteractPacket) dataPacket;
            if(interact.action != ACTION_OPEN_INVENTORY) return;
            event.setCancelled(true);
            player.craftingType = CRAFTING_SMALL;
            PlayerData dat = Citrine.getData(player);
            if (!dat.inventoryOpen) {
                player.getInventory().open(player);
                dat.inventoryOpen = true;
            }
        }
        else if(dataPacket instanceof ContainerClosePacket) {
            ContainerClosePacket containerClosePacket = (ContainerClosePacket) dataPacket;
            if(containerClosePacket.windowId == ContainerIds.INVENTORY) {
                PlayerData dat = Citrine.getData(player);
                dat.inventoryOpen = false;
                player.craftingType = CRAFTING_SMALL;
                player.resetCraftingGridType();
                ContainerClosePacket pk = new ContainerClosePacket();
                pk.wasServerInitiated = false;
                pk.windowId = -1;
                player.dataPacket(pk);
            }
        }
        else if(dataPacket instanceof TextPacket) {
            event.setCancelled(true); // dont send the original text

            PlayerData data = Citrine.getData(player);
            if(data == null) return;

            // anti-spam
            if(System.currentTimeMillis() - data.lastChatTime < 250) {
                data.lastChatTime = System.currentTimeMillis();
                return;
            }

            TextPacket textPacket = (TextPacket) dataPacket;

            // format message
            if (textPacket.type != TextPacket.TYPE_CHAT) return;
            String chatMessage = textPacket.message;
            int breakLine = chatMessage.indexOf('\n');
            if (breakLine != -1)
                chatMessage = chatMessage.substring(0, breakLine);
            chatMessage = TextFormat.clean(chatMessage, true);

            String prefix = "";
            if(player.getExperienceLevel() >= 1) prefix += TextFormat.GRAY + "" + player.getExperienceLevel() + " " + TextFormat.RESET;
            prefix += TextFormat.GREEN + player.getName() + " " + TextFormat.DARK_GRAY + "[" + TextFormat.GREEN + "+" + TextFormat.DARK_GRAY + "] " + TextFormat.BOLD + "" + TextFormat.GRAY + "» " + TextFormat.RESET;

            // cant have longth length
            if(chatMessage.length() >= 64) return;

            data.lastChatTime = System.currentTimeMillis();
            Citrine.sendMessageToLevel(player.getLevel(), prefix + chatMessage);
        }
        else if(dataPacket instanceof AnimatePacket) {
            AnimatePacket anim = (AnimatePacket) dataPacket;
            if(anim.action == AnimatePacket.Action.SWING_ARM) {
                PlayerData data = Citrine.getData(player);
                if(data == null) return;
                data.lastSwingTime = System.currentTimeMillis();
            }
        }
        else if(dataPacket instanceof MovePlayerPacket) {
            MovePlayerPacket move = (MovePlayerPacket) dataPacket;
            PlayerData data = Citrine.getData(player);
            if(data == null) return;

            // accept teleports before checking
            if(data.targetTeleportPos != null) {
                data.lastX = data.targetTeleportPos.x;
                data.lastY = data.targetTeleportPos.y;
                data.lastZ = data.targetTeleportPos.z;
                if(MathUtil.distance(move.x, move.y, move.z, data.targetTeleportPos.x, data.targetTeleportPos.y, data.targetTeleportPos.z) > 1) {
                    event.setCancelled(true);

                    data.teleportWaitTicks++;
                    if(data.teleportWaitTicks > 30) {
                        player.setPositionAndRotation(data.targetTeleportPos.asVector3(), data.lastYaw, data.lastPitch, data.lastYaw);
                        player.sendPosition(data.targetTeleportPos.asVector3(), data.lastYaw, data.lastPitch, 2);
                        data.teleportWaitTicks = 0;
                    }
                    return;
                }
                else {
                    data.targetTeleportPos = null;
                }
            }

            boolean onGround = !(MathUtil.getNearestSolidBlock(new Vector3(move.x, move.y, move.z), player.level, 1.5f) instanceof BlockAir);
            data.onGround = onGround;
            if(onGround) {
                data.lastGroundPos = new Vector3f(move.x, move.y, move.z);
                data.touchedGroundSinceTp = true;
                data.onGroundTicks++;
                data.offGroundTicks = 0;
            }
            else {
                data.offGroundTicks++;
                data.onGroundTicks = 0;
            }

            if(data.lerpTicks > 0)
                data.lastGroundPos = new Vector3f(move.x, move.y, move.z);

            LevelData levelDat = Citrine.getLevelData(player.level);
            if(levelDat != null) levelDat.handleMove(player, move.x, move.y, move.z, move.pitch, move.yaw);
            for (Check c : AnticheatMain.checks) {
                if (!c.check(move, data, player)) break;
            }

            data.lerpTicks--;
            if(data.lerpTicks < 0) data.lerpTicks = 0;

            data.lastX = move.x;
            data.lastY = move.y;
            data.lastZ = move.z;
            data.lastPitch = move.pitch;
            data.lastYaw = move.yaw;
        }
        else if(dataPacket instanceof InventoryTransactionPacket) {
            InventoryTransactionPacket transactionPacket = (InventoryTransactionPacket) dataPacket;

            if(transactionPacket.transactionType == TYPE_USE_ITEM_ON_ENTITY) {
                // copied from nukkit
                UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) transactionPacket.transactionData;
                Entity target = player.level.getEntity(useItemOnEntityData.entityRuntimeId);
                if (target == null)
                {
                    PlayerData dat = Citrine.getData(player);
                    if(dat.refData != null) {
                        for(Player p : dat.refData.players) {
                            if(p.getId() == useItemOnEntityData.entityRuntimeId) {
                                target = p;
                                break;
                            }
                        }
                    }
                    if(target == null) return;
                }

                Item item = useItemOnEntityData.itemInHand;
                if(useItemOnEntityData.actionType == USE_ITEM_ON_ENTITY_ACTION_ATTACK) {
                    event.setCancelled(true);

                    if (!player.canInteract(target, player.isCreative() ? 8 : 5)) {
                        return;
                    } else if (target instanceof Player) {
                        if ((((Player) target).getGamemode() & 0x01) > 0) {
                            return;
                        }
                    }

                    PlayerData data = Citrine.getData(player);
                    for (Check c : AnticheatMain.checks) {
                        if (!c.checkAttack(data, player)) break;
                    }

                    Vector2f angles = calcAngle(player.getPosition().asVector3f(), target.getPosition().asVector3f());
                    if(Math.abs(angleDelta((float) player.pitch, angles.x)) >= 67.5f) return;
                    if(Math.abs(angleDelta((float) player.yaw, angles.y)) >= 67.5f) return;

                    Enchantment[] enchantments = item.getEnchantments();

                    float itemDamage = item.getAttackDamage();
                    for (Enchantment enchantment : enchantments)
                        itemDamage += enchantment.getDamageBonus(target);

                    Map<EntityDamageEvent.DamageModifier, Float> damage = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
                    damage.put(EntityDamageEvent.DamageModifier.BASE, itemDamage);

                    float knockBack = 0.3f;
                    Enchantment knockBackEnchantment = item.getEnchantment(Enchantment.ID_KNOCKBACK);
                    if (knockBackEnchantment != null)
                        knockBack += knockBackEnchantment.getLevel() * 0.1f;

                    EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(player, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage, knockBack, enchantments);
                    if (player.isSpectator()) entityDamageByEntityEvent.setCancelled();

                    if(target instanceof NPC) {

                    }
                    else {
                        if (target instanceof Player) {
                            attack_player((Player) target, entityDamageByEntityEvent);
                        } else if (target instanceof EntityLiving) {
                            attack_entityliving((EntityLiving) target, entityDamageByEntityEvent);
                        } else if (target instanceof Entity) {
                            attack_entity(target, entityDamageByEntityEvent);
                        }
                    }

                    //if (!target.attack(entityDamageByEntityEvent)) {
                    //    if (item.isTool() && player.isSurvival()) {
                    //        player.getInventory().sendContents(player);
                    //    }


                        //return;
                    //}

                    for (Enchantment enchantment : item.getEnchantments())
                        enchantment.doPostAttack(player, target);

                    //if (item.isTool() && (player.isSurvival() || player.isAdventure())) {
                    //    if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                    //        player.getInventory().setItemInHand(Item.get(0));
                    //    } else {
                    //        if (item.getId() == 0 || player.getInventory().getItemInHand().getId() == item.getId()) {
                    //            player.getInventory().setItemInHand(item);
                    //        } else {
                    //        }
                    //    }
                    //}

                    player.getInventory().getItemInHand().setDamage(0);

                    return;
                }

                return;
            }

            if(transactionPacket.transactionType != TYPE_USE_ITEM) return;
            event.setCancelled(true);
            PlayerInventory inventory = player.getInventory();
            UseItemData useItemData = (UseItemData) transactionPacket.transactionData;

            BlockVector3 blockVector = useItemData.blockPos;
            BlockFace face = useItemData.face;

            int type = useItemData.actionType;
            switch (type) {
                case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_BLOCK:
                    if(player.gamemode == 2) return;
                    player.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);
                    //if (player.canInteract(blockVector.add(0.5, 0.5, 0.5), 9999)) {
                        if (player.isCreative()) {
                            Item i = inventory.getItemInHand();
                            if (useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, player, player.level) != null)
                                return;
                        } else if (inventory.getItemInHand().equals(useItemData.itemInHand)) {
                            //Item i = inventory.getItemInHand();
                            Item i = useItemData.itemInHand;
                            Item oldItem = i.clone();
                            if ((i = useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, player, player.level)) != null) {
                                if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                    inventory.setItemInHand(i);
                                    inventory.sendHeldItem(player.getViewers().values());
                                }
                                return;
                            }
                        }
                    //}

                    //inventory.sendHeldItem(player);
                    if (blockVector.distanceSquared(player) > 10000)
                        return;
                    Block target = player.level.getBlock(blockVector.asVector3());
                    Block block = target.getSide(face);
                    player.level.sendBlocks(new Player[]{player}, new Block[]{target, block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                    return;
                case InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK:
                    if (!player.spawned || !player.isAlive() || player.gamemode == 2)
                        return;

                    player.resetCraftingGridType();
                    Item i = player.getInventory().getItemInHand();
                    Item oldItem = i.clone();

                    if (player.canInteract(blockVector.add(0.5, 0.5, 0.5), player.isCreative() ? 13 : 7) && (i = useBreakOn(blockVector.asVector3(), face, i, player, true, player.level)) != null) {
                        if (player.isSurvival()) {
                            if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                inventory.setItemInHand(i);
                                inventory.sendHeldItem(player.getViewers().values());
                            }
                        }

                        return;
                    }

                    inventory.sendContents(player);
                    target = player.level.getBlock(blockVector.asVector3());
                    BlockEntity blockEntity = player.level.getBlockEntity(blockVector.asVector3());
                    player.level.sendBlocks(new Player[]{player}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                    inventory.sendHeldItem(player);
                    if (blockEntity instanceof BlockEntitySpawnable)
                        ((BlockEntitySpawnable) blockEntity).spawnTo(player);
                    return;
                case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR:
                    event.setCancelled(false);
                    return;
                default:
                    event.setCancelled(false);
                    break;
            }
        }
        else if(dataPacket instanceof PlayerAuthInputPacket) {
            PlayerAuthInputPacket move = (PlayerAuthInputPacket) dataPacket;
            event.setCancelled(true);
            //Vector3f pos = move.getPosition();
            //Citrine.getLevelData(player.level).handleMove(player, pos.x, pos.y, pos.z, move.getPitch(), move.getYaw());
        }
    }
}
