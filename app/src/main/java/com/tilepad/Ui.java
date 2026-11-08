package com.tilepad;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Day and night color tokens (the Preconditioning app's palette) and small view factories.
 * Every view is built in code; {@link #apply} picks the palette before any view is built.
 */
final class Ui {
    static boolean NIGHT = true;
    /** Window, tiles, raised surfaces inside popups, and hairlines. */
    static int BG, TILE, TILE_PRESSED, SURFACE, SURFACE_PRESSED, LINE;
    /** Primary, secondary and faint text. */
    static int TEXT, SECONDARY, MUTED, FAINT;
    /** Accent fill, its pressed shade, accent-colored text, and the soft accent tint. */
    static int ACCENT, ACCENT_PRESSED, LINK, TINT;
    static int GREEN, AMBER, RED, RED_PRESSED, RED_TINT, PURPLE, SCRIM;
    /** Text and glyphs on an accent or red fill. */
    static final int ON_ACCENT = 0xffffffff;

    static { apply(true); }

    static void apply(boolean night) {
        NIGHT = night;
        if (night) {
            BG = 0xff0b0f17; TILE = 0xff161c27; TILE_PRESSED = 0xff1f2733;
            SURFACE = 0xff1f2733; SURFACE_PRESSED = 0xff2a3242; LINE = 0xff2a3242;
            TEXT = 0xffe8ecf3; SECONDARY = 0xffc3cad6; MUTED = 0xff97a1b3; FAINT = 0xff5d6778;
            ACCENT = 0xff2678eb; ACCENT_PRESSED = 0xff1f63c4; LINK = 0xff6aaeff; TINT = 0xff1d3150;
            GREEN = 0xff2fbf6b; AMBER = 0xfff59e0b; RED = 0xffe5484d; RED_PRESSED = 0xffc23a3f;
            RED_TINT = 0xff3a1c1f; PURPLE = 0xff8b6cff; SCRIM = 0xb305080e;
        } else {
            BG = 0xffffffff; TILE = 0xfff0f2f6; TILE_PRESSED = 0xffe2e6ed;
            SURFACE = 0xffeaedf2; SURFACE_PRESSED = 0xffdce1e9; LINE = 0xffdfe3ea;
            TEXT = 0xff101522; SECONDARY = 0xff45536a; MUTED = 0xff677388; FAINT = 0xffa3acbb;
            ACCENT = 0xff007aff; ACCENT_PRESSED = 0xff0062cc; LINK = 0xff007aff; TINT = 0xffe7f0ff;
            GREEN = 0xff1aa251; AMBER = 0xffd98a00; RED = 0xffe5484d; RED_PRESSED = 0xffc23a3f;
            RED_TINT = 0xfffde8e8; PURPLE = 0xff6c4cf0; SCRIM = 0x66101522;
        }
    }

    /** Label color for a button with this fill: white on accent or red, otherwise secondary text. */
    static int onFill(int fill) { return fill == ACCENT || fill == RED || fill == GREEN ? ON_ACCENT : SECONDARY; }

    /** Pressed shade for a button fill. */
    static int pressedFor(int fill) {
        if (fill == ACCENT) return ACCENT_PRESSED;
        if (fill == RED) return RED_PRESSED;
        if (fill == TILE) return TILE_PRESSED;
        return SURFACE_PRESSED;
    }

    final Context context;
    private final float density;

    Ui(Context context) {
        this.context = context;
        density = context.getResources().getDisplayMetrics().density;
    }

    int dp(float value) { return Math.round(value * density); }

    static int tone(PreconPresenter.Tone tone) {
        switch (tone) {
            case READY: return ACCENT;
            case BUSY: return MUTED;
            case GOOD: return GREEN;
            case WARN: return AMBER;
            case BAD: return RED;
            default: return MUTED;
        }
    }

    static int alpha(int color, float alpha) {
        return Color.argb(Math.round(alpha * 255), Color.red(color), Color.green(color), Color.blue(color));
    }

    TextView text(String value, float sp, int color, boolean bold) {
        TextView v = new TextView(context);
        v.setText(value);
        v.setTextSize(sp);
        v.setTextColor(color);
        v.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        v.setIncludeFontPadding(false);
        return v;
    }

    TextView line(String value, float sp, int color, boolean bold) {
        TextView v = text(value, sp, color, bold);
        v.setSingleLine(true);
        v.setEllipsize(TextUtils.TruncateAt.END);
        return v;
    }

    ImageView glyph(Glyph.Kind kind, int color, int sizeDp) {
        ImageView v = new ImageView(context);
        v.setImageDrawable(new Glyph(kind, color, dp(sizeDp)));
        v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        v.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        return v;
    }

    /** Small all-caps section label, like the Preconditioning app's card captions. */
    TextView caption(String value) {
        return line(value.toUpperCase(java.util.Locale.US), 12, MUTED, true);
    }

    LinearLayout column() { LinearLayout v = new LinearLayout(context); v.setOrientation(LinearLayout.VERTICAL); return v; }
    LinearLayout row() { LinearLayout v = new LinearLayout(context); v.setOrientation(LinearLayout.HORIZONTAL); return v; }

    GradientDrawable rounded(int color, float radiusDp) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radiusDp));
        return shape;
    }

    GradientDrawable outlined(int fill, float radiusDp, float strokeDp, int stroke) {
        GradientDrawable shape = rounded(fill, radiusDp);
        shape.setStroke(dp(strokeDp), stroke);
        return shape;
    }

    GradientDrawable dashed(float radiusDp, int stroke) {
        GradientDrawable shape = rounded(Color.TRANSPARENT, radiusDp);
        shape.setStroke(dp(2), stroke, dp(9), dp(7));
        return shape;
    }

    GradientDrawable circle(int color) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(color);
        return shape;
    }

    /** Pressed, TUNE-focused (activated) and resting states for any tappable surface. */
    Drawable pressable(int fill, int pressed, float radiusDp) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, rounded(pressed, radiusDp));
        states.addState(new int[]{android.R.attr.state_activated}, outlined(fill, radiusDp, 3, ACCENT));
        states.addState(new int[]{}, rounded(fill, radiusDp));
        return states;
    }

    Drawable pressableOutline(float radiusDp) {
        StateListDrawable states = new StateListDrawable();
        GradientDrawable down = dashed(radiusDp, LINE); down.setColor(TILE);
        states.addState(new int[]{android.R.attr.state_pressed}, down);
        states.addState(new int[]{android.R.attr.state_activated}, outlined(Color.TRANSPARENT, radiusDp, 3, ACCENT));
        states.addState(new int[]{}, dashed(radiusDp, LINE));
        return states;
    }

    @SuppressWarnings("deprecation")
    static void background(View view, Drawable drawable) { view.setBackgroundDrawable(drawable); }

    /** A rounded pill button with an optional leading glyph. */
    LinearLayout button(String title, Glyph.Kind glyph, int fill, int pressed, int textColor) {
        LinearLayout v = row();
        v.setGravity(Gravity.CENTER);
        v.setPadding(dp(28), 0, dp(28), 0);
        v.setMinimumWidth(dp(150));
        background(v, pressable(fill, pressed, 30));
        if (glyph != null) {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dp(24), dp(24));
            p.rightMargin = dp(10);
            v.addView(glyph(glyph, textColor, 24), p);
        }
        v.addView(text(title, 19, textColor, true));
        v.setClickable(true);
        v.setFocusable(false);
        v.setContentDescription(title);
        return v;
    }

    /** A round icon-only button, e.g. back or the edit badges. */
    FrameLayout iconButton(Glyph.Kind kind, int fill, int pressed, int color, int sizeDp, int glyphDp) {
        FrameLayout v = new FrameLayout(context);
        StateListDrawable states = new StateListDrawable();
        GradientDrawable down = circle(pressed);
        GradientDrawable focus = circle(fill); focus.setStroke(dp(3), ACCENT);
        states.addState(new int[]{android.R.attr.state_pressed}, down);
        states.addState(new int[]{android.R.attr.state_activated}, focus);
        states.addState(new int[]{}, circle(fill));
        background(v, states);
        v.addView(glyph(kind, color, glyphDp), new FrameLayout.LayoutParams(dp(glyphDp), dp(glyphDp), Gravity.CENTER));
        v.setLayoutParams(new FrameLayout.LayoutParams(dp(sizeDp), dp(sizeDp)));
        v.setClickable(true);
        v.setFocusable(false);
        return v;
    }
}
