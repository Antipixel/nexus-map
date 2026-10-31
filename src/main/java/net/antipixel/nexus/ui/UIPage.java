package net.antipixel.nexus.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A group of UIComponents
 * @author Antipixel
 */
public class UIPage
{
	private Map<Integer, Set<UIComponent>> componentSets;

	/**
	 * Constructs a new UI page
	 */
	public UIPage()
	{
		this.componentSets = new HashMap<>();
	}

	/**
	 * Sets the visibility for all the components in the page
	 * @param visibility true for visible, false for hidden
	 */
	public void setVisibility(boolean visibility)
	{
		setVisibility(visibility, null);
	}

	/**
	 * Sets the visibility for components in the page
	 * @param visibility true for visible, false for hidden
	 * @param componentSetId an arbitrary value to control which component set to change the visibility for, use null to apply to all sets
	 */
	public void setVisibility(boolean visibility, Integer componentSetId)
	{
		Stream<UIComponent> stream;

		// If Id is null, flat stream of every UIComponent in the componentSets map
		if (componentSetId == null)
		{
			stream = this.componentSets.values().stream().flatMap(Set::stream);
		}
		// If Id is found, just stream that set
		else if (this.componentSets.containsKey(componentSetId))
		{
			stream = this.componentSets.get(componentSetId).stream();
		}
		// If no set exists, bail out
		else
		{
			return;
		}

		// Update the visibility for each of the components
		stream.forEach(c -> c.setVisibility(visibility));
	}

	/**
	 * Adds a UI component to the page
	 * @param component the component to add
	 * @param componentSetId an arbitrary value to control which component set to add items to
	 */
	public void add(UIComponent component, int componentSetId)
	{
		if (!this.componentSets.containsKey(componentSetId))
		{
			this.componentSets.put(componentSetId, new HashSet<>());
		}
		this.componentSets.get(componentSetId).add(component);
	}

	/**
	 * Removes a UI component from the page
	 * @param component the component to remove
	 * @param componentSetId an arbitrary value to control which component set to remove items from
	 */
	public void remove(UIComponent component, int componentSetId)
	{
		if (this.componentSets.containsKey(componentSetId))
		{
			this.componentSets.get(componentSetId).remove(component);
		}
	}
}
