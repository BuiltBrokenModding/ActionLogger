package com.builtbroken.logger.database;

import com.builtbroken.logger.util.DBUtils;

import java.sql.Connection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/24/2018.
 */
public class EventDatabase
{
    private static final String CREATE_INTERACTION_TABLE = "CREATE TABLE INTERACTION " +
            "(ID int NOT NULL AUTO_INCREMENT, " +
            "time BIGINT, " +
            "dim int, " +
            "x int, " +
            "y int, " +
            "z int, " +
            "face TINYINT, " +
            "action TINYINT, " +
            "player int, " +
            "item varchar(255), " +
            "block varchar(255), " +
            "meta TINYINT, " +
            "tile varchar(255), " +
            "PRIMARY KEY (ID), " +
            "FOREIGN KEY (player) REFERENCES PLAYERS(id))";

    private static final String CREATE_BLOCK_BREAK_TABLE = "CREATE TABLE BLOCK_BREAK " +
            "(ID int NOT NULL AUTO_INCREMENT, " +
            "time BIGINT, " +
            "dim int, " +
            "x int, " +
            "y int, " +
            "z int, " +
            "player int, " +
            "item varchar(255), " +
            "block varchar(255), " +
            "meta TINYINT, " +
            "tile varchar(255), " +
            "PRIMARY KEY (ID), " +
            "FOREIGN KEY (player) REFERENCES PLAYERS(id))";

    private static final String CREATE_BLOCK_PLACE_TABLE = "CREATE TABLE BLOCK_PLACE " +
            "(ID int NOT NULL AUTO_INCREMENT, " +
            "time BIGINT, " +
            "dim int, " +
            "x int, " +
            "y int, " +
            "z int, " +
            "player int, " +
            "item varchar(255), " +
            "block varchar(255), " +
            "meta TINYINT, " +
            "tile varchar(255), " +
            "block_new varchar(255), " +
            "meta_new TINYINT, " +
            "placed_against varchar(255), " +
            "PRIMARY KEY (ID), " +
            "FOREIGN KEY (player) REFERENCES PLAYERS(id))";

    private static final String CREATE_ENTITY_DEATH_TABLE = "CREATE TABLE ENTITY_DEATH " +
            "(ID int NOT NULL AUTO_INCREMENT, " +
            "time BIGINT, " +
            "dim int, " +
            "x int, " +
            "y int, " +
            "z int, " +
            "entity varchar(255), " +
            "type varchar(255), " +
            "source varchar(255), " +
            "cause varchar(255), " +
            "PRIMARY KEY (ID))";

    public static void generateTablesIfMissing(Connection connection)
    {
        DBUtils.createTableIfMissing(connection, "INTERACTION", CREATE_INTERACTION_TABLE);
        DBUtils.createTableIfMissing(connection, "BLOCK_BREAK", CREATE_BLOCK_BREAK_TABLE);
        DBUtils.createTableIfMissing(connection, "BLOCK_PLACE", CREATE_BLOCK_PLACE_TABLE);

        DBUtils.createTableIfMissing(connection, "ENTITY_DEATH", CREATE_ENTITY_DEATH_TABLE);
    }
}
