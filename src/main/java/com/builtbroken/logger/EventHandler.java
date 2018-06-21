package com.builtbroken.logger;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/21/2018.
 */
public class EventHandler
{
    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event)
    {
        EntityPlayer player = event.entityPlayer;
        String data = "[ H: " + heldItemAsString(player);
        data += " - A: " + event.action;
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
        {
            data += " - F: " + event.face;
            data += " - B: " + Block.blockRegistry.getNameForObject(event.world.getBlock(event.x, event.y, event.z));
            data += " - M: " + event.world.getBlock(event.x, event.y, event.z);
            data += " - T: " + event.world.getTileEntity(event.x, event.y, event.z);
        }
        data += " ]";

        ActionLogger.thread.logAction(
                event.world, event.x, event.y, event.z,
                "INTERACTION",
                data,
                player);
    }

    @SubscribeEvent
    public void onPlaceBlock(BlockEvent.PlaceEvent event)
    {
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
                "BLOCK-PLACE",
                data,
                player);
    }

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event)
    {
        EntityPlayer player = event.getPlayer();

        String data = "[ H: " + heldItemAsString(player);
        data += " - B: " + Block.blockRegistry.getNameForObject(event.world.getBlock(event.x, event.y, event.z));
        data += " - M: " + event.world.getBlock(event.x, event.y, event.z);
        data += " - T: " + event.world.getTileEntity(event.x, event.y, event.z);
        data += " ]";

        ActionLogger.thread.logAction(
                event.world, event.x, event.y, event.z,
                "BLOCK-BREAK",
                data,
                player);
    }

    private String heldItemAsString(EntityPlayer player)
    {
        ItemStack stack = player.getHeldItem();
        if (stack != null)
        {
            return "HELD: " + stack.getUnlocalizedName();
        }
        return "HAND";
    }

}
