package com.tilepad;

/**
 * One-button battery preconditioning; all state comes from the service's snapshot. Ready shows
 * a Start pill, a request in flight shows its elapsed time, and Active offers Turn off.
 */
final class PreconTile extends StatusTile {
    PreconTile(Ui ui, TileSpec spec, EditListener edits) {
        super(ui, spec, edits, Glyph.Kind.BATTERY_HEAT, "Battery");
        bind(PreconditioningSnapshot.checking());
    }

    void bind(PreconditioningSnapshot s) {
        PreconPresenter.Tone tone = PreconPresenter.tone(s);
        int color = Ui.tone(tone);
        boolean muted = tone == PreconPresenter.Tone.MUTED || tone == PreconPresenter.Tone.READY;
        setIcon(muted ? (tone == PreconPresenter.Tone.READY ? Ui.LINK : Ui.MUTED) : color, PreconPresenter.busy(s));
        setDot(tone == PreconPresenter.Tone.BUSY ? 0 : muted ? Ui.FAINT : color);
        boolean ready = PreconPresenter.tap(s) == PreconPresenter.Tap.START && !s.requested && s.indicator != 1
            && (s.state == PreconditioningSnapshot.State.IDLE || s.state == PreconditioningSnapshot.State.ENDED);
        if (ready) {
            show(headline, "Ready");
            show(detail, "");
            setAction("Start", true);
        } else if (s.state == PreconditioningSnapshot.State.WAITING && s.requested) {
            show(headline, "Waiting");
            show(detail, "For the car");
            setAction(s.elapsed(), false);
        } else if (s.state == PreconditioningSnapshot.State.ACTIVE) {
            show(headline, "Active");
            show(detail, s.requested ? s.elapsed() : "");
            setAction(s.canStop ? "Turn off" : null, false);
            if (!s.canStop) show(meta, PreconPresenter.hint(s));
        } else {
            setAction(null, false);
            show(headline, PreconPresenter.headline(s));
            show(detail, PreconPresenter.detail(s));
            show(meta, PreconPresenter.hint(s));
        }
        setContentDescription("Battery preconditioning, " + s.title() + ". " + s.detail);
    }
}
