package com.builtbroken.logger.data.event.block;

import com.builtbroken.logger.data.ActionType;
import com.builtbroken.logger.data.DataPool;
import com.builtbroken.logger.data.event.EventData;
import com.builtbroken.logger.data.user.User;
import com.builtbroken.logger.database.UserDatabase;
import net.minecraft.block.Block;
import net.minecraftforge.event.world.BlockEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/23/2018.
 */
public class EventDataBlockPlace extends EventData
{
    private final static String query = "INSERT INTO BLOCK_PLACE (time, dim, x, y, z, player, item, block, meta, tile, block_new, meta_new, placed_against)"
            + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final static DataPool<EventDataBlockPlace> dataPool = new DataPool(100);

    public User user;

    public String heldItem;

    public String blockAgainstName;

    public String blockName;
    public String tileName;
    public int blockMeta;

    public String blockNewName;
    public int blockNewMeta;

    protected EventDataBlockPlace(long time, int dim, int x, int y, int z)
    {
        super(ActionType.INTERACTION, time, dim, x, y, z);
    }

    @Override
    public void onReclaimed()
    {
        super.onReclaimed();
        user = null;
        heldItem = null;
        blockNewName = null;
        blockName = null;
        blockAgainstName = null;
        tileName = null;
        blockMeta = 0;
        blockNewMeta = 0;
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

        String data = "[ H: " + heldItem;
        data += " - B: " + blockName;
        data += " - M: " + blockMeta;
        data += " - T: " + tileName;

        data += " - B2: " + blockNewName;
        data += " - M2: " + blockNewMeta;

        data += " - A: " + blockAgainstName;
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
        preparedStmt.setInt(6, user.id);
        preparedStmt.setString(7, heldItem);

        preparedStmt.setString(8, blockName);
        preparedStmt.setInt(9, blockMeta);
        preparedStmt.setString(10, tileName);

        preparedStmt.setString(11, blockNewName);
        preparedStmt.setInt(12, blockNewMeta);

        preparedStmt.setString(13, blockAgainstName);
        preparedStmt.execute();
    }

    public static EventDataBlockPlace get(long time, int dim, int x, int y, int z)
    {
        EventDataBlockPlace object = dataPool.get();
        if (object == null)
        {
            object = new EventDataBlockPlace(time, dim, x, y, z);
        }
        return object;
    }

    public static EventDataBlockPlace get(BlockEvent.PlaceEvent event)
    {
        EventDataBlockPlace object = get(System.currentTimeMillis(), event.world.provider.dimensionId, event.x, event.y, event.z);

        object.user = UserDatabase.getUser(event.player);
        object.heldItem = heldItemAsString(event.player);
        object.blockAgainstName = Block.blockRegistry.getNameForObject(event.placedAgainst);

        object.blockName = Block.blockRegistry.getNameForObject(event.blockSnapshot.replacedBlock);
        object.blockMeta = event.blockSnapshot.meta;
        object.tileName = "" + event.world.getTileEntity(event.x, event.y, event.z);

        object.blockNewName = Block.blockRegistry.getNameForObject(event.block);
        object.blockMeta = event.blockMetadata;
        return object;
    }

}
