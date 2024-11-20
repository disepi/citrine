package com.disepi.citrine.items.loot;

import cn.nukkit.item.Item;

public class TableEntry {
    public Item item;
    public int amount;
    public float chance;

    public TableEntry(Item item, int amount, float chance) {
        this.item = item;
        this.amount = amount;
        this.chance = chance;
    }
}
