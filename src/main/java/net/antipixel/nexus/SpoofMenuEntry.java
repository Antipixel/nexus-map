package net.antipixel.nexus;

import javax.annotation.Nonnull;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Imitates a menu entry of the vanilla client teleport widget, used for triggering
 * the hotkey reassignment dialog box in the Better Teleport Menu plugin
 * @author Antipixel
 */
public class SpoofMenuEntry implements MenuEntry
{
    private Widget widget;

    /**
     * Constructs a new spoof menu entry
     * @param widget the original teleport button widget
     */
    public SpoofMenuEntry(Widget widget)
    {
        this.widget = widget;
    }

    @Override
    public String getOption()
    {
        return "";
    }

    @Override
    public MenuEntry setOption(String option)
    {
        return this;
    }

    @Override
    public String getTarget()
    {
        return "";
    }

    @Override
    public MenuEntry setTarget(String target)
    {
        return this;
    }

    @Override
    public int getIdentifier()
    {
        return 9;
    }

    @Override
    public MenuEntry setIdentifier(int identifier)
    {
        return this;
    }

    @Override
    public MenuAction getType() {
        return MenuAction.CC_OP_LOW_PRIORITY;
    }

    @Override
    public MenuEntry setType(MenuAction type)
    {
        return this;
    }

    @Override
    public int getParam0()
    {
        return this.widget.getIndex();
    }

    @Override
    public MenuEntry setParam0(int param0)
    {
        return this;
    }

    @Override
    public int getParam1()
    {
        // The ID for the widget that is sent in the menu click event is 1 higher
        // than the ID of the widget that the teleport names are stored on
        return this.getWidget().getId() + 1;
    }

    @Override
    public MenuEntry setParam1(int param1)
    {
        return this;
    }

    @Override
    public boolean isForceLeftClick()
    {
        return false;
    }

    @Override
    public MenuEntry setForceLeftClick(boolean forceLeftClick)
    {
        return this;
    }

    @Override
    public int getWorldViewId()
    {
        return 0;
    }

    @Override
    public MenuEntry setWorldViewId(int worldViewId)
    {
        return null;
    }

    @Override
    public boolean isDeprioritized()
    {
        return false;
    }

    @Override
    public MenuEntry setDeprioritized(boolean deprioritized)
    {
        return this;
    }

    @Override
    public MenuEntry onClick(Consumer<MenuEntry> callback)
    {
        return this;
    }

    @Override
    public Consumer<MenuEntry> onClick()
    {
        return null;
    }

    @Override
    public boolean isItemOp() {
        return false;
    }

    @Override
    public int getItemOp() {
        return 0;
    }

    @Override
    public int getItemId() {
        return 0;
    }

    @Override
    public MenuEntry setItemId(int itemId)
    {
        return null;
    }

    @Nullable
    @Override
    public Widget getWidget() {
        return this.widget;
    }

    @Nullable
    @Override
    public NPC getNpc() {
        return null;
    }

    @Nullable
    @Override
    public Player getPlayer() {
        return null;
    }

    @Nullable
    @Override
    public Actor getActor() {
        return null;
    }

    @Nullable
    @Override
    public Menu getSubMenu()
    {
        return null;
    }

    @Nonnull
    @Override
    public Menu createSubMenu()
    {
        return null;
    }

    @Override
    public void deleteSubMenu()
    {

    }
}
