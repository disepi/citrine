package com.disepi.citrine.items.loot.tables;

import cn.nukkit.block.BlockStone;
import cn.nukkit.item.*;
import com.disepi.citrine.items.SpellLifeItem;
import com.disepi.citrine.items.SpellSwiftnessItem;
import com.disepi.citrine.items.loot.TableEntry;

public class SpellOreTable extends BaseTable {

    public void setupTable() {
        this.addEntry(new TableEntry(new ItemBone(), 1, 0.7f)); // placeholder
        this.addEntry(new TableEntry(new SpellSwiftnessItem(), 1, 0.1f));
        this.addEntry(new TableEntry(new SpellLifeItem(), 1, 0.1f));
    }

}
