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
    /**
     * Some vendor stacks pass raw HID wheel units through instead of Android's
     * documented 1.0 per notch: one detent arrives as 120, so two of them would
     * read as 240 notches. Anything at that magnitude is HID units, because no
     * wheel reports 60+ notches in a single event.
     */
    private static final float HID_UNITS = 120;
    private static final float HID_THRESHOLD = 60;
    private float vertical, horizontal;

    /** Returns the whole notches to send, keeping the remainder for the next event. */
    int consume(boolean verticalAxis, float delta) {
        if (!Float.isFinite(delta) || delta == 0) return 0;
        if (Math.abs(delta) >= HID_THRESHOLD) delta /= HID_UNITS;
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
