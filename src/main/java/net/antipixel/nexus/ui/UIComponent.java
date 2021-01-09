package net.antipixel.nexus.ui;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;

/**
 * UI Component classes allow for complex user interface functionality by
 * wrapping the gaming widget and carefully controlling its behaviour
 * @author Antipixel
 */
public abstract class UIComponent
{
	private static final int MAX_OPACITY = 255;
	private static final String BTN_NAME_FORMAT = "<col=ff9040>%s</col>";

	/* Underlying widget */
	private Widget widget;

	/* Actions and events */
	private List<MenuAction> actions;
	private ComponentEventListener hoverListener;
	private ComponentEventListener leaveListener;

	/**
	 * Constructs a new UIComponent
	 * @param widget the underlying game widget
	 */
	public UIComponent(Widget widget)
	{
		this.widget = widget;

		// Assign the event listeners to the widget
		this.widget.setOnOpListener((JavaScriptCallback) this::onActionSelected);
		this.widget.setOnMouseOverListener((JavaScriptCallback) this::onMouseHover);
		this.widget.setOnMouseLeaveListener((JavaScriptCallback) this::onMouseLeave);
		this.widget.setHasListener(true);

		this.actions = new ArrayList<>();
	}

	/**
	 * Adds an action option to the component's menu
	 * @param action the action name
 	 * @param callback the callback event, which is trigger upon the
	 *                 selection of this menu option
	 */
	public void addAction(String action, MenuAction callback)
	{
		this.widget.setAction(actions.size(), action);
		this.actions.add(callback);
	}

	/**
	 * Triggered upon the selection of menu option
	 * @param e the script event
	 */
	private void onActionSelected(ScriptEvent e)
	{
		// If there's no actions specified, ignore
		if (this.actions.isEmpty())
			return;

		// Get the action action event object for this menu option
		MenuAction actionEvent = this.actions.get(e.getOp() - 1);

		// Call the action listener for this option
		actionEvent.onMenuAction();
	}

	/**
	 * Triggered upon the mouse entering the component
	 * @param e the script event
	 */
	protected void onMouseHover(ScriptEvent e)
	{
		// If a hover event is specified, trigger it
		if (this.hoverListener != null)
			this.hoverListener.onComponentEvent(this);
	}

	/**
	 * Triggered upon the mouse leaving the component
	 * @param e the script event
	 */
	protected void onMouseLeave(ScriptEvent e)
	{
		// If a leave event is specified, trigger it
		if (this.leaveListener != null)
			this.leaveListener.onComponentEvent(this);
	}

	/**
	 * Sets a listener which will be called upon the mouse
	 * hovering over the widget
	 * @param listener the listener
	 */
	public void setOnHoverListener(ComponentEventListener listener)
	{
		this.hoverListener = listener;
	}

	/**
	 * Sets a listener which will be called upon the mouse
	 * exiting from over the widget
	 * @param listener the listener
	 */
	public void setOnLeaveListener(ComponentEventListener listener)
	{
		this.leaveListener = listener;
	}

	/**
	 * Sets the name of the component widget
	 * @param name the component name
	 */
	public void setName(String name)
	{
		this.widget.setName(String.format(BTN_NAME_FORMAT, name));
	}

	/**
	 * Sets the component size
	 * @param width the component width
	 * @param height the component height
	 */
	public void setSize(int width, int height)
	{
		this.widget.setOriginalWidth(width);
		this.widget.setOriginalHeight(height);
	}

	/**
	 * Sets the position of the component, relative
	 * to the parent layer widget
	 * @param x the x position
	 * @param y the y position
	 */
	public void setPosition(int x, int y)
	{
		this.widget.setOriginalX(x);
		this.widget.setOriginalY(y);
	}

	/**
	 * Sets the X position of the component, relative
	 * to the parent layer
	 * @param x the x position
	 */
	public void setX(int x)
	{
		this.widget.setOriginalX(x);
	}

	/**
	 * Sets the Y position of the component, relative
	 * to the parent layer
	 * @param y the x position
	 */
	public void setY(int y)
	{
		this.widget.setOriginalY(y);
	}

	/**
	 * Gets the X position of the component, relative
	 * to the the parent layer
	 * @return the x position
	 */
	public int getX()
	{
		return this.widget.getOriginalX();
	}

	/**
	 * Gets the Y position of the component, relative
	 * to the the parent layer
	 * @return the y position
	 */
	public int getY()
	{
		return this.widget.getOriginalY();
	}


	/**
	 * Sets the visibility of the component
	 * @param visible true for visible, false for hidden
	 */
	public void setVisibility(boolean visible)
	{
		this.widget.setHidden(!visible);
	}

	/**
	 * Sets the opacity of the widget
	 * @param opacity the opacity value. Expects a value
	 *                between 0.0 (transparent) and 1.0 (opaque)
	 */
	public void setOpacity(float opacity)
	{
		// Cap the opacity to 1.0
		if (opacity > 1.0)
			opacity = 1.0f;

		// Invert the percentage
		float percentage = 1.0f - opacity;

		// Convert the percentage value to a 0-255 integer
		this.widget.setOpacity((int)(percentage * MAX_OPACITY));
	}

	/**
	 * Gets the opacity of the widget
	 * @return an opacity value between 0.0 (transparent) and 1.0 (opaque)
	 */
	public float getOpacity()
	{
		// Convert the opacity to a percentage
		float opacity = (float)this.widget.getOpacity() / MAX_OPACITY;

		// Invert the percentage
		return 1.0f - opacity;
	}

	/**
	 * Gets the underlying widget for this component
	 * @return the underlying widget
	 */
	public Widget getWidget()
	{
		return this.widget;
	}
}
