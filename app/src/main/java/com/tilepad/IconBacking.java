package com.tilepad;

/**
 * Decides what a shortcut's own icon needs behind it to stay visible on the tile. Pure, so it's
 * unit-tested; TileView measures the icon (how much of its square it covers, and how light its
 * visible pixels are) and draws the result.
 *
 * <ul>
 *   <li>An icon in the tile's own tone that fills its square (a white rounded-square icon in day
 *       mode) reads as one big button: give it a hairline EDGE.</li>
 *   <li>A glyph in the tile's own tone with nothing behind it (a white symbol in day mode) all
 *       but vanishes: give it a contrasting PLATE.</li>
 *   <li>Anything else already stands out: NONE.</li>
 * </ul>
 */
final class IconBacking {
    enum Kind { NONE, EDGE, PLATE }

    /** Share of the icon's square that must be opaque for it to count as a filled shape. */
    static final float FILLED = 0.55f;
    /** Mean luminance (0..1) of the opaque pixels above which an icon is "light", below which "dark". */
    static final float LIGHT = 0.82f, DARK = 0.18f;

    static Kind of(float coverage, float luminance, boolean night) {
        if (coverage <= 0f) return Kind.NONE;
        boolean sameTone = night ? luminance < DARK : luminance > LIGHT;
        if (!sameTone) return Kind.NONE;
        return coverage >= FILLED ? Kind.EDGE : Kind.PLATE;
    }

    private IconBacking() { }
}
