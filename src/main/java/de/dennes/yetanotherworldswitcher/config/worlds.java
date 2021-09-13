package de.dennes.yetanotherworldswitcher.config;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import de.dennes.yetanotherworldswitcher.YetAnotherWorldSwitcher;
import de.dennes.yetanotherworldswitcher.worlds.lobby;
import de.dennes.yetanotherworldswitcher.worlds.lobbyList;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class worlds
{
    private Plugin plugin = YetAnotherWorldSwitcher.getPlugin(YetAnotherWorldSwitcher.class);
    private File worldsFile = null;
    private FileConfiguration worldsYML = new YamlConfiguration();
    private MultiverseCore core;

    public worlds(MultiverseCore mCore)
    {
        core = mCore;
        saveDefaultWorldsYML();
    }

    private void reloadWorlds()
    {
        if(worldsFile == null)
        {
            worldsFile = new File(plugin.getDataFolder(),"worlds.yml");
        }
        worldsYML = YamlConfiguration.loadConfiguration(worldsFile);
        InputStream defaultStream = plugin.getResource("worlds.yml");

        if(defaultStream != null)
        {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            worldsYML.setDefaults(defaultConfig);
        }
    }

    private FileConfiguration getWorldsYML()
    {
        if(worldsYML == null)
        {
            reloadWorlds();
        }
        return worldsYML;
    }

    private void saveWorldsYML()
    {
        if(worldsYML == null || worldsFile == null)
        {
            return;
        }
        try{
            getWorldsYML().save(worldsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save worlds.yml to " + worldsFile, e);
        }
    }

    private void saveDefaultWorldsYML()
    {
        if(worldsFile == null)
        {
            worldsFile = new File(plugin.getDataFolder(), "worlds.yml");
        }
        if(!worldsFile.exists())
        {
            worldsFile.getParentFile().mkdirs();
            plugin.saveResource("worlds.yml", false);
            writeWorldsToFile();
        }

        reloadWorlds();
    }

    private void writeWorldsToFile()
    {
        boolean access, isClosed;
        String aliases, itemOverwrites; //ToDo: Entweder moch ausführen, wenn existiert oder 2 get-methoden schreiben

        try {
            getWorldsYML().load(worldsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        MVWorldManager worldManager = core.getMVWorldManager();
        Collection<MultiverseWorld> worldsUnf = worldManager.getMVWorlds();

        for(MultiverseWorld world : worldsUnf)
        {
            access = getAccessibleWorlds().contains(world.getName());
            isClosed = getClosedWorlds().contains(world.getName());

            String path = "worlds." + world.getName();

            getWorldsYML().set(path + ".unchangeables.name",world.getName());
            getWorldsYML().set(path + ".alias","");
            getWorldsYML().set(path + ".itemOverwrite","");
            getWorldsYML().set(path + ".slotOverwrite","");
            getWorldsYML().set(path + ".accessible",access);
            getWorldsYML().set(path + ".isClosed",isClosed);

            if(plugin.getConfig().getBoolean("DisplayMoreInfo"))
            {
                path += ".unchangeables";
                getWorldsYML().set(path + ".permission", world.getAccessPermission().getName());
                getWorldsYML().set(path + ".spawnLocation.x", world.getSpawnLocation().getX());
                getWorldsYML().set(path + ".spawnLocation.y", world.getSpawnLocation().getY());
                getWorldsYML().set(path + ".spawnLocation.z", world.getSpawnLocation().getZ());
                getWorldsYML().set(path + ".spawnLocation.pitch", world.getSpawnLocation().getPitch());
                getWorldsYML().set(path + ".spawnLocation.yaw", world.getSpawnLocation().getYaw());
                getWorldsYML().set(path + ".playerLimit", world.getPlayerLimit());
            }
        }
        saveWorldsYML();
    }

    private ArrayList<String> getAccessibleWorlds()
    {
        ArrayList<String> worldList = new ArrayList<>();

        MVWorldManager worldManager = core.getMVWorldManager();
        Collection<MultiverseWorld> worldsUnf = worldManager.getMVWorlds();

        for(MultiverseWorld world : worldsUnf)
        {
            String path = "worlds." + world.getName();
            String worldName = world.getName();

            if(getWorldsYML().getBoolean(path + ".accessible"))
            {
                worldList.add(worldName);
            }
        }
        return worldList;
    }

    private ArrayList<String> getClosedWorlds()
    {
        ArrayList<String> worldList = new ArrayList<>();

        MVWorldManager worldManager = core.getMVWorldManager();
        Collection<MultiverseWorld> worldsUnf = worldManager.getMVWorlds();

        for(MultiverseWorld world : worldsUnf)
        {
            String path = "worlds." + world.getName();
            String worldName = world.getName();

            if(getWorldsYML().getBoolean(path + ".isClosed"))
            {
                worldList.add(worldName);
            }
        }
        return worldList;
    }

    public void importWorldsFromFile()
    {
        MVWorldManager worldManager = core.getMVWorldManager();
        Collection<MultiverseWorld> worldsUnf = worldManager.getMVWorlds();

        List<String> lobbyNames = getAccessibleWorlds();
        //List<String> closedLobbyNames = new worlds(core).getClosedWorlds();

        for(MultiverseWorld world : worldsUnf)
        {
            if(lobbyNames.contains(world.getName()))
            {
                String path = "worlds." + world.getName();
                new lobby(getWorldsYML().getString(path + ".unchangeables.name"),
                        getWorldsYML().getString(path +".alias"),
                        getWorldsYML().getString(path +".itemOverwrite"),
                        getWorldsYML().getInt(path +".slotOverwrite"),
                        world.getPlayerLimit(),
                        true,
                        getWorldsYML().getBoolean(path +".isClosed")
                );
            }
        }
        //List <lobby> test = lobbyList.getLobbies();
    }

    public void exportLobbiesToFile()
    {
        try
        {
            worldsYML.load(worldsFile);
            MVWorldManager worldManager = core.getMVWorldManager();
            Collection<MultiverseWorld> worldsUnf = worldManager.getMVWorlds();

            importWorldsFromFile();

            lobbyList lL = new lobbyList();
            final List<lobby> lobbies = lL.getLobbies();

            for(lobby map : lobbies)
            {
                String mapName = map.getName();
                String path = "worlds." + mapName;

                getWorldsYML().set(path + ".unchangeables.name", mapName);
                getWorldsYML().set(path + ".alias",map.getAlias());
                getWorldsYML().set(path + ".itemOverwrite",map.getItemOverwrite());
                getWorldsYML().set(path + ".slotOverwrite",map.getSlotOverwrite());
                getWorldsYML().set(path + ".accessible",map.isAccessible());
                getWorldsYML().set(path + ".isClosed",map.isClosed());

                if(plugin.getConfig().getBoolean("DisplayMoreInfo"))
                {
                    for (MultiverseWorld world : worldsUnf)
                    {
                        if (world.getName().equals(map.getName()))
                        {
                            path += ".unchangeables";
                            getWorldsYML().set(path + ".permission", world.getAccessPermission().getName());
                            getWorldsYML().set(path + ".spawnLocation.x", world.getSpawnLocation().getX());
                            getWorldsYML().set(path + ".spawnLocation.y", world.getSpawnLocation().getY());
                            getWorldsYML().set(path + ".spawnLocation.z", world.getSpawnLocation().getZ());
                            getWorldsYML().set(path + ".spawnLocation.pitch", world.getSpawnLocation().getPitch());
                            getWorldsYML().set(path + ".spawnLocation.yaw", world.getSpawnLocation().getYaw());
                            getWorldsYML().set(path + ".playerLimit", world.getPlayerLimit());
                            break;
                        }
                    }
                }
            }
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
        saveWorldsYML();
    }
}
