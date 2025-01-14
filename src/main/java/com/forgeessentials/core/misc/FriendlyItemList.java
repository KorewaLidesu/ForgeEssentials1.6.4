package com.forgeessentials.core.misc;

import com.forgeessentials.util.OutputHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FriendlyItemList {
    private HashMap<String, Item> itemMap = new HashMap<String, Item>();
    private HashMap<String, Block> blockMap = new HashMap<String, Block>();

    private static FriendlyItemList instance;

    public FriendlyItemList()
    {
        instance = this;
        makeList();
        OutputHandler.felog.info("Item & blockList made. Added " + itemMap.size() + " items and " + blockMap.size() + " blocks.");
    }

    public static FriendlyItemList instance()
    {
        return instance;
    }

    public void makeList()
    {
        for (Item item : Item.itemsList)
        {
            if (item != null)
            {
                try
                {
                    itemMap.put(item.getUnlocalizedName().toLowerCase().replaceAll("tile.", "").replaceAll("item.", ""), item);
                }
                catch (Exception e)
                {
                    OutputHandler.felog.finer("Not added to list: " + item.getClass().getName());
                }
            }
        }

        for (Block block : Block.blocksList)
        {
            if (block != null && block.blockID != 0)
            {
                try
                {

                    blockMap.put(block.getUnlocalizedName().toLowerCase().replaceAll("item.", "").replaceAll("tile.", ""), block);
                }
                catch (Exception e)
                {
                    OutputHandler.felog.finer("Not added to list: " + block.getClass().getName());
                }
            }
        }
    }

    public Item getItemForName(String name)
    {
        return itemMap.get(name.toLowerCase());
    }

    public Block getBlockForName(String name)
    {
        return blockMap.get(name.toLowerCase());
    }

    public List<String> getItemList()
    {
        return Arrays.asList(itemMap.keySet().toArray(new String[0]));
    }

    public List<String> getBlockList()
    {
        return Arrays.asList(blockMap.keySet().toArray(new String[0]));
    }

    public List<String> getAllList()
    {
        String[] array1 = blockMap.keySet().toArray(new String[0]);
        String[] array2 = itemMap.keySet().toArray(new String[0]);

        String[] array1and2 = new String[array1.length + array2.length];

        System.arraycopy(array1, 0, array1and2, 0, array1.length);
        System.arraycopy(array2, 0, array1and2, array1.length, array2.length);

        return Arrays.asList(array1and2);
    }
}
