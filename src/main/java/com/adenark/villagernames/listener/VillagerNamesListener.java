package com.adenark.villagernames.listener;

import java.util.Random;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
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
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return; // Stops the event from firing twice
        Entity entity = event.getRightClicked();
        if (entity instanceof Villager) {
            // If the player is holding a name tag
            if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.NAME_TAG)) {
                Component playerItem = event.getPlayer().getInventory().getItemInMainHand().getItemMeta().displayName();
                assert playerItem != null;
                final String playerItemName = PlainTextComponentSerializer.plainText().serialize(playerItem);

                // Rename the villager using the name tag's item name
                applyName((Villager) entity, playerItemName);

                // Remove the villager's profession on rename
                ((Villager) entity).setProfession(Villager.Profession.NONE);

                // Play a sound for renaming a villager
                Sound sound = Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT;
                event.getPlayer().playSound(event.getPlayer().getLocation(), sound, 100, 0);
            } else {
                if (applyGeneratedName((Villager) entity)) {
                    // Play a sound for generating a new name
                    Sound sound = Sound.ENTITY_VILLAGER_CELEBRATE;
                    event.getPlayer().playSound(event.getPlayer().getLocation(), sound, 100, 0);
                }
            }
        }
    }

    /**
     * When a villager's career changes, this event is fired. If configured to display professions, the villager name
     * will append its profession.
     *
     * @see VillagerNames#FULL_DISPLAY_NAME_WITH_PROFESSION
     * @param event VillagerCareerChangeEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onVillagerCareerChangeEvent(VillagerCareerChangeEvent event) {
        if (!VillagerNames.DISPLAY_NAMES_WITH_PROFESSION) return;
        if (event.getEntity().customName() == null) return;
        PersistentDataContainer persistentData = event.getEntity().getPersistentDataContainer();
        if (persistentData.get(new NamespacedKey(plugin, nameKey), PersistentDataType.STRING) == null) return;
        setVillagerCustomName(
            event.getEntity(),
            persistentData.get(new NamespacedKey(plugin, nameKey), PersistentDataType.STRING),
            event.getProfession());
    }

    /**
     * Picks a random name from the list before applying it to a villager.
     *
     * @see VillagerNamesListener#onPlayerInteractEntity(PlayerInteractEntityEvent)
     * @param villager Villager
     * @return Whether the method generated a new name for the villager.
     */
    public static boolean applyGeneratedName(final Villager villager) {
        PersistentDataContainer persistentData = villager.getPersistentDataContainer();
        String existingName = persistentData.get(new NamespacedKey(plugin, nameKey), PersistentDataType.STRING);

        // Cancel if a name already exists under the VillagerNames data tag
        if (existingName != null) return false;

        // Generate and apply a new name to the villager
        int randomIndex = new Random().nextInt(VillagerNames.NAMES.size());
        final String villagerName = VillagerNames.NAMES.get(randomIndex);
        applyName(villager, villagerName);
        return true;
    }

    /**
     * Sets the VillagerNames and CustomName data tags of a villager.
     *
     * @param villager Villager
     * @param villagerName String
     */
    static void applyName(final Villager villager, String villagerName) {
        PersistentDataContainer persistentData = villager.getPersistentDataContainer();

        // Assign the villager the name under the VillagerNames data tag
        persistentData.set(new NamespacedKey(plugin, nameKey), PersistentDataType.STRING, villagerName);

        // Set the villager's nameplate
        setVillagerCustomName(villager, villagerName, villager.getProfession());
        if (VillagerNames.SET_CUSTOM_NAME_VISIBLE) villager.setCustomNameVisible(true);
    }

    /**
     * Sets the CustomName data tag of a villager.
     *
     * @param villager Villager
     * @param villagerName String
     * @param profession Villager.Profession
     */
    static void setVillagerCustomName(final Villager villager, String villagerName, Villager.Profession profession) {
        String customNameString;
        if (VillagerNames.DISPLAY_NAMES_WITH_PROFESSION && profession != Villager.Profession.NONE) {
            String professionString = profession.toString();
            professionString = professionString.substring(0, 1).toUpperCase() +
                professionString.substring(1).toLowerCase();
            customNameString = VillagerNames.FULL_DISPLAY_NAME_WITH_PROFESSION
                .replaceAll("%name%", villagerName)
                .replaceAll("%profession%", professionString);
        } else {
            customNameString = VillagerNames.FULL_DISPLAY_NAME;
            customNameString = customNameString.replaceAll("%name%", villagerName);
        }
        final TextComponent customName = Component.text(customNameString);
        villager.customName(customName);
    }
}
