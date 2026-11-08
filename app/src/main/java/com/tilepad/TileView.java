package com.tilepad;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** A square grid tile: rounded card, content, and edit badges for removable tiles. */
class TileView extends FrameLayout {
    interface EditListener {
        void onRename(TileView tile);
        void onRemove(TileView tile);
    }

    final Ui ui;
    TileSpec spec;
    private final View rename, remove;

    TileView(Ui ui, TileSpec spec, final EditListener edits) {
        super(ui.context);
        this.ui = ui;
        this.spec = spec;
        setClipChildren(false);
        setClickable(true);
        setFocusable(false); // TUNE has its own highlight; avoid a second native focus ring.
        if (spec.kind == TileSpec.Kind.ADD) Ui.background(this, ui.pressableOutline(26));
        else Ui.background(this, ui.pressable(Ui.TILE, Ui.TILE_PRESSED, 26));

        rename = badge(Glyph.Kind.PENCIL, Ui.ACCENT, Gravity.TOP | Gravity.LEFT, "Rename");
        remove = badge(Glyph.Kind.MINUS, Ui.RED, Gravity.TOP | Gravity.RIGHT, "Remove");
        rename.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) { edits.onRename(TileView.this); }
        });
        remove.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) { edits.onRemove(TileView.this); }
        });
    }

    private View badge(Glyph.Kind kind, int fill, int gravity, String description) {
        FrameLayout badge = ui.iconButton(kind, fill, Ui.pressedFor(fill), Ui.ON_ACCENT, 38, 20);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ui.dp(38), ui.dp(38), gravity);
        p.leftMargin = p.rightMargin = p.topMargin = ui.dp(6);
        badge.setContentDescription(description);
        badge.setVisibility(GONE);
        addView(badge, p);
        return badge;
    }

    /** Content goes beneath the badges. */
    void setContent(View content) {
        addView(content, 0, new FrameLayout.LayoutParams(-1, -1));
    }

    void setEditing(boolean editing) {
        int visibility = editing && !spec.isCore() ? VISIBLE : GONE;
        rename.setVisibility(visibility);
        remove.setVisibility(visibility);
    }

    void setAvailable(boolean value) {
        float alpha = value ? 1f : 0.45f;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != rename && child != remove) child.setAlpha(alpha);
        }
    }

    /** Centered icon over a name: the layout for every tile that is not a status tile. */
    static TileView simple(Ui ui, TileSpec spec, EditListener edits, View icon, int iconDp, String label) {
        TileView tile = new TileView(ui, spec, edits);
        LinearLayout column = ui.column();
        column.setGravity(Gravity.CENTER);
        column.setPadding(ui.dp(12), ui.dp(22), ui.dp(12), ui.dp(8));
        column.addView(icon, new LinearLayout.LayoutParams(ui.dp(iconDp), ui.dp(iconDp)));
        TextView name = ui.text(LabelWrap.wrap(label), 16, Ui.TEXT, false);
        name.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        name.setTag("label");
        // Always two lines tall, so icons line up whether a name wraps or not.
        name.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        name.setLines(2);
        name.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-2, -2);
        p.topMargin = ui.dp(14);
        column.addView(name, p);
        tile.setContent(column);
        tile.setContentDescription(label);
        return tile;
    }

    /** A soft tinted rounded square holding a glyph in the tint's own color. */
    static FrameLayout chip(Ui ui, Glyph.Kind kind, int tint, int color) {
        FrameLayout chip = new FrameLayout(ui.context);
        Ui.background(chip, ui.rounded(tint, 19));
        chip.addView(ui.glyph(kind, color, 34), new FrameLayout.LayoutParams(ui.dp(34), ui.dp(34), Gravity.CENTER));
        return chip;
    }

    /**
     * A shortcut's own icon, with whatever backing {@link IconBacking} says it needs to stand
     * out from the tile: a hairline edge for a filled icon in the tile's own tone (a white
     * square in day mode), or a contrasting plate for a bare glyph in that tone.
     */
    static View appIcon(Ui ui, Drawable icon) {
        ImageView v = new ImageView(ui.context);
        v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (icon != null) v.setImageDrawable(icon);
        else v.setImageDrawable(new Glyph(Glyph.Kind.APPS, Ui.MUTED, ui.dp(48)));
        v.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        IconBacking.Kind backing = icon == null ? IconBacking.Kind.NONE : backing(icon);
        if (backing == IconBacking.Kind.NONE) return v;
        FrameLayout plate = new FrameLayout(ui.context);
        plate.addView(v, new FrameLayout.LayoutParams(-1, -1));
        back(ui, plate, v, backing, 70);
        return plate;
    }

    /** Styles plate (the icon's parent FrameLayout, sizeDp square) for the given backing. */
    static void back(Ui ui, FrameLayout plate, View icon, IconBacking.Kind backing, int sizeDp) {
        int inset = 0;
        if (backing == IconBacking.Kind.EDGE) {
            Ui.background(plate, ui.outlined(Ui.NIGHT ? 0x00000000 : 0xffffffff, sizeDp * 0.26f, 1,
                Ui.NIGHT ? Ui.MUTED : 0xffcfd5de));
            inset = ui.dp(sizeDp * 0.06f);
        } else if (backing == IconBacking.Kind.PLATE) {
            Ui.background(plate, ui.rounded(Ui.NIGHT ? Ui.SECONDARY : Ui.MUTED, sizeDp * 0.27f));
            inset = ui.dp(sizeDp * 0.19f);
        } else {
            Ui.background(plate, null);
        }
        FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) icon.getLayoutParams();
        p.leftMargin = p.topMargin = p.rightMargin = p.bottomMargin = inset;
        icon.setLayoutParams(p);
    }

    /** Draws the icon small and reads how much of its square it covers and how light it is. */
    static IconBacking.Kind backing(Drawable icon) {
        if (icon == null) return IconBacking.Kind.NONE;
        final int size = 48;
        try {
            android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888);
            android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
            android.graphics.Rect old = new android.graphics.Rect(icon.getBounds());
            icon.setBounds(0, 0, size, size);
            icon.draw(canvas);
            icon.setBounds(old);
            int[] pixels = new int[size * size];
            bitmap.getPixels(pixels, 0, size, 0, 0, size, size);
            bitmap.recycle();
            int opaque = 0;
            double light = 0;
            for (int c : pixels) {
                if ((c >>> 24) < 128) continue;
                opaque++;
                light += (0.2126 * ((c >> 16) & 0xff) + 0.7152 * ((c >> 8) & 0xff) + 0.0722 * (c & 0xff)) / 255.0;
            }
            if (opaque == 0) return IconBacking.Kind.NONE;
            return IconBacking.of(opaque / (float) pixels.length, (float) (light / opaque), Ui.NIGHT);
        } catch (RuntimeException e) {
            return IconBacking.Kind.NONE;
        }
    }

    void setLabel(String label) {
        TextView name = (TextView) findViewWithTag("label");
        if (name != null) name.setText(LabelWrap.wrap(label));
        setContentDescription(label);
    }
}
