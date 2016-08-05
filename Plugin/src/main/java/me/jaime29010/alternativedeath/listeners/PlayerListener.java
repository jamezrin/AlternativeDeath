package me.jaime29010.alternativedeath.listeners;

import me.jaime29010.alternativedeath.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerListener implements Listener {
    private final Main main;
    public PlayerListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

    }
}