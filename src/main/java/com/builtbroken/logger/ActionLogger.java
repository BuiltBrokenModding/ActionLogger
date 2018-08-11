package com.builtbroken.logger;

import com.builtbroken.logger.data.IEventData;
import com.builtbroken.logger.database.DBConnection;
import com.builtbroken.logger.database.EventDatabase;
import com.builtbroken.logger.database.UserDatabase;
import com.builtbroken.logger.event.BlockEventHandler;
import com.builtbroken.logger.event.EntityEventHandler;
import com.builtbroken.logger.thread.ThreadDatabase;
import com.builtbroken.logger.thread.ThreadFlatFile;
import com.builtbroken.logger.thread.ThreadWriter;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Main mod class for Action Logger. Handles registering events, loading configs, and starting systems
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/21/2018.
 */
@Mod(modid = "bbmactionlogger", acceptableRemoteVersions = "*")
public class ActionLogger
{
    //Stuff for flat files
    public static File saveFolder;
    public static int lineCountLimit = 10000;
    public static ThreadWriter thread;

    //Database stuff
    public static boolean useDatabase = false;

    public static String database_url = "jdbc:mysql://localhost:3306/";
    public static String database_name = "action_logger";
    public static String database_username = "root";
    public static String database_password = "";

    public static int database_save_time = 10000;
    public static int database_write_limit = 1000;

    public static boolean database_log_writes = true;

    public static Logger logger = LogManager.getLogger("bbmactionlogger");
    public static DBConnection dbConnection;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Init files
        saveFolder = new File(event.getModConfigurationDirectory(), "bbm/actionlogger/logs");
        if (!saveFolder.exists())
        {
            logger.info("Creating save folder(s) for logs: " + saveFolder);
            saveFolder.mkdirs();
        }


        //Register events
        BlockEventHandler blockEventHandler = new BlockEventHandler();
        MinecraftForge.EVENT_BUS.register(blockEventHandler);

        EntityEventHandler entityEventHandler = new EntityEventHandler();
        MinecraftForge.EVENT_BUS.register(entityEventHandler);


        //Load configurations
        Configuration configuration = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/actionlogger/main.cfg"));
        configuration.load();

        lineCountLimit = configuration.getInt("line_limit", "flat_file", lineCountLimit, 100, 1000000, "Number of lines before making a new flat file");

        final String database_category = "database";
        useDatabase = configuration.getBoolean("enable", database_category, useDatabase, "Enables MySQL database handling, will disable flat file.");
        database_url = configuration.getString("url", database_category, database_url, "URL path to use to get to the database");
        database_name = configuration.getString("name", database_category, database_name, "Name of the database to use");
        database_username = configuration.getString("username", database_category, database_username, "Username to use to access the database");
        database_password = configuration.getString("password", database_category, database_password, "Password to use to access the database");

        database_log_writes = configuration.getBoolean("log_write_count", database_category, useDatabase, "Outputs the number of entries written each cycle.");

        database_write_limit = configuration.getInt("write_limit", database_category, lineCountLimit, 100, 10000000,
                "Number of entries to write to a database in a single update cycle(thread_save_timer). Keep the value high as the plugin" +
                        " generates a lot of entries while player's are active. Though its not advices to set it too high as this can tie up CPU and network resources. " +
                        "Set to -1 to write all entries every cycle (only recommend for large servers)");

        //Get string
        String databaseSaveTimeString = configuration.getString("thread_save_timer", database_category, "1M",
                "How long before running a write/save operation in the thread. " +
                        "Format: [integer][unit], units -> m(miliseconds), t(game ticks, 20 per second), S(seconds), M(minutes); case sensitive."
                        + " Example: 3S -> 3 seconds, 10M -> 10 minutes. Its recommend to keep the number low to prevent RAM build up. As data will stay in RAM until" +
                        "the thread has saved the data. Recommended values are between 1 second to 3 minutes. Only reason for higher values is to reduce CPU or " +
                        "network usage accessing the database.");

        //Format
        databaseSaveTimeString = databaseSaveTimeString.trim();
        databaseSaveTimeString = databaseSaveTimeString.replaceAll("\\s+", "");

        String type = databaseSaveTimeString.substring(databaseSaveTimeString.length() - 1);
        if (type.length() == 1 && (type.equals("m") || type.equals("t") || type.equals("S") || type.equals("M")))
        {
            String numberString = databaseSaveTimeString.substring(0, databaseSaveTimeString.length() - 1);
            try
            {
                int number = Integer.parseInt(numberString);
                if(type.equals("m"))
                {
                    database_save_time = number;
                }
                else if(type.equals("t"))
                {
                    database_save_time = number * 50;
                }
                else if(type.equals("S"))
                {
                    database_save_time = number * 1000;
                }
                else if(type.equals("M"))
                {
                    database_save_time = number * 1000 * 60;
                }
            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("ActionLogger: Failed to read config value 'thread_save_timer' due to invalid number '" + numberString + "' input");
            }

            if(database_save_time < 0)
            {
                database_save_time = -database_save_time;
                logger.warn("Config: 'thread_save_timer' was set negative, set positive to prevent issues. Please fix this issue!");
            }

            if(database_save_time < 100)
            {
                database_save_time = 100;
                logger.warn("Config: its not recommended to set 'thread_save_timer' under 100ms as this prevents the thread from sharing resources with the main thread. " +
                        "To prevent lag forcing to 100ms");
            }
        }
        else
        {
            throw new IllegalArgumentException("ActionLogger: Failed to read config value 'thread_save_timer' due to invalid type '" + type + "' input");
        }

        configuration.save();
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        ActionLogger.logger.info("Creating thread writer to handel logs");
        if (!useDatabase)
        {
            thread = new ThreadFlatFile();
            ActionLogger.logger.info("Using flat file event writer");
        }
        else
        {
            thread = new ThreadDatabase();
            getDbConnection();
            ActionLogger.logger.info("Using database event writer");
        }
        thread.start();
    }

    @Mod.EventHandler
    public void serverStop(FMLServerStoppingEvent event)
    {
        if (thread != null)
        {
            ActionLogger.logger.info("Killing thread writer");
            thread.stop();
            thread = null;
        }
    }

    public static DBConnection getDbConnection()
    {
        if (dbConnection == null)
        {
            dbConnection = new DBConnection();
            dbConnection.url = ActionLogger.database_url + ActionLogger.database_name;
            dbConnection.username = ActionLogger.database_username;
            dbConnection.password = ActionLogger.database_password;
            dbConnection.start();

            UserDatabase.generateTablesIfMissing(dbConnection.getConnection());
            EventDatabase.generateTablesIfMissing(dbConnection.getConnection());

        }
        return dbConnection;
    }

    public static void log(IEventData object)
    {
        thread.writeQueue.add(object);
    }
}
