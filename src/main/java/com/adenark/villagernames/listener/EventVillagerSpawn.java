package com.adenark.villagernames.listener;

import java.util.Random;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
     * When a villager acquires a trade, this event is fired.
     * If the villager already has a name, the villager's name will display its profession.
     *
     * @param event VillagerAcquireTradeEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        if (!VillagerNames.DISPLAY_NAMES_WITH_PROFESSION) return;
        if (event.getEntity().customName() == null) return;
        PersistentDataContainer persistentData = event.getEntity().getPersistentDataContainer();
        if (persistentData.get(new NamespacedKey(VillagerNames.getInstance(), "name"),
                PersistentDataType.STRING) != null) {
            setVillagerCustomName((Villager) event.getEntity(), persistentData.get(
                new NamespacedKey(VillagerNames.getInstance(), "name"), PersistentDataType.STRING),
                true);
        }
    }

    /**
     * When a player right-clicks an entity, this event is fired.
     * A probability calculation is run to to determine whether a villager will be assigned a custom name.
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
        if (persistentData.get(new NamespacedKey(VillagerNames.getInstance(), "name"),
            PersistentDataType.STRING) == null) {

            // Calculate random chance of village having custom name
            double randomNum = Math.random();

            // If not using rand
            if (!rand) randomNum = 0;

            // Debug message
            if (VillagerNames.DEBUG) {
                VillagerNames.getInstance().getServer().getConsoleSender().sendMessage(
                    ChatColor.GREEN + "[Debug] " + ChatColor.RESET + "Villager percent chance: "
                        + ChatColor.WHITE + (float) randomNum + "/" + VillagerNames.SPAWN_WITH_NAME_CHANCE
                );
            }

            // Spawn with a custom name with a certain chance
            if (randomNum <= VillagerNames.SPAWN_WITH_NAME_CHANCE) {
                // Retrieve a name from the name list at a random index
                int randomIndex = new Random().nextInt(VillagerNames.NAMES.size());
                final String villagerName = VillagerNames.NAMES.get(randomIndex);
                // Assign the villager the name under the Plugin namespace
                persistentData.set(new NamespacedKey(VillagerNames.getInstance(), "name"),
                    PersistentDataType.STRING, villagerName);
                // Set villager's custom name
                setVillagerCustomName(villager, villagerName, VillagerNames.DISPLAY_NAMES_WITH_PROFESSION
                    && villager.getProfession() != Villager.Profession.NONE);
                if (VillagerNames.SET_CUSTOM_NAME_VISIBLE) villager.setCustomNameVisible(true);
                return true;
            } else {
                persistentData.set(new NamespacedKey(VillagerNames.getInstance(), "villager_name"),
                    PersistentDataType.STRING, NO_TAG);
            }
        }
        return false;
    }

    static void setVillagerCustomName(Villager villager, String villagerName, boolean includeProfession) {
        String customNameString;
        if (includeProfession) {
            String profession = villager.getProfession().name();
            profession = profession.substring(0, 1).toUpperCase() + profession.substring(1).toLowerCase();
            customNameString = VillagerNames.FULL_DISPLAY_NAME_WITH_PROFESSION;
            customNameString = customNameString.replaceAll("%name%", villagerName)
                .replaceAll("%profession%", profession);
        } else {
            customNameString = VillagerNames.FULL_DISPLAY_NAME;
            customNameString = customNameString.replaceAll("%name%", villagerName);
        }
        final TextComponent customName = Component.text(customNameString);
        villager.customName(customName);
    }
}
