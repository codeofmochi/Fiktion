package ch.epfl.sweng.fiktion;

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
        // get my position marker UI
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("My position"));
        // busy wait until GPS is ready
        while(!LocationActivity.gmaps.hasLocation());
        try {
            // try to click the marker
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }
}
