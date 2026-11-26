package cn.crossdesk.mobile;

/**
 * Converts raw pointer axis deltas into whole wheel notches.
 *
 * Android reports one wheel notch as 1.0 on AXIS_VSCROLL / AXIS_HSCROLL, but
 * high-resolution wheels and smooth-scrolling drivers send fractions. Sending
 * one notch per event would drop those movements, so fractions are carried over
 * to the next event on the same axis. A single event is clamped because some
 * vendor stacks report dozens of notches for one detent.
 */
final class RemoteWheel {
    /** Largest number of notches accepted from one event. */
    private static final float MAXIMUM_NOTCHES = 3;
    private float vertical, horizontal;

    /** Returns the whole notches to send, keeping the remainder for the next event. */
    int consume(boolean verticalAxis, float delta) {
        if (!Float.isFinite(delta) || delta == 0) return 0;
        delta = Math.max(-MAXIMUM_NOTCHES, Math.min(MAXIMUM_NOTCHES, delta));
        float total = (verticalAxis ? vertical : horizontal) + delta;
        int notches = (int) total;
        if (verticalAxis) vertical = total - notches;
        else horizontal = total - notches;
        return notches;
    }

    void reset() {
        vertical = 0;
        horizontal = 0;
    }
}
