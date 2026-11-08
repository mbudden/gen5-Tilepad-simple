package com.tilepad;

/**
 * Connection state, network name and address; tapping opens Android's network picker.
 * On a hotspot-only unit it offers to turn on the Wi-Fi client instead.
 */
final class WifiTile extends StatusTile {
    WifiTile(Ui ui, TileSpec spec, EditListener edits) {
        super(ui, spec, edits, Glyph.Kind.WIFI, "Wi-Fi");
        bind(ConnectionSnapshot.unknown());
    }

    void bind(ConnectionSnapshot c) {
        setAction(null, false);
        switch (c.state) {
            case CONNECTED: {
                glyph.setKind(Glyph.Kind.WIFI);
                glyph.setSignal(c.signal < 0 ? 3 : c.signal);
                setIcon(Ui.LINK, false);
                setDot(Ui.GREEN);
                show(headline, "Connected");
                show(detail, c.network);
                show(meta, c.linkMbps > 0 ? c.ip + " · " + c.linkMbps + " Mbps" : c.ip);
                setContentDescription("Wi-Fi connected to " + c.network + ", " + c.ip);
                break;
            }
            case DISCONNECTED:
                glyph.setKind(Glyph.Kind.WIFI); glyph.setSignal(0);
                setIcon(Ui.MUTED, false);
                setDot(Ui.AMBER);
                show(headline, "No network");
                show(detail, "");
                show(meta, "Tap to choose");
                setContentDescription("Wi-Fi not connected");
                break;
            case OFF:
                glyph.setKind(Glyph.Kind.WIFI_OFF); glyph.setSignal(0);
                setIcon(Ui.MUTED, false);
                setDot(Ui.FAINT);
                show(headline, "Off");
                show(detail, "");
                show(meta, "Tap for settings");
                setContentDescription("Wi-Fi off");
                break;
            default:
                glyph.setKind(Glyph.Kind.WIFI); glyph.setSignal(0);
                setIcon(Ui.MUTED, false);
                setDot(0);
                show(headline, "Checking…");
                show(detail, "");
                show(meta, "");
                setContentDescription("Wi-Fi");
        }
    }

    /** The unit can't join networks yet; pendingReboot once the client is on but not started. */
    void bindClientOff(boolean pendingReboot) {
        setAction(null, false);
        glyph.setKind(Glyph.Kind.WIFI_OFF); glyph.setSignal(0);
        setIcon(Ui.MUTED, false);
        setDot(pendingReboot ? Ui.AMBER : Ui.FAINT);
        show(headline, pendingReboot ? "Reboot" : "Hotspot only");
        show(detail, pendingReboot ? "To finish turning on" : "");
        show(meta, pendingReboot ? "Tap to reboot" : "Tap to turn on");
        setContentDescription(pendingReboot ? "Wi-Fi client on, reboot to finish" : "Wi-Fi client off");
    }
}
