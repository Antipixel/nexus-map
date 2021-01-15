package net.antipixel.nexus;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import net.antipixel.nexus.definition.RegionDefinition;
import net.antipixel.nexus.definition.TeleportDefinition;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class NexusPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(NexusMapPlugin.class);
		RuneLite.main(args);
	}
}