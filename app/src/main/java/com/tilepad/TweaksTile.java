package com.tilepad;

/** Same layout as Wi-Fi, ADB and Battery: the headline says how many tweaks are on. */
final class TweaksTile extends StatusTile {
    TweaksTile(Ui ui, TileSpec spec, EditListener edits) {
        super(ui, spec, edits, Glyph.Kind.TWEAKS, "Tweaks");
        bind(TweaksSnapshot.checking());
    }

    void bind(TweaksSnapshot s) {
        setAction(null, false);
        boolean checking = s.enabled < 0 && s.problem == null;
        setIcon(s.problem != null ? Ui.AMBER : s.on() ? Ui.LINK : Ui.MUTED, checking);
        setDot(checking ? 0 : s.problem != null ? Ui.AMBER : s.on() ? Ui.GREEN : Ui.FAINT);
        show(headline, checking ? "Checking…" : s.problem != null ? "Unavailable"
            : s.enabled == 0 ? "Off" : s.enabled == s.total ? "All on" : s.enabled + " on");
        show(detail, s.enabled < 0 ? "" : s.enabled == 0 ? s.total + " available"
            : s.enabled == s.total ? s.total + " tweaks" : (s.total - s.enabled) + " off");
        show(meta, "Tap to manage");
        setContentDescription("Tweaks, " + (s.enabled >= 0 ? (s.on() ? "on, " : "off, ") + s.enabled + " of " + s.total + " enabled" : checking ? "checking" : s.problem));
    }
}
