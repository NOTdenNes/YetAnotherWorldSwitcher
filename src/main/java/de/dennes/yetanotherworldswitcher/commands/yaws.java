package de.dennes.yetanotherworldswitcher.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class yaws implements CommandExecutor
{
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

            if(player.hasPermission("yaws.command.yaws"))
            {
                player.sendMessage(ChatColor.AQUA + "===-==-==-===" + ChatColor.GOLD + ChatColor.BOLD + " YetAnotherWorldSwitcher " + ChatColor.RESET + ChatColor.AQUA + "===-==-==-===");
                player.sendMessage("Displays all commands available to you:"); //ChatColor.AQUA +
                player.sendMessage(ChatColor.GOLD + " /yaws " + ChatColor.RESET + "Shows this list");
                if(player.hasPermission("yaws.command.sw"))
                {
                    player.sendMessage(ChatColor.GOLD + " /switchworld [name] " + ChatColor.RESET + "Sends you to the specified world");
                    player.sendMessage(ChatColor.GOLD + " /sw [name] " + ChatColor.RESET + "Alias for " + ChatColor.GOLD + "/switchworld");
                }
                if(player.hasPermission("yaws.command.sm"))
                {
                    player.sendMessage(ChatColor.GOLD + " /switchmenu " + ChatColor.RESET + "Opens the menu to switch between worlds");
                    player.sendMessage(ChatColor.GOLD + " /sm " + ChatColor.RESET + "Alias for " + ChatColor.GOLD + "/switchmenu");
                }
                if(player.hasPermission("yaws.command.loc"))
                {
                    player.sendMessage(ChatColor.GOLD + " /whereami " + ChatColor.RESET + "Tells you in which world you are");
                }
                if(player.hasPermission("yaws.command.map.lock"))
                {
                    player.sendMessage(ChatColor.GOLD + " /closemap [name] " + ChatColor.RESET + "Declines access to a map without req. permission");
                }
                if(player.hasPermission("yaws.command.map.unlock"))
                {
                    player.sendMessage(ChatColor.GOLD + " /openmap [name] " + ChatColor.RESET + "Allows every player to enter a map");
                }
                if(player.hasPermission("yaws.command.map.pl"))
                {
                    player.sendMessage(ChatColor.GOLD + " /playerlimit [name] <value> " + ChatColor.RESET + "Sets the playerlimit for a map. \n '-1' = unlimited. Gets current limit then no value is assigned");
                }
            }
        }
        return true;
    }
}
