package me.jaime29010.alternativedeath.utils;

import net.md_5.bungee.api.ChatColor;

public class PluginUtils {
    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
