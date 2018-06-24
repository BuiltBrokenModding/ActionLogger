package com.builtbroken.logger.event;

import com.builtbroken.logger.ActionLogger;
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
        /*
        EntityPlayer player = event.player;

        String data = "[ H: " + heldItemAsString(player);
        data += " - P: " + Block.blockRegistry.getNameForObject(event.placedBlock);
        data += " - A: " + Block.blockRegistry.getNameForObject(event.placedAgainst);
        data += " - B: " + Block.blockRegistry.getNameForObject(event.world.getBlock(event.x, event.y, event.z));
        data += " - M: " + event.world.getBlock(event.x, event.y, event.z);
        data += " - T: " + event.world.getTileEntity(event.x, event.y, event.z);
        data += " ]";

        ActionLogger.thread.logAction(
                event.world, event.x, event.y, event.z,
                ActionType.BLOCK_PLACE,
                data,
                player);
                */
    }

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event)
    {
        /*
        EntityPlayer player = event.getPlayer();

        String data = "[ H: " + heldItemAsString(player);
        data += " - B: " + Block.blockRegistry.getNameForObject(event.world.getBlock(event.x, event.y, event.z));
        data += " - M: " + event.world.getBlock(event.x, event.y, event.z);
        data += " - T: " + event.world.getTileEntity(event.x, event.y, event.z);
        data += " ]";

        ActionLogger.thread.logAction(
                event.world, event.x, event.y, event.z,
                ActionType.BLOCK_BREAK,
                data,
                player);
                */
    }

}
