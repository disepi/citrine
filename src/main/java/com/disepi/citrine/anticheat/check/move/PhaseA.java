package com.disepi.citrine.anticheat.check.move;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.MovePlayerPacket;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.PlayerData;

public class PhaseA extends Check {
    // sucks ass
    public PhaseA() {
        super("Phase",  "Woodpecker", "Exploit", 25);
    }

    public boolean check(MovePlayerPacket e, PlayerData d, Player p) {

        Block block = p.getLevel().getBlock(new Vector3(e.x, e.y, e.z));
        if(block.isSolid() && block.isFullBlock()) {
            this.violate(p, d, 1, true, "");
            return false;
        }

        reward(d, 0.1f);
        return true;
    }
}
