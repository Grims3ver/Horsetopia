package ca.stepwisegamestudios.aarden.listeners;

import ca.stepwisegamestudios.aarden.traits.TraitRegistry;
import ca.stepwisegamestudios.aarden.utils.SupportedMountType;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TraitCleanupListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Entity vehicle = player.getVehicle();

        if (!(vehicle instanceof AbstractHorse mount)) return;
        if (!SupportedMountType.isSupported(mount)) return;

        TraitRegistry.revertDashBoostIfActive(mount);
        TraitRegistry.endGhostHorseIfActive(player, mount);
    }
}
