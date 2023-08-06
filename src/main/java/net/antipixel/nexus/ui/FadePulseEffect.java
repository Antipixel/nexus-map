package net.antipixel.nexus.ui;

/**
 * A component effect which fades the opacity in a pulsing fashion
 * @author Antipixel
 */
public class FadePulseEffect implements ComponentEffect
{
    private static final float MAX_OPACITY = 1.0f;

    private Direction fadeDirection;
    private float minimumOpacity;
    private float opacity;
    private float speed;

    /**
     * Constructs a new pulse fade effect object
     * @param minOpacity the minimum opacity to fade to
     * @param speed the speed of the fade effect as a percentage
     *              e.g. a value of 0.012 will fade 1.2% each tick
     */
    public FadePulseEffect(float minOpacity, float speed)
    {
        this.fadeDirection = Direction.OUT;
        this.minimumOpacity = minOpacity;
        this.opacity = MAX_OPACITY;
        this.speed = speed;
    }

    @Override
    public void onUpdate()
    {
		if (this.opacity <= this.minimumOpacity)
			this.fadeDirection = Direction.IN;

		if (this.opacity >= MAX_OPACITY)
            this.fadeDirection = Direction.OUT;

		this.opacity += this.fadeDirection == Direction.IN ? speed : -speed;
    }

    @Override
    public void apply(UIComponent component)
    {
        component.setOpacity(this.opacity);
    }

    @Override
    public void onRemoved(UIComponent component)
    {
        // Reset the component to full opacity
        component.setOpacity(MAX_OPACITY);
    }

    /**
     * Direction of the fade
     */
    enum Direction
    {
        IN,
        OUT
    }
}
