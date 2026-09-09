package com.yourserver.mininghelmet;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * /mininghelmet                -> shows help/info
 * /mininghelmet get            -> gives yourself 1 helmet (random color)
 * /mininghelmet give <player>  -> gives <player> 1 helmet (random color)
 *
 * No quantity argument — run the command again to give more (e.g. spam
 * /mininghelmet give Steve for multiple).
 *
 * Every successful get/give is written to console for auditing.
 *
 * Register in onEnable():
 *   MiningHelmetCommand cmd = new MiningHelmetCommand(this);
 *   getCommand("mininghelmet").setExecutor(cmd);
 *   getCommand("mininghelmet").setTabCompleter(cmd);
 */
public class MiningHelmetCommand implements CommandExecutor, TabCompleter {

    private static final Component PREFIX = Component.text("[MiningHelmet] ", NamedTextColor.DARK_GRAY);
    private final Logger logger;

    public MiningHelmetCommand(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "get" -> handleGet(sender);
            case "give" -> handleGive(sender, args);
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("--- Minings Helmet ---", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/mininghelmet get", NamedTextColor.YELLOW)
                .append(Component.text(" - Give yourself a MiningHelmet", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/mininghelmet give <player>", NamedTextColor.YELLOW)
                .append(Component.text(" - Give a player a MiningHelmet. Must be Online", NamedTextColor.GRAY)));
    }

    private void handleGet(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendError(sender, "Only players can use this command.");
            return;
        }
        if (!player.hasPermission("mininghelmet.get")) {
            sendError(sender, "You don't have permission to do that.");
            return;
        }

        boolean dropped = giveHelmet(player);
        if (dropped) {
            sendError(sender, "Your inventory was full — the helmet dropped at your feet.");
        }
        logger.info("[Audit] " + player.getName() + " used /mininghelmet get" + (dropped ? " (dropped, inventory full)" : ""));
        broadcastReceived(player);
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("mininghelmet.give")) {
            sendError(sender, "You don't have permission to do that.");
            return;
        }
        if (args.length < 2) {
            sendError(sender, "Usage: /mininghelmet give <player>");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sendError(sender, "Player \"" + args[1] + "\" not found or not online.");
            return;
        }

        boolean dropped = giveHelmet(target);
        if (dropped) {
            target.sendMessage(PREFIX.append(Component.text(
                    "Your inventory was full — the helmet dropped at your feet.", NamedTextColor.RED)));
        }
        logger.info("[Audit] " + sender.getName() + " used /mininghelmet give on " + target.getName() +
                (dropped ? " (dropped, inventory full)" : ""));
        broadcastReceived(target);
    }

    /** Bright green — reads as lime in chat. */
    private void broadcastReceived(Player recipient) {
        Bukkit.broadcast(Component.text(
                recipient.getName() + " has been given 1 MiningHelmet.", NamedTextColor.GREEN));
    }

    private void sendError(CommandSender sender, String message) {
        sender.sendMessage(PREFIX.append(Component.text(message, NamedTextColor.RED)));
    }

    /** Gives the player 1 helmet, dropping it at their feet if their inventory is full. Returns true if dropped. */
    private boolean giveHelmet(Player player) {
        ItemStack helmet = MiningHelmet.create(); // random color each time
        var leftover = player.getInventory().addItem(helmet);
        if (!leftover.isEmpty()) {
            leftover.values().forEach(item ->
                    player.getWorld().dropItemNaturally(player.getLocation(), item));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(List.of("get", "give"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> filter(List<String> options, String typed) {
        return options.stream()
                .filter(o -> o.toLowerCase().startsWith(typed.toLowerCase()))
                .collect(Collectors.toList());
    }
}
