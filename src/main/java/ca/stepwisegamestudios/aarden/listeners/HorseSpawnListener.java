package ca.stepwisegamestudios.aarden.listeners;

import ca.stepwisegamestudios.aarden.Horsetopia;
import ca.stepwisegamestudios.aarden.api.BetterHorsesAPI;
import ca.stepwisegamestudios.aarden.api.events.BetterHorseSpawnEvent;
import ca.stepwisegamestudios.aarden.training.TrainingManager;
import ca.stepwisegamestudios.aarden.utils.MountConfig;
import ca.stepwisegamestudios.aarden.utils.SupportedMountType;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class HorseSpawnListener implements Listener {

    private static final String[] GENDERS = {"male", "female"};
    private final Random random = new Random();
    private final NamespacedKey genderKey = new NamespacedKey(JavaPlugin.getPlugin(Horsetopia.class), "gender");
    private final NamespacedKey growthKey = new NamespacedKey(JavaPlugin.getPlugin(Horsetopia.class), "growth_stage");

    @EventHandler
    public void onHorseSpawn(CreatureSpawnEvent event) {
        Horsetopia plugin = Horsetopia.getInstance();
        if (!(event.getEntity() instanceof AbstractHorse horse)) return;
        SupportedMountType mountType = SupportedMountType.fromEntity(horse).orElse(null);
        if (mountType == null) return;
        makeSkeletonHorseMountableIfEnabled(plugin, horse);
        if (!mountType.isEnabled(plugin.getConfig())) return;
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;
        plugin.debugLog("HORSE_SPAWN", "NATURAL", true, "Natural mount spawn detected for " + horse.getUniqueId() + ".");

        // Set gender
        String gender = GENDERS[random.nextInt(GENDERS.length)];
        PersistentDataContainer data = horse.getPersistentDataContainer();
        data.set(genderKey, PersistentDataType.STRING, gender);

        int stage = 10;

        //if (BetterHorses.getInstance().getConfig().getBoolean("horse-growth-settings.enabled")) {
        boolean growthEnabled = MountConfig.isGrowthEnabled(Horsetopia.getInstance().getConfig(), mountType);
        if (growthEnabled) {
            // Set random growth stage
            stage = random.nextInt(11); // 0–10

            // Set scale
            double minScale = 0.7;
            double maxScale = Horsetopia.getInstance().getConfig().getDouble("horse-growth-settings.max-size", 1.3);
            double scale = minScale + ((maxScale - minScale) / 10.0) * stage;

            try {
                AttributeInstance attr = horse.getAttribute(Attribute.valueOf("SCALE"));
                if (attr != null) {
                    attr.setBaseValue(scale);
                }
            } catch (Exception ignored) {
                // SCALE not supported – fallback for older Paper versions
            }

            // Set to adult if threshold met
            int threshold = 7; // percentage of max growth (70%)
            if (stage >= threshold && !horse.isAdult()) {
                horse.setAdult();
                horse.setAgeLock(false);
            } else if (stage < threshold) {
                horse.setBaby();
                horse.setAgeLock(true);
            } else {
                horse.setAgeLock(false);
            }
        }

        data.set(growthKey, PersistentDataType.INTEGER, stage);
        TrainingManager.ensureBaseStats(horse);
        TrainingManager.recalculateAndApplyBonuses(horse);
        BetterHorsesAPI.callSpawnEvent(horse, null, BetterHorseSpawnEvent.SpawnCause.NATURAL);
        plugin.debugLog("HORSE_SPAWN", "COMPLETE", true, "Initialized natural mount " + horse.getUniqueId() + " stage=" + stage + ".");
    }

    private void makeSkeletonHorseMountableIfEnabled(Horsetopia plugin, AbstractHorse horse) {
        if (!(horse instanceof SkeletonHorse)) return;
        if (!plugin.getConfig().getBoolean("settings.mountable-skeleton-horses", true)) return;

        horse.setTamed(true);
        plugin.debugLog("HORSE_SPAWN", "SKELETON_MOUNTABLE", true,
                "Marked skeleton horse " + horse.getUniqueId() + " as tamed for vanilla mounting.");
    }
}
