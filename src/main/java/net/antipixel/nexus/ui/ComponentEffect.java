package net.antipixel.nexus.ui;

/**
 * An interface for implementing an effect on a UIComponent
 * @author Antipixel
 */
public interface ComponentEffect
{
    void onUpdate();
    void apply(UIComponent component);
    void onRemoved(UIComponent component);
}
