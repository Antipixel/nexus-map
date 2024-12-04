package net.antipixel.nexus;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.inject.Provides;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import net.antipixel.nexus.config.NexusConfig;
import net.antipixel.nexus.config.TeleportNameMode;
import net.antipixel.nexus.definition.IconDefinition;
import net.antipixel.nexus.definition.RegionDefinition;
import net.antipixel.nexus.definition.TeleportDefinition;
import net.antipixel.nexus.sprites.SpriteDefinition;
import net.antipixel.nexus.ui.FadePulseEffect;
import net.antipixel.nexus.ui.UIButton;
import net.antipixel.nexus.ui.UICheckBox;
import net.antipixel.nexus.ui.UIComponent;
import net.antipixel.nexus.ui.UIFadeButton;
import net.antipixel.nexus.ui.UIGraphic;
import net.antipixel.nexus.ui.UIPage;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.SoundEffectID;
import net.runelite.api.SpriteID;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
	name = "Nexus Menu Map",
	description = "Replaces the player owned house teleport Nexus menu",
	tags = {"poh", "portal", "teleport", "nexus"}
)
public class NexusMapPlugin extends Plugin
{
	/* Packed Widget IDs */
	private static final int GROUP_NEXUS_PORTAL = 17;
	private static final int ID_PORTAL_WINDOW = 0x110001;
	private static final int ID_PORTAL_PANEL = 0x110002;
	private static final int ID_PORTAL_MODEL = 0x110003;
	private static final int ID_SCRY_TEXT = 0x110004;
	private static final int ID_SCRY_SELECT = 0x110005;
	private static final int ID_TELEPORTS_SECTION = 0x110006;
	private static final int ID_KEYEVENTS_ALTERNATE = 0x110007;
	private static final int ID_KEYEVENTS_PRIMARY = 0x110008;
	private static final int ID_SCROLLBOX_BORDER = 0x110009;
	private static final int ID_SCRY_RADIO_PANE = 0x11000A;
	private static final int ID_TELEPORT_LIST = 0x11000B;
	private static final int ID_LOC_LABELS_PRIMARY = 0x11000C;
	private static final int ID_SCROLLBAR = 0x11000E;
	private static final int ID_SPRITES_PRIMARY = 0x11000F;
	private static final int ID_LOC_LABELS_ALTERNATE = 0x110010;
	private static final int ID_SPRITES_ALTERNATE = 0x110012;

	/* Widget dimensions and positions */
	private static final int TELE_ICON_SIZE = 24;
	private static final int MAP_SECTION_POS_X = 7;
	private static final int MAP_SECTION_POS_Y = 35;
	private static final int INDEX_MAP_SPRITE_WIDTH = 478;
	private static final int INDEX_MAP_SPRITE_HEIGHT = 272;
	private static final int REGION_MAP_SPRITE_WIDTH = 478;
	private static final int REGION_MAP_SPRITE_HEIGHT = 272;
	private static final int MAP_ICON_WIDTH = 50;
	private static final int MAP_ICON_HEIGHT = 41;

	/* Used to move around the inside of the window to help hide the dreaded pixel */
	private static final int PORTAL_PANEL_DEFAULT_X = 0;
	private static final int PORTAL_PANEL_DEFAULT_Y = 40;
	private static final int PORTAL_PANEL_HIDDEN_X = 4;
	private static final int PORTAL_PANEL_HIDDEN_Y = 14;

	/* Script, Sprite IDs */
	private static final int SCRIPT_TRIGGER_KEY = 1437;
	private static final int REGION_MAP_MAIN = -18200;
	private static final int VARBIT_NEXUS_MODE = 6671;

	/* Menu actions */
	private static final String ACTION_TEXT_TELE = "Teleport";
	private static final String ACTION_TEXT_SCRY = "Scry";
	private static final String ACTION_TEXT_SELECT = "Select";
	private static final String ACTION_TEXT_BACK = "Back";
	private static final String ACTION_TEXT_HOTKEY = "Set Hotkey";
	private static final String NAME_TEXT_TOGGLE = "Map Mode";

	private static final float FADE_EFFECT_MIN_OPACITY = 0.50f;
	private static final float FADE_EFFECT_SPEED = 0.01f;

	/* Configuration Group & Keys */
	private static final String CFG_GROUP = "nexusMapCFG";
	private static final String CFG_KEY_STATE = "prevState";
	private static final String CFG_BTM_KEY = "keybind.telenexus";

	/* Definition JSON files */
	private static final String DEF_FILE_REGIONS = "RegionDef.json";
	private static final String DEF_FILE_SPRITES = "SpriteDef.json";

