package com.disepi.citrine.utils;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.Citrine;

import java.util.Map;

public class GameItemHandlerUtil {

    public static void breakBlock(Vector3 vector, boolean createParticles, Level level) {
        Block target = level.getBlock(vector);

        Item item = null;
        if (item == null)
            item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);


        if (createParticles) {
            Map<Integer, Player> players = level.getChunkPlayers((int) target.x >> 4, (int) target.z >> 4);
            level.addParticle(new DestroyBlockParticle(target.add(0.5), target), players.values());
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
    }

    public static void handleUseHubItem(Player player) {
        Citrine.getLevelData(player.level).handleLeave(player);
        Citrine.switchPlayerLevel(Citrine.getLevelData(Citrine.hubLevel), Citrine.getData(player));
        Citrine.getData(player).hasInfiniteHealth = true;
    }

    public static void handleUseSpellLife(Player p, Item item) {
        // alert if cant use
        if(p.getHealth() == p.getMaxHealth()) {
            p.dataPacket(SoundUtil.getSoundPacket(Sound.NOTE_BASS, 1, 1, p));
            p.sendMessage(TextFormat.BOLD + "" + TextFormat.RED + "» " + TextFormat.RESET + "" + TextFormat.GRAY + "You already have full health!");
            return;
        }

        // effects
        p.sendMessage("§a§l» §r§cYou cast §flife giving§c, healing you for §64 hearts");
        p.level.addSound(p.getPosition(), Sound.CONDUIT_ATTACK);

        // do abilities
        p.heal(2 * 4);

        // bullshit ahead
        for(int i = 0; i < p.getInventory().getSize(); i++) {
            Item it = p.getInventory().getItem(i);
            if(it.getNetworkId() == item.getNetworkId())
                p.getInventory().setItem(i, it.decrement(1));
        }
    }

    public static void handleUseSpellSwiftness(Player p, Item item) {
        // effects
        p.level.addSound(p.getPosition(), Sound.RANDOM_POTION_BREWED);
        p.sendMessage(TextFormat.BOLD + "" + TextFormat.LIGHT_PURPLE + "» " + TextFormat.RESET + "" + TextFormat.DARK_AQUA + "You cast " + TextFormat.AQUA + "Spell of Swiftness" + TextFormat.DARK_AQUA + ", granting you " + TextFormat.YELLOW + "Speed" + TextFormat.DARK_AQUA + " for " +TextFormat.RESET + "5 seconds");

        // do abilities
        Effect speed = Effect.getEffect(Effect.SPEED);
        speed.setDuration(5 * 20);
        speed.setAmbient(true);
        speed.setAmplifier(2);
        speed.setVisible(false);
        speed.setColor(0,0,255);
        p.addEffect(speed);

        // bullshit ahead
        for(int i = 0; i < p.getInventory().getSize(); i++) {
            Item it = p.getInventory().getItem(i);
            if(it.getNetworkId() == item.getNetworkId())
                p.getInventory().setItem(i, it.decrement(1));
        }
    }

    public static void handleUseCosmeticPicker(Player player) {
        // sound
        player.dataPacket(SoundUtil.getSoundPacket(Sound.RANDOM_POP, 1, 0.5f, player));

        // form
        FormWindowSimple window = new FormWindowSimple(TextFormat.BOLD + "" + TextFormat.DARK_GRAY + "Your SKY Locker", "Welcome to your locker! " + TextFormat.GRAY + "Choose a category to modify.");

        ElementButton veh = new ElementButton(TextFormat.BOLD + "" + TextFormat.DARK_PURPLE + "Spawn Vehicle " + TextFormat.RESET + "" + TextFormat.LIGHT_PURPLE + "[1/17]\n" + TextFormat.RESET + "" + TextFormat.DARK_GRAY + "Cloud");
        window.addButton(veh);

        // handler
        window.addHandler(new FormResponseHandler() {
            @Override
            public void handle(Player player, int i) {
                FormResponseSimple response = window.getResponse();
                if(response == null) return;
                int clicked = response.getClickedButtonId();
            }
        });

        player.showFormWindow(window);
    }
}
