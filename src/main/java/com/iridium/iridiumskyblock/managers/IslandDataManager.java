package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IslandDataManager {

    //This class is used for getting islands in order of value or votes e.g. for /is top or /is visit

    //From Index (Starts at 0 inclusive)
    //To Index exclusive
    public static List<Integer> getIslands(IslandSortType sortType, int fromIndex, int toIndex, boolean ignorePrivate) {
        List<Integer> islands = new ArrayList<>();
        Connection connection = IridiumSkyblock.getSqlManager().getConnection();
        try {
            int index = 0;
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM islanddata ORDER BY ? DESC;");
            statement.setString(1, sortType.name);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next() && index < toIndex) {
                if (resultSet.getBoolean("private") && ignorePrivate) continue;
                if (index >= fromIndex) {
                    islands.add(resultSet.getInt("islandID"));
                }
                index++;
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return islands;
    }

    public static void save(Island island) {
        Connection connection = IridiumSkyblock.getSqlManager().getConnection();
        try {
            PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM islanddata where islandID=?;");
            deleteStatement.setInt(1, island.getId());
            deleteStatement.executeUpdate();
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO islanddata (islandID,value,votes,private) VALUES (?,?,?,?);");
            insertStatement.setInt(1, island.getId());
            insertStatement.setDouble(2, island.getValue());
            insertStatement.setInt(3, island.getVotes());
            insertStatement.setBoolean(4, !island.isVisit());
            insertStatement.executeUpdate();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public enum IslandSortType {
        VALUE("value"), VOTES("votes");
        public String name;

        IslandSortType(String name) {
            this.name = name;
        }
    }
}