	private static final String TELE_NAME_PATTERN = "<col=ffffff>(.+)</col> : +(.+)";
	private static final String PARENTHESISED_ALIAS_FORMAT = "%s (%s)";
	private static final String SHORTCUT_COLOUR_TAG = "<col=ffffff>";
	private static final String PLUGIN_NAME_BTM = "Better Teleport Menu";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private NexusConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private PluginManager pluginManager;

	@Inject
	private EventBus eventBus;

	@Inject
	private Gson gson;

	private RegionDefinition[] regionDefinitions;
	private SpriteDefinition[] spriteDefinitions;

	private Map<IntegerBooleanPair, TeleportDefinition> teleportDefinitions;
	private Map<IntegerBooleanPair, Teleport> availableTeleports;
	private Map<IntegerBooleanPair, UIButton> activeTeleportButtons;

	private boolean mapEnabled;
	private boolean switchingModes;
	private String teleportAction;

	/* Widgets */
	/**
	 * A list of widgets that the plugin does not require
	 * in order to function, for them to be hidden and shown as required
	 */
	private ImmutableList<Integer> hiddenWidgetIDs = ImmutableList.of(
		ID_PORTAL_MODEL,
		ID_SCRY_TEXT,
		ID_SCRY_SELECT,
		ID_SCROLLBOX_BORDER,
		ID_TELEPORT_LIST,
		ID_SCROLLBAR
	);
	private UIGraphic mapGraphic;
	private UIGraphic[] indexRegionGraphics;
	private UIButton[] indexRegionIcons;

	private UIPage indexPage;
	private List<UIPage> mapPages;

	private FadePulseEffect fadeEffect;
	private Queue<Runnable> clientTickQueue;

	@Override
	protected void startUp()
	{
		this.loadDefinitions();
		this.buildTeleportDefinitionLookup();

		// Add the custom sprites to the sprite manager
		this.spriteManager.addSpriteOverrides(spriteDefinitions);

		this.activeTeleportButtons = new HashMap<>();
		this.clientTickQueue = new ArrayDeque<>();
		this.fadeEffect = new FadePulseEffect(FADE_EFFECT_MIN_OPACITY, FADE_EFFECT_SPEED);
	}

	@Subscribe
	public void onClientTick(ClientTick e)
	{
		// Invoke any queued methods
		if (!this.clientTickQueue.isEmpty())
		{
			this.clientThread.invokeLater(this.clientTickQueue.remove());
		}

		// Update component effects
		this.fadeEffect.onUpdate();
		this.activeTeleportButtons.values().forEach(UIComponent::refreshEffect);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged e)
	{
		switch (e.getKey())
		{
			case NexusConfig.KEY_TELEPORT_ICON_BORDER:
				this.clientThread.invokeLater(this::updateIconBorderStyle);
				break;
			case NexusConfig.KEY_TELEPORT_FADE_ANIM:
				this.clientThread.invokeLater(this::resetFadeAnimation);
				break;
			case NexusConfig.KEY_DISPLAY_SHORTCUTS:
			case NexusConfig.KEY_TELEPORT_NAME:
				this.clientThread.invokeLater(this::updateTeleportButtonNames);
				break;
			default:
				// Hotkey config changes from the Better Teleport Menu plugin
				if (e.getKey().startsWith(CFG_BTM_KEY))
				{
					// Unfortunately there's no way to confirm the Better Teleport Menu
					// plugin has completed changing the shortcut key text in the vanilla
					// teleport list, so to make sure we're updating from the *new* shortcut
					// key value, we'll add a couple updates functions into a queue to be performed
					// over the next client ticks
					this.clientTickQueue.add(this::updateTeleportButtonNames);
					this.clientTickQueue.add(this::updateTeleportButtonNames);
				}
				break;
		}
	}

