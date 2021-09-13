package de.dennes.yetanotherworldswitcher.commands;

import de.dennes.yetanotherworldswitcher.YetAnotherWorldSwitcher;
import de.dennes.yetanotherworldswitcher.worlds.lobby;
import de.dennes.yetanotherworldswitcher.worlds.lobbyList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class switchworld implements TabExecutor
{
    private final YetAnotherWorldSwitcher yaws;

    public switchworld(YetAnotherWorldSwitcher yaws)
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
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            for (lobby lobby : getLobbyList())
            {
                if(args.length == 1 && lobby.getName().equalsIgnoreCase(args[0]) && player.hasPermission("yaws.switch.closed"))
                {
                    Location loc = YetAnotherWorldSwitcher.getCore().getMVWorldManager().getMVWorld(args[0]).getSpawnLocation();
                    player.teleport(loc);

                    player.sendMessage("Successfully teleported to world " + ChatColor.GOLD + lobby.getName());
                    return true;
                }
                else if(args.length == 1 && lobby.getName().equalsIgnoreCase(args[0]) && !player.hasPermission("yaws.switch.closed"))
                {
                    player.sendMessage(ChatColor.RED + "You do not have the permission to join " + ChatColor.GOLD + lobby.getName());
                    return true;
                }
            }
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
