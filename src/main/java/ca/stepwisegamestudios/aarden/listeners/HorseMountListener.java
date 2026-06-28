package ca.stepwisegamestudios.aarden.listeners;

import ca.stepwisegamestudios.aarden.Horsetopia;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class HorseMountListener implements Listener {

    @EventHandler
    public void onHorseMount(PlayerInteractEntityEvent event) {

        if (!(event.getRightClicked() instanceof AbstractHorse horse)) return;
        if (!(event.getPlayer() instanceof Player player)) return;

        if (player.hasPermission("betterhorses.bypass")) return;

        FileConfiguration config = Horsetopia.getInstance().getConfig();

        if (!config.getBoolean("settings.restrict-mounting-to-owner", false)) {
            return;
        }

        NamespacedKey ownerKey = new NamespacedKey(Horsetopia.getInstance(), "owner");
        var data = horse.getPersistentDataContainer();

        if (!data.has(ownerKey, PersistentDataType.STRING)) return;

        String ownerUUID = data.get(ownerKey, PersistentDataType.STRING);
        if (ownerUUID == null) return;

        if (!player.getUniqueId().toString().equals(ownerUUID)) {
            event.setCancelled(true);
            Horsetopia.getInstance().getLang().sendFormatted(player, "messages.not-horse-owner");
        }
    }
}
