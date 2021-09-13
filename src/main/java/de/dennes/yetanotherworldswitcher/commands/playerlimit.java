package de.dennes.yetanotherworldswitcher.commands;

import de.dennes.yetanotherworldswitcher.YetAnotherWorldSwitcher;
import de.dennes.yetanotherworldswitcher.worlds.lobby;
import de.dennes.yetanotherworldswitcher.worlds.lobbyList;
import org.apache.commons.lang.math.NumberUtils;
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

public class playerlimit implements TabExecutor
{
    private final YetAnotherWorldSwitcher yaws;

    public playerlimit(YetAnotherWorldSwitcher yaws)
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
        if(args.length == 0 && sender instanceof Player) //Test if cmd has no arguments and is run by player. Return playerlimit of world where player is located. Player only, no console
        {
            Player player = (Player) sender;

            for(lobby lobby : getLobbyList())
            {
                if(lobby.getName().equalsIgnoreCase(player.getLocation().getWorld().getName()))
                {
                    if(lobby.getPlayerLimit() == -1) //Test if unlimited
                    {
                        player.sendMessage("World " + ChatColor.GOLD + ChatColor.ITALIC + player.getLocation().getWorld().getName() + ChatColor.RESET + " does not have a playerlimit");
                    }
                    else
                    {
                        player.sendMessage("World " + ChatColor.GOLD + ChatColor.ITALIC + player.getLocation().getWorld().getName() + ChatColor.RESET + " has a limit of " + ChatColor.GOLD + lobby.getPlayerLimit() + ChatColor.RESET + " players");
                    }
                    return true;
                }
            }

        }
        else if(args.length == 0 && sender instanceof ConsoleCommandSender) //Test if cmd has no arguments and is run by console. Error
        {
            yaws.getLogger().info("You need to enter the name of a map or be a player");
            return false;
        }
        else if(args.length == 1) //Test if one argument is given. It's either a worldname or a numerical value
        {
            for (lobby lobby : getLobbyList())
            {
                if(NumberUtils.isNumber(args[0])) //Test if value without worldname. Changes playerlimit in world where player is located. Player only, no console
                {
                    int temp = lobby.getPlayerLimit();

                    if(sender instanceof Player) //Test if player
                    {
                        Player player = (Player) sender;

                        lobby.setPlayerLimit(Integer.parseInt(args[0]));
                        YetAnotherWorldSwitcher.getCore().getMVWorldManager().getMVWorld(lobby.getName()).setPlayerLimit(Integer.parseInt(args[0]));

                        player.sendMessage("Updated the playerlimit in world " + ChatColor.GOLD + ChatColor.ITALIC + lobby.getName() + ChatColor.RESET + " from " + ChatColor.GOLD + temp + ChatColor.RESET + " to " + ChatColor.GOLD + args[0] + ChatColor.RESET + " players.");
                        yaws.getLogger().info("Updated the playerlimit in world " + lobby.getName() + " from " + temp + " to " + args[0] + " players.");
                        return true;
                    }
                    else //Error if else
                    {
                        yaws.getLogger().info("You need to be a player in order to do that. Sorry");
                        return false;
                    }
                }
                else if(lobby.getName().equalsIgnoreCase(args[0])) //Test if Worldname without value
                {
                    if(sender instanceof Player) //Different output, depending on if sender is a player or the console. This: Player
                    {
                        Player player = (Player) sender;
                        if (lobby.getPlayerLimit() == -1) //Test if unlimited
                        {
                            player.sendMessage("World " + ChatColor.GOLD + ChatColor.ITALIC + lobby.getName() + ChatColor.RESET + " does not have a playerlimit");
                        } else
                        {
                            player.sendMessage("World " + ChatColor.GOLD + ChatColor.ITALIC + lobby.getName() + ChatColor.RESET + " has a limit of " + ChatColor.GOLD + lobby.getPlayerLimit() + ChatColor.RESET + " players");
                        }
                    }
                    else //This: Console
                    {
                        if (lobby.getPlayerLimit() == -1) //Test if unlimited
                        {
                            yaws.getLogger().info("World " + lobby.getName() + " does not have a playerlimit");
                        } else
                        {
                            yaws.getLogger().info("World " + lobby.getName() + " has a limit of " + lobby.getPlayerLimit() + " players");
                        }
                    }
                    return true;
                }
            }
        }
        else if(args.length == 2) //Test if two arguments are given
        {
            for (lobby lobby : getLobbyList())
            {
                if(lobby.getName().equalsIgnoreCase(args[0])) //Test if first argument is a worldname
                {
                    if(NumberUtils.isNumber(args[1])) //Test if second argument is a numerical value
                    {
                        int temp = lobby.getPlayerLimit();
                        lobby.setPlayerLimit(Integer.parseInt(args[1]));
                        YetAnotherWorldSwitcher.getCore().getMVWorldManager().getMVWorld(lobby.getName()).setPlayerLimit(Integer.parseInt(args[1]));

                        if (sender instanceof Player)
                        {
                            Player player = (Player) sender;
                            player.sendMessage("Updated the playerlimit in world " + ChatColor.GOLD + ChatColor.ITALIC + lobby.getName() + ChatColor.RESET + " from " + ChatColor.GOLD + temp + ChatColor.RESET + " to " + ChatColor.GOLD + args[1] + ChatColor.RESET + " players.");
                        }
                        yaws.getLogger().info("Updated the playerlimit in world " + lobby.getName() + " from " + temp + " to " + args[1] + " players.");
                        return true;
                    }
                    else //Error-message if second value is not a number
                    {
                        if (sender instanceof Player) //Test if player
                        {
                            Player player = (Player) sender;
                            player.sendMessage(ChatColor.RED + "Error: Value " + ChatColor.GOLD + args[1] + ChatColor.RED + " is not a number");
                        }
                        else //Error-message if console
                        {
                            yaws.getLogger().info("Value " + args[1] + " is not a number");
                        }
                        return false;
                    }
                }
            }
        }
        else //Error
        {
            yaws.getLogger().warning("An unknown error occurred whilst trying to change the playerlimit. Please contact the developer of this plugin");
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
        if(args.length > 0)
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
