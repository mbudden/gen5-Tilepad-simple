package com.tilepad;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Information tiles (Wi-Fi, ADB, Tweaks, Battery), all one layout, in the Preconditioning app's
 * card style: a status dot and all-caps caption with a small glyph in the corner, one big state
 * word (headline), a detail line under it, and at the bottom either a muted hint (meta) or an
 * action pill such as Start.
 */
abstract class StatusTile extends TileView {
    final Glyph glyph;
    final SpinnerView spinner;
    final TextView caption, headline, detail, meta, action;
    private final View dot;

    StatusTile(Ui ui, TileSpec spec, EditListener edits, Glyph.Kind kind, String captionText) {
        super(ui, spec, edits);
        LinearLayout content = ui.column();
        content.setPadding(ui.dp(16), ui.dp(15), ui.dp(14), ui.dp(15));

        FrameLayout header = new FrameLayout(ui.context);
        LinearLayout label = ui.row();
        label.setGravity(Gravity.CENTER_VERTICAL);
        dot = new View(ui.context);
        LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(ui.dp(7), ui.dp(7));
        dp.rightMargin = ui.dp(6);
        label.addView(dot, dp);
        caption = ui.caption(captionText);
        label.addView(caption);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, ui.dp(26), Gravity.LEFT | Gravity.TOP);
        lp.rightMargin = ui.dp(34);
        header.addView(label, lp);

        FrameLayout icon = new FrameLayout(ui.context);
        spinner = new SpinnerView(ui.context, ui.dp(2));
        icon.addView(spinner, new FrameLayout.LayoutParams(-1, -1));
        glyph = new Glyph(kind, Ui.MUTED, ui.dp(26));
        ImageView image = new ImageView(ui.context);
        image.setImageDrawable(glyph);
        image.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        icon.addView(image, new FrameLayout.LayoutParams(ui.dp(26), ui.dp(26), Gravity.CENTER));
        FrameLayout.LayoutParams ip = new FrameLayout.LayoutParams(ui.dp(38), ui.dp(38), Gravity.RIGHT | Gravity.TOP);
        ip.topMargin = -ui.dp(6);
        ip.rightMargin = -ui.dp(4);
        header.addView(icon, ip);
        content.addView(header, new LinearLayout.LayoutParams(-1, ui.dp(26)));

        headline = ui.line("", 26, Ui.TEXT, true);
        addLine(content, headline, 10);
        detail = ui.line("", 15, Ui.SECONDARY, false);
        addLine(content, detail, 6);
        content.addView(new View(ui.context), new LinearLayout.LayoutParams(-1, 0, 1f));

        FrameLayout bottom = new FrameLayout(ui.context);
        meta = ui.line("", 13, Ui.MUTED, false);
        bottom.addView(meta, new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM | Gravity.LEFT));
        action = ui.line("", 15, Ui.ON_ACCENT, true);
        action.setGravity(Gravity.CENTER);
        action.setPadding(ui.dp(20), 0, ui.dp(20), 0);
        action.setVisibility(GONE);
        bottom.addView(action, new FrameLayout.LayoutParams(-2, ui.dp(36), Gravity.BOTTOM | Gravity.LEFT));
        content.addView(bottom, new LinearLayout.LayoutParams(-1, ui.dp(36)));
        setContent(content);
    }

    private void addLine(LinearLayout parent, TextView view, int topDp) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = ui.dp(topDp);
        parent.addView(view, p);
    }

    /** The caption's status dot; 0 hides it (the caption keeps its place). */
    void setDot(int color) {
        dot.setVisibility(color == 0 ? INVISIBLE : VISIBLE);
        if (color != 0) Ui.background(dot, ui.circle(color));
    }

    /** Glyph and spinner color, and whether the spinner turns. */
    void setIcon(int color, boolean spinning) {
        glyph.setColor(color);
        spinner.setColor(color);
        spinner.setSpinning(spinning);
    }

    /**
     * The bottom action pill in place of the hint: primary is a solid accent pill (Start),
     * otherwise a soft tinted one (elapsed time, Turn off). Null shows the hint instead.
     */
    void setAction(String label, boolean primary) {
        if (label == null) { action.setVisibility(GONE); meta.setVisibility(VISIBLE); return; }
        action.setText(label);
        action.setTextColor(primary ? Ui.ON_ACCENT : Ui.LINK);
        Ui.background(action, ui.rounded(primary ? Ui.ACCENT : Ui.TINT, 18));
        action.setVisibility(VISIBLE);
        meta.setVisibility(INVISIBLE);
    }

    /** Empty text hides the row but keeps its slot, so rows line up across tiles. */
    static void show(TextView view, String text) {
        view.setText(text == null ? "" : text);
        view.setVisibility(text == null || text.isEmpty() ? INVISIBLE : VISIBLE);
    }
}
