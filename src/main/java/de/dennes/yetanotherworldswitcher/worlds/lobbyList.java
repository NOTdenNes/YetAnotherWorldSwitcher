package de.dennes.yetanotherworldswitcher.worlds;

import java.util.ArrayList;
import java.util.List;

public class lobbyList
{
    private static List<lobby> lobbies = new ArrayList<>();

    public lobbyList()
    {

    }

    public static void addToLobbyList(lobby lobby)
    {
        lobbies.add(lobby);
    }

    public static List<lobby> getLobbies()
    {
        return lobbies;
    }

    public static void setLobbies(List<lobby> lobbys)
    {
        lobbies = lobbys;

    }
}
