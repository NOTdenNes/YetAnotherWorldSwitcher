package de.dennes.yetanotherworldswitcher.events;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import de.dennes.yetanotherworldswitcher.YetAnotherWorldSwitcher;
import de.dennes.yetanotherworldswitcher.userInterface.lobbyWindow;
import de.dennes.yetanotherworldswitcher.worlds.lobby;
import de.dennes.yetanotherworldswitcher.worlds.lobbyList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class listeners implements Listener
{
    private final Plugin plugin = YetAnotherWorldSwitcher.getPlugin(YetAnotherWorldSwitcher.class);
    FileConfiguration config = plugin.getConfig();

    @EventHandler
    public void onLobbyClick(final InventoryClickEvent e)
    {
        if(!(e.getWhoClicked() instanceof Player)) //Test if event is triggered by stg else than a player
        {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        UUID playerUUID = p.getUniqueId();

        UUID inventoryUUID = lobbyWindow.openLobbyWindows.get(playerUUID);
        if(inventoryUUID != null)
        {
            e.setCancelled(true);
            final ItemStack clickedItem = e.getCurrentItem();

            //int testSlot = e.getRawSlot();
            //int testSize = lobbyWindow.inventoriesByUUID.get(inventoryUUID).getSize();

            if(clickedItem == null || clickedItem.getType().isAir() || clickedItem.getType() == Material.LIGHT_GRAY_STAINED_GLASS_PANE || e.getRawSlot() < 0 || e.getRawSlot() >= lobbyWindow.inventoriesByUUID.get(inventoryUUID).getSize()) //Test if clicked Item is not valid
            {
                return;
            }

            lobby gotoLobby = calcGoToLobby(e), currentLobby = calcCurrentLobby(p);

            //Define basic values

            MVWorldManager mv = YetAnotherWorldSwitcher.getCore().getMVWorldManager();

            if (clickedItem.getType() == Material.valueOf(config.getString("CloseMenuItem").toUpperCase())) //Test if closing-item
            {
                p.closeInventory();
                return;
            }

            String lobbyName;
            Location loc;
            if(plugin.getConfig().getBoolean("UseAliases") && !gotoLobby.getItemOverwrite().equals("")) //Test if alias should be shown (if set) instead of default value
            {
                lobbyName = gotoLobby.getAlias();
                loc = mv.getMVWorld(gotoLobby.getName()).getSpawnLocation();
            }
            else
            {
                lobbyName = gotoLobby.getName();
                loc = mv.getMVWorld(lobbyName).getSpawnLocation();
            }

            //Define which world the player is sent to

            if (clickedItem.getType() == Material.valueOf(config.getString("ClosedWorldItem").toUpperCase())) //Test if closed world
            {
                if (p.hasPermission("yaws.switch.closed")) //Test if player has permission to enter closed worlds
                {
                    if(currentLobby.getCurrPlayers() > 0) //Test if world has more than 0 players
                    {
                        currentLobby.setCurrPlayers(currentLobby.getCurrPlayers() - 1); //Reduces the currentplayercount by 1 if more than 0 players
                    }
                    p.teleport(loc);
                    p.sendMessage("Successfully teleported to world " + ChatColor.GOLD + lobbyName); //Teleports the player and send a message if successful
                    gotoLobby.setCurrPlayers(gotoLobby.getCurrPlayers() + 1); //Adds 1 to currentplayercount
                }
                else
                {
                    p.sendMessage(ChatColor.RED + "You do not have the permission to join " + ChatColor.GOLD + lobbyName);
                }
            }
            else if (clickedItem.getType() == Material.valueOf(config.getString("CurrentWorldItem").toUpperCase())) //Test if current world
            {
                p.sendMessage(ChatColor.RED + "You are already where you wanna be");
            }
            else if (clickedItem.getType() == Material.valueOf(config.getString("FullWorldItem").toUpperCase())) //Test if full World
            {
                p.sendMessage(ChatColor.RED + "That world has already " + ChatColor.GOLD + gotoLobby.getCurrPlayers() + ChatColor.RED + " out of " + ChatColor.GOLD + gotoLobby.getPlayerLimit() + ChatColor.RED + " players");
            }
            else if (clickedItem.getType() == Material.valueOf(config.getString("AvailableWorldItem").toUpperCase())) //Test if available world
            {
                if(currentLobby.getCurrPlayers() > 0) //Test if world has more than 0 players
                {
                    currentLobby.setCurrPlayers(currentLobby.getCurrPlayers() - 1); //Reduces the currentplayercount by 1 if more than 0 players
                }
                p.teleport(loc);
                p.sendMessage("Successfully teleported to world " + ChatColor.GOLD + lobbyName); //Teleports the player and send a message if successful
                gotoLobby.setCurrPlayers(gotoLobby.getCurrPlayers() + 1); //Adds 1 to currentplayercount
            }
            else if(plugin.getConfig().getBoolean("UseItemOverwrites")) //Test if ItemOverwrite
            {
                if(!gotoLobby.getItemOverwrite().equals("") && clickedItem.getType() == Material.valueOf(gotoLobby.getItemOverwrite().toUpperCase())) //Test if there's no overwritten item and if the clicked item is the correct item
                {
                    boolean doubles = false;

                    if(!gotoLobby.getName().equals(currentLobby.getName()) && gotoLobby.getItemOverwrite().equalsIgnoreCase(currentLobby.getItemOverwrite())) //Test if there's just one overwritten item per world
                    {
                        doubles = true;
                        plugin.getLogger().warning("Double itemOverwrites found for '" + gotoLobby.getName() + "' and '" + currentLobby.getName() + "'. Said item is '" + gotoLobby.getItemOverwrite() + "'. Please change one of them");
                    }

                    if(gotoLobby == currentLobby) //Test if current world is goto world
                    {
                        p.sendMessage(ChatColor.RED + "You are already where you wanna be");
                    }
                    else if(gotoLobby.getCurrPlayers() == gotoLobby.getPlayerLimit()) //Test if world is full
                    {
                        p.sendMessage(ChatColor.RED + "That world has already " + ChatColor.GOLD + gotoLobby.getCurrPlayers() + ChatColor.RED + " out of " + ChatColor.GOLD + gotoLobby.getPlayerLimit() + ChatColor.RED + " players");
                    }
                    else if(gotoLobby.isClosed() && !p.hasPermission("yaws.switch.closed")) //Test if closed and no permission
                    {
                        p.sendMessage(ChatColor.RED + "You do not have the permission to join " + ChatColor.GOLD + lobbyName);
                    }
                    else if(!doubles && (!gotoLobby.isClosed() || p.hasPermission("yaws.switch.closed"))) //Test if no double itemOverwrites, lobby is not closed and player has permission to enter, if necessary
                    {
                        if(currentLobby.getCurrPlayers() > 0) //Test if world has more than 0 players
                        {
                            currentLobby.setCurrPlayers(currentLobby.getCurrPlayers() - 1); //Reduces the currentplayercount by 1 if more than 0 players
                        }
                        p.teleport(loc);
                        p.sendMessage("Successfully teleported to world " + ChatColor.GOLD + lobbyName); //Teleports the player and send a message if successful
                        gotoLobby.setCurrPlayers(gotoLobby.getCurrPlayers() + 1);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLobbyClose(InventoryCloseEvent e)
    {
        Player player = (Player) e.getPlayer();
        UUID playerUUID = player.getUniqueId();

        lobbyWindow.openLobbyWindows.remove(playerUUID);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e)
    {
        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();

        lobbyWindow.openLobbyWindows.remove(playerUUID);

        lobby currentLobby = calcCurrentLobby(player);
        if(currentLobby.getCurrPlayers() > 0)
        {
            currentLobby.setCurrPlayers(currentLobby.getCurrPlayers() - 1);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        lobby currentLobby = calcCurrentLobby(e.getPlayer());
        currentLobby.setCurrPlayers(currentLobby.getCurrPlayers() + 1);

        if (config.getBoolean("NavUIItemVisible"))
        {
            Player p = e.getPlayer();
            ItemStack itemInNavSlot = p.getInventory().getItem(plugin.getConfig().getInt("NavUIItemSlotID"));

            if((itemInNavSlot == null) || !(itemInNavSlot.isSimilar(getNavItem()))) //(itemInNavSlot.getItemMeta().isUnbreakable() != getNavItem().getItemMeta().isUnbreakable()))
            {
                if(itemInNavSlot != getNavItem() && itemInNavSlot != null)
                {
                    p.getWorld().dropItemNaturally(p.getLocation(), itemInNavSlot); //drop);
                    p.sendMessage(ChatColor.RED + "There was no free space in your hotbar for the 'WorldSwitcher' so it got dropped at your current location");
                }
                if(itemInNavSlot == null || itemInNavSlot.getAmount() < 1)
                {
                    p.getInventory().setItem(plugin.getConfig().getInt("NavUIItemSlotID"), getNavItem());
                }
            }
            if(Arrays.stream(p.getInventory().getContents()).filter(itemStack -> itemStack != null && itemStack.isSimilar(getNavItem())).mapToInt(ItemStack::getAmount).sum() > 1)
            {
                ItemStack delStack = getNavItem();
                for(int i = 1; i <= 64; i++)
                {
                    delStack.setAmount(i);
                    p.getInventory().remove(delStack);
                }
                p.getInventory().setItem(plugin.getConfig().getInt("NavUIItemSlotID"), getNavItem());
            }
        }
        else
        {
            Player p = e.getPlayer();
            if((p.getInventory().getItem(plugin.getConfig().getInt("NavUIItemSlotID")) == null) || (p.getInventory().getItem(plugin.getConfig().getInt("NavUIItemSlotID")).isSimilar(getNavItem())))
            {
                p.getInventory().remove(getNavItem());
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryClickEvent e)
    {
        if(config.getBoolean("NavUIItemVisible"))
        {
            if(e.getCurrentItem() != null && e.getCurrentItem().isSimilar(getNavItem()))
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler//(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        if(plugin.getConfig().getString("PlaceWorldsInMenu").equalsIgnoreCase("manually") || plugin.getConfig().getString("PlaceWorldsInMenu").equalsIgnoreCase("automatic"))
        {
            if (config.getBoolean("NavUIItemVisible"))
            {
                Player p = e.getPlayer();
                if (e.getItem() != null && p.getInventory().getHeldItemSlot() == plugin.getConfig().getInt("NavUIItemSlotID") && (e.getItem().isSimilar(getNavItem())))
                {
                    lobbyWindow lw = new lobbyWindow(p);
                    lw.openLobbySwitcher(p);
                }

            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent e)
    {
        if(plugin.getConfig().getBoolean("NavUIItemVisible"))
        {
            ItemStack item = e.getItemDrop().getItemStack();
            if(item.isSimilar(getNavItem()))
            {
                e.setCancelled(true);
            }
        }
        else
        {
            ItemStack item = e.getItemDrop().getItemStack();
            if(item.isSimilar(getNavItem()))
            {
                e.setCancelled(true);

                ItemStack delStack = getNavItem();
                for(int i = 1; i <= 64; i++)
                {
                    delStack.setAmount(i);
                    e.getPlayer().getInventory().remove(delStack);
                }
            }
        }

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e)
    {
        if(plugin.getConfig().getBoolean("NavUIItemVisible"))
        {
            Player p = e.getPlayer();
            p.getInventory().setItem(plugin.getConfig().getInt("NavUIItemSlotID"), getNavItem());
        }
    }

    private lobby calcGoToLobby(InventoryClickEvent e)
    {
        List<lobby> lobbies = lobbyList.getLobbies();

        if(plugin.getConfig().getString("PlaceWorldsInMenu").equalsIgnoreCase("manually")) //Test if manually and sets goto-Lobby and rawslot
        {
            for(lobby lobby : lobbies)
            {
                if(lobby.getSlotOverwrite() > 8 && lobby.getSlotOverwrite() < 45) //If manually & valid slotOverwrite
                {
                    if (e.getRawSlot() == lobby.getSlotOverwrite()) //Set rawSlot as slotOverwrite and set lobby where the player wants to go
                    {
                        return lobby;
                    }
                }
            }
        }
        else if(plugin.getConfig().getString("PlaceWorldsInMenu").equalsIgnoreCase("automatic")) //If automatic sets goto-lobby and rawslot
        {
            int rawSlot = (e.getCurrentItem().getAmount()) - 1; //Rawslot depending on location in inv
            for(lobby lobby : lobbies)
            {
                if(lobbies.get(rawSlot).getName().equalsIgnoreCase(lobby.getName())) //set lobby where the player wants to go
                {
                    return lobby;
                }
            }
        }
        else //Error if neither
        {
            plugin.getLogger().severe("Value " + plugin.getConfig().getString("PlaceWorldsInMenu") + " is not a valid value. Check your config.yml");
        }
        return null;
    }

    private lobby calcCurrentLobby(Player p)
    {
        for(lobby lobby : lobbyList.getLobbies()) //Sets the current world where the player is currently located
        {
            if(lobby.getName().equalsIgnoreCase(p.getLocation().getWorld().getName()))
            {
                return lobby;
            }
        }
        return null;
    }

    public ItemStack getNavItem()
    {
        ItemStack item = new ItemStack(Material.valueOf(config.getString("NavUIItem").toUpperCase()));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "World Switcher");
        meta.addEnchant(Enchantment.BINDING_CURSE,1,true);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.setUnbreakable(true);

        item.setItemMeta(meta);
        return item;
    }
}
