package de.dennes.yetanotherworldswitcher;

import com.onarandombox.MultiverseCore.MultiverseCore;
import de.dennes.yetanotherworldswitcher.commands.*;
import de.dennes.yetanotherworldswitcher.config.worlds;
import de.dennes.yetanotherworldswitcher.events.listeners;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class YetAnotherWorldSwitcher extends JavaPlugin
{
    private static MultiverseCore core;
    private worlds w;

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(new listeners(), this);
        core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");

        saveDefaultConfig();
        w = new worlds(core);
        w.importWorldsFromFile();

        if(this.getConfig().getString("PlaceWorldsInMenu").equalsIgnoreCase("manually") || this.getConfig().getString("PlaceWorldsInMenu").equalsIgnoreCase("automatic"))
        {
            this.getCommand("yaws").setExecutor(new yaws());
            this.getCommand("switchmenu").setExecutor(new switchmenu(this));
            this.getCommand("switchworld").setExecutor(new switchworld(this));
            this.getCommand("whereami").setExecutor(new whereami(this));
            this.getCommand("closemap").setExecutor(new closemap(this));
            this.getCommand("openmap").setExecutor(new openmap(this));
            this.getCommand("playerlimit").setExecutor(new playerlimit(this));
        }
        else
        {
            this.getLogger().severe("Value '" + this.getConfig().getString("PlaceWorldsInMenu") + "' in your config.yml is not a valid value");
        }
    }

    @Override
    public void onDisable()
    {
        w.exportLobbiesToFile();
    }

    public static MultiverseCore getCore() {
        return core;
    }
}
