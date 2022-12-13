package com.adenark.villagernames.listener;

import com.adenark.villagernames.VillagerNames;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

/**
 * Spigot listeners for when the Villager
 * spawns into the world.
 */
public class EventVillagerSpawn implements Listener {

    private static String NO_TAG = "none_tag";

    /**
     * When a player right clicks a villager with a profession this event is fired.
     * This code checks whether the village custom name has the profession attached yet.
     * i.e. 'Baron' will become 'Baron The Farmer'
     *
     * @param event VillagerAcquireTradeEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        if (event.getEntity().getCustomName() != null) {
            PersistentDataContainer persistentData = event.getEntity().getPersistentDataContainer();
            if (persistentData.get(new NamespacedKey(VillagerNames.getInstance(), "villager_name"), PersistentDataType.STRING) != null) {
                setProffName((Villager) event.getEntity(), persistentData.get(new NamespacedKey(VillagerNames.getInstance(), "villager_name"), PersistentDataType.STRING));
            }
        }
    }

    /**
     * On entity right click this event is fired.
     * This code checks and runs a probability calculation to see whether the villager should
     * have a custom name.
     *
     * @param event PlayerInteractEntityEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return; // Stops the double click event fire
        Entity entity = event.getRightClicked();
        if (entity instanceof Villager) {
            if (performVillagerTag((Villager) entity))
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 0);
        }
    }

    public static boolean performVillagerTag(final Villager villager) {
        return performVillagerTag(villager, true);
    }

    /**
     * @see onPlayerInteractEntity
     * @param villager Villager
     * @param rand Is the custom name attaching to a villager random?
     * @return The villager has been right clicked before.
     */
    public static boolean performVillagerTag(final Villager villager, boolean rand) {
        // Checks whether Villager has been
        PersistentDataContainer persistentData = villager.getPersistentDataContainer();
        if (persistentData.get(new NamespacedKey(VillagerNames.getInstance(), "villager_name"), PersistentDataType.STRING) == null) {
            // Calculate random chance of village having custom name
            double randomNum = Math.random();

            // If not using rand
            if (!rand) randomNum = 0;

            // Debug message
            if (VillagerNames.DEBUG) {
                VillagerNames.getInstance().getServer().getConsoleSender().sendMessage(
                    ChatColor.GREEN + "[Debug] " + ChatColor.RESET +
                        "Villager percent chance: " + ChatColor.WHITE + (float) randomNum + "/" + VillagerNames.NAME_POTENTIAL
                );
            }

            // Spawn with custom name if chance is met
            if (randomNum <= VillagerNames.NAME_POTENTIAL) {
                // Generate a custom name
                int randomIndex = new Random().nextInt(VillagerNames.NAMES.size());
                final String villagerName = VillagerNames.NAMES.get(randomIndex);
                persistentData.set(new NamespacedKey(VillagerNames.getInstance(), "villager_name"), PersistentDataType.STRING, villagerName);
                if (villager.getProfession() != Villager.Profession.NONE) {
                    // Set Villagers' custom name with profession
                    setProffName(villager, villagerName);
                } else {
                    // Set Villagers' custom name
                    String custom_name = VillagerNames.NAME_DISPLAY;
                    custom_name = custom_name.replaceAll("%custom_name%", villagerName);
                    villager.setCustomName(custom_name);
                }
                if (VillagerNames.NAME_VISIBLE) {
                    villager.setCustomNameVisible(true);
                }
                return true;
            } else {
                persistentData.set(new NamespacedKey(VillagerNames.getInstance(), "villager_name"), PersistentDataType.STRING, NO_TAG);
            }
        }
        return false;
    }

    static void setProffName(Villager villager, String villagerName) {
        String custom_name = VillagerNames.NAME_DISPLAY_WITH_PROFF;
        String profession = villager.getProfession().name();
        profession = profession.substring(0, 1).toUpperCase() + profession.substring(1).toLowerCase();
        custom_name = custom_name.replaceAll("%profession%", profession);
        custom_name = custom_name.replaceAll("%custom_name%", villagerName);
        villager.setCustomName(custom_name);
    }


}
