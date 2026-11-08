package com.tilepad;

/**
 * ADB-over-Wi-Fi toggle. "On" means adbd listens over TCP (any computer on the network can
 * connect); "off" means it doesn't. MainActivity reads the state from adbd's properties.
 */
final class AdbTile extends StatusTile {
    AdbTile(Ui ui, TileSpec spec, EditListener edits) {
        super(ui, spec, edits, Glyph.Kind.BUG, "ADB");
        bind(AdbSnapshot.checking());
    }

    void bind(AdbSnapshot s) {
        setAction(null, false);
        switch (s.state) {
            case ON:
                state(Ui.LINK, false, Ui.GREEN, "On", "Port " + s.port, "Tap to turn off");
                setContentDescription("ADB on, any computer on the network can connect on port " + s.port);
                break;
            case STARTING:
                state(Ui.AMBER, true, Ui.AMBER, "Starting", "Opening port", "");
                setContentDescription("ADB starting");
                break;
            case STOPPING:
                state(Ui.MUTED, true, Ui.FAINT, "Stopping", "Closing port", "");
                setContentDescription("ADB stopping");
                break;
            case ERROR:
                state(Ui.RED, false, Ui.RED, "Error", "Didn't switch", "Tap for details");
                setContentDescription("ADB error. " + s.detail);
                break;
            case CHECKING:
                state(Ui.MUTED, true, 0, "Checking…", "", "");
                setContentDescription("ADB, checking");
                break;
            default: // OFF
                state(Ui.MUTED, false, Ui.FAINT, "Off", "Port closed", "Tap to turn on");
                setContentDescription("ADB off");
        }
    }

    private void state(int icon, boolean spinning, int dotColor, String head, String det, String hint) {
        setIcon(icon, spinning);
        setDot(dotColor);
        show(headline, head);
        show(detail, det);
        show(meta, hint);
    }
}
