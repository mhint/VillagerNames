package com.adenark.villagernames.listener;

import java.util.Random;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
import org.bukkit.plugin.java.JavaPlugin;
import com.adenark.villagernames.VillagerNames;

public class VillagerNamesListener implements Listener {
    private static final JavaPlugin plugin = VillagerNames.getInstance();
    private static final String nameKey = "name";

    /**
     * When a player right-clicks an entity, this event is fired.
     *
     * @param event PlayerInteractEntityEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return; // Stops the double click event fire
        Entity entity = event.getRightClicked();
        if (entity instanceof Villager) {
            if (saveVillagerTag((Villager) entity)) {
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    100, 0);
            }
        }
    }

    /**
     * When a villager acquires a trade, this event is fired. If configured to display professions, the villager
     * name will append its profession.
     *
     * @see VillagerNames#FULL_DISPLAY_NAME_WITH_PROFESSION
     * @param event VillagerAcquireTradeEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        if (!VillagerNames.DISPLAY_NAMES_WITH_PROFESSION) return;
        if (event.getEntity().customName() == null) return;
        PersistentDataContainer persistentData = event.getEntity().getPersistentDataContainer();
        if (persistentData.get(new NamespacedKey(plugin, nameKey), PersistentDataType.STRING) != null) {
            setVillagerCustomName(
                    (Villager) event.getEntity(),
                    persistentData.get(new NamespacedKey(plugin, nameKey),
                            PersistentDataType.STRING),
                    true);
        }
    }

    /**
     * @see VillagerNamesListener#onPlayerInteractEntity(PlayerInteractEntityEvent)
     * @param villager Villager
     * @return Whether a custom name was stored with the villager.
     */
    public static boolean saveVillagerTag(final Villager villager) {
        // Return false and do nothing more if a villager has already been named
        PersistentDataContainer persistentData = villager.getPersistentDataContainer();
        if (persistentData.get(new NamespacedKey(plugin, nameKey), PersistentDataType.STRING) != null) return false;

        // Retrieve a name from the name list at a random index
        int randomIndex = new Random().nextInt(VillagerNames.NAMES.size());
        final String villagerName = VillagerNames.NAMES.get(randomIndex);

        // Assign the villager the name under the Plugin namespace
        persistentData.set(new NamespacedKey(plugin, nameKey), PersistentDataType.STRING, villagerName);

        // Set villager's custom name
        setVillagerCustomName(villager, villagerName, VillagerNames.DISPLAY_NAMES_WITH_PROFESSION
            && villager.getProfession() != Villager.Profession.NONE);
        if (VillagerNames.SET_CUSTOM_NAME_VISIBLE) villager.setCustomNameVisible(true);
        return true;
    }

    /**
     * Sets the CustomName NBT tag of a named villager.
     *
     * @param villager Villager
     * @param villagerName String
     * @param includeProfession boolean
     */
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
