package com.disepi.citrine.items;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.block.customblock.CustomBlockDefinition;
import cn.nukkit.block.customblock.data.BlockCreativeCategory;
import cn.nukkit.block.customblock.data.Materials;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.utils.TextFormat;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.entity.hub.BoomboxNPC;
import com.disepi.citrine.entity.hub.PoisonBoomboxNPC;
import com.disepi.citrine.utils.Log;

public class BlockPoisonBoombox extends Block implements CustomBlock {

    @Override
    public CustomBlockDefinition getDefinition() {
        return CustomBlockDefinition
                .builder(
                        this,
                        Materials
                                .builder()
                                .any(Materials.RenderMethod.OPAQUE, "hivecommon:boombox_poison_side")
                                .up(Materials.RenderMethod.OPAQUE,  "hivecommon:boombox_poison_up")
                                .down(Materials.RenderMethod.OPAQUE,  "hivecommon:boombox_poison_up"),
                        BlockCreativeCategory.NONE)
                .breakTime(60)
                .build();
    }



    public ItemBlock getHeldForm() {
        ItemBlock _this = this.asItemBlock();
        _this.setCustomName(TextFormat.RESET + "" + this.getName());
        return _this;
    }

    @Override
    public String getName() {
        return TextFormat.GREEN + "Poison Boom Box";
    }

    @Override
    public String getNamespaceId() {
        return "hive:poison_boombox";
    }

    @Override
    public int getId() {
        return CustomBlock.super.getId();
    }

    public static void handlePlace(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block correctBlock = block.getLevelBlockAtLayer(1);
        new PoisonBoomboxNPC(target.level, (float) correctBlock.x + 0.5f, (float) correctBlock.y, (float) correctBlock.z + 0.5f, 3 * 20, 6, 1, Citrine.getData(player).refData, player).spawnToAll();
    }
}
