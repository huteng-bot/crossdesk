package cn.crossdesk.mobile;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Wheel notch accumulation checked without a device, a surface or a session. */
@RunWith(AndroidJUnit4.class)
public class RemoteWheelTest {
    @Test public void oneEventPerNotchMatchesTheOldBehaviour() {
        RemoteWheel wheel = new RemoteWheel();
        assertEquals(1, wheel.consume(true, 1));
        assertEquals(-1, wheel.consume(true, -1));
        assertEquals(0, wheel.consume(true, 0));
    }

    @Test public void fractionalDeltasAccumulateInsteadOfBeingDropped() {
        RemoteWheel wheel = new RemoteWheel();
        assertEquals(0, wheel.consume(true, .4f));
        assertEquals(0, wheel.consume(true, .4f));
        assertEquals(1, wheel.consume(true, .4f));
        assertEquals(0, wheel.consume(true, .5f));
        assertEquals(1, wheel.consume(true, .5f));
    }

    @Test public void reversingDirectionDoesNotEmitAPhantomNotch() {
        RemoteWheel wheel = new RemoteWheel();
        assertEquals(0, wheel.consume(true, .9f));
        assertEquals(0, wheel.consume(true, -1));
        assertEquals(-1, wheel.consume(true, -1));
    }

    @Test public void oneHugeEventIsClampedSoADetentCannotFloodTheWire() {
        RemoteWheel wheel = new RemoteWheel();
        assertEquals(3, wheel.consume(true, 999));
        assertEquals(-3, wheel.consume(true, -999));
    }

    @Test public void horizontalAndVerticalRemaindersAreIndependent() {
        RemoteWheel wheel = new RemoteWheel();
        assertEquals(0, wheel.consume(true, .6f));
        assertEquals(0, wheel.consume(false, .6f));
        assertEquals(1, wheel.consume(false, .6f));
        assertEquals(0, wheel.consume(true, .2f));
        assertEquals(1, wheel.consume(true, .2f));
    }

    @Test public void resetClearsBothRemaindersForANewGesture() {
        RemoteWheel wheel = new RemoteWheel();
        assertEquals(0, wheel.consume(true, .8f));
        assertEquals(0, wheel.consume(false, .8f));
        wheel.reset();
        assertEquals(0, wheel.consume(true, .8f));
        assertEquals(0, wheel.consume(false, .8f));
    }

    @Test public void nonFiniteDeltasAreIgnoredWithoutCorruptingTheAccumulator() {
        RemoteWheel wheel = new RemoteWheel();
        assertEquals(0, wheel.consume(true, Float.NaN));
        assertEquals(0, wheel.consume(true, Float.POSITIVE_INFINITY));
        assertEquals(1, wheel.consume(true, 1));
    }
}
