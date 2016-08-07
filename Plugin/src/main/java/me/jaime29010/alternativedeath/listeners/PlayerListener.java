package me.jaime29010.alternativedeath.listeners;

import me.jaime29010.alternativedeath.Main;
import org.bukkit.entity.Player;
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
        //TODO Trying to catch the moment before death of the player
        //TODO Maybe we can use packets to listen to a incoming packet with the health, save it until the player deaths and send it again to the server
        //TODO To the killer it should look like the player has died...
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
        }
    }
}