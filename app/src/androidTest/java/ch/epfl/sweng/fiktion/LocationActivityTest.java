package ch.epfl.sweng.fiktion;

import android.location.Location;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.views.LocationActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

/**
 * Created by dialexo on 18.10.17.
 */
public class LocationActivityTest {

    @Rule
    public final ActivityTestRule<LocationActivity> mActivityRule =
            new ActivityTestRule<>(LocationActivity.class);

    /**
     * Tests if marker exists on map
     */
    @Test
    public void testMarkerMyLocationExists() {
        // setup UI automator
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        // busy wait until GPS is ready
        long t= System.currentTimeMillis();
        long end = t+15000;
        while(System.currentTimeMillis() < end && !LocationActivity.gmaps.hasLocation());

        // get marker when popped
        UiObject marker = device.findObject(new UiSelector().descriptionContains("My position"));
        try {
            // try to click the marker
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }
}
