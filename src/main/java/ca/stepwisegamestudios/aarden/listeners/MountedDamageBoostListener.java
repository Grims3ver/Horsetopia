package ca.stepwisegamestudios.aarden.listeners;

import ca.stepwisegamestudios.aarden.Horsetopia;
import ca.stepwisegamestudios.aarden.utils.SupportedMountType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MountedDamageBoostListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onMountedDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player player)) return;

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof AbstractHorse mount) || !SupportedMountType.isSupported(mount)) return;

        FileConfiguration config = Horsetopia.getInstance().getConfig();
        double percentage = config.getDouble("settings.mounted-damage-boost.percentage", 0.0);
        if (percentage == 0) {
            return;
        }

        double multiplier = 1 + (percentage / 100.0);
        event.setDamage(event.getDamage() * multiplier);
    }
}
