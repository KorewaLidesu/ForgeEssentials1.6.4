package com.forgeessentials.chat;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.chat.commands.*;
import com.forgeessentials.chat.irc.IRCChatFormatter;
import com.forgeessentials.chat.irc.IRCHelper;
import com.forgeessentials.chat.irc.PlayerEventHandler;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.compat.CommandSetChecker;
import com.forgeessentials.core.misc.packetInspector.PacketAnalyzerRegistry;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.modules.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

@FEModule(name = "Chat", parentMod = ForgeEssentials.class, configClass = ConfigChat.class)
public class ModuleChat {
    @FEModule.Config
    public static ConfigChat conf;

    @FEModule.ModuleDir
    public static File moduleDir;

    public static PrintWriter chatLog;
    public static PrintWriter cmdLog;
    public static File logdir;
    public static boolean connectToIRC;
    private MailSystem mailsystem;
    private PlayerEventHandler ircPlayerHandler;

    public ModuleChat()
    {
    }
    @FEModule.Init
    public void load(FEModuleInitEvent e)
    {

        MinecraftForge.EVENT_BUS.register(new ChatFormatter());
        MinecraftForge.EVENT_BUS.register(new CommandMuter());

        if (!IRCHelper.suppressEvents && connectToIRC)
        {
            ircPlayerHandler = new PlayerEventHandler();
            MinecraftForge.EVENT_BUS.register(ircPlayerHandler);
            MinecraftForge.EVENT_BUS.register(new IRCChatFormatter());
            GameRegistry.registerPlayerTracker(ircPlayerHandler);
        }

        PacketAnalyzerRegistry.register(new PacketAnalyzerChat(), new int[] { 201 });
    }

    @FEModule.PostInit
    public void postLoad(FEModulePostInitEvent e)
    {
        mailsystem = new MailSystem();

    }

    @FEModule.ServerInit
    public void serverStarting(FEModuleServerInitEvent e)
    {
        e.registerServerCommand(new CommandMsg());
        e.registerServerCommand(new CommandR());
        e.registerServerCommand(new CommandNickname());
        e.registerServerCommand(new CommandPm());
        e.registerServerCommand(new CommandMute());
        e.registerServerCommand(new CommandUnmute());
        e.registerServerCommand(new CommandMail());
        e.registerServerCommand(new CommandAutoMessage());

        try
        {
            logdir = new File(moduleDir, "logs/");
            logdir.mkdirs();
        }
        catch (Exception e1)
        {
            OutputHandler.felog.warning("Could not create chat log directory!");
        }
        try
        {
            File file = new File(logdir, "chat-" + FunctionHelper.getCurrentDateString() + ".log");
            if (!file.exists())
            {
                file.createNewFile();
            }
            chatLog = new PrintWriter(file);
        }
        catch (Exception e1)
        {
            OutputHandler.felog.warning("Could not create chat log file!");
        }

        try
        {
            File file = new File(logdir, "cmd-" + FunctionHelper.getCurrentDateString() + ".log");
            if (!file.exists())
            {
                file.createNewFile();
            }
            cmdLog = new PrintWriter(file);
        }
        catch (Exception e1)
        {
            OutputHandler.felog.warning("Could not create command log file!");
        }

        APIRegistry.permReg.registerPermissionLevel("fe.chat.usecolor", RegGroup.MEMBERS);
        APIRegistry.permReg.registerPermissionLevel("fe.chat.nickname.others", RegGroup.OWNERS);
    }

    @FEModule.ServerPostInit()
    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        removeTell(FMLCommonHandler.instance().getMinecraftServerInstance());
        new AutoMessage(FMLCommonHandler.instance().getMinecraftServerInstance());
        MailSystem.LoadAll();
        GameRegistry.registerPlayerTracker(mailsystem);
        if (connectToIRC)
        {
            IRCHelper.connectToServer();
        }
    }

    @FEModule.ServerStop()
    public void serverStopping(FEModuleServerStopEvent e)
    {
        MailSystem.SaveAll();

        chatLog.close();
        cmdLog.close();
        if (connectToIRC)
        {
            IRCHelper.shutdown();
        }
    }

    private void removeTell(MinecraftServer server)
    {
        if (server.getCommandManager() instanceof CommandHandler)
        {
            try
            {
                Set<?> cmdList = ReflectionHelper
                        .getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), CommandSetChecker.FIELDNAME);

                ICommand toRemove = null;
                Class<?> cmdClass = null;
                for (Object cmdObj : cmdList)
                {
                    ICommand cmd = (ICommand) cmdObj;
                    if (cmd.getCommandName().equalsIgnoreCase("tell"))
                    {
                        try
                        {
                            cmdClass = cmd.getClass();
                            Package pkg = cmdClass.getPackage();
                            if (pkg == null || !pkg.getName().contains("ForgeEssentials"))
                            {
                                toRemove = cmd;
                                break;
                            }
                        }
                        catch (Exception e)
                        {
                            OutputHandler.felog.finer("Can't remove " + cmd.getCommandName());
                            OutputHandler.felog.finer("" + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                }
                if (toRemove != null)
                {
                    OutputHandler.felog.finer("Removing command '" + toRemove.getCommandName() + "' from class: " + cmdClass.getName());
                    cmdList.remove(toRemove);
                }
                ReflectionHelper
                        .setPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), cmdList, "commandSet", "b", "field_71561_b");

                Map<String, CommandMsg> cmds = ReflectionHelper
                        .getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), "commandMap", "a", "field_71562_a");
                if (cmds.containsKey("tell"))
                {
                    OutputHandler.felog.finer("Removing command tell from vanilla set.");
                    cmds.remove("tell");
                    cmds.put("tell", new CommandMsg());
                }
                ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), cmds, "commandMap", "a", "field_71562_a");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
