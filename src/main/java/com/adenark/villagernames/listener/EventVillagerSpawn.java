package com.adenark.villagernames.listener;

import java.util.Random;
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
import com.adenark.villagernames.VillagerNames;

public class EventVillagerSpawn implements Listener {
    private static String NO_TAG = "none_tag";

    /**
     * When a player right-clicks a villager with a profession, this event is fired.
     * This checks whether the villager's name has the profession attached yet.
     * For example, "Baron" will become "Baron the Farmer"
     *
     * @param event VillagerAcquireTradeEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        if (event.getEntity().getCustomName() != null) {
            PersistentDataContainer persistentData = event.getEntity().getPersistentDataContainer();
            if (persistentData.get(new NamespacedKey(VillagerNames.getInstance(), "villager_name"),
                    PersistentDataType.STRING) != null) {
                setProfessionName((Villager) event.getEntity(), persistentData.get(new NamespacedKey(VillagerNames.getInstance(),
                    "villager_name"), PersistentDataType.STRING));
            }
        }
    }

    /**
     * When a player right-clicks an entity, this event is fired.
     * This code checks and runs a probability calculation to see whether a villager should
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
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    100, 0);
        }
    }

    public static boolean performVillagerTag(final Villager villager) {
        return performVillagerTag(villager, true);
    }

    /**
     * @see EventVillagerSpawn#onPlayerInteractEntity(PlayerInteractEntityEvent)
     * @param villager Villager
     * @param rand Is the custom name being assigned to a villager random?
     * @return Whether the villager has been right-clicked before.
     */
    public static boolean performVillagerTag(final Villager villager, boolean rand) {
        // Checks whether a villager has been named
        PersistentDataContainer persistentData = villager.getPersistentDataContainer();
        if (persistentData.get(new NamespacedKey(VillagerNames.getInstance(), "villager_name"),
            PersistentDataType.STRING) == null) {

            // Calculate random chance of village having custom name
            double randomNum = Math.random();

            // If not using rand
            if (!rand) randomNum = 0;

            // Debug message
            if (VillagerNames.DEBUG) {
                VillagerNames.getInstance().getServer().getConsoleSender().sendMessage(
                    ChatColor.GREEN + "[Debug] " + ChatColor.RESET + "Villager percent chance: "
                        + ChatColor.WHITE + (float) randomNum + "/" + VillagerNames.NAME_POTENTIAL
                );
            }

            // Spawn with a custom name with a certain chance
            if (randomNum <= VillagerNames.NAME_POTENTIAL) {
                // Generate a custom name
                int randomIndex = new Random().nextInt(VillagerNames.NAMES.size());
                final String villagerName = VillagerNames.NAMES.get(randomIndex);
                persistentData.set(new NamespacedKey(VillagerNames.getInstance(), "villager_name"),
                    PersistentDataType.STRING, villagerName);
                if (villager.getProfession() != Villager.Profession.NONE) {
                    // Set villagers' custom names to have their profession
                    setProfessionName(villager, villagerName);
                } else {
                    // Set villagers' custom name
                    String custom_name = VillagerNames.NAME_DISPLAY;
                    custom_name = custom_name.replaceAll("%custom_name%", villagerName);
                    villager.setCustomName(custom_name);
                }
                if (VillagerNames.NAME_VISIBLE) {
                    villager.setCustomNameVisible(true);
                }
                return true;
            } else {
                persistentData.set(new NamespacedKey(VillagerNames.getInstance(), "villager_name"),
                    PersistentDataType.STRING, NO_TAG);
            }
        }
        return false;
    }

    static void setProfessionName(Villager villager, String villagerName) {
        String custom_name = VillagerNames.NAME_DISPLAY_WITH_PROFF;
        String profession = villager.getProfession().name();
        profession = profession.substring(0, 1).toUpperCase() + profession.substring(1).toLowerCase();
        custom_name = custom_name.replaceAll("%profession%", profession);
        custom_name = custom_name.replaceAll("%custom_name%", villagerName);
        villager.setCustomName(custom_name);
    }
}
