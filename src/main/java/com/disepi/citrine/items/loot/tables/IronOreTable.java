package com.disepi.citrine.items.loot.tables;

import cn.nukkit.block.BlockStone;
import cn.nukkit.item.*;
import com.disepi.citrine.items.loot.TableEntry;

public class IronOreTable extends BaseTable {

    public void setupTable() {
        this.addEntry(new TableEntry(new ItemArrow(), 1, 0.5f));
        this.addEntry(new TableEntry(new ItemArrow(), 2, 0.5f));
        this.addEntry(new TableEntry(new BlockStone(5).asItemBlock(), 32, 0.5f));
        this.addEntry(new TableEntry(new BlockStone(5).asItemBlock(), 16, 0.5f));
    }

}
