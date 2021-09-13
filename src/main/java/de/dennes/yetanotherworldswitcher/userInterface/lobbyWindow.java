package de.dennes.yetanotherworldswitcher.userInterface;

import de.dennes.yetanotherworldswitcher.YetAnotherWorldSwitcher;
import de.dennes.yetanotherworldswitcher.worlds.lobby;
import de.dennes.yetanotherworldswitcher.worlds.lobbyList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class lobbyWindow
{
    public static Map<UUID, UUID> openLobbyWindows = new HashMap<>();
    public static Map<UUID, lobbyWindow> inventoriesByUUID = new HashMap<>();

    private final Inventory inv;
    private UUID uuid;

    private Plugin plugin = YetAnotherWorldSwitcher.getPlugin(YetAnotherWorldSwitcher.class);
    private lobby currentLobby;

    public lobbyWindow(final Player p)
    {
        for(lobby lobby : getListOfLobbies())
        {
            if(lobby.getName().equalsIgnoreCase(p.getLocation().getWorld().getName()))
            {
                setCurrentLobby(lobby);
            }
        }

        inv = Bukkit.createInventory(null, getSize(), "World Selector");
        uuid = UUID.randomUUID();
        initializeItems(p);
        inventoriesByUUID.put(getUuid(), this);
    }

    private List<lobby> getListOfLobbies()
    {
        return lobbyList.getLobbies();
    }

    private long getMaxSlotID()
    {
        return getListOfLobbies()
                .stream()
                .max(Comparator.comparing(lobby::getSlotOverwrite))
                .orElseThrow(NoSuchElementException::new)
                .getSlotOverwrite();
    }

    private long getMinSlotID()
    {
        return getListOfLobbies()
                .stream()
                .min(Comparator.comparing(lobby::getSlotOverwrite))
                .orElseThrow(NoSuchElementException::new)
                .getSlotOverwrite();
    }

    private long calcNumberOfLobbys()
    {
        return getListOfLobbies()
                .stream()
                .filter(lobby::isAccessible)
                .count();
    }

    public void openLobbySwitcher(final Player p)
    {
        p.openInventory(inv);
        openLobbyWindows.put(p.getUniqueId(), getUuid());
    }

    private void initializeItems(Player p)
    {
        if(plugin.getConfig().getString("PlaceWorldsInMenu").equalsIgnoreCase("automatic"))
        {
            int slotNr = 0, lobbyNr = 0;
            if (calcNumberOfLobbys() < 28)
            {
                for (int i = 0; i < getSize(); i += 9) //TODO: Rewrite this like manual
                {
                    if (i == 0)
                    {
                        for (int j = 0; j < 9; j++)
                        {
                            inv.setItem(slotNr, createOtherGuiItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", ""));
                            slotNr++;
                        }
                    }
                    else if ((i - 9) < calcNumberOfLobbys())
                    {
                        for (int row = 0; row < (getSize() - 18) / 9; row++)
                        {
                            inv.setItem(slotNr, createOtherGuiItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", ""));
                            slotNr++;
                            for (int slot = 0; slot < 7; slot++)
                            {
                                if (lobbyNr < calcNumberOfLobbys())
                                {
                                    inv.setItem(slotNr, createLobbyGuiItem(lobbyNr, p));
                                    lobbyNr++;
                                }
                                else
                                {
                                    inv.setItem(slotNr, createOtherGuiItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", ""));
                                }
                                slotNr++;
                            }
                            inv.setItem(slotNr, createOtherGuiItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", ""));
                            slotNr++;
                        }
                    }
                    else
                    {
                        for (int k = 0; k < 9; k++)
                        {
                            inv.setItem(slotNr, createOtherGuiItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", ""));
                            slotNr++;
                        }
                        inv.setItem((getSize() - 5), createOtherGuiItem(Material.valueOf(plugin.getConfig().getString("CloseMenuItem").toUpperCase()), ChatColor.RED + " >> Close Window << ", ""));
                        slotNr++;
                    }
                }
            }
            else if (calcNumberOfLobbys() >= 29 && calcNumberOfLobbys() <= 45)
            {
                for (int slot = 0; slot < calcNumberOfLobbys(); slot++)
                {
                    inv.setItem(slotNr, createLobbyGuiItem(lobbyNr, p));
                    slotNr++;
                    lobbyNr++;
                }
                inv.setItem((getSize() - 5), createOtherGuiItem(Material.valueOf(plugin.getConfig().getString("CloseMenuItem").toUpperCase()), ChatColor.RED + " >> Close Window << ", ""));
            }
            else
            {
                System.err.print("Invalid number of lobbys. Amount of lobbies is limited at 44");
            }
        }
        else if(plugin.getConfig().getString("PlaceWorldsInMenu").equalsIgnoreCase("manually"))
        {
            if(getMinSlotID() > 8 && getMaxSlotID() < 45)
            {
                for (int i = 0; i < getSize(); i++)
                {
                    inv.setItem(i, createOtherGuiItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", ""));
                }
                int l = 0;
                for (lobby lobby : getListOfLobbies())
                {
                    inv.setItem(lobby.getSlotOverwrite(), createLobbyGuiItem(l, p));
                    l++;
                }
                inv.setItem((getSize() - 5), createOtherGuiItem(Material.valueOf(plugin.getConfig().getString("CloseMenuItem").toUpperCase()), ChatColor.RED + " >> Close Window << ", ""));
            }
            else
            {
                if(getMaxSlotID() > 44)
                {
                    for (lobby lobby : getListOfLobbies()) {
                        if (lobby.getSlotOverwrite() == getMaxSlotID()) {
                            plugin.getLogger().severe("Highest possible slotOverwrite is 44. Yours is " + getMaxSlotID() + ", belonging to " + lobby.getName());
                            break;
                        }
                    }
                }
                if(getMinSlotID() < 9)
                {
                    for (lobby lobby : getListOfLobbies()) {
                        if (lobby.getSlotOverwrite() == getMaxSlotID()) {
                            plugin.getLogger().severe("Lowest possible slotOverwrite is 9. Yours is " + getMinSlotID() + ", belonging to " + lobby.getName());
                            break;
                        }
                    }
                }
            }
        }
        else
        {
            plugin.getLogger().severe("Value " + plugin.getConfig().getString("PlaceWorldsInMenu") + " is not valid. Check your config.yml or contact the plugin's developer");
        }
    }

    private ItemStack createLobbyGuiItem(int lobbyNr, Player p)
    {
        ItemStack item;
        ItemMeta meta;
        String configItem;
        FileConfiguration config = plugin.getConfig();
        boolean isCurrent = false;
        int itemAmount = 1;
        String nameSuffix = "", loreConnect = "";

        String testWorld = getListOfLobbies().get(lobbyNr).getName();

        if ("automatic".equals(config.getString("PlaceWorldsInMenu")))
        {
            itemAmount = lobbyNr + 1;
        }

        if(getListOfLobbies().get(lobbyNr) == getCurrentLobby())
        {
            isCurrent = true;
        }

        if (!(getListOfLobbies().get(lobbyNr).isClosed()) && !isCurrent)
        {
            if ((getListOfLobbies().get(lobbyNr).getCurrPlayers() >= getListOfLobbies().get(lobbyNr).getPlayerLimit()) && (getListOfLobbies().get(lobbyNr).getPlayerLimit() != -1)) //Full World
            {
                if(plugin.getConfig().getBoolean("UseItemOverwrites") && !getListOfLobbies().get(lobbyNr).getItemOverwrite().equals(""))
                {
                        configItem = getListOfLobbies().get(lobbyNr).getItemOverwrite().toUpperCase();
                }
                else
                {
                    configItem = config.getString("FullWorldItem").toUpperCase();
                }
                item = new ItemStack(Material.valueOf(configItem), itemAmount);
                meta = item.getItemMeta();

                loreConnect = ChatColor.RED + "Unable to connect!";

                assert meta != null;
                if(plugin.getConfig().getBoolean("UseAliases") && !getListOfLobbies().get(lobbyNr).getItemOverwrite().equals(""))
                {
                    meta.setDisplayName(getListOfLobbies().get(lobbyNr).getAlias() + nameSuffix);
                }
                else
                {
                    meta.setDisplayName(ChatColor.GREEN + "Lobby #" + lobbyNr + nameSuffix);
                }
                meta.setLore(Arrays.asList(ChatColor.GRAY + "Players: " + getListOfLobbies()
                            .get(lobbyNr)
                            .getCurrPlayers() + "/" + getListOfLobbies()
                            .get(lobbyNr)
                            .getPlayerLimit(), "", loreConnect));
                    item.setItemMeta(meta);
                return item;
            }
            else if ((getListOfLobbies().get(lobbyNr).getCurrPlayers() < getListOfLobbies().get(lobbyNr).getPlayerLimit()) || (getListOfLobbies().get(lobbyNr).getPlayerLimit() == -1)) //Available World
            {
                if(plugin.getConfig().getBoolean("UseItemOverwrites") && !getListOfLobbies().get(lobbyNr).getItemOverwrite().equals(""))
                {
                    configItem = getListOfLobbies()
                            .get(lobbyNr)
                            .getItemOverwrite()
                            .toUpperCase();
                }
                else
                {
                    configItem = config
                            .getString("AvailableWorldItem")
                            .toUpperCase();
                }
                item = new ItemStack(Material.valueOf(configItem), itemAmount);
                meta = item.getItemMeta();

                loreConnect = ChatColor.YELLOW + "Click here to connect!";

                assert meta != null;
                if(plugin.getConfig().getBoolean("UseAliases") && !getListOfLobbies().get(lobbyNr).getItemOverwrite().equals(""))
                {
                    meta.setDisplayName(getListOfLobbies().get(lobbyNr).getAlias() + nameSuffix);
                }
                else
                {
                    meta.setDisplayName(ChatColor.GREEN + "Lobby #" + lobbyNr + nameSuffix);
                }

                if (getPlayerLimit(getListOfLobbies().get(lobbyNr).getName()) == -1)
                {
                    meta.setLore(Arrays.asList(ChatColor.GRAY + "Players: unlimited", "", loreConnect));
                }
                else if (getPlayerLimit(getListOfLobbies().get(lobbyNr).getName()) > 0)
                {
                    meta.setLore(Arrays.asList(ChatColor.GRAY + "Players: " + getListOfLobbies()
                            .get(lobbyNr)
                            .getCurrPlayers() + "/" + getListOfLobbies()
                            .get(lobbyNr)
                            .getPlayerLimit(), "", loreConnect));
                }
                item.setItemMeta(meta);
                return item;
            }
            else
            {
                throw new IllegalStateException("Unexpected value in worlds.yml: (Playerlimit: " + getListOfLobbies().get(lobbyNr).getPlayerLimit() + ")");
            }
        }
        else if (getListOfLobbies().get(lobbyNr).isClosed() && !isCurrent && (plugin.getConfig().getBoolean("SeeClosedWorlds") || p.hasPermission("yaws.switch.closed"))) //Closed World
        {
            if(plugin.getConfig().getBoolean("UseItemOverwrites") && !getListOfLobbies().get(lobbyNr).getItemOverwrite().equals(""))
            {
                configItem = getListOfLobbies()
                        .get(lobbyNr)
                        .getItemOverwrite()
                        .toUpperCase();
            }
            else
            {
                configItem = config
                        .getString("ClosedWorldItem")
                        .toUpperCase();
            }
            item = new ItemStack(Material.valueOf(configItem), itemAmount);
            meta = item.getItemMeta();

            nameSuffix = ChatColor.RED + " [CLOSED] ";
            if(p.hasPermission("yaws.switch.closed"))
            {
                loreConnect = ChatColor.YELLOW + "Click here to connect!";
            }
            else
            {
                loreConnect = ChatColor.RED + "No permission to join!";
            }

            assert meta != null;
            if(plugin.getConfig().getBoolean("UseAliases") && !getListOfLobbies().get(lobbyNr).getItemOverwrite().equals(""))
            {
                meta.setDisplayName(getListOfLobbies().get(lobbyNr).getAlias() + nameSuffix);
            }
            else
            {
                meta.setDisplayName(ChatColor.GREEN + "Lobby #" + lobbyNr + nameSuffix);
            }

            if (getPlayerLimit(getListOfLobbies().get(lobbyNr).getName()) == -1)
            {
                meta.setLore(Arrays.asList(ChatColor.GRAY + "Players: none", "", loreConnect));
            }
            else if (getPlayerLimit(getListOfLobbies().get(lobbyNr).getName()) > 0)
            {
                meta.setLore(Arrays.asList(ChatColor.GRAY + "Players: ?/?", "", loreConnect));
            }

            item.setItemMeta(meta);
            return item;
        }
        else if(isCurrent) //Current World
        {
            if(plugin.getConfig().getBoolean("UseItemOverwrites") && !getListOfLobbies().get(lobbyNr).getItemOverwrite().equals(""))
            {
                configItem = getListOfLobbies()
                        .get(lobbyNr)
                        .getItemOverwrite()
                        .toUpperCase();
            }
            else
            {
                configItem = config
                        .getString("CurrentWorldItem")
                        .toUpperCase();
            }
            item = new ItemStack(Material.valueOf(configItem), itemAmount);
            meta = item.getItemMeta();

            loreConnect = ChatColor.RED + "Current location!";

            assert meta != null;
            if(plugin.getConfig().getBoolean("UseAliases") && !getListOfLobbies().get(lobbyNr).getItemOverwrite().equals(""))
            {
                meta.setDisplayName(getListOfLobbies().get(lobbyNr).getAlias() + nameSuffix);
            }
            else
            {
                meta.setDisplayName(ChatColor.GREEN + "Lobby #" + lobbyNr + nameSuffix);
            }

            if (getPlayerLimit(getListOfLobbies().get(lobbyNr).getName()) == -1)
            {
                meta.setLore(Arrays.asList(ChatColor.GRAY + "Players: unlimited", "", loreConnect));
            }
            else if (getPlayerLimit(getListOfLobbies().get(lobbyNr).getName()) > 0)
            {
                meta.setLore(Arrays.asList(ChatColor.GRAY + "Players: " + getListOfLobbies()
                        .get(lobbyNr)
                        .getCurrPlayers() + "/" + getListOfLobbies()
                        .get(lobbyNr)
                        .getPlayerLimit(), "", loreConnect));
            }

            item.setItemMeta(meta);
            return item;
        }
        else
        {
            item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            meta = item.getItemMeta();
            meta.setDisplayName(" ");

            item.setItemMeta(meta);

            return item;
        }
    }

    protected ItemStack createOtherGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    public void delete(){
        for (Player p : Bukkit.getOnlinePlayers()){
            UUID u = openLobbyWindows.get(p.getUniqueId());
            if (u.equals(getUuid())){
                p.closeInventory();
            }
        }
        inventoriesByUUID.remove(getUuid());
    }

    private int getPlayerLimit(String lobbyName)
    {
        return getListOfLobbies()
                .stream()
                .filter((lobby) -> lobby.getName().equals(lobbyName))
                .findAny()
                .get()
                .getPlayerLimit();
    }

    public int getSize()
    {
        double size = 0;
        int div = 9;
        if(plugin.getConfig().getString("PlaceWorldsInMenu").equalsIgnoreCase("automatic"))
        {
            if (calcNumberOfLobbys() <= 28) {
                size = (Math.ceil((double) calcNumberOfLobbys() / div) * div) + 18;
            } else if (calcNumberOfLobbys() <= 45) {
                size = (Math.ceil((double) calcNumberOfLobbys() / div) * div) + 9;
            }
        }
        else if(plugin.getConfig().getString("PlaceWorldsInMenu").equalsIgnoreCase("manually"))
        {
            size = (Math.ceil((double) getMaxSlotID() / div) * div) + 9; //18
        }
        return (int) size;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public static Map<UUID, UUID> getOpenLobbyWindows() {
        return openLobbyWindows;
    }

    public static Map<UUID, lobbyWindow> getInventoriesByUUID() {
        return inventoriesByUUID;
    }

    public lobby getCurrentLobby()
    {
        return currentLobby;
    }

    public void setCurrentLobby(lobby currentLobby)
    {
        this.currentLobby = currentLobby;
    }
}
