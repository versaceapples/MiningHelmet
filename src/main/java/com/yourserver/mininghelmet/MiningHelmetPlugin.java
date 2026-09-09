package com.yourserver.mininghelmet;

import org.bukkit.plugin.java.JavaPlugin;

public class MiningHelmetPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        var command = getCommand("mininghelmet");
        if (command == null) {
            getLogger().severe("Command 'mininghelmet' is missing from plugin.yml — disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        MiningHelmetCommand executor = new MiningHelmetCommand(this);
        command.setExecutor(executor);
        command.setTabCompleter(executor);

        getServer().getPluginManager().registerEvents(new MiningHelmetListener(), this);
        MiningHelmet.startEffectTask(this);

        getLogger().info("MiningHelmet plugin enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MiningHelmet plugin disabled.");
    }
}
