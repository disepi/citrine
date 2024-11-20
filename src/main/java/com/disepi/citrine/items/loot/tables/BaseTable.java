package com.disepi.citrine.items.loot.tables;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import com.disepi.citrine.items.loot.TableEntry;
import com.disepi.citrine.utils.Log;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class BaseTable {
    public CopyOnWriteArrayList<TableEntry> table = new CopyOnWriteArrayList<>();

    public void addEntry(TableEntry entry) {
        table.add(entry);
    }

    public void setupTable() {

    }

    public Item getRandomItem() {
        CopyOnWriteArrayList<TableEntry> randTable = this.table;
        Collections.shuffle(randTable);

        float random = new Random().nextFloat();
        for(TableEntry entry : randTable) {
            if(random < entry.chance) {
                Item it = entry.item;
                it.count = entry.amount;
                return it;
            }
        }

        TableEntry entry = randTable.get(0);
        Item it = entry.item;
        it.count = entry.amount;
        return it;
    }

    public CopyOnWriteArrayList<Item> getItems(int amount) {
        CopyOnWriteArrayList<Item> items = new CopyOnWriteArrayList<Item>();
        for(int i = 0; i < amount; i++) {
            Item newItem = getRandomItem();;
            while(items.contains(newItem))
                newItem = getRandomItem();
            items.add(newItem);
        }

        return items;
    }

    public BaseTable() {
        setupTable();
    }
}
