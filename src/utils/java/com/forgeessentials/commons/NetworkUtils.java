package com.forgeessentials.commons;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class NetworkUtils
{
    public static SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("forgeessentials");
}
