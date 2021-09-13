package de.dennes.yetanotherworldswitcher.worlds;

public class lobby
{
    private String name;
    private String alias;
    private String itemOverwrite;
    private int slotOverwrite;
    private int playerLimit;
    private int currPlayers;
    private boolean accessible;
    private boolean isClosed;

    public lobby(String name)
    {
        this.name = name;
        this.alias = name;
        this.itemOverwrite = null;
        this.slotOverwrite = 0;
        this.playerLimit = -1;
        this.currPlayers = 0;
        this.accessible = false;
        this.isClosed = false;
        lobbyList.addToLobbyList(this);
    }

    public lobby(String name, String alias, String itemOverwrite, int slotOverwrite, int playerLimit, boolean accessible, boolean isClosed) {
        this.name = name;
        this.alias = alias;
        this.itemOverwrite = itemOverwrite;
        this.slotOverwrite = slotOverwrite;
        this.playerLimit = playerLimit;
        this.currPlayers = 0;
        this.accessible = accessible;
        this.isClosed = isClosed;
        lobbyList.addToLobbyList(this);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getPlayerLimit()
    {
        return playerLimit;
    }

    public void setPlayerLimit(int playerLimit)
    {
        this.playerLimit = playerLimit;
    }

    public int getCurrPlayers()
    {
        return currPlayers;
    }

    public void setCurrPlayers(int currPlayers)
    {
        this.currPlayers = currPlayers;
    }

    public boolean isAccessible()
    {
        return accessible;
    }

    public void setAccessible(boolean accessible)
    {
        this.accessible = accessible;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed)
    {
        isClosed = closed;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getItemOverwrite() {
        return itemOverwrite;
    }

    public void setItemOverwrite(String itemOverwrite) {
        this.itemOverwrite = itemOverwrite;
    }

    public int getSlotOverwrite() {
        return slotOverwrite;
    }

    public void setSlotOverwrite(int slotOverwrite) {
        this.slotOverwrite = slotOverwrite;
    }
}
