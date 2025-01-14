package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.EnumGameType;

import java.util.Arrays;
import java.util.List;

public class CommandServerSettings extends FEcmdModuleCommands {
    public static List<String> options = Arrays.asList("allowFlight", "allowPVP", "buildLimit", "difficulty", "MOTD", "spawnProtection", "gamemode");

    @Override
    public String getCommandName()
    {
        return "serversettings";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[]
                { "ss" };
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer())
        {
            return;
        }
        DedicatedServer server = (DedicatedServer) FMLCommonHandler.instance().getMinecraftServerInstance();
        if (args.length == 0)
        {
            ChatUtils.sendMessage(sender, "Available options:");
            ChatUtils.sendMessage(sender, options.toString());
            return;
        }

        if (args[0].equalsIgnoreCase("allowFlight"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "allowFlight: " + server.isFlightAllowed());
            }
            else
            {
                server.setAllowFlight(Boolean.parseBoolean(args[1]));
                server.setProperty("allow-flight", Boolean.parseBoolean(args[1]));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "allowFlight: " + server.isFlightAllowed());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("allowPVP"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "allowPVP: " + server.isPVPEnabled());
            }
            else
            {
                server.setAllowPvp(Boolean.parseBoolean(args[1]));
                server.setProperty("pvp", Boolean.parseBoolean(args[1]));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "allowPVP: " + server.isPVPEnabled());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("buildLimit"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "buildLimit: " + server.getBuildLimit());
            }
            else
            {
                server.setBuildLimit(parseIntWithMin(sender, args[1], 0));
                server.setProperty("max-build-height", parseIntWithMin(sender, args[1], 0));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "buildLimit: " + server.getBuildLimit());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("MOTD"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "MOTD: " + server.getMOTD());
            }
            else
            {
                String msg = "";
                for (String var : FunctionHelper.dropFirstString(args))
                {
                    msg += " " + var;
                }
                server.setMOTD(msg.substring(1));
                server.setProperty("motd", msg.substring(1));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "MOTD: " + server.getMOTD());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("spawnProtection"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "spawnProtection: " + server.getSpawnProtectionSize());
            }
            else
            {
                server.setProperty("spawn-protection", parseIntWithMin(sender, args[1], 0));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "spawnProtection: " + server.getSpawnProtectionSize());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("gamemode"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "gamemode: " + server.getGameType().getName());
            }
            else
            {
                server.setProperty("gamemode", args[1]);
                server.setGameType(EnumGameType.getByID(Integer.parseInt(args[1])));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "gamemode: " + server.getGameType().getName());
            }
            return;

        }

        if (args[0].equalsIgnoreCase("difficulty"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "difficulty: " + server.getDifficulty());
            }
            else
            {
                server.setProperty("difficulty", args[1]);
                server.setDifficultyForAllWorlds(Integer.parseInt(args[1]));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "difficulty: " + server.getDifficulty());
            }
            return;

        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsFromIterableMatchingLastWord(args, options);
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/serversettings [option] [value] View or change server settings (in server.properties).";
    }
}
