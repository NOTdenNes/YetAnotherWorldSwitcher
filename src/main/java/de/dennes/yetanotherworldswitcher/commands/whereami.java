package de.dennes.yetanotherworldswitcher.commands;

import de.dennes.yetanotherworldswitcher.YetAnotherWorldSwitcher;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class whereami implements CommandExecutor
{
    private final YetAnotherWorldSwitcher yaws;

    public whereami(YetAnotherWorldSwitcher yaws)
    {
        this.yaws = yaws;
    }


    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            player.sendMessage("You are currently in world " + ChatColor.ITALIC + player.getLocation().getWorld().getName());
        }
        else
        {
            yaws.getLogger().info("You are literally everywhere. This command makes more sense when you're a player");
        }
        return true;
    }
}