	@Provides
	NexusConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NexusConfig.class);
	}

	@Override
	protected void shutDown()
	{
		// Remove the custom sprites
		this.spriteManager.removeSpriteOverrides(spriteDefinitions);

		this.regionDefinitions = null;
		this.spriteDefinitions = null;
		this.teleportDefinitions.clear();
	}

	/**
	 * Loads the definition files
	 */
	private void loadDefinitions()
	{
		// Load the definitions files for the regions and sprite override
		this.regionDefinitions = loadDefinitionResource(RegionDefinition[].class, DEF_FILE_REGIONS, gson);
		this.spriteDefinitions = loadDefinitionResource(SpriteDefinition[].class, DEF_FILE_SPRITES, gson);
	}

	/**
	 * Builds a lookup table for teleport definitions
	 */
	private void buildTeleportDefinitionLookup()
	{
		this.teleportDefinitions = new HashMap<>();

		// Iterate through all regions looking for their teleport definitions
		for (RegionDefinition regionDef : this.regionDefinitions)
		{
			for (TeleportDefinition teleportDef : regionDef.getTeleportDefinitions())
			{
				this.teleportDefinitions.put(teleportDef.getKey(), teleportDef);
			}
		}
	}

	/**
	 * Loads a definition resource from a JSON file
	 *
	 * @param classType the class into which the data contained in the JSON file will be read into
	 * @param resource  the name of the resource (file name)
	 * @param gson      a reference to the GSON object
	 * @param <T>       the class type
	 * @return the data read from the JSON definition file
	 */
	private <T> T loadDefinitionResource(Class<T> classType, String resource, Gson gson)
	{
		// Load the resource as a stream and wrap it in a reader
		InputStream resourceStream = classType.getResourceAsStream(resource);
		InputStreamReader definitionReader = new InputStreamReader(resourceStream);

		// Load the objects from the JSON file
		return gson.fromJson(definitionReader, classType);
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked e)
	{
		// When switching between Teleport and Scry mode in the standard
		// Nexus menu, the entire interface is reloaded, triggering the
		// WidgetLoaded event. By listening out for a menu option click
		// event on either of the radio buttons, we can set a flag indicating
		// that the widget reload was triggered by the switching of the mode
		if (e.getWidget() != null
			&& e.getWidget().getId() == ID_SCRY_RADIO_PANE)
		{
			this.switchingModes = true;
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged e)
	{
		if (e.getVarbitId() != VARBIT_NEXUS_MODE)
		{
			return;
		}

		// Update the action text in the menu
		this.teleportAction = this.getModeAction();
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded e)
	{
		if (e.getGroupId() == GROUP_NEXUS_PORTAL)
		{
			// The main window layer
			Widget window = this.client.getWidget(ID_PORTAL_WINDOW);

			// Builds a list of teleports that are
			// actually available to the player
			this.buildAvailableTeleportList();
			this.updateDisplayedMenu();

			// Create the page objects, onto which the UI
			// components will be placed
			this.createMenuPages();

			// Create the custom widgets
			this.createIndexMenu(window);
			this.createMapGraphic(window);
			this.createBackButton(window);
			this.createTeleportWidgets(window);
			this.createToggleCheckbox(window);

			this.updateMapState(window);
		}
	}

	/**
	 * Shows or hides the default menu widgets
	 *
	 * @param visible the desired visibility state of the widgets,
	 *                true to set them to visible, false for hidden
	 */
	private void setDefaultWidgetVisibility(boolean visible)
	{
		// Iterate though each of the non-essential widgets
		for (Integer packedID : this.hiddenWidgetIDs)
		{
			Widget w = this.client.getWidget(packedID);
			if (w != null)
			{
				// Update their visibility
				w.setHidden(!visible);
			}
		}

		// The keyevent widgets need to be visible for keybinds to work.
		// However, they leave a tiny annoying 1x1 pixel on the screen.
		// Moving them around or changing their properties so they're unseen is difficult,
		// as anything that causes them not to be rendered causes problems.
		// I have resorted to moving the entire inside of the window so that the pixel
		// is within the drop shadow of the "Show Map" button...
		// TODO: This is absolutely, **incredibly** cursed, think of something better later...
		Widget w = this.client.getWidget(ID_PORTAL_PANEL);
		if (w != null)
		{
			if (visible)
			{
				// Default state as observed in-game
				w.setPos(PORTAL_PANEL_DEFAULT_X, PORTAL_PANEL_DEFAULT_Y);
			}
			else
			{
				// Hide within the shadow of the "Show Map" text...
				w.setPos(PORTAL_PANEL_HIDDEN_X, PORTAL_PANEL_HIDDEN_Y);
			}
			w.revalidate();
		}
	}

	/**
	 * Updates the border style
	 */
	private void updateIconBorderStyle()
	{
		this.activeTeleportButtons.values().forEach((button) -> button.setBorder(config.borderStyle()));
	}

	/**
	 * Resets each of the teleport buttons to fully opaque
	 */
	private void resetFadeAnimation()
	{
		this.activeTeleportButtons.values().forEach((teleButton) ->
		{
			if (config.fadeAnimation())
			{
				teleButton.setEffect(this.fadeEffect);
			}
			else
			{
				teleButton.clearEffect();
			}
		});
	}

	/**
	 * Updates the names of the teleport button widgets from the values
	 * stored in the vanilla client menu, synchronizing any changes made
	 * to the original text (e.g. the shortcut key changing, or location name)
	 */
	private void updateTeleportButtonNames()
	{
		// Rebuild the teleport list from the vanilla menu
		this.buildAvailableTeleportList();

		// Update only the active teleports
		for (Map.Entry<IntegerBooleanPair, UIButton> entry : this.activeTeleportButtons.entrySet())
		{
			// Grab the active teleport
			UIButton teleportButton = entry.getValue();

			// Update the widget name
			TeleportDefinition teleportDef = this.teleportDefinitions.get(entry.getKey());
			Teleport teleport = this.getAvailableTeleport(teleportDef);
			teleportButton.setName(this.generateTeleportName(teleport));
		}
	}

	/**
	 * Constructs the list of teleports available for the player to use
	 */
	private void buildAvailableTeleportList()
	{
		this.availableTeleports = new HashMap<>();

		// Fetch all teleports for both the primary and alternate teleport widgets,
		// appending the results of both to the available teleports maps
		this.availableTeleports.putAll(this.getTeleportsFromWidgets(false));
		this.availableTeleports.putAll(this.getTeleportsFromWidgets(true));
	}

	/**
	 * Updates which menu is being displayed
	 */
	private void updateDisplayedMenu()
	{
		// If the menu is currently switching modes, don't update
		if (this.switchingModes)
		{
			// Reset the flag
			this.switchingModes = false;
		}
		else
		{
			// Set the initial map state from config
			this.mapEnabled = this.getInitialMapState();
		}
	}

	/**
	 * Gets the preferred initial menu to display upon
	 * first opening the Nexus menu, as set in the config
	 *
	 * @return true if the initial menu should be the map,
	 * or false if the original menu should be displayed
	 */
	private boolean getInitialMapState()
	{
		switch (config.initialMode())
		{
			case NEXUS_MAP:
				return true;
			case DEFAULT_MENU:
				return false;
			case REMEMBER_PREVIOUS:
				return this.getPreviousDisplayMode();
		}
		return false;
	}

	/**
	 * Extracts information from a nexus portals teleport list and returns the information as a Teleport list,
	 * containing the name, index, shortcut key and type of teleport (either primary or alternate)
	 *
	 * @param alt         true if grabbing alternate teleports, false if primary
	 * @return a list containing all available teleports
	 */
	private Map<IntegerBooleanPair, Teleport> getTeleportsFromWidgets(boolean alt)
	{
		// Get the parent widgets containing the sprite/labels list
		Widget labelsWidget = this.client.getWidget(alt ? ID_LOC_LABELS_ALTERNATE : ID_LOC_LABELS_PRIMARY);
		Widget spritesWidget = this.client.getWidget(alt ? ID_SPRITES_ALTERNATE : ID_SPRITES_PRIMARY);

		// Create a map in which to place the available teleport options
		Map<IntegerBooleanPair, Teleport> teleports = new HashMap<>();

		// Early return
		if (labelsWidget == null || spritesWidget == null)
		{
			return teleports;
		}

		// Grab the children of the widget, each of which have a text
		// attribute containing the teleport location name and key shortcut
		Widget[] labels = labelsWidget.getDynamicChildren();

		// Grab the children of the widget, each of which have a sprite
		Widget[] sprites = spritesWidget.getDynamicChildren();

		// Sanity check
		if (labels.length == 0 || labels.length != sprites.length)
		{
			return teleports;
		}

		// Compile the pattern that will match the teleport label
		// and place the hotkey and teleport name into groups
		Pattern pattern = Pattern.compile(TELE_NAME_PATTERN);

		for (int i = 0; i < labels.length; i++)
		{
			Widget label = labels[i];
			Widget sprite = sprites[i];

			String shortcutKey;

			// For teleports with a shortcut defined, the teleport widget text will
			// contain the shortcut key sandwiched between colour tags. If these tags
			// are present, the shortcut key and teleport name will need to be extracted.
			if (label.getText().contains(SHORTCUT_COLOUR_TAG))
			{
				// Create a pattern matcher with the widgets text content
				Matcher matcher = pattern.matcher(label.getText());

				// If the text doesn't match the pattern, skip onto the next
				if (!matcher.matches())
				{
					continue;
				}

				// Extract the pertinent information
				shortcutKey = matcher.group(1);
			}
			else
			{
				// No shortcut key defined
				shortcutKey = null;
			}

			IntegerBooleanPair key = new IntegerBooleanPair(sprite.getSpriteId(), alt);

			// If a teleport by this spriteID cannot be found in the teleport definitions,
			// skip. This likely means a new teleport has been added to the Nexus that
			// hasn't been updated into the definitions yet
			if (!this.teleportDefinitions.containsKey(key))
			{
				continue;
			}

			// Get the teleport definition from the lookup table
			TeleportDefinition teleportDef = this.teleportDefinitions.get(key);
			teleports.put(key, new Teleport(teleportDef, label, shortcutKey));
		}

		return teleports;
	}

	/**
	 * Creates the pages for the nexus menu, which are used to group the
	 * various UI components in order to conveniently switch between them
	 */
	private void createMenuPages()
	{
		this.indexPage = new UIPage();
		this.mapPages = new ArrayList<>(regionDefinitions.length);

		// Add a page for each region
		for (int i = 0; i < regionDefinitions.length; i++)
		{
			this.mapPages.add(new UIPage());
		}
	}

	/**
	 * Creates the widgets and components required for the index menu,
	 * such as the index maps and the region icons
	 *
	 * @param window the layer on which to create the widgets
	 */
	private void createIndexMenu(Widget window)
	{
		// Create a graphic widget for the background image of the index page
		Widget backingWidget = window.createChild(-1, WidgetType.GRAPHIC);

		// Wrap in a UIGraphic, set dimensions, position and sprite
		UIGraphic indexBackingGraphic = new UIGraphic(backingWidget);
		indexBackingGraphic.setPosition(MAP_SECTION_POS_X, MAP_SECTION_POS_Y);
		indexBackingGraphic.setSize(INDEX_MAP_SPRITE_WIDTH, INDEX_MAP_SPRITE_HEIGHT);
		indexBackingGraphic.setSprite(REGION_MAP_MAIN);

		// Initialise the arrays for the map graphics and icons
		this.indexRegionGraphics = new UIGraphic[regionDefinitions.length];
		this.indexRegionIcons = new UIButton[regionDefinitions.length];

		// Add the backing graphic to the index page
		this.indexPage.add(indexBackingGraphic);

		for (int i = 0; i < regionDefinitions.length; i++)
		{
			// Get definition for the region
			RegionDefinition regionDef = this.regionDefinitions[i];

			// Create a widget for the region sprite graphic
			Widget regionGraphic = window.createChild(-1, WidgetType.GRAPHIC);

			// Wrap in UIGraphic, update the size and position to match that of
			// the backing graphic. Set the sprite to that of the current region
			this.indexRegionGraphics[i] = new UIGraphic(regionGraphic);
			this.indexRegionGraphics[i].setPosition(MAP_SECTION_POS_X, MAP_SECTION_POS_Y);
			this.indexRegionGraphics[i].setSize(INDEX_MAP_SPRITE_WIDTH, INDEX_MAP_SPRITE_HEIGHT);
			this.indexRegionGraphics[i].setSprite(regionDef.getIndexSprite());

			// Add the component to the index page
			this.indexPage.add(this.indexRegionGraphics[i]);

			// If there's no teleports defined for this region, skip onto the next
			// before the icon widget is created and has its listeners attached
			if (!regionDef.hasTeleports())
			{
				continue;
			}

			// Create the widget for the regions icon
			Widget regionIcon = window.createChild(-1, WidgetType.GRAPHIC);

			// Get the definition for the regions icon
			IconDefinition iconDef = regionDef.getIcon();

			// Wrap in UIBUtton, position the component. attach listeners, etc.
			this.indexRegionIcons[i] = new UIButton(regionIcon);
			this.indexRegionIcons[i].setName(regionDef.getName());
			this.indexRegionIcons[i].setPosition(iconDef.getX() + MAP_SECTION_POS_X, iconDef.getY() + MAP_SECTION_POS_Y);
			this.indexRegionIcons[i].setSize(MAP_ICON_WIDTH, MAP_ICON_HEIGHT);
			this.indexRegionIcons[i].setSprites(iconDef.getSpriteStandard(), iconDef.getSpriteHover());
			this.indexRegionIcons[i].setOnHoverListener((c) -> onIconHover(regionDef.getId()));
			this.indexRegionIcons[i].setOnLeaveListener((c) -> onIconLeave(regionDef.getId()));
			this.indexRegionIcons[i].addAction(ACTION_TEXT_SELECT, () -> onIconClicked(regionDef.getId()));

			// Add to the index page
			this.indexPage.add(this.indexRegionIcons[i]);
		}
	}

	/**
	 * Creates the graphic used to display the custom map sprite on each of the map pages
	 *
	 * @param window the layer on which to create the widget
	 */
	private void createMapGraphic(Widget window)
	{
		// Create the widget for the map graphic
		Widget mapWidget = window.createChild(-1, WidgetType.GRAPHIC);

		// Wrap the widget in a UIGraphic
		this.mapGraphic = new UIGraphic(mapWidget);
		this.mapGraphic.setPosition(7, 35);
		this.mapGraphic.setSize(REGION_MAP_SPRITE_WIDTH, REGION_MAP_SPRITE_HEIGHT);

		// Add the map graphic to each of the map pages
		this.mapPages.forEach(page -> page.add(this.mapGraphic));
	}

	/**
	 * Creates the back arrow, used to return to the index page
	 *
	 * @param window the layer on which to create the widget
	 */
	private void createBackButton(Widget window)
	{
		// Create the widget for the button
		Widget backArrowWidget = window.createChild(-1, WidgetType.GRAPHIC);

		// Wrap as a button, set the position, sprite, etc.
		UIButton backArrowButton = new UIFadeButton(backArrowWidget);
		backArrowButton.setSprites(SpriteID.GE_BACK_ARROW_BUTTON);
		backArrowButton.setPosition(13, 41);
		backArrowButton.setSize(30, 23);

		// Assign the callback for the button
		backArrowButton.addAction(ACTION_TEXT_BACK, this::onBackButtonPressed);

		// Add the back arrow to each map page
		this.mapPages.forEach(page -> page.add(backArrowButton));
	}

	/**
	 * Creates the teleport icon widgets and places them
	 * in their correct position on the nexus widget pane
	 *
	 * @param window the layer on which to create the widget
	 */
	private void createTeleportWidgets(Widget window)
	{
		// Clear the current list of active teleport buttons to avoid duplicates
		this.activeTeleportButtons.clear();

		// Check for the Better Teleport Menu plugin, if installed and active,
		// add rebind options to the teleport button menus
		boolean betterTeleportMenuActive = this.isBetterTeleportMenuActive();

		// Iterate through each of the map regions
		for (int i = 0; i < regionDefinitions.length; i++)
		{
			// Current map region
			RegionDefinition regionDef = this.regionDefinitions[i];

			// Get the definitions for the teleports within this map region
			TeleportDefinition[] teleportDefs = regionDef.getTeleportDefinitions();

			// Iterate through each of the *defined* teleports, not just
			// the teleports that are available to the player
			for (TeleportDefinition teleportDef : teleportDefs)
			{
				// Create the teleport icon widget
				Widget teleportWidget = window.createChild(-1, WidgetType.GRAPHIC);

				// Create a button wrapper for the teleport widget. Set the dimensions,
				// the position and the visibility to hidden
				UIButton teleportButton = new UIButton(teleportWidget);
				teleportButton.setSize(TELE_ICON_SIZE, TELE_ICON_SIZE);
				teleportButton.setX(teleportDef.getSpriteX() + MAP_SECTION_POS_X);
				teleportButton.setY(teleportDef.getSpriteY() + MAP_SECTION_POS_Y);
				teleportButton.setVisibility(false);

				// If enabled in config, apply fade pulsing effect
				if (config.fadeAnimation())
				{
					teleportButton.setEffect(this.fadeEffect);
				}

				// Add the teleport button to this regions map page
				this.mapPages.get(i).add(teleportButton);

				// Check that the teleport is available to the player
				if (this.isTeleportAvailable(teleportDef))
				{
					// Grab the teleport from the list of available teleports
					Teleport teleport = this.getAvailableTeleport(teleportDef);

					// Set the sprite to the active icon for this spell
					teleportButton.setSprites(teleportDef.getEnabledSpriteForDisplay());
					teleportButton.setBorder(config.borderStyle());

					// Create the teleport name, formatted with alias and optional shortcut key
					String teleportName = this.generateTeleportName(teleport);

					// Assign the teleport name
					teleportButton.setName(teleportName);

					// Set the teleport action type, which will either be Teleport
					// or Scry, depending on the value of the VarBit
					this.teleportAction = this.getModeAction();

					// Add the menu options and listener, activate listeners
					teleportButton.addAction(teleportAction, () -> triggerTeleport(teleport));

					// If the Better Teleport Menu plugin is active, add a rebind action
					if (betterTeleportMenuActive)
					{
						teleportButton.addAction(ACTION_TEXT_HOTKEY, () -> triggerRebindDialog(teleport));
					}

					this.activeTeleportButtons.put(teleportDef.getKey(), teleportButton);
				}
				else
				{
					// If the spell isn't available to the player, display the
					// deactivated spell icon instead
					teleportButton.setSprites(teleportDef.getDisabledSprite());
				}
			}
		}
	}

	/**
	 * Generates the name for the teleport button widget
	 * @param teleport the teleport object
	 * @return the generated teleport name
	 */
	private String generateTeleportName(Teleport teleport)
	{
		TeleportNameMode nameMode = this.config.teleportName();

		String name = teleport.getName();

		if (teleport.hasAlias())
		{
			switch (nameMode)
			{
				case BOTH:
					name = this.getParenthesisedAlias(teleport.getDefinition());
					break;
				case ALIAS:
					name = teleport.getAlias();
					break;
			}
		}

		if (config.displayShortcuts() && teleport.hasShortcutKey())
		{
			name = String.format("[%s] %s", teleport.getKeyShortcut(), name);
		}

		return name;
	}

	/**
	 * Gets the teleport name with the original name appended in parentheses
	 * @param teleportDef the teleport definition
	 * @return the parenthesised name/alias
	 */
	private String getParenthesisedName(TeleportDefinition teleportDef)
	{
		return String.format(PARENTHESISED_ALIAS_FORMAT, teleportDef.getAlias(), teleportDef.getName());
	}

	/**
	 * Gets the teleport name with the alias appended in parentheses
	 * @param teleportDef the teleport definition
	 * @return the parenthesised name/alias
	 */
	private String getParenthesisedAlias(TeleportDefinition teleportDef)
	{
		return String.format(PARENTHESISED_ALIAS_FORMAT, teleportDef.getName(), teleportDef.getAlias());
	}

	/**
	 * Creates the checkbox for toggling the state of the map
	 * @param window the layer on which to create the widget
	 */
	private void createToggleCheckbox(Widget window)
	{
		// Create the graphic widget for the checkbox
		Widget toggleWidget = window.createChild(-1, WidgetType.GRAPHIC);
		Widget labelWidget = window.createChild(-1, WidgetType.TEXT);

		// Wrap in checkbox, set size, position, etc.
		UICheckBox mapToggle = new UICheckBox(toggleWidget, labelWidget);
		mapToggle.setPosition(10, 10);
		mapToggle.setName(NAME_TEXT_TOGGLE);
		mapToggle.setEnabled(this.mapEnabled);
		mapToggle.setText("Show Map");
		mapToggle.setToggleListener(this::onMapStateToggled);
	}

	/**
	 * Updates the state of the widgets depending on map state
	 * @param window the window layer
	 */
	private void updateMapState(Widget window)
	{
		// If the map is enabled, display the custom widgets
		if (this.mapEnabled)
		{
			// Hide the default widgets and display the map index map
			this.setDefaultWidgetVisibility(false);
			this.displayIndexPage();
		}
		else
		{
			// Hide all custom widgets and show the default widgets
			this.indexPage.setVisibility(false);
			this.mapPages.forEach(page -> page.setVisibility(false));
			this.setDefaultWidgetVisibility(true);
		}

		// Save the new map mode to the config
		this.setPreviousDisplayMode(mapEnabled);
	}

	/**
	 * Displays the index page and makes sure
	 * that each of the map pages are hidden
	 */
	private void displayIndexPage()
	{
		this.indexPage.setVisibility(true);
		this.mapPages.forEach(page -> page.setVisibility(false));
	}

	/**
	 * Displays the map page for the given region ID
	 * @param regionID the region ID to display
	 */
	private void displayMapPage(int regionID)
	{
		// Hide the index page
		this.indexPage.setVisibility(false);

		// Make sure all other map pages a hidden
		this.mapPages.forEach(page -> page.setVisibility(false));
		this.mapPages.get(regionID).setVisibility(true);

		// Set the sprite to that of the specified region
		this.mapGraphic.setSprite(regionDefinitions[regionID].getMapSprite());
	}

	/**
	 * Called when the map state checkbox is toggled
	 * @param src the checkbox component
	 */
	private void onMapStateToggled(UIComponent src)
	{
		// The checkbox component
		UICheckBox toggleCheckbox = (UICheckBox)src;

		// Update the map enabled flag
		this.mapEnabled = toggleCheckbox.isEnabled();

		// Update the map state
		this.updateMapState(client.getWidget(ID_PORTAL_PANEL));

		// *Boop*
		this.client.playSoundEffect(SoundEffectID.UI_BOOP);
	}

	/**
	 * Called when the mouse enters the icon
	 * @param regionID the ID of the region represented by the icon
	 */
	private void onIconHover(int regionID)
	{
		// Move the map sprite for this region up by 2 pixels, and
		// set the opacity to 75% opaque
		this.indexRegionGraphics[regionID].setY(MAP_SECTION_POS_Y - 2);
		this.indexRegionGraphics[regionID].setOpacity(.75f);
		this.indexRegionGraphics[regionID].getWidget().revalidate();
	}

	/**
	 * Called when the mouse exits the icon
	 * @param regionID the ID of the region represented by the icon
	 */
	private void onIconLeave(int regionID)
	{
		// Restore the original position and set back to fully opaque
		this.indexRegionGraphics[regionID].setY(MAP_SECTION_POS_Y);
		this.indexRegionGraphics[regionID].setOpacity(1.0f);
		this.indexRegionGraphics[regionID].getWidget().revalidate();
	}

	/**
	 * Called when a region icon is selected
	 * @param regionID the ID of the region represented by the icon
	 */
	private void onIconClicked(int regionID)
	{
		// Display the map page for the region
		this.displayMapPage(regionID);

		// *Boop*
		this.client.playSoundEffect(SoundEffectID.UI_BOOP);
	}

	/**
	 * Called when the back arrow has been pressed
	 */
	private void onBackButtonPressed()
	{
		// Go back to the index page
		this.displayIndexPage();

		// *Boop*
		this.client.playSoundEffect(SoundEffectID.UI_BOOP);
	}

	/**
	 * Teleports the player to the specified teleport location
	 * @param teleport the teleport location
	 */
	private void triggerTeleport(Teleport teleport)
	{
		// Get the appropriate widget parent for the teleport, depending
		// on whether the teleport is of primary or alternate type
		int packedID = teleport.isAlt() ? ID_KEYEVENTS_ALTERNATE : ID_KEYEVENTS_PRIMARY;

		// Get the child index of the teleport
		final int widgetIndex = teleport.getChildIndex();

		// Call a CS2 script which will trigger the widget's keypress event.
		// Credit to Abex for discovering this clever trick.
		this.clientThread.invokeLater(() -> client.runScript(SCRIPT_TRIGGER_KEY, packedID, widgetIndex));
	}

	/**
	 * Triggers the rebind dialog in the Better Teleport Menu plugin
	 * @param teleport the teleport to rebind
	 */
	private void triggerRebindDialog(Teleport teleport)
	{
		// If the Better Teleport Menu isn't active, abort
		if (!this.isBetterTeleportMenuActive())
		{
			return;
		}

		// The Better Teleport Menu plugin listens for menu clicks on the event bus to
		// detect the "Set Hotkey" option being selected on a teleport on the vanilla menu.
		// For the plugin to detect this menu action being triggered on our custom teleport
		// icon widgets, we need to create a fake menu entry click event containing the widget
		// ID and index of the vanilla menu teleport widget and post it on the event bus.
		MenuEntry spoofMenuEntry = new SpoofMenuEntry(teleport.getWidget());

		this.eventBus.post(new MenuOptionClicked(spoofMenuEntry));
	}

	/**
	 * Checks if Abex's Better Teleport Menu plugin is installed and active
	 * @return true if active, otherwise false
	 */
	private boolean isBetterTeleportMenuActive()
	{
		// Cycle through each of the installed plugins checking for Better Teleport Menu
		for (Plugin plugin : this.pluginManager.getPlugins())
		{
			boolean pluginNameMatches = plugin.getName().equals(PLUGIN_NAME_BTM);

			// Check it's both installed and enabled
			if (pluginNameMatches && this.pluginManager.isPluginEnabled(plugin))
				return true;
		}

		return false;
	}

	/**
	 * Checks if there's a teleport available for a given teleport definition
	 * @param teleportDefinition the teleport definition
	 * @return true, if the teleport is available to the player, otherwise false
	 */
	private boolean isTeleportAvailable(TeleportDefinition teleportDefinition)
	{
		return this.getAvailableTeleport(teleportDefinition) != null;
	}

	/**
	 * Gets the teleport corresponding to the specified teleport definition
	 * @param teleportDefinition the teleport definition
	 * @return the teleport option, or null if no teleport exists by the given name
	 */
	private Teleport getAvailableTeleport(TeleportDefinition teleportDefinition)
	{
		return this.availableTeleports.get(teleportDefinition.getKey());
	}

	/**
	 * Gets the mode the menu was in at last use, this value is
	 * stored in the config manager and persists between sessions
	 * @return true if the last used mode was the map mode, false
	 * if it was in the default nexus menu mode
	 */
	private boolean getPreviousDisplayMode()
	{
		// Get the stored previous display mode
		Boolean mode = this.configManager.getConfiguration(CFG_GROUP, CFG_KEY_STATE, Boolean.class);

		// If the mode has yet to be defined, return false
		if (mode == null)
		{
			return false;
		}

		// Otherwise return the value pulled from config
		return mode;
	}

	/**
	 * Stores the display mode the menu was last in into the config
	 * @param mode true for map mode, false for the standard menu
	 */
	private void setPreviousDisplayMode(boolean mode)
	{
		this.configManager.setConfiguration(CFG_GROUP, CFG_KEY_STATE, mode);
	}

	/**
	 * Gets the menu action appropriate for the current nexus mode
	 * @return the action string
	 */
	private String getModeAction()
	{
		// Get the current mode for the nexus. 0 = Teleport, 1 = Scry
		int mode = this.client.getVarbitValue(VARBIT_NEXUS_MODE);

		// Return "Teleport" or "Scry", depending on the mode
		return (mode == 1) ? ACTION_TEXT_SCRY : ACTION_TEXT_TELE;
	}
}
