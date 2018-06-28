package com.builtbroken.logger.database;

import com.builtbroken.logger.ActionLogger;
import com.builtbroken.logger.data.user.User;
import com.builtbroken.logger.util.DBUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/24/2018.
 */
public class UserDatabase
{
    private static final HashMap<String, User> nameToUser = new HashMap();
    private static final HashMap<UUID, User> uuidToUser = new HashMap();
    private static final HashMap<Integer, User> idToUser = new HashMap();

    private static final String TABLE_PLAYERS = "AL_PLAYERS";

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_PLAYERS + " " +
            "(ID int NOT NULL AUTO_INCREMENT, " +
            "username varchar(255), " +
            "uuid varchar(255), " +
            "PRIMARY KEY (ID))";

    private final static String INSERT_STATEMENT = "INSERT INTO " + TABLE_PLAYERS + " (username, uuid)"
            + " values (?, ?)";

    private final static String SELECT_STATEMENT = "SELECT * FROM " + TABLE_PLAYERS + " WHERE uuid = ?";


    //TODO add way to track user's name changes

    public static User getUser(EntityPlayer player)
    {
        User user = uuidToUser.get(player.getGameProfile().getId());
        if (user == null)
        {
            user = nameToUser.get(player.getGameProfile().getName().toLowerCase());
        }

        if (user == null)
        {
            user = createUser(player.getGameProfile().getName(), player.getGameProfile().getId());
        }

        return user;
    }

    protected static User createUser(String username, UUID uuid)
    {
        User user = checkIfInDB(uuid);
        if (user == null)
        {
            try
            {
                Connection connection = ActionLogger.getDbConnection().getConnection();
                PreparedStatement preparedStmt = connection.prepareStatement(INSERT_STATEMENT);
                preparedStmt.setString(1, username);
                preparedStmt.setString(2, uuid.toString());
                preparedStmt.execute();

                user = checkIfInDB(uuid);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                return null;
            }
        }

        if (user != null)
        {
            nameToUser.put(user.name.toLowerCase(), user);
            uuidToUser.put(user.uuid, user);
            idToUser.put(user.id, user);
        }

        return user;
    }

    protected static User checkIfInDB(UUID uuid)
    {
        try
        {
            Connection connection = ActionLogger.getDbConnection().getConnection();
            PreparedStatement preparedStmt = connection.prepareStatement(SELECT_STATEMENT);
            preparedStmt.setString(1, uuid.toString());

            ResultSet rs = preparedStmt.executeQuery();
            if (rs.next())
            {
                int id = rs.getInt("ID");
                String username = rs.getString("username");
                User user = new User();
                user.id = id;
                user.name = username;
                user.uuid = uuid;
                return user;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void generateTablesIfMissing(Connection connection)
    {
        DBUtils.createTableIfMissing(connection, "PLAYERS", CREATE_USER_TABLE);
    }
}
