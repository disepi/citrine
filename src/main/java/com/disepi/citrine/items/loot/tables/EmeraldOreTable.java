package com.disepi.citrine.items.loot.tables;

import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import com.disepi.citrine.items.loot.TableEntry;

public class EmeraldOreTable extends BaseTable {

    public void setupTable() {
        this.addEntry(new TableEntry(new ItemHelmetDiamond(), 1, 0.1f));
        this.addEntry(new TableEntry(new ItemChestplateDiamond(), 1, 0.1f));
        this.addEntry(new TableEntry(new ItemLeggingsDiamond(), 1, 0.1f));
        this.addEntry(new TableEntry(new ItemBootsDiamond(), 1, 0.1f));


        Enchantment sharp = Enchantment.getEnchantment(9);
        sharp.setLevel(1);

        ItemSwordDiamond diamondSharp = new ItemSwordDiamond();
        ItemSwordIron ironSharp = new ItemSwordIron();
        diamondSharp.addEnchantment(sharp);
        ironSharp.addEnchantment(sharp);

        Enchantment prot = Enchantment.getEnchantment(0);
        ItemHelmetIron helmProt = new ItemHelmetIron();
        ItemChestplateIron chestProt = new ItemChestplateIron();
        ItemLeggingsIron legProt = new ItemLeggingsIron();
        ItemBootsIron bootProt = new ItemBootsIron();

        helmProt.addEnchantment(prot);
        chestProt.addEnchantment(prot);
        legProt.addEnchantment(prot);
        bootProt.addEnchantment(prot);

        this.addEntry(new TableEntry(helmProt, 1, 0.1f));
        this.addEntry(new TableEntry(chestProt, 1, 0.1f));
        this.addEntry(new TableEntry(legProt, 1, 0.1f));
        this.addEntry(new TableEntry(bootProt, 1, 0.1f));

        this.addEntry(new TableEntry(diamondSharp, 1, 0.2f));
        this.addEntry(new TableEntry(ironSharp, 1, 0.3f));
    }

}
