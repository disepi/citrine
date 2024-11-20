package com.disepi.citrine.utils;

import com.disepi.citrine.data.maps.TeamType;

public class ChatSymbol {
    public static String levelSymbol = "\uE18C";
    public static String lengthSymbol = "\uE182";
    public static String mysterySymbol = "\uE187";
    public static String killSymbol = "\uE184";

    // should use a dictionary   never fixed
    public static String getTeamColor(TeamType type) {
        switch(type) {
            case RED -> {
                return "\uE1A0";
            }
            case GOLD -> {
                return "\uE1A6";
            }
            case MAGENTA -> {
                return "\uE1A4";
            }
            case AQUA -> {
                return "\uE1A5";
            }
            case GREEN -> {
                return "\uE1A9";
            }
            case GRAY -> {
                return "\uE1A7";
            }
            case BLUE -> {
                return "\uE1A1";
            }
            case LIME -> {
                return "\uE1A3";
            }
            case YELLOW -> {
                return "\uE1A2";
            }
            case PURPLE -> {
                return "\uE1A8";
            }
            case DARK_GRAY -> {
                return "\uE1AA";
            }
            case CYAN -> {
                return "\uE1AB";
            }
        }
        return "";
    }
}
