package net.antipixel.nexus.config;

import net.antipixel.nexus.ui.BorderStyle;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

/**
 * Config options
 * @author Antipixel
 */
@ConfigGroup("nexusmenu")
public interface NexusConfig extends Config
{
	String KEY_DISPLAY_SHORTCUTS = "displayShortcuts";
	String KEY_INITIAL_MODE = "initialMode";
	String KEY_TELEPORT_NAME = "teleportName";
	String KEY_TELEPORT_ICON_BORDER = "teleportIconBorder";
	String KEY_TELEPORT_FADE_ANIM = "teleportFade";

	@ConfigSection(
			name = "Appearance",
			description = "Modify the appearance of the map",
			position = 3
	)
	String CONFIG_SECTION_APPEARANCE = "Appearance";

	@ConfigItem(
		keyName = KEY_DISPLAY_SHORTCUTS,
		name = "Display shortcuts",
		description = "Display the shortcut keys in the right-click menu of each teleport",
		position = 0
	)
	default boolean displayShortcuts()
	{
		return true;
	}

	@ConfigItem(
		keyName = KEY_INITIAL_MODE,
		name = "Open to",
		description = "Configures which menu will be first displayed when the Nexus menu is opened",
		position = 1
	)
	default DisplayMode initialMode()
	{
		return DisplayMode.NEXUS_MAP;
	}

	@ConfigItem(
			keyName = KEY_TELEPORT_NAME,
			name = "Teleport Name",
			description = "Configures how the name of each teleport is to be displayed",
			position = 2
	)
	default TeleportNameMode teleportName()
	{
		return TeleportNameMode.BOTH;
	}

	@ConfigItem(
			keyName = KEY_TELEPORT_ICON_BORDER,
			name = "Teleport Icon Border",
			description = "Select the border style for the teleport icons",
			position = 3,
			section = CONFIG_SECTION_APPEARANCE
	)
	default BorderStyle borderStyle()
	{
		return BorderStyle.BLACK;
	}

	@ConfigItem(
			keyName = KEY_TELEPORT_FADE_ANIM,
			name = "Teleport Icon Fade Animation",
			description = "Fade animation for the teleport icons",
			position = 4,
			section = CONFIG_SECTION_APPEARANCE
	)
	default boolean fadeAnimation()
	{
		return false;
	}
}
