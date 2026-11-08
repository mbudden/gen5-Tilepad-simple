package com.tilepad;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IconBackingTest {
    @Test public void lightFilledIconGetsAnEdgeInDayMode() {
        // The dashcam icon: a white rounded square with a small colored glyph.
        assertEquals(IconBacking.Kind.EDGE, IconBacking.of(0.9f, 0.93f, false));
    }

    @Test public void lightGlyphGetsAPlateInDayMode() {
        // Settings' Accounts icon: a white person on a transparent background.
        assertEquals(IconBacking.Kind.PLATE, IconBacking.of(0.3f, 1f, false));
    }

    @Test public void colorfulIconsAreLeftAlone() {
        assertEquals(IconBacking.Kind.NONE, IconBacking.of(0.9f, 0.5f, false));
        assertEquals(IconBacking.Kind.NONE, IconBacking.of(0.9f, 0.5f, true));
    }

    @Test public void lightIconsAreFineAtNight() {
        assertEquals(IconBacking.Kind.NONE, IconBacking.of(0.3f, 1f, true));
        assertEquals(IconBacking.Kind.NONE, IconBacking.of(0.9f, 0.93f, true));
    }

    @Test public void darkIconsGetTheMirrorTreatmentAtNight() {
        assertEquals(IconBacking.Kind.PLATE, IconBacking.of(0.3f, 0.05f, true));
        assertEquals(IconBacking.Kind.EDGE, IconBacking.of(0.9f, 0.05f, true));
        assertEquals(IconBacking.Kind.NONE, IconBacking.of(0.3f, 0.05f, false));
    }

    @Test public void anEmptyIconNeedsNothing() {
        assertEquals(IconBacking.Kind.NONE, IconBacking.of(0f, 0f, false));
    }
}
