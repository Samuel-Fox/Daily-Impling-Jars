package com.dailyimplingjars;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DailyImplingJarsTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(DailyImplingJarsPlugin.class);
		RuneLite.main(args);
	}
}