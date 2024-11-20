package com.disepi.citrine.items.loot.tables;

import cn.nukkit.block.*;
import cn.nukkit.item.*;
import com.disepi.citrine.items.BlockBoombox;
import com.disepi.citrine.items.BlockFrozenBoombox;
import com.disepi.citrine.items.BlockKnockbackBoombox;
import com.disepi.citrine.items.BlockPoisonBoombox;
import com.disepi.citrine.items.loot.TableEntry;

public class EnderChestTable extends BaseTable {

    public void setupTable() {
        this.addEntry(new TableEntry(new ItemSnowball(), 3, 0.25f));
        this.addEntry(new TableEntry(new BlockStone(5).asItemBlock(), 32, 0.5f));

        this.addEntry(new TableEntry(new ItemHelmetDiamond(), 1, 0.3f));
        this.addEntry(new TableEntry(new ItemChestplateDiamond(), 1, 0.3f));
        this.addEntry(new TableEntry(new ItemLeggingsDiamond(), 1, 0.3f));
        this.addEntry(new TableEntry(new ItemBootsDiamond(), 1, 0.3f));
        this.addEntry(new TableEntry(new ItemSwordDiamond(), 1, 0.5f));

        this.addEntry(new TableEntry(new ItemEnderPearl(), 1, 0.25f));
        this.addEntry(new TableEntry(new ItemBow(), 1, 0.25f));
        this.addEntry(new TableEntry(new ItemArrow(), 6, 0.25f));
        this.addEntry(new TableEntry(new ItemAppleGold(), 1, 0.25f));

        // boombox
        //this.addEntry(new TableEntry(new BlockBoombox().getHeldForm(), 1, 0.1f));
        //this.addEntry(new TableEntry(new BlockPoisonBoombox().getHeldForm(), 1, 0.1f));
        //this.addEntry(new TableEntry(new BlockFrozenBoombox().getHeldForm(), 1, 0.1f));
        //this.addEntry(new TableEntry(new BlockKnockbackBoombox().getHeldForm(), 1, 0.1f));
    }

}
