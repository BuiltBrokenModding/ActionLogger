package com.builtbroken.logger.event;

import com.builtbroken.logger.ActionLogger;
import com.builtbroken.logger.data.event.EventDataBlockBreak;
import com.builtbroken.logger.data.event.EventDataBlockPlace;
import com.builtbroken.logger.data.event.EventDataInteraction;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/21/2018.
 */
public class BlockEventHandler
{
    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event)
    {
        ActionLogger.log(EventDataInteraction.get(event));
    }

    @SubscribeEvent
    public void onPlaceBlock(BlockEvent.PlaceEvent event)
    {
        ActionLogger.log(EventDataBlockPlace.get(event));
    }

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event)
    {
        ActionLogger.log(EventDataBlockBreak.get(event));
    }

}
