package com.yourserver.mininghelmet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mining Helmet — item factory + the task that detects equip/unequip and
 * applies truly infinite Haste + Night Vision while worn.
 *
 * There is no native Bukkit/Paper event that fires on armor equip/unequip
 * (PlayerArmorChangeEvent is NOT part of the standard API — that was a
 * mistake in an earlier version of this file). Instead, a repeating task
 * polls each online player's helmet slot once a second purely to detect
 * the equip/unequip transition — the chime sound, applying the infinite
 * effects on equip, and removing them instantly on unequip. The effects
 * themselves use PotionEffect.INFINITE_DURATION, so once applied they
 * never need to be topped up or reapplied while the helmet stays on.
 */
public class MiningHelmet {

    public static final NamespacedKey KEY = new NamespacedKey("mininghelmet", "mining_helmet");

    // Tweak these to taste
    private static final int HASTE_AMPLIFIER = 1;          // 1 = Haste II (confirmed from screenshot)
    private static final int CHECK_INTERVAL_TICKS = 20;     // how often the task checks for equip/unequip (1s)

    private static final Random RANDOM = new Random();

    /** Tracks who is currently wearing the helmet, so the equip chime only fires once per equip. */
    static final Set<UUID> currentlyWearing = ConcurrentHashMap.newKeySet();

    /**
     * Builds the item: a diamond helmet named "Mining Helmet" with
     * Protection IV / Fire Protection IV / Blast Protection IV, Mending,
     * and a green "Infused with Haste" lore line.
     *
     * Color tier (yellow vs red name) is picked randomly each time, 50/50.
     */
    public static ItemStack create() {
        boolean rare = RANDOM.nextBoolean();

        ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Mining Helmet")
                .color(rare ? NamedTextColor.RED : NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false));

        meta.addEnchant(Enchantment.PROTECTION, 4, true);
        meta.addEnchant(Enchantment.FIRE_PROTECTION, 4, true);
        meta.addEnchant(Enchantment.BLAST_PROTECTION, 4, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        // The actual enchant lines above are drawn by vanilla from the enchants
        // themselves; this extra line is the custom green "Infused with Haste" text.
        meta.lore(List.of(
                Component.text("Infused with Haste")
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
        ));

        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }

    /** Checks whether a given item is a Mining Helmet. */
    public static boolean isMiningHelmet(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        Byte tag = meta.getPersistentDataContainer().get(KEY, PersistentDataType.BYTE);
        return tag != null && tag == (byte) 1;
    }

    private static void applyEffects(Player player) {
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.HASTE,
                PotionEffect.INFINITE_DURATION,
                HASTE_AMPLIFIER,
                true,   // ambient (softer particles)
                false,  // particles off entirely — cleaner look
                false   // no icon spam
        ));
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                PotionEffect.INFINITE_DURATION,
                0,      // Night Vision only has one level
                true,
                false,
                false
        ));
    }

    private static void removeEffects(Player player) {
        player.removePotionEffect(PotionEffectType.HASTE);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    /** Starts the repeating task that detects equip/unequip and applies the permanent effects. Call once in onEnable(). */
    public static void startEffectTask(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID id = player.getUniqueId();
                boolean wearingNow = isMiningHelmet(player.getInventory().getHelmet());
                boolean wasWearing = currentlyWearing.contains(id);

                if (wearingNow && !wasWearing) {
                    currentlyWearing.add(id);
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 100f, 1.0f);
                    applyEffects(player); // infinite duration — no need to reapply while still worn
                }
                if (!wearingNow && wasWearing) {
                    currentlyWearing.remove(id);
                    removeEffects(player);
                }
            }
        }, 0L, CHECK_INTERVAL_TICKS);
    }
}
