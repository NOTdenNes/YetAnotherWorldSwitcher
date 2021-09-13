package de.dennes.yetanotherworldswitcher.commands;

import de.dennes.yetanotherworldswitcher.YetAnotherWorldSwitcher;
import de.dennes.yetanotherworldswitcher.worlds.lobby;
import de.dennes.yetanotherworldswitcher.worlds.lobbyList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class closemap implements TabExecutor
{
    private final YetAnotherWorldSwitcher yaws;

    public closemap(YetAnotherWorldSwitcher yaws)
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
        if(args.length == 0 && sender instanceof Player)
        {
            Player player = (Player) sender;

            for(lobby lobby : getLobbyList())
            {
                if(lobby.getName().equalsIgnoreCase(player.getLocation().getWorld().getName())) {

                    if (!lobby.isClosed())
                    {
                        lobby.setClosed(true);
                        player.sendMessage("World " + ChatColor.GOLD + ChatColor.ITALIC + player.getLocation().getWorld().getName() + ChatColor.RESET + " is now locked to the public.");
                        yaws.getLogger().info("World " +  lobby.getName() + " got locked by " + player.getName());
                        return true;
                    }
                }
                else
                {
                    player.sendMessage("World " + ChatColor.GOLD + ChatColor.ITALIC + player.getLocation().getWorld().getName() + ChatColor.RESET + " is already locked to the public.");
                    return true;
                }
            }

        }
        else if(args.length == 0 && sender instanceof ConsoleCommandSender)
        {
            yaws.getLogger().info("You need to enter the name of a map");
            return false;
        }
        else if(args.length > 0)
        {
            for (lobby lobby : getLobbyList())
            {
                if(lobby.getName().equalsIgnoreCase(args[0]))
                {
                    if(!lobby.isClosed())
                    {
                        lobby.setClosed(true);
                        if(sender instanceof Player)
                        {
                            Player player = (Player) sender;
                            player.sendMessage("World " + ChatColor.GOLD + ChatColor.ITALIC + lobby.getName() + ChatColor.RESET + " is now locked to the public.");
                            yaws.getLogger().info("World " +  lobby.getName() + " got locked by " + player.getName());
                        }
                        else
                        {
                            yaws.getLogger().info("World " +  lobby.getName() + " is now locked to the public.");
                        }
                    }
                    else
                    {
                        if(sender instanceof Player)
                        {
                            Player player = (Player) sender;
                            player.sendMessage("World " + ChatColor.GOLD + ChatColor.ITALIC + lobby.getName() + ChatColor.RESET + " is already locked to the public.");
                        }
                        else
                        {
                            yaws.getLogger().info("World " +  lobby.getName() + " is already locked to the public.");
                        }
                    }
                    return true;
                }

            }
        }
        else
        {
            yaws.getLogger().warning("An unknown error occurred whilst trying to close a map. Please contact the developer of this plugin");
            return true;
        }
        return true;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args)
    {
        List<String> worldNames = new ArrayList<>();
        if(args.length == 1)
        {
            for (lobby lobby : getLobbyList())
            {
                worldNames.add(lobby.getName());
            }
        }
        return worldNames;
    }

    private List<lobby> getLobbyList()
    {
        lobbyList lL = new lobbyList();

        return lL.getLobbies();
    }
}
