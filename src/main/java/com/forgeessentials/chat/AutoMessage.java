package com.forgeessentials.chat;

import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.tasks.TaskRegistry;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Random;

public class AutoMessage implements Runnable {
    public static int waittime;
    public static boolean random;
    public static ArrayList<String> msg = new ArrayList<String>();
    public static boolean enable;

    MinecraftServer server;
    public static int currentMsgID;

    public AutoMessage(MinecraftServer server)
    {
        this.server = server;

        if (msg.isEmpty())
        {
            currentMsgID = 0;
        }
        else
        {
            currentMsgID = new Random().nextInt(msg.size());
        }

        TaskRegistry.registerRecurringTask(this, 0, waittime, 0, 0, 0, waittime, 0, 0);
    }

    @Override
    public void run()
    {
        if (server.getAllUsernames().length != 0 && enable && !msg.isEmpty())
        {
            ChatUtils.sendMessage(server.getConfigurationManager(), msg.get(currentMsgID));

            if (random)
            {
                currentMsgID = new Random().nextInt(msg.size());
            }
            else
            {
                currentMsgID++;
                if (currentMsgID >= msg.size())
                {
                    currentMsgID = 0;
                }
            }
        }
        System.gc();
    }
}
