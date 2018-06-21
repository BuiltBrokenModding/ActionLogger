package com.builtbroken.logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/21/2018.
 */
@Mod(modid = "bbmactionlogger", acceptableRemoteVersions = "*")
public class ActionLogger
{
    public static File saveFolder;
    public static int lineCountLimit = 10000;
    public static ThreadWriter thread;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        saveFolder = new File(event.getModConfigurationDirectory(), "bbm/action_logger/logs");
        if (!saveFolder.exists())
        {
            saveFolder.mkdirs();
        }

        EventHandler handler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        thread = new ThreadWriter();
        new Thread(thread).start();
    }

    @Mod.EventHandler
    public void serverStop(FMLServerStoppingEvent event)
    {
        thread.run = false;
        thread.saveAll();
        thread = null;
    }
}
