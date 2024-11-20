package com.disepi.citrine.items.loot.tables;

import cn.nukkit.block.BlockCobweb;
import cn.nukkit.block.BlockStone;
import cn.nukkit.item.*;
import com.disepi.citrine.items.BlockBoombox;
import com.disepi.citrine.items.BlockFrozenBoombox;
import com.disepi.citrine.items.BlockKnockbackBoombox;
import com.disepi.citrine.items.BlockPoisonBoombox;
import com.disepi.citrine.items.loot.TableEntry;

public class GoldOreTable extends BaseTable {

    public void setupTable() {
        //this.addEntry(new TableEntry(new BlockBoombox().getHeldForm(), 1, 0.1f));
        //this.addEntry(new TableEntry(new BlockPoisonBoombox().getHeldForm(), 1, 0.1f));
        //this.addEntry(new TableEntry(new BlockFrozenBoombox().getHeldForm(), 1, 0.1f));
        //this.addEntry(new TableEntry(new BlockKnockbackBoombox().getHeldForm(), 1, 0.1f));

        this.addEntry(new TableEntry(new ItemHelmetChain(), 1, 0.15f));
        this.addEntry(new TableEntry(new ItemChestplateChain(), 1, 0.15f));
        this.addEntry(new TableEntry(new ItemLeggingsChain(), 1, 0.15f));
        this.addEntry(new TableEntry(new ItemBootsChain(), 1, 0.15f));

        this.addEntry(new TableEntry(new ItemBow(), 1, 0.15f));
        this.addEntry(new TableEntry(new ItemSwordIron(), 1, 0.15f));

        this.addEntry(new TableEntry(new BlockCobweb().asItemBlock(), 1, 0.15f));
    }

}
