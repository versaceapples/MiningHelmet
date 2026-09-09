package com.yourserver.mininghelmet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Cleans up per-player equip-tracking state when a player disconnects, so
 * it doesn't linger forever in MiningHelmet's currentlyWearing set.
 *
 * Also blocks the Mining Helmet from being upgraded via a smithing table
 * (e.g. into a netherite helmet) — the enchants, Mending, and infinite
 * effect are meant to stay tied to this exact diamond helmet.
 *
 * Register in onEnable():
 *   getServer().getPluginManager().registerEvents(new MiningHelmetListener(), this);
 */
public class MiningHelmetListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        MiningHelmet.currentlyWearing.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        if (MiningHelmet.isMiningHelmet(event.getInventory().getInputEquipment())) {
            event.setResult(null);
        }
    }
}

