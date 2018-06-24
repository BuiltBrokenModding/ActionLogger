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

        useDatabase = configuration.getBoolean("enable", "database", useDatabase, "Allows enabling MySQL database handling, will disable flat file.");
        database_url = configuration.getString("url", "database", database_url, "URL path to use to get to the database");
        database_name = configuration.getString("name", "database", database_name, "Name of the database to use");
        database_username = configuration.getString("username", "database", database_username, "Username to use to access the database");
        database_password = configuration.getString("password", "database", database_password, "Password to use to access the database");

        configuration.save();
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        if (!useDatabase)
        {
            thread = new ThreadFlatFile();
        }
        else
        {
            thread = new ThreadDatabase();
        }
        getDbConnection();
        thread.start();
    }

    @Mod.EventHandler
    public void serverStop(FMLServerStoppingEvent event)
    {
        if (thread != null)
        {
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
