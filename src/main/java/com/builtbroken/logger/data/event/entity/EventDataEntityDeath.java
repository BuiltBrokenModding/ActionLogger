package com.builtbroken.logger.data.event.entity;

import com.builtbroken.logger.data.ActionType;
import com.builtbroken.logger.data.DataPool;
import com.builtbroken.logger.data.event.EventData;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/24/2018.
 */
public class EventDataEntityDeath extends EventData
{
    private final static String query = "INSERT INTO ENTITY_DEATH (time, dim, x, y, z, entity, type, source, cause)"
            + " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final static DataPool<EventDataEntityDeath> dataPool = new DataPool(100);

    public String entity;
    public String damageType;
    public String damageSource;
    public String damageCause;

    protected EventDataEntityDeath(long time, int dim, int x, int y, int z)
    {
        super(ActionType.INTERACTION, time, dim, x, y, z);
    }

    @Override
    public void onReclaimed()
    {
        super.onReclaimed();
        entity = null;
        damageType = null;
        damageSource = null;
        damageCause = null;
    }

    @Override
    public void reclaim()
    {
        dataPool.reclaim(this);
    }

    @Override
    public String generateFlatFile()
    {
        String out = super.generateFlatFile();

        String data = "[ ";
        data += " | T: " + damageType;
        data += " | E: " + damageCause;
        data += " | S: " + damageSource;
        data += " ]";

        return out + " | " + data;
    }

    @Override
    public void writeToDataBase(Connection connection) throws SQLException
    {
        PreparedStatement preparedStmt = connection.prepareStatement(query);
        preparedStmt.setLong(1, time);
        preparedStmt.setInt(2, dim);
        preparedStmt.setInt(3, x);
        preparedStmt.setInt(4, y);
        preparedStmt.setInt(5, z);
        preparedStmt.setString(6, entity);
        preparedStmt.setString(7, damageType);
        preparedStmt.setString(8, damageSource);
        preparedStmt.setString(9, damageCause);
        preparedStmt.execute();
    }

    public static EventDataEntityDeath get(long time, int dim, int x, int y, int z)
    {
        EventDataEntityDeath object = dataPool.get();
        if (object == null)
        {
            object = new EventDataEntityDeath(time, dim, x, y, z);
        }
        return object;
    }

    public static EventDataEntityDeath get(LivingDeathEvent event)
    {
        EventDataEntityDeath object = get(System.currentTimeMillis(), event.entity.worldObj.provider.dimensionId, (int) event.entity.posX, (int) event.entity.posY, (int) event.entity.posZ);

        object.damageType = event.source.damageType;
        object.damageSource = toString(event.source.getEntity());
        object.damageCause = toString(event.source.getSourceOfDamage());
        object.entity = toString(event.entity);
        return object;
    }

}
