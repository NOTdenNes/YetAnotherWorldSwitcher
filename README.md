# About YAWS:

Do you know this situation: You are in the overworld and you wanna go to the nether, but you're too lazy to walk 10 block to your netherportal? Or are you too afraid to enter an end-portal because you have a portal-phobia? Seek no more. Using my quite user-friendly plugin these problems belong to the past. With just two(!) clicks, you can warp between worlds and dimensions

# **FEATURES**


### Default configuration:

![Example_2](https://user-images.githubusercontent.com/87833245/133897358-cac4761a-685d-419a-b925-9ca3218d5a61.png)


### Another example configuration:

![Example_1](https://user-images.githubusercontent.com/87833245/133897365-b3875b68-645d-4d33-9e45-4d4e781499d3.png)

#### Worldswitching menu:
   * Customizable items
   * Customizable itemnames (Supporting colorcodes)
   * Customizable item-positions
   * Custom menusize, depending on the amount of worlds you have (44 max)
#### Worlditems:
   * Option to lock worlds for players without a certain permission
   * Shows if a world is locked or not
   * Shows the amount of current and possible players per world
 
# **COMMANDS & PERMISSIONS**

#### Basic commands: (optional permission node: yaws.command.default.*)
 * /yaws
    * required Permission (if not optional node used): yaws.command.yaws
    * Description: Displays all available commands, depending on permissions
 * /switchmenu
    * required Permission: yaws.command.sm
    * Description: Opens a menu in order to switch between worlds
 * /switchworld <world>
    * required Permission: yaws.command.sw
    * Description: Manually sends a player to another world
 * /whereami
    * required Permission: yaws.command.loc
    * Description: Tells a player in which world a player is

#### Management commands: (optional permission node: yaws.command.map.*)
 * /closemap [world]
    * required Permission: yaws.command.map.lock
    * Description: Declines access to a map without the required permission
 * /openmap [world]
    * required Permission: yaws.command.map.unlock
    * Description: Allows every player to enter a map regardless of permission
 * /playerlimit [world] [value]
    * required Permission: yaws.command.map.pl
    * Description: Sets the playerlimit for a world. Gets the playerlimit if no value is provided

##### Other permissions:
  * yaws.switch.closed
  * Description: Permission node used for players / groups allowing them to join closed worlds
  
# **DEPENDENCIES**
 * [Multiverse](https://www.spigotmc.org/resources/multiverse-core.390/) (required)
  
## BASIC TUTORIAL
  1. Set up your spigot-server (1.17 or higher)
  2. Put the plugins (Yaws & Multiverse) in your servers' plugin-folder
  3. Start the server once and close it again
  4. After the first start, the plugin creates a worlds.yml-File based on the worlds within your server-folder. Each world looks like this: 
```
worlds:
     world:
          unchangeables:               (Changing values in this category don't have an effect to the plugin)
               name: world      
               playerLimit: -1         (This can just be changed using the command /playerlimit)
          alias: §a§lOverworld         (the alias supports colorcodes)
          itemOverwrite: grass_block
          slotOverwrite: 11            (valid values go from 9 to 44)
          accessible: true             (If accessible = false, the world will not be used by this plugin)
          isClosed: false              (If isclosed = true, players need a permission in order to join said world)
```
  
  5. Set up your config.yml to whatever you want
  6. Set up your permissions. Example using the default permissions.yml:
```
server.basics:
    default: not-op
    children:
        yaws.command.default.*: true
```

  
## PLANNED FEATURES
  * [ ] A messages.yml will be added to customize messages (duh)
  * [ ] A update-class will be added to notify if an update for the plugin is available
