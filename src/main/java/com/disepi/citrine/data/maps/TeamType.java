package com.disepi.citrine.data.maps;

import cn.nukkit.utils.TextFormat;

public enum TeamType {
    YELLOW("Yellow", TextFormat.YELLOW),
    LIME("Lime", TextFormat.GREEN),
    RED("Red", TextFormat.RED),
    BLUE("Blue", TextFormat.BLUE),
    GOLD("Gold", TextFormat.GOLD),
    MAGENTA("Magenta", TextFormat.LIGHT_PURPLE),
    AQUA("Aqua", TextFormat.AQUA),
    GRAY("Gray", TextFormat.GRAY),
    PURPLE("Purple", TextFormat.DARK_PURPLE),
    GREEN("Green", TextFormat.DARK_GREEN),
    DARK_GRAY("Dark Gray", TextFormat.DARK_GRAY),
    CYAN("Cyan", TextFormat.DARK_AQUA);

    public String name;
    public TextFormat format;

    TeamType(String name, TextFormat color) {
        this.name = name;
        this.format = color;
    }
}
