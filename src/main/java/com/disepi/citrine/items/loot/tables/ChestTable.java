package com.disepi.citrine.items.loot.tables;

import cn.nukkit.block.*;
import cn.nukkit.item.*;
import com.disepi.citrine.items.BlockBoombox;
import com.disepi.citrine.items.BlockFrozenBoombox;
import com.disepi.citrine.items.BlockKnockbackBoombox;
import com.disepi.citrine.items.BlockPoisonBoombox;
import com.disepi.citrine.items.loot.TableEntry;

public class ChestTable extends BaseTable {

    public void setupTable() {
        this.addEntry(new TableEntry(new ItemSwordIron(), 1, 0.5f));
        this.addEntry(new TableEntry(new ItemSnowball(), 2, 0.5f));
        this.addEntry(new TableEntry(new BlockStone(5).asItemBlock(), 32, 0.5f));

        this.addEntry(new TableEntry(new ItemHelmetChain(), 1, 0.4f));
        this.addEntry(new TableEntry(new ItemChestplateChain(), 1, 0.4f));
        this.addEntry(new TableEntry(new ItemLeggingsChain(), 1, 0.4f));
        this.addEntry(new TableEntry(new ItemBootsChain(), 1, 0.4f));

        this.addEntry(new TableEntry(new ItemHelmetIron(), 1, 0.15f));
        this.addEntry(new TableEntry(new ItemChestplateIron(), 1, 0.15f));
        this.addEntry(new TableEntry(new ItemLeggingsIron(), 1, 0.15f));
        this.addEntry(new TableEntry(new ItemBootsIron(), 1, 0.15f));

        this.addEntry(new TableEntry(new ItemEnderPearl(), 1, 0.25f));
        this.addEntry(new TableEntry(new ItemBow(), 1, 0.25f));
        this.addEntry(new TableEntry(new ItemArrow(), 2, 0.25f));

        // boombox
        //this.addEntry(new TableEntry(new BlockBoombox().getHeldForm(), 1, 0.1f));
        //this.addEntry(new TableEntry(new BlockPoisonBoombox().getHeldForm(), 1, 0.1f));
        //this.addEntry(new TableEntry(new BlockFrozenBoombox().getHeldForm(), 1, 0.1f));
        //this.addEntry(new TableEntry(new BlockKnockbackBoombox().getHeldForm(), 1, 0.1f));
    }

}
