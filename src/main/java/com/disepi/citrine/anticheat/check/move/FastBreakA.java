package com.disepi.citrine.anticheat.check.move;

import cn.nukkit.Player;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.PlayerData;

public class FastBreakA extends Check {
    public FastBreakA() {
        super("FastBreak",  "SugarRush","Exploit",3);
    }

    public boolean checkBreak(PlayerData d, Player p) {
        if(p.getGamemode() == 1) return true;
        long breakDelta = (System.currentTimeMillis() - d.lastBreakTime);
        if(breakDelta < 46) {
            this.violate(p, d, 1, true, "breakDelta=" + breakDelta);
            return false;
        }
        d.lastBreakTime = System.currentTimeMillis();
        return true;
    }
}
