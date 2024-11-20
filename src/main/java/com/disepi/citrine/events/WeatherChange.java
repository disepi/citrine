package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.WeatherChangeEvent;

public class WeatherChange implements Listener {
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        // weather should always stay clear
        if(event.toWeatherState() == true) event.setCancelled(true);
    }
}
