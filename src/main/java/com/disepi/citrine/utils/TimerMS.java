package com.disepi.citrine.utils;

public class TimerMS {
    public long lastMs = System.currentTimeMillis();

    public void reset()
    {
        lastMs = System.currentTimeMillis();
    }

    public long getTimeElapsed() {
        return System.currentTimeMillis() - lastMs;
    }

    public boolean hasTimeElapsed(long time, boolean reset)
    {
        if (System.currentTimeMillis() - lastMs > time)
        {
            if (reset)
                reset();
            return true;
        }
        return false;
    }
}
