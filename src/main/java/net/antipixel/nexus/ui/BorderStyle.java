package net.antipixel.nexus.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Sprite border style
 * @author Antipixel
 */
@Getter
@AllArgsConstructor
public enum BorderStyle
{
    NONE(0, "None"),
    BLACK(1, "Black"),
    WHITE(2, "White");

    private int id;
    private String name;

    @Override
    public String toString()
    {
        return this.getName();
    }
}
