package ca.stepwisegamestudios.aarden.listeners;

import ca.stepwisegamestudios.aarden.Horsetopia;
import ca.stepwisegamestudios.aarden.api.events.BetterHorseAbilityUseEvent;
import ca.stepwisegamestudios.aarden.traits.TraitRegistry;
import ca.stepwisegamestudios.aarden.utils.SupportedMountType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class TraitActivationListener implements Listener {

    private final NamespacedKey traitKey = new NamespacedKey(Horsetopia.getInstance(), "trait");

    @EventHandler
    public void onTraitKeyPressed(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Horsetopia plugin = Horsetopia.getInstance();
        plugin.debugLog("TRAIT_ACTIVATION", "EVENT", true, "Trait key pressed.");
        Entity vehicle = player.getVehicle();

        if (!(vehicle instanceof AbstractHorse mount)) return;
        if (!SupportedMountType.isSupported(mount)) return;

        PersistentDataContainer data = mount.getPersistentDataContainer();
        if (!data.has(traitKey, PersistentDataType.STRING)) return;

        String trait = data.get(traitKey, PersistentDataType.STRING);
        if (trait == null) return;

        BetterHorseAbilityUseEvent abilityEvent = new BetterHorseAbilityUseEvent(player, mount, trait.toLowerCase());
        Bukkit.getPluginManager().callEvent(abilityEvent);
        if (abilityEvent.isCancelled()) {
            plugin.debugLog("TRAIT_ACTIVATION", "EVENT", false, "Ability use cancelled for " + player.getName() + " trait=" + trait + ".");
            event.setCancelled(true);
            return;
        }

        String selectedTrait = normalizeTraitKey(abilityEvent.getTraitKey());
        plugin.debugLog("TRAIT_ACTIVATION", "TRIGGER", true, "Player " + player.getName() + " activated trait " + selectedTrait + ".");
        switch (selectedTrait) {
            case "hellmare":
                TraitRegistry.activateHellmare(player, mount);
                break;
            case "dashboost":
                TraitRegistry.activateDashBoost(player, mount);
                break;
            case "kickback":
                TraitRegistry.activateKickback(player, mount);
                break;
            case "ghosthorse":
                TraitRegistry.activateGhostHorse(player, mount);
                break;
            case "revenantcurse":
                TraitRegistry.activateRevenantCurse(player, mount);
                break;
            default:
                plugin.debugLog(
                        "TRAIT_ACTIVATION",
                        "UNKNOWN_TRAIT",
                        false,
                        "No active ability mapping exists for trait key '" + abilityEvent.getTraitKey() + "'."
                );
                break;
        }

        event.setCancelled(true); // Prevent item swap
    }

    private String normalizeTraitKey(String traitKey) {
        if (traitKey == null) {
            return "";
        }
        return traitKey
                .toLowerCase()
                .replace("_", "")
                .replace("-", "")
                .replace(" ", "")
                .trim();
    }
}
