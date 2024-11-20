package com.disepi.citrine.items.loot.tables;

import cn.nukkit.item.*;
import com.disepi.citrine.items.loot.TableEntry;

public class DiamondOreTable extends BaseTable {

    public void setupTable() {
        this.addEntry(new TableEntry(new ItemHelmetDiamond(), 1, 0.5f));
        this.addEntry(new TableEntry(new ItemChestplateDiamond(), 1, 0.3f));
        this.addEntry(new TableEntry(new ItemLeggingsDiamond(), 1, 0.4f));
        this.addEntry(new TableEntry(new ItemBootsDiamond(), 1, 0.4f));
        this.addEntry(new TableEntry(new ItemSwordDiamond(), 1, 0.5f));
    }

}
