package com.disepi.citrine.anticheat.check.move;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.MovePlayerPacket;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.PlayerData;

public class BadPacketsA extends Check {
    public BadPacketsA() {
        super("Pitch",  "BP-219", "Exploit", 1);
    }

    public boolean check(MovePlayerPacket e, PlayerData d, Player p) {
        if(e.pitch >= 91.f || e.pitch <= -91.f) {
            this.violate(p, d, 1, true, "");
            return false;
        }
        return true;
    }
}
