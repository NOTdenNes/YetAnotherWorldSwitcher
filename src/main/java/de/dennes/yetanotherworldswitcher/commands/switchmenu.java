package de.dennes.yetanotherworldswitcher.commands;

import de.dennes.yetanotherworldswitcher.YetAnotherWorldSwitcher;
import de.dennes.yetanotherworldswitcher.userInterface.lobbyWindow;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class switchmenu implements CommandExecutor
{
    private final YetAnotherWorldSwitcher yaws;

    public switchmenu(YetAnotherWorldSwitcher yaws)
    {
        this.yaws = yaws;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;

            lobbyWindow lw = new lobbyWindow(player);
            lw.openLobbySwitcher(player);
        }
        else
        {
            yaws.getLogger().info("You need to be a player. Sowwy :(");
        }
        return true;
    }
}
