package com.builtbroken.logger.event;

import com.builtbroken.logger.ActionLogger;
import com.builtbroken.logger.data.event.entity.EventDataEntityDeath;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/22/2018.
 */
public class EntityEventHandler
{
    //@SubscribeEvent
    public void itemPickupEvent(EntityItemPickupEvent event)
    { /*
        String data = "[ ";
        data += "I: " + Item.itemRegistry.getNameForObject(event.item.getEntityItem().getItem());
        data += " | M: " + event.item.getEntityItem().getItemDamage();
        data += " | N: " + event.item.getEntityItem().getTagCompound();
        data += " ]";

        ActionLogger.thread.logAction(
                event.entityLiving.worldObj,
                (int) event.entityLiving.posX, (int) event.entityLiving.posY, (int) event.entityLiving.posZ,
                ActionType.ITEM_PICKUP,
                data,
                event.entityLiving instanceof EntityPlayer ? (EntityPlayer) event.entityLiving : null);
                */
    }

    @SubscribeEvent
    public void entityDeathEvent(LivingDeathEvent event)
    {
        ActionLogger.log(EventDataEntityDeath.get(event));
    }

    //@SubscribeEvent
    public void entityAttackEvent(LivingAttackEvent event)
    {
        /*
        String data = "[ ";
        data += "T: " + event.source.damageType;
        data += " | E: " + event.source.getEntity();
        data += " | S: " + event.source.getSourceOfDamage();
        data += " | D: " + event.ammount;
        data += " ]";

        ActionLogger.thread.logAction(
                event.entityLiving.worldObj,
                (int) event.entityLiving.posX, (int) event.entityLiving.posY, (int) event.entityLiving.posZ,
                ActionType.ENTITY_ATTACK,
                data,
                event.entityLiving instanceof EntityPlayer ? (EntityPlayer) event.entityLiving : null);
                */
    }

    //@SubscribeEvent
    public void entityHurtEvent(LivingHurtEvent event)
    {
        /*
        String data = "[ ";
        data += "T: " + event.source.damageType;
        data += " | E: " + event.source.getEntity();
        data += " | S: " + event.source.getSourceOfDamage();
        data += " | D: " + event.ammount;
        data += " ]";

        ActionLogger.thread.logAction(
                event.entityLiving.worldObj,
                (int) event.entityLiving.posX, (int) event.entityLiving.posY, (int) event.entityLiving.posZ,
                ActionType.ENTITY_HURT,
                data,
                event.entityLiving instanceof EntityPlayer ? (EntityPlayer) event.entityLiving : null);
                */
    }

    //@SubscribeEvent
    public void entityJoinWorld(EntityJoinWorldEvent event)
    {
        /*
        String data = "[ ";
        data += " I: " + event.entity.getEntityId();
        data += " | S: " + EntityList.getEntityString(event.entity);
        data += " | T: " + event.entity.ticksExisted;


        data += " ]";

        ActionLogger.thread.logAction(
                event.entity.worldObj,
                (int) event.entity.posX, (int) event.entity.posY, (int) event.entity.posZ,
                ActionType.ENTITY_ENTER_WORLD,
                data,
                event.entity instanceof EntityPlayer ? (EntityPlayer) event.entity : null);
                */
    }
}
