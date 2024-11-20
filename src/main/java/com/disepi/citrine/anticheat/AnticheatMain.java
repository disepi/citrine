package com.disepi.citrine.anticheat;

import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.anticheat.check.move.*;

import java.util.concurrent.CopyOnWriteArrayList;

public class AnticheatMain {
    public static CopyOnWriteArrayList<Check> checks = new CopyOnWriteArrayList<Check>();
    public static int checkAmount = 1;

    public static void initializeChecks() {
        checks.add(new SpeedA());
        checks.add(new SpeedB());
        checks.add(new SpeedC());
        checks.add(new FlyA());
        checks.add(new FlyB());
        checks.add(new FlyC());
        checks.add(new FastBreakA());
        //checks.add(new PhaseA());
        checks.add(new CombatA());
        checks.add(new BadPacketsA());
        //checks.add(new NoSwingA());
    }
}